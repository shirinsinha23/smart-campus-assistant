package com.example.smartcampusassistant.cafeteria.service;

import com.example.smartcampusassistant.cafeteria.model.Order;
import com.example.smartcampusassistant.cafeteria.model.Payment;
import com.example.smartcampusassistant.cafeteria.model.PaymentStatus;
import com.example.smartcampusassistant.cafeteria.repository.OrderRepository;
import com.example.smartcampusassistant.cafeteria.repository.PaymentRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // UPI payment methods
    private static final Map<String, String> UPI_APPS = new HashMap<>();
    static {
        UPI_APPS.put("googlepay", "Google Pay");
        UPI_APPS.put("phonepe", "PhonePe");
        UPI_APPS.put("paytm", "Paytm");
        UPI_APPS.put("amazonpay", "Amazon Pay");
        UPI_APPS.put("bhim", "BHIM");
        UPI_APPS.put("whatsapp", "WhatsApp Pay");
    }

    @Transactional
    public Payment initiateUPIPayment(Long orderId, Long userId, String upiId) {
        log.info("📝 Initiating UPI payment for order: {} by user: {}", orderId, userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if payment already exists for this order
        List<Payment> existingPayments = paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);
        if (!existingPayments.isEmpty()) {
            log.warn("⚠️ Pending payment already exists for order: {}", orderId);
            return existingPayments.get(0);
        }

        Payment payment = Payment.builder()
                .order(order)
                .user(user)
                .amount(order.getTotalAmount())
                .paymentMethod("UPI")
                .upiId(upiId)
                .transactionId("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8))
                .status(PaymentStatus.PENDING)
                .paymentReference("UPI-" + System.currentTimeMillis())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("✅ UPI payment initiated with ID: {} and Transaction ID: {}", saved.getId(), saved.getTransactionId());

        return saved;
    }

    @Transactional
    public Payment confirmPayment(Long paymentId, String transactionId) {
        log.info("📝 Confirming payment: {} with transaction: {}", paymentId, transactionId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setCompletedAt(LocalDateTime.now());

        // Update order status to CONFIRMED
        Order order = payment.getOrder();
        order.setStatus(com.example.smartcampusassistant.cafeteria.model.OrderStatus.CONFIRMED);

        orderRepository.save(order);
        Payment saved = paymentRepository.save(payment);

        log.info("✅ Payment confirmed successfully: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Payment failPayment(Long paymentId, String reason) {
        log.info("❌ Failing payment: {} reason: {}", paymentId, reason);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        Payment saved = paymentRepository.save(payment);

        log.info("✅ Payment marked as failed: {}", saved.getId());
        return saved;
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Double getTotalSpentByUser(Long userId) {
        Double total = paymentRepository.getTotalSpentByUser(userId);
        return total != null ? total : 0.0;
    }

    public Map<String, Object> getPaymentStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSpent", getTotalSpentByUser(userId));
        stats.put("totalPayments", paymentRepository.findByUserId(userId).size());
        stats.put("pendingPayments", paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.PENDING).size());
        stats.put("completedPayments", paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.COMPLETED).size());
        stats.put("failedPayments", paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.FAILED).size());
        return stats;
    }

    public String generateUPIQR(String upiId, Double amount, String orderNumber, String name) {
        // Generate UPI QR code string
        // Format: upi://pay?pa=UPI_ID&pn=NAME&am=AMOUNT&tn=ORDER_NUMBER
        String encodedName = name.replace(" ", "%20");
        return "upi://pay?pa=" + upiId + "&pn=" + encodedName + "&am=" + amount + "&tn=Order%20" + orderNumber + "&cu=INR";
    }

    public Map<String, String> getUPIApps() {
        return UPI_APPS;
    }

    @Transactional
    public Payment handleUPICallback(String transactionId, String status, String orderId) {
        log.info("📝 Handling UPI callback: transactionId={}, status={}, orderId={}", transactionId, status, orderId);

        // Find payment by transaction ID or order ID
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseGet(() -> {
                    if (orderId != null) {
                        List<Payment> payments = paymentRepository.findByOrderId(Long.parseLong(orderId));
                        return payments.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING).findFirst().orElse(null);
                    }
                    return null;
                });

        if (payment == null) {
            log.warn("⚠️ No payment found for transaction: {}", transactionId);
            throw new RuntimeException("Payment not found for transaction: " + transactionId);
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            return confirmPayment(payment.getId(), transactionId);
        } else {
            return failPayment(payment.getId(), "Payment failed with status: " + status);
        }
    }
}