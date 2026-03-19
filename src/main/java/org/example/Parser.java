package org.example;

public class Parser {
    private final String date;

    public Parser(String date) {
        this.date = date.replace(".", "");
    }

    public String getDate() {
        return date;
    }
}
