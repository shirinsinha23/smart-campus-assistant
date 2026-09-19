package com.example.smartcampusassistant.cafeteria.service;

import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.Order;
import com.example.smartcampusassistant.cafeteria.model.OrderItem;
import com.example.smartcampusassistant.cafeteria.model.OrderStatus;
import com.example.smartcampusassistant.cafeteria.model.Restaurant;
import com.example.smartcampusassistant.cafeteria.repository.MealItemRepository;
import com.example.smartcampusassistant.cafeteria.repository.OrderRepository;
import com.example.smartcampusassistant.cafeteria.repository.RestaurantRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MealItemRepository mealItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public Order placeOrder(Long userId, List<OrderItem> orderItems, String specialInstructions) {
        log.info("📝 Placing order for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }

        double totalAmount = 0.0;
        List<OrderItem> processedItems = new ArrayList<>();
        Long restaurantId = null;

        for (OrderItem item : orderItems) {
            MealItem mealItem = mealItemRepository.findById(item.getMealItem().getId())
                    .orElseThrow(() -> new RuntimeException("Meal item not found: " + item.getMealItem().getId()));

            if (!mealItem.getIsAvailable()) {
                throw new RuntimeException("Meal item is not available: " + mealItem.getName());
            }

            // Get restaurant ID from meal item
            if (restaurantId == null) {
                restaurantId = mealItem.getRestaurantId();
                if (restaurantId == null) {
                    throw new RuntimeException("Meal item does not belong to any restaurant: " + mealItem.getName());
                }
            } else if (!restaurantId.equals(mealItem.getRestaurantId())) {
                throw new RuntimeException("Cannot order from multiple restaurants in one order");
            }

            item.setMealItem(mealItem);
            item.setPrice(mealItem.getPrice());
            item.setQuantity(item.getQuantity());

            totalAmount += mealItem.getPrice() * item.getQuantity();
            processedItems.add(item);
        }

        // Get the restaurant
        Restaurant restaurant = null;
        if (restaurantId != null) {
            restaurant = restaurantRepository.findById(restaurantId).orElse(null);
            if (restaurant == null) {
                log.warn("⚠️ Restaurant not found for ID: {}", restaurantId);
            }
        }

        Order order = Order.builder()
                .user(user)
                .orderItems(processedItems)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .restaurant(restaurant)
                .specialInstructions(specialInstructions)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .estimatedReadyTime(LocalDateTime.now().plusMinutes(15))
                .build();

        Order saved = orderRepository.save(order);
        log.info("✅ Order placed successfully with ID: {} and Order Number: {}", saved.getId(), saved.getOrderNumber());
        return saved;
    }

    public List<Order> getOrdersByUser(Long userId) {
        log.info("📋 Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        log.info("📋 Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("📝 Updating order status: {} -> {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if (newStatus == OrderStatus.READY) {
            order.setEstimatedReadyTime(LocalDateTime.now());
        }

        Order updated = orderRepository.save(order);
        log.info("✅ Order status updated successfully: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("❌ Cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info("✅ Order cancelled successfully: {}", orderId);
    }

    public List<Order> getActiveOrders() {
        log.info("📋 Fetching active orders");
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }

    public long getPendingOrderCount() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.info("📋 Fetching orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }

    // ===== NEW RESTAURANT ORDER METHODS =====

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        log.info("📋 Fetching orders for restaurant: {}", restaurantId);
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status) {
        log.info("📋 Fetching orders for restaurant: {} with status: {}", restaurantId, status);
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status);
    }

    public long countOrdersByRestaurant(Long restaurantId) {
        return orderRepository.countByRestaurantId(restaurantId);
    }

    public long countOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.countByRestaurantIdAndStatus(restaurantId, status);
    }

    public List<Order> getOrdersByRestaurantAndDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("📋 Fetching orders for restaurant: {} between {} and {}", restaurantId, startDate, endDate);
        return orderRepository.findOrdersByRestaurantAndDateRange(restaurantId, startDate, endDate);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}