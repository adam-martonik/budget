
package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager {
    private final String filePath = "categories.dat";
    private List<String> customCategories;

    public CategoryManager() {
        this.customCategories = new ArrayList<>();
        loadCategories();
    }

    public void addCategory(String category) {
        if (!customCategories.contains(category)) {
            customCategories.add(category);
            saveCategories();
        }
    }

    public List<String> getCategories() {
        return new ArrayList<>(customCategories);
    }

    private void saveCategories() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(customCategories);
            System.out.println("Kategórie uložené!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            customCategories = (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            customCategories = new ArrayList<>();
        }
    }
}