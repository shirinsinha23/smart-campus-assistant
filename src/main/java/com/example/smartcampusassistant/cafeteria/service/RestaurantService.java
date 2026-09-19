// RestaurantService.java
package com.example.smartcampusassistant.cafeteria.service;

import com.example.smartcampusassistant.cafeteria.dto.MealItemDTO;
import com.example.smartcampusassistant.cafeteria.dto.RestaurantResponseDTO;
import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.Restaurant;
import com.example.smartcampusassistant.cafeteria.repository.MealItemRepository;
import com.example.smartcampusassistant.cafeteria.repository.RestaurantRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MealItemRepository mealItemRepository;
    private final UserRepository userRepository;

    // ===== RESTAURANT MANAGEMENT =====

    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant, Long ownerId) {
        log.info("📋 Creating restaurant: {} for owner: {}", restaurant.getName(), ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        restaurant.setOwner(owner);
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("✅ Restaurant created successfully with ID: {}", saved.getId());
        return saved;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }

    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
    }

    public Restaurant getRestaurantByOwner(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found for owner: " + ownerId));
    }

    public List<Restaurant> getRestaurantsByCategory(String category) {
        return restaurantRepository.findByCategory(category);
    }

    @Transactional
    public Restaurant updateRestaurant(Long restaurantId, Restaurant restaurantDetails) {
        log.info("📝 Updating restaurant: {}", restaurantId);

        Restaurant existing = getRestaurantById(restaurantId);

        if (restaurantDetails.getName() != null) existing.setName(restaurantDetails.getName());
        if (restaurantDetails.getDescription() != null) existing.setDescription(restaurantDetails.getDescription());
        if (restaurantDetails.getIcon() != null) existing.setIcon(restaurantDetails.getIcon());
        if (restaurantDetails.getCategory() != null) existing.setCategory(restaurantDetails.getCategory());
        if (restaurantDetails.getAddress() != null) existing.setAddress(restaurantDetails.getAddress());
        if (restaurantDetails.getPhoneNumber() != null) existing.setPhoneNumber(restaurantDetails.getPhoneNumber());
        if (restaurantDetails.getEmail() != null) existing.setEmail(restaurantDetails.getEmail());
        if (restaurantDetails.getLogoUrl() != null) existing.setLogoUrl(restaurantDetails.getLogoUrl());
        if (restaurantDetails.getOpeningTime() != null) existing.setOpeningTime(restaurantDetails.getOpeningTime());
        if (restaurantDetails.getClosingTime() != null) existing.setClosingTime(restaurantDetails.getClosingTime());
        if (restaurantDetails.getIsActive() != null) existing.setIsActive(restaurantDetails.getIsActive());

        return restaurantRepository.save(existing);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        log.info("🗑️ Deleting restaurant: {}", restaurantId);

        // Delete all meal items first
        List<MealItem> items = mealItemRepository.findByRestaurantId(restaurantId);
        if (!items.isEmpty()) {
            mealItemRepository.deleteAll(items);
            log.info("✅ Deleted {} meal items for restaurant", items.size());
        }

        restaurantRepository.deleteById(restaurantId);
        log.info("✅ Restaurant deleted successfully: {}", restaurantId);
    }

    // ===== MEAL ITEMS FOR RESTAURANT =====

    public List<MealItem> getRestaurantItems(Long restaurantId) {
        return mealItemRepository.findByRestaurantId(restaurantId);
    }

    public List<MealItem> getRestaurantAvailableItems(Long restaurantId) {
        return mealItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    @Transactional
    public MealItem addMenuItemToRestaurant(Long restaurantId, MealItem mealItem) {
        log.info("📋 Adding menu item to restaurant: {}", restaurantId);

        Restaurant restaurant = getRestaurantById(restaurantId);
        mealItem.setRestaurant(restaurant);

        MealItem saved = mealItemRepository.save(mealItem);
        log.info("✅ Menu item added successfully: {}", saved.getName());
        return saved;
    }

    @Transactional
    public void deleteMenuItemFromRestaurant(Long itemId, Long restaurantId) {
        log.info("🗑️ Deleting menu item: {} from restaurant: {}", itemId, restaurantId);

        MealItem item = mealItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Meal item not found: " + itemId));

        if (!item.getRestaurantId().equals(restaurantId)) {
            throw new RuntimeException("Item does not belong to this restaurant");
        }

        mealItemRepository.delete(item);
        log.info("✅ Menu item deleted successfully");
    }
    // Add this method to RestaurantService
    public List<RestaurantResponseDTO> getAllRestaurantsWithItems() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RestaurantResponseDTO convertToDTO(Restaurant restaurant) {
        RestaurantResponseDTO dto = RestaurantResponseDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .icon(restaurant.getIcon())
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .isActive(restaurant.getIsActive())
                .ownerId(restaurant.getOwner() != null ? restaurant.getOwner().getId() : null)
                .ownerName(restaurant.getOwner() != null ? restaurant.getOwner().getName() : null)
                .items(restaurant.getItems().stream()
                        .map(this::convertMealItemToDTO)
                        .collect(Collectors.toList()))
                .build();
        return dto;
    }

    private MealItemDTO convertMealItemToDTO(MealItem item) {
        return MealItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .mealType(item.getMealType() != null ? item.getMealType().name() : null)
                .preparationTime(item.getPreparationTime())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}