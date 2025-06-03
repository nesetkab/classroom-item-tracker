package com.example.classroomitemtracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * NEW: Endpoint to get the names of all trackable items for dropdowns.
     * Maps to: GET /api/items/names
     */
    @GetMapping("/items/names")
    public ResponseEntity<List<String>> getTrackedItemNames() {
        return ResponseEntity.ok(inventoryService.getTrackedItemNames());
    }

    /**
     * Endpoint to checkout an item to a student.
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutItem(@RequestParam String itemName, @RequestParam String studentName) {
        String result = inventoryService.checkoutItem(studentName, itemName);
        if (result.startsWith("Error")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint to checkin an item from a student.
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
     * Endpoint to get the full summary report.
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
     * Endpoint to update the total quantity of an item.
     * Maps to: PUT /api/items/{itemName}/quantity
     */
    @PutMapping("/items/{itemName}/quantity")
    public ResponseEntity<String> updateItemQuantity(
            @PathVariable String itemName,
            @RequestParam int total) {
        String result = inventoryService.updateItemTotalCount(itemName, total);
        if (result.startsWith("Error")) {
            // 400 Bad Request is a good status for invalid user input
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }
}