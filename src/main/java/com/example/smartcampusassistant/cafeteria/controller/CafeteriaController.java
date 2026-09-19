package com.example.smartcampusassistant.cafeteria.controller;

import com.example.smartcampusassistant.cafeteria.dto.RestaurantResponseDTO;
import com.example.smartcampusassistant.cafeteria.model.CafeteriaFeedback;  // ← Fixed import
import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.Menu;
import com.example.smartcampusassistant.cafeteria.model.Order;
import com.example.smartcampusassistant.cafeteria.model.OrderStatus;
import com.example.smartcampusassistant.cafeteria.model.Restaurant;
import com.example.smartcampusassistant.cafeteria.service.CafeteriaService;
import com.example.smartcampusassistant.cafeteria.service.OrderService;
import com.example.smartcampusassistant.cafeteria.service.RestaurantService;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cafeteria")
@RequiredArgsConstructor
@Slf4j
public class CafeteriaController {

    private final CafeteriaService cafeteriaService;
    private final OrderService orderService;
    private final RestaurantService restaurantService;
    private final UserRepository userRepository;

    // ===== RESTAURANT ENDPOINTS =====

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        log.info("📋 Get all restaurants");
        return ResponseEntity.ok(restaurantService.getActiveRestaurants());
    }

    @GetMapping("/restaurants/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurantsAdmin() {
        log.info("📋 Get all restaurants (admin)");
        return ResponseEntity.ok(restaurantService.getAllRestaurantsWithItems());
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long restaurantId) {
        log.info("📋 Get restaurant by ID: {}", restaurantId);
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    @GetMapping("/restaurants/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Restaurant> getRestaurantByOwner(@PathVariable Long ownerId) {
        log.info("📋 Get restaurant by owner: {}", ownerId);
        return ResponseEntity.ok(restaurantService.getRestaurantByOwner(ownerId));
    }

    @GetMapping("/restaurants/category/{category}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCategory(@PathVariable String category) {
        log.info("📋 Get restaurants by category: {}", category);
        return ResponseEntity.ok(restaurantService.getRestaurantsByCategory(category));
    }

    @PostMapping("/restaurants")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant,
                                                       @RequestParam Long ownerId) {
        log.info("📝 Create restaurant: {}", restaurant.getName());
        Restaurant created = restaurantService.createRestaurant(restaurant, ownerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long restaurantId,
                                                       @RequestBody Restaurant restaurant) {
        log.info("📝 Update restaurant: {}", restaurantId);
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, restaurant));
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        log.info("🗑️ Delete restaurant: {}", restaurantId);
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    // ===== RESTAURANT MENU ITEMS =====

    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<List<MealItem>> getRestaurantItems(@PathVariable Long restaurantId) {
        log.info("📋 Get items for restaurant: {}", restaurantId);
        return ResponseEntity.ok(restaurantService.getRestaurantItems(restaurantId));
    }

    @GetMapping("/restaurants/{restaurantId}/items/available")
    public ResponseEntity<List<MealItem>> getRestaurantAvailableItems(@PathVariable Long restaurantId) {
        log.info("📋 Get available items for restaurant: {}", restaurantId);
        return ResponseEntity.ok(restaurantService.getRestaurantAvailableItems(restaurantId));
    }

    @PostMapping("/restaurants/{restaurantId}/items")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MealItem> addMenuItem(@PathVariable Long restaurantId,
                                                @RequestBody MealItem mealItem) {
        log.info("📝 Add menu item to restaurant: {}", restaurantId);
        MealItem created = restaurantService.addMenuItemToRestaurant(restaurantId, mealItem);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/restaurants/{restaurantId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long restaurantId,
                                               @PathVariable Long itemId) {
        log.info("🗑️ Delete menu item: {} from restaurant: {}", itemId, restaurantId);
        restaurantService.deleteMenuItemFromRestaurant(itemId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    // ===== RESTAURANT ORDERS =====

    @GetMapping("/restaurants/{restaurantId}/orders")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<Order>> getRestaurantOrders(@PathVariable Long restaurantId) {
        log.info("📋 Get orders for restaurant: {}", restaurantId);
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @GetMapping("/restaurants/{restaurantId}/orders/status/{status}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<Order>> getRestaurantOrdersByStatus(@PathVariable Long restaurantId,
                                                                   @PathVariable String status) {
        log.info("📋 Get orders for restaurant: {} with status: {}", restaurantId, status);
        return ResponseEntity.ok(orderService.getOrdersByRestaurantAndStatus(
                restaurantId,
                OrderStatus.valueOf(status.toUpperCase())
        ));
    }

    @GetMapping("/restaurants/{restaurantId}/orders/stats")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getRestaurantOrderStats(@PathVariable Long restaurantId) {
        log.info("📋 Get order stats for restaurant: {}", restaurantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orderService.countOrdersByRestaurant(restaurantId));
        stats.put("pending", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDING));
        stats.put("confirmed", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.CONFIRMED));
        stats.put("preparing", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PREPARING));
        stats.put("ready", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.READY));
        stats.put("completed", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.COMPLETED));
        stats.put("cancelled", orderService.countOrdersByRestaurantAndStatus(restaurantId, OrderStatus.CANCELLED));
        return ResponseEntity.ok(stats);
    }

    // ===== MENU ENDPOINTS =====

    @GetMapping("/menu/today")
    public ResponseEntity<List<Menu>> getTodayMenu() {
        log.info("📋 Get today's menu");
        return ResponseEntity.ok(cafeteriaService.getTodayMenus());
    }

    @GetMapping("/menu/date/{date}")
    public ResponseEntity<List<Menu>> getMenuByDate(@PathVariable String date) {
        log.info("📋 Get menu for date: {}", date);
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(cafeteriaService.getMenusByDate(localDate));
    }

    @GetMapping("/menu/meal/{mealType}")
    public ResponseEntity<List<Menu>> getMenuByMealType(@PathVariable String mealType) {
        log.info("📋 Get menu for meal type: {}", mealType);
        return ResponseEntity.ok(cafeteriaService.getMenusByDate(LocalDate.now()));
    }

    @PostMapping("/menu")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        log.info("📝 Create menu request: {}", menu.getDate());
        return new ResponseEntity<>(cafeteriaService.createMenu(menu), HttpStatus.CREATED);
    }

    @PutMapping("/menu/{menuId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long menuId, @RequestBody Menu menu) {
        log.info("📝 Update menu request: {}", menuId);
        return ResponseEntity.ok(cafeteriaService.updateMenu(menuId, menu));
    }

    @DeleteMapping("/menu/{menuId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        log.info("🗑️ Delete menu request: {}", menuId);
        cafeteriaService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }

    // ===== MEAL ITEM ENDPOINTS =====

    @GetMapping("/items")
    public ResponseEntity<List<MealItem>> getAllMealItems() {
        log.info("📋 Get all meal items");
        return ResponseEntity.ok(cafeteriaService.getAllMealItems());
    }

    @GetMapping("/items/available")
    public ResponseEntity<List<MealItem>> getAvailableMealItems() {
        log.info("📋 Get available meal items");
        return ResponseEntity.ok(cafeteriaService.getAvailableMealItems());
    }

    @GetMapping("/items/type/{mealType}")
    public ResponseEntity<List<MealItem>> getMealItemsByType(@PathVariable String mealType) {
        log.info("📋 Get meal items by type: {}", mealType);
        return ResponseEntity.ok(cafeteriaService.getMealItemsByType(
                com.example.smartcampusassistant.cafeteria.model.MealType.valueOf(mealType.toUpperCase())
        ));
    }

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<MealItem> createMealItem(@RequestBody MealItem mealItem) {
        log.info("📝 Create meal item request: {}", mealItem.getName());
        return new ResponseEntity<>(cafeteriaService.createMealItem(mealItem), HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<MealItem> updateMealItem(@PathVariable Long itemId, @RequestBody MealItem mealItem) {
        log.info("📝 Update meal item request: {}", itemId);
        return ResponseEntity.ok(cafeteriaService.updateMealItem(itemId, mealItem));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteMealItem(@PathVariable Long itemId) {
        log.info("🗑️ Delete meal item request: {}", itemId);
        cafeteriaService.deleteMealItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // ===== ORDER ENDPOINTS =====

    @PostMapping("/order")
    public ResponseEntity<Order> placeOrder(@RequestBody Order orderRequest) {
        log.info("📝 Place order request for user: {}", orderRequest.getUser().getId());
        Order order = orderService.placeOrder(
                orderRequest.getUser().getId(),
                orderRequest.getOrderItems(),
                orderRequest.getSpecialInstructions()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        log.info("📋 Get orders for user: {}", userId);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        log.info("📋 Get order by ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        log.info("📋 Get orders by status: {}", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(
                com.example.smartcampusassistant.cafeteria.model.OrderStatus.valueOf(status.toUpperCase())
        ));
    }

    @PutMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        log.info("📝 Update order status request: {} -> {}", orderId, status);
        Order updated = orderService.updateOrderStatus(
                orderId,
                com.example.smartcampusassistant.cafeteria.model.OrderStatus.valueOf(status.toUpperCase())
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        log.info("❌ Cancel order request: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/pending/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Map<String, Long>> getPendingOrderCount() {
        log.info("📋 Get pending order count");
        Map<String, Long> response = new HashMap<>();
        response.put("pendingCount", orderService.getPendingOrderCount());
        return ResponseEntity.ok(response);
    }

    // ===== QUEUE STATUS ENDPOINT =====

    @GetMapping("/queue/status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        log.info("📋 Get queue status");
        Map<String, Object> response = new HashMap<>();
        response.put("pendingOrders", orderService.getPendingOrderCount());
        response.put("averageWaitTime", "15 minutes");
        response.put("status", "Normal");
        return ResponseEntity.ok(response);
    }

    // ===== CAFETERIA FEEDBACK ENDPOINTS =====

    @PostMapping("/feedback")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<CafeteriaFeedback> submitCafeteriaFeedback(@RequestBody CafeteriaFeedback feedback) {
        log.info("📝 Submit cafeteria feedback request from user: {}", feedback.getUser().getId());
        // Save feedback logic here
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/feedback/meal/{mealItemId}")
    public ResponseEntity<Map<String, Object>> getMealItemFeedback(@PathVariable Long mealItemId) {
        log.info("📋 Get feedback for meal item: {}", mealItemId);
        Map<String, Object> response = new HashMap<>();
        response.put("mealItemId", mealItemId);
        response.put("message", "Feedback endpoint ready");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/orders/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("📋 Get all cafeteria orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}