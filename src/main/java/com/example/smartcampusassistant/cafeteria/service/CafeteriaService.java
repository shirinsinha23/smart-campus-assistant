package com.example.smartcampusassistant.cafeteria.service;

import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.MealType;
import com.example.smartcampusassistant.cafeteria.model.Menu;
import com.example.smartcampusassistant.cafeteria.repository.MealItemRepository;
import com.example.smartcampusassistant.cafeteria.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeteriaService {

    private final MenuRepository menuRepository;
    private final MealItemRepository mealItemRepository;

    // ===== MENU MANAGEMENT =====

    @Transactional
    public Menu createMenu(Menu menu) {
        log.info("📋 Creating menu for date: {} and meal type: {}", menu.getDate(), menu.getMealType());
        menu.setCreatedAt(LocalDate.now());
        Menu saved = menuRepository.save(menu);
        log.info("✅ Menu created successfully with ID: {}", saved.getId());
        return saved;
    }

    public Menu getMenuByDateAndMealType(LocalDate date, MealType mealType) {
        log.info("📋 Fetching menu for date: {} and meal type: {}", date, mealType);
        return menuRepository.findByDateAndMealTypeAndIsActiveTrue(date, mealType).orElse(null);
    }

    public List<Menu> getTodayMenus() {
        log.info("📋 Fetching today's menus");
        return menuRepository.findActiveMenusByDate(LocalDate.now());
    }

    public List<Menu> getMenusByDate(LocalDate date) {
        log.info("📋 Fetching menus for date: {}", date);
        return menuRepository.findByDate(date);
    }

    @Transactional
    public Menu updateMenu(Long menuId, Menu menuDetails) {
        log.info("📝 Updating menu: {}", menuId);
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuId));

        if (menuDetails.getDate() != null) {
            existingMenu.setDate(menuDetails.getDate());
        }
        if (menuDetails.getMealType() != null) {
            existingMenu.setMealType(menuDetails.getMealType());
        }
        if (menuDetails.getItems() != null) {
            existingMenu.setItems(menuDetails.getItems());
        }
        if (menuDetails.getSpecialNote() != null) {
            existingMenu.setSpecialNote(menuDetails.getSpecialNote());
        }
        if (menuDetails.getIsActive() != null) {
            existingMenu.setIsActive(menuDetails.getIsActive());
        }

        return menuRepository.save(existingMenu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        log.info("🗑️ Deleting menu: {}", menuId);
        menuRepository.deleteById(menuId);
        log.info("✅ Menu deleted successfully: {}", menuId);
    }

    // ===== MEAL ITEM MANAGEMENT =====

    @Transactional
    public MealItem createMealItem(MealItem mealItem) {
        log.info("📋 Creating meal item: {}", mealItem.getName());
        MealItem saved = mealItemRepository.save(mealItem);
        log.info("✅ Meal item created successfully with ID: {}", saved.getId());
        return saved;
    }

    public List<MealItem> getAllMealItems() {
        return mealItemRepository.findAll();
    }

    public List<MealItem> getAvailableMealItems() {
        return mealItemRepository.findByIsAvailableTrue();
    }

    public List<MealItem> getMealItemsByType(MealType mealType) {
        return mealItemRepository.findByMealType(mealType);
    }

    @Transactional
    public MealItem updateMealItem(Long itemId, MealItem itemDetails) {
        log.info("📝 Updating meal item: {}", itemId);
        MealItem existingItem = mealItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Meal item not found with id: " + itemId));

        if (itemDetails.getName() != null) {
            existingItem.setName(itemDetails.getName());
        }
        if (itemDetails.getDescription() != null) {
            existingItem.setDescription(itemDetails.getDescription());
        }
        if (itemDetails.getPrice() != null) {
            existingItem.setPrice(itemDetails.getPrice());
        }
        if (itemDetails.getMealType() != null) {
            existingItem.setMealType(itemDetails.getMealType());
        }
        if (itemDetails.getIsAvailable() != null) {
            existingItem.setIsAvailable(itemDetails.getIsAvailable());
        }

        return mealItemRepository.save(existingItem);
    }

    @Transactional
    public void deleteMealItem(Long itemId) {
        log.info("🗑️ Deleting meal item: {}", itemId);
        mealItemRepository.deleteById(itemId);
        log.info("✅ Meal item deleted successfully: {}", itemId);
    }
}