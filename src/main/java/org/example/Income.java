package org.example;

public class Income extends Transaction {

    public Income(String date, double sum, String id, String name, String category) {
        super(date, sum, id, name, category);
    }

    @Override
    public String getType() {
        return "Income";
    }
}