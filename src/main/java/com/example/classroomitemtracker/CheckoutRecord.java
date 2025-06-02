package com.example.classroomitemtracker;

public class CheckoutRecord {
    // Corresponds to STRING studentName
    private String studentName;

    // Corresponds to STRING itemName
    private String itemName;

    // This is a default constructor.
    public CheckoutRecord() {
    }

    // CONSTRUCTOR CheckoutRecord(sName, iName)
    public CheckoutRecord(String studentName, String itemName) {
        this.studentName = studentName;
        this.itemName = itemName;
    }

    // FUNCTION getStudentName()
    public String getStudentName() {
        return studentName;
    }

    // FUNCTION getItemName()
    public String getItemName() {
        return itemName;
    }
}