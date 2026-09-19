package com.example.smartcampusassistant.cafeteria.repository;

import com.example.smartcampusassistant.cafeteria.model.Order;
import com.example.smartcampusassistant.cafeteria.model.OrderStatus;
import com.example.smartcampusassistant.cafeteria.model.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :time")
    List<Order> findOrdersByStatusAndCreatedBefore(@Param("status") OrderStatus status, @Param("time") LocalDateTime time);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN :statuses")
    long countOrdersByStatus(@Param("statuses") List<OrderStatus> statuses);

    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id = :orderId")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("newStatus") OrderStatus newStatus);

    long countByStatus(OrderStatus status);

    // ===== RESTAURANT QUERIES =====

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    long countByRestaurantId(Long restaurantId);

    long countByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByRestaurantAndDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ===== PAYMENT QUERIES =====

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :status AND o.createdAt < :time")
    List<Order> findOrdersByPaymentStatusAndCreatedBefore(
            @Param("status") PaymentStatus status,
            @Param("time") LocalDateTime time
    );

    @Modifying
    @Query("UPDATE Order o SET o.paymentStatus = :newStatus WHERE o.id = :orderId")
    int updateOrderPaymentStatus(
            @Param("orderId") Long orderId,
            @Param("newStatus") PaymentStatus newStatus
    );

    long countByPaymentStatus(PaymentStatus paymentStatus);

    // ===== USER + PAYMENT QUERIES =====

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.paymentStatus = :paymentStatus")
    List<Order> findByUserIdAndPaymentStatus(
            @Param("userId") Long userId,
            @Param("paymentStatus") PaymentStatus paymentStatus
    );
    @EntityGraph(attributePaths = {"user", "restaurant", "orderItems", "orderItems.mealItem"})
    List<Order> findAll();
}