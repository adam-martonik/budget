package org.example;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class TransactionManager {
    private final String filePath;
    private List<Transaction> transactions;

    public TransactionManager() {
        this.filePath = "transactions.dat";
        this.transactions = new ArrayList<>();
        loadTransactions();
    }

    public void saveTransactions(Transaction transaction) {
        transactions.add(transaction);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(transactions);
            System.out.println("Transakcie uložené!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            transactions = (List<Transaction>) ois.readObject();
            return transactions;
        } catch (IOException | ClassNotFoundException e) {
            transactions = new ArrayList<>();
            return transactions;
        }
    }

    public void clearAllTransactions() {
        transactions = new ArrayList<>();
        saveToFile();
    }
}