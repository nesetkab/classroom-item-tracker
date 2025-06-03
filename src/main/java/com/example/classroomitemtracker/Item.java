package com.example.classroomitemtracker;

public class Item {
    // Corresponds to STRING name
    private String name;

    // Corresponds to INTEGER totalCount
    private int totalCount;

    // Corresponds to INTEGER availableCount
    private int availableCount;

    // This is a default constructor. It's useful for some frameworks, but we won't use it directly.
    public Item() {
    }

    // CONSTRUCTOR Item(itemName, initialTotal)
    public Item(String itemName, int initialTotal) {
        this.name = itemName;
        this.totalCount = initialTotal;
        this.availableCount = initialTotal;
    }

    // FUNCTION getName()
    public String getName() {
        return name;
    }

    // FUNCTION getAvailableCount()
    public int getAvailableCount() {
        return availableCount;
    }

    // FUNCTION getTotalCount()
    public int getTotalCount() {
        return totalCount;
    }

    // FUNCTION decrementAvailable()
    public boolean decrementAvailable() {
        if (this.availableCount > 0) {
            this.availableCount--;
            return true;
        } else {
            return false;
        }
    }

    // FUNCTION incrementAvailable()
    public boolean incrementAvailable() {
        if (this.availableCount < this.totalCount) {
            this.availableCount++;
            return true;
        } else {
            return false;
        }
    }

    // Add these two methods inside the Item class

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }
}