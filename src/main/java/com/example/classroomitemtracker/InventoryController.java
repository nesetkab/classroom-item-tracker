package com.example.classroomitemtracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Marks this class as a REST controller
@RequestMapping("/api") // All endpoints in this class will start with /api
public class InventoryController {

    // Spring will automatically provide the single instance of InventoryService here
    @Autowired
    private InventoryService inventoryService;

    /**
     * Endpoint to initialize a new item type.
     * Maps to: POST /api/items
     * Example Usage: POST /api/items?name=calculator&total=15
     */
    @PostMapping("/items")
    public ResponseEntity<?> initializeNewItem(@RequestParam String name, @RequestParam int total) {
        Item newItem = inventoryService.initializeNewItem(name, total);
        if (newItem != null) {
            // 201 Created is a more appropriate success status for creating a resource
            return new ResponseEntity<>(newItem, HttpStatus.CREATED);
        } else {
            // 400 Bad Request is good for invalid input, like a duplicate item name
            return new ResponseEntity<>("Item already exists or total is invalid.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to checkout an item to a student.
     * Maps to: POST /api/checkout
     * Example Usage: POST /api/checkout?itemName=calculator&studentName=Ada Lovelace
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutItem(@RequestParam String itemName, @RequestParam String studentName) {
        String result = inventoryService.checkoutItem(studentName, itemName);
        // If the service message contains "Error", send a "Bad Request" status.
        if (result.startsWith("Error")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result); // ok() is a shortcut for HttpStatus.OK
    }

    /**
     * Endpoint to checkin an item from a student.
     * Maps to: POST /api/checkin
     * Example Usage: POST /api/checkin?itemName=calculator&studentName=Ada Lovelace
     */
    @PostMapping("/checkin")
    public ResponseEntity<String> checkinItem(@RequestParam String itemName, @RequestParam String studentName) {
        String result = inventoryService.checkinItem(studentName, itemName);
        if (result.startsWith("Error")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * The `summary` command from your pseudocode is best split into two API endpoints
     * for a clean REST design. This one gets the full report.
     *
     * Maps to: GET /api/summary
     * Example Usage: GET /api/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryReport() {
        List<Item> itemCounts = inventoryService.getAllItemCounts();
        List<CheckoutRecord> missingItems = inventoryService.getMissingItemsReport();

        Map<String, Object> summaryReport = new HashMap<>();
        summaryReport.put("itemAvailability", itemCounts);
        summaryReport.put("missingItems", missingItems);

        return ResponseEntity.ok(summaryReport);
    }
    // Add this method inside the InventoryController class

    /**
     * Endpoint to delete an item type.
     * Maps to: DELETE /api/items/{itemName}
     * Example Usage: DELETE /api/items/calculator
     */
    @DeleteMapping("/items/{itemName}")
    public ResponseEntity<String> deleteItem(@PathVariable String itemName) {
        String result = inventoryService.deleteItem(itemName);
        if (result.startsWith("Error")) {
            // HttpStatus.CONFLICT (409) is a good status code for "cannot perform because of current state"
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(result);
    }
}