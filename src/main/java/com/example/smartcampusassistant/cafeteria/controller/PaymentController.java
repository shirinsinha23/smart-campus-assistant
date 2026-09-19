package com.example.smartcampusassistant.cafeteria.controller;

import com.example.smartcampusassistant.cafeteria.model.Payment;
import com.example.smartcampusassistant.cafeteria.model.PaymentStatus;
import com.example.smartcampusassistant.cafeteria.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // ===== INITIATE UPI PAYMENT =====
    @PostMapping("/upi/initiate")
    public ResponseEntity<Map<String, Object>> initiateUPIPayment(
            @RequestParam Long orderId,
            @RequestParam Long userId,
            @RequestParam String upiId) {

        log.info("📝 Initiate UPI payment: orderId={}, userId={}, upiId={}", orderId, userId, upiId);

        try {
            Payment payment = paymentService.initiateUPIPayment(orderId, userId, upiId);

            // Generate UPI QR code string
            String upiQR = paymentService.generateUPIQR(
                    upiId,
                    payment.getAmount(),
                    payment.getOrder().getOrderNumber(),
                    payment.getUser().getName()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", payment);
            response.put("upiQR", upiQR);
            response.put("upiApps", paymentService.getUPIApps());
            response.put("message", "UPI payment initiated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Failed to initiate UPI payment:", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ===== CONFIRM PAYMENT =====
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable Long paymentId,
            @RequestParam String transactionId) {

        log.info("📝 Confirm payment: paymentId={}, transactionId={}", paymentId, transactionId);
        Payment payment = paymentService.confirmPayment(paymentId, transactionId);
        return ResponseEntity.ok(payment);
    }

    // ===== FAIL PAYMENT =====
    @PutMapping("/{paymentId}/fail")
    public ResponseEntity<Payment> failPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason) {

        log.info("❌ Fail payment: paymentId={}, reason={}", paymentId, reason);
        Payment payment = paymentService.failPayment(paymentId, reason != null ? reason : "Payment failed");
        return ResponseEntity.ok(payment);
    }

    // ===== GET PAYMENT BY ID =====
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long paymentId) {
        log.info("📋 Get payment by ID: {}", paymentId);
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    // ===== GET PAYMENTS BY USER =====
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable Long userId) {
        log.info("📋 Get payments for user: {}", userId);
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    // ===== GET PAYMENTS BY ORDER =====
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable Long orderId) {
        log.info("📋 Get payments for order: {}", orderId);
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    // ===== GET PAYMENTS BY STATUS =====
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        log.info("📋 Get payments by status: {}", status);
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(PaymentStatus.valueOf(status.toUpperCase())));
    }

    // ===== GET PAYMENT STATS =====
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats(@PathVariable Long userId) {
        log.info("📋 Get payment stats for user: {}", userId);
        return ResponseEntity.ok(paymentService.getPaymentStats(userId));
    }

    // ===== GET TOTAL SPENT =====
    @GetMapping("/user/{userId}/total-spent")
    public ResponseEntity<Map<String, Object>> getTotalSpent(@PathVariable Long userId) {
        log.info("📋 Get total spent for user: {}", userId);
        Map<String, Object> response = new HashMap<>();
        response.put("totalSpent", paymentService.getTotalSpentByUser(userId));
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    // ===== UPI CALLBACK (Webhook) =====
    @PostMapping("/upi/callback")
    public ResponseEntity<Map<String, Object>> handleUPICallback(
            @RequestParam String transactionId,
            @RequestParam String status,
            @RequestParam(required = false) String orderId) {

        log.info("📝 UPI Callback received: transactionId={}, status={}, orderId={}", transactionId, status, orderId);

        try {
            Payment payment = paymentService.handleUPICallback(transactionId, status, orderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", payment);
            response.put("message", "Payment callback processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Failed to process UPI callback:", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ===== GET UPI APPS =====
    @GetMapping("/upi/apps")
    public ResponseEntity<Map<String, String>> getUPIApps() {
        log.info("📋 Get UPI apps");
        return ResponseEntity.ok(paymentService.getUPIApps());
    }

    // ===== GENERATE UPI QR =====
    @PostMapping("/upi/qr/generate")
    public ResponseEntity<Map<String, Object>> generateUPIQR(
            @RequestParam String upiId,
            @RequestParam Double amount,
            @RequestParam String orderNumber,
            @RequestParam String name) {

        log.info("📝 Generate UPI QR: upiId={}, amount={}, orderNumber={}", upiId, amount, orderNumber);

        String qrString = paymentService.generateUPIQR(upiId, amount, orderNumber, name);
        Map<String, Object> response = new HashMap<>();
        response.put("qrString", qrString);
        response.put("upiId", upiId);
        response.put("amount", amount);
        response.put("orderNumber", orderNumber);
        return ResponseEntity.ok(response);
    }
}