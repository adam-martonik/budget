
package org.example;

public class CurrencyConverter {
    // Exchange rates to EUR (fixed rates - update as needed)
    private static final double CZK_TO_EUR = 0.0422;
    private static final double HUF_TO_EUR = 0.00253;
    private static final double USD_TO_EUR = 0.92;
    private static final double EUR_TO_EUR = 1.0;

    public static double convertToEuro(double amount, String currency) {
        return switch (currency) {
            case "CZK" -> amount * CZK_TO_EUR;
            case "HUF" -> amount * HUF_TO_EUR;
            case "USD" -> amount * USD_TO_EUR;
            case "EUR" -> amount;
            default -> amount;
        };
    }

    public static String getCurrencySymbol(String currency) {
        return switch (currency) {
            case "CZK" -> "Kč";
            case "HUF" -> "Ft";
            case "USD" -> "$";
            case "EUR" -> "€";
            default -> "";
        };
    }
}