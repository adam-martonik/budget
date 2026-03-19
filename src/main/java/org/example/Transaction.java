package org.example;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public abstract class Transaction implements Serializable {
    private final String date;
    private final double sum;
    private final String id;
    private final String name;
    private final String category;

    protected Transaction(String date, double sum, String id, String name, String category) {
        this.date = date;
        this.sum = sum;
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public abstract String getType();

    public String getDate() {
        return date;
    }
    public double getSum() {
        return sum;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }

    public String toString(){
        return getType() + " | " + date + " | €" + String.format("%.2f", sum) + " | " + id + " | " + name + " | [" + category + "]";
    }
}