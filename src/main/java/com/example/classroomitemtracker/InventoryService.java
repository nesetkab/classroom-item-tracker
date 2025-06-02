package com.example.classroomitemtracker;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service // Tells Spring to manage this class as a service
public class InventoryService {

    // Replaces `itemsList = new ArrayList<Item>()`.
    // We use a Map to easily find items by their name (e.g., "calculator").
    // A ConcurrentHashMap is "thread-safe," which is good practice for web applications.
    private final Map<String, Item> itemsMap = new ConcurrentHashMap<>();

    // Replaces `checkedOutRecords = new ArrayList<CheckoutRecord>()`.
    // A CopyOnWriteArrayList is also thread-safe.
    private final List<CheckoutRecord> checkedOutRecords = new CopyOnWriteArrayList<>();


    // --- Core Logic Methods based on your Pseudocode ---

    /**
     * Corresponds to the logic inside the 'InitializeSystem' loop for adding a new item.
     * In a web app, initialization happens one item at a time via API calls,
     * not in a single loop on startup.
     *
     * @param itemName The name of the item to add (e.g., "calculator").
     * @param total The total number of this item available.
     * @return The newly created Item object, or null if it already exists or input is invalid.
     */
    public Item initializeNewItem(String itemName, int total) {
        if (total < 0) {
            System.err.println("Total count cannot be negative.");
            return null;
        }
        if (itemsMap.containsKey(itemName)) {
            System.err.println("Item '" + itemName + "' already exists.");
            return null;
        }

        Item newItem = new Item(itemName, total);
        itemsMap.put(itemName, newItem);
        System.out.println("'" + itemName + "' added with " + total + " items.");
        return newItem;
    }

    /**
     * Corresponds to `CheckoutItem(studentName, itemName)`.
     * We'll return a String message to show the result to the user.
     */
    public String checkoutItem(String studentName, String itemName) {
        // Corresponds to `findItemInList(itemName)`
        Item itemToCheckout = itemsMap.get(itemName);

        if (itemToCheckout == null) {
            return "Error: Item type '" + itemName + "' is not tracked.";
        }

        // The decrementAvailable() method already checks if count > 0.
        if (itemToCheckout.decrementAvailable()) {
            CheckoutRecord newRecord = new CheckoutRecord(studentName, itemName);
            checkedOutRecords.add(newRecord);
            return "'" + itemName + "' checked out to " + studentName + ".";
        } else {
            return "Sorry, no '" + itemName + "' are currently available.";
        }
    }

    /**
     * Corresponds to `CheckinItem(studentName, itemName)`.
     * Returns a String message with the result.
     */
    public String checkinItem(String studentName, String itemName) {
        Item itemToReturn = itemsMap.get(itemName);

        if (itemToReturn == null) {
            return "Error: Item type '" + itemName + "' is not tracked by the system.";
        }

        // Find and remove the matching checkout record.
        // We use a modern Java stream and lambda for a concise search.
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
     * Gets a list of all currently tracked items and their counts.
     * Part of the `GenerateSummaryReport` logic.
     */
    public List<Item> getAllItemCounts() {
        return itemsMap.values().stream().collect(Collectors.toList());
    }

    /**
     * Gets a list of all items that are currently checked out.
     * Part of the `GenerateSummaryReport` logic.
     */
    public List<CheckoutRecord> getMissingItemsReport() {
        return this.checkedOutRecords;
    }
}