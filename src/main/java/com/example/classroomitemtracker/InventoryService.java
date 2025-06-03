package com.example.classroomitemtracker;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final Map<String, Item> itemsMap = new ConcurrentHashMap<>();
    private final List<CheckoutRecord> checkedOutRecords = new CopyOnWriteArrayList<>();

    /**
     * This method is automatically run by Spring once after the service has been created.
     * It pre-populates our inventory with the three fixed items.
     */
    @PostConstruct
    public void initializeDefaultItems() {
        itemsMap.put("Calculator", new Item("Calculator", 10)); // 10 calculators
        itemsMap.put("Pencil", new Item("Pencil", 30));         // 30 pencils
        itemsMap.put("iPad", new Item("iPad", 5));              // 5 iPads
        System.out.println("Default items have been initialized.");
    }

    // --- Core Logic Methods ---

    public String checkoutItem(String studentName, String itemName) {
        Item itemToCheckout = itemsMap.get(itemName);
        if (itemToCheckout == null) {
            // This check is now mostly for safety; the dropdown should prevent this.
            return "Error: Item type '" + itemName + "' is not tracked.";
        }

        if (itemToCheckout.decrementAvailable()) {
            CheckoutRecord newRecord = new CheckoutRecord(studentName, itemName);
            checkedOutRecords.add(newRecord);
            return "'" + itemName + "' checked out to " + studentName + ".";
        } else {
            return "Sorry, no '" + itemName + "' are currently available.";
        }
    }

    public String checkinItem(String studentName, String itemName) {
        Item itemToReturn = itemsMap.get(itemName);
        if (itemToReturn == null) {
            return "Error: Item type '" + itemName + "' is not tracked by the system.";
        }

        boolean recordFoundAndRemoved = checkedOutRecords.removeIf(record ->
                record.getStudentName().equalsIgnoreCase(studentName) &&
                        record.getItemName().equalsIgnoreCase(itemName)
        );

        if (recordFoundAndRemoved) {
            itemToReturn.incrementAvailable();
            return "'" + itemName + "' returned by " + studentName + ".";
        } else {
            return "Error: System does not show " + studentName + " having a '" + itemName + "'. Check for typos.";
        }
    }

    // --- Methods for Reporting ---

    /**
     * NEW: A method to get the names of the items we are tracking.
     * The frontend will use this to build the dropdown menus.
     */
    public List<String> getTrackedItemNames() {
        return new ArrayList<>(itemsMap.keySet());
    }

    public List<Item> getAllItemCounts() {
        return new ArrayList<>(itemsMap.values());
    }

    public List<CheckoutRecord> getMissingItemsReport() {
        return this.checkedOutRecords;
    }

    // Add this method inside the InventoryService class

    /**
     * Updates the total count for a given item.
     *
     * @param itemName      The name of the item to update.
     * @param newTotalCount The new total quantity for the item.
     * @return A message indicating the result.
     */
    public String updateItemTotalCount(String itemName, int newTotalCount) {
        Item item = itemsMap.get(itemName);
        if (item == null) {
            return "Error: Item '" + itemName + "' not found.";
        }

        if (newTotalCount < 0) {
            return "Error: Total count cannot be negative.";
        }

        int currentlyCheckedOut = item.getTotalCount() - item.getAvailableCount();
        if (newTotalCount < currentlyCheckedOut) {
            return "Error: Cannot set total to " + newTotalCount + ". There are currently "
                    + currentlyCheckedOut + " items checked out.";
        }

        // Calculate the difference to adjust the available count
        int difference = newTotalCount - item.getTotalCount();
        item.setAvailableCount(item.getAvailableCount() + difference);
        item.setTotalCount(newTotalCount);

        return "Total count for '" + itemName + "' updated to " + newTotalCount + ".";
    }
}