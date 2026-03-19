package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainUI extends JFrame {
    private final TransactionManager manager;
    private final CategoryManager categoryManager;
    private JTextField amountField;
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> currencyComboBox;
    private JComboBox<String> paymentMethodComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextArea transactionDisplayArea;
    private JTextArea categoryStatsArea;
    private JTextArea periodStatsArea;
    private JLabel idCounterLabel;
    private JComboBox<String> filterTypeComboBox;
    private JComboBox<String> filterCategoryComboBox;
    private JComboBox<String> sortComboBox;
    private JComboBox<String> periodComboBox;
    private JPanel statsPanel;
    private JSplitPane mainSplitPane;
    private int transactionCounter;
    private List<String> customCategories;
    private static final String CATEGORIES_FILE = "categories.dat";

    private static final String[] DEFAULT_CATEGORIES = {"Trip", "Fun", "Car", "Grocery", "Food", "Games", "Coffee", "Salary"};

    public MainUI() {
        manager = new TransactionManager();
        categoryManager = new CategoryManager();
        customCategories = new ArrayList<>(categoryManager.getCategories());
        initializeUI();
        loadTransactionCounter();
    }

    private void initializeUI() {
        setTitle("Budget Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1060);
        setLocationRelativeTo(null);
        setResizable(false);

        // Paleta zelenej
        Color bgLight = new Color(232, 245, 233);
        Color bgMedium = new Color(200, 230, 201);
        Color accent = new Color(56, 142, 60);
        Color accentDark = new Color(46, 125, 50);
        Color textPrimary = new Color(33, 33, 33);
        Color textInverse = new Color(250, 250, 250);

        UIManager.put("TitledBorder.titleColor", accent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgLight);

        JPanel inputPanel = createInputPanel(bgLight, bgMedium, textPrimary, accent, accentDark, textInverse);
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel filterPanel = createFilterPanel(bgLight, bgMedium, textPrimary);
        
        JPanel displayPanel = createDisplayPanel(bgLight, bgMedium, textPrimary);
        
        statsPanel = createStatsPanel(bgLight, bgMedium, textPrimary);
        
        // Split pane - filtre a display
        JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, displayPanel);
        leftSplit.setDividerLocation(250);
        leftSplit.setResizeWeight(0.2);
        
        // Main split pane - content a statistics
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, statsPanel);
        mainSplitPane.setDividerLocation(1000);
        mainSplitPane.setResizeWeight(0.75);
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(bgLight, accent, accentDark, textInverse);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createInputPanel(Color bgLight, Color bgMedium, Color textPrimary, Color accent, Color accentDark, Color textInverse) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));
        panel.setBackground(bgMedium);

        JLabel nameLabel = new JLabel("Meno transakcie:");
        nameLabel.setForeground(textPrimary);
        nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(textPrimary);
        amountField = new JTextField();
        panel.add(amountLabel);
        panel.add(amountField);

        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setForeground(textPrimary);
        currencyComboBox = new JComboBox<>(new String[]{"EUR", "CZK", "HUF", "USD"});
        panel.add(currencyLabel);
        panel.add(currencyComboBox);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(textPrimary);
        typeComboBox = new JComboBox<>(new String[]{"Expense", "Income"});
        panel.add(typeLabel);
        panel.add(typeComboBox);

        JLabel paymentLabel = new JLabel("Spôsob platby:");
        paymentLabel.setForeground(textPrimary);
        paymentMethodComboBox = new JComboBox<>(new String[]{"Kartou", "Hotovosť"});
        panel.add(paymentLabel);
        panel.add(paymentMethodComboBox);

        JLabel categoryLabel = new JLabel("Kategória:");
        categoryLabel.setForeground(textPrimary);
        categoryComboBox = new JComboBox<>();
        updateCategoryComboBox();
        panel.add(categoryLabel);
        panel.add(categoryComboBox);

        idCounterLabel = new JLabel("Next ID: ");
        idCounterLabel.setForeground(textPrimary);
        panel.add(idCounterLabel);
        
        JButton addCategoryButton = new JButton("+ Vlastná Kategória");
        stylePrimaryButton(addCategoryButton, accent, accentDark, textInverse);
        addCategoryButton.setPreferredSize(new Dimension(180, 30));
        addCategoryButton.addActionListener(e -> addCustomCategory());
        panel.add(addCategoryButton);

        return panel;
    }

    private JPanel createFilterPanel(Color bgLight, Color bgMedium, Color textPrimary) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgLight);

        JPanel filterSection = new JPanel();
        filterSection.setLayout(new GridLayout(5, 1, 5, 10));
        filterSection.setBorder(BorderFactory.createTitledBorder("Filter & Sort"));
        filterSection.setBackground(bgMedium);

        JLabel filterTypeLabel = new JLabel("Filter by Type:");
        filterTypeLabel.setForeground(textPrimary);
        filterTypeComboBox = new JComboBox<>(new String[]{"All", "Expense", "Income"});
        filterTypeComboBox.addActionListener(e -> applyFiltersAndSort());
        filterSection.add(filterTypeLabel);
        filterSection.add(filterTypeComboBox);

        JLabel filterCategoryLabel = new JLabel("Filter by Category:");
        filterCategoryLabel.setForeground(textPrimary);
        filterCategoryComboBox = new JComboBox<>();
        updateFilterCategoryComboBox();
        filterCategoryComboBox.addActionListener(e -> applyFiltersAndSort());
        filterSection.add(filterCategoryLabel);
        filterSection.add(filterCategoryComboBox);

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(textPrimary);
        sortComboBox = new JComboBox<>(new String[]{"Date (Newest)", "Date (Oldest)", "Amount (High to Low)", "Amount (Low to High)", "ID"});
        sortComboBox.addActionListener(e -> applyFiltersAndSort());
        filterSection.add(sortLabel);
        filterSection.add(sortComboBox);

        panel.add(filterSection, BorderLayout.NORTH);

        JPanel categoryStatsSection = new JPanel(new BorderLayout());
        categoryStatsSection.setBorder(BorderFactory.createTitledBorder("Category Summary"));
        categoryStatsSection.setBackground(bgMedium);

        categoryStatsArea = new JTextArea();
        categoryStatsArea.setEditable(false);
        categoryStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        categoryStatsArea.setBackground(Color.WHITE);
        categoryStatsArea.setForeground(textPrimary);

        JScrollPane categoryScrollPane = new JScrollPane(categoryStatsArea);
        categoryStatsSection.add(categoryScrollPane, BorderLayout.CENTER);

        panel.add(categoryStatsSection, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(250, 0));

        return panel;
    }

    private JPanel createDisplayPanel(Color bgLight, Color bgMedium, Color textPrimary) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Transactions"));
        panel.setBackground(bgMedium);

        transactionDisplayArea = new JTextArea();
        transactionDisplayArea.setEditable(false);
        transactionDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        transactionDisplayArea.setBackground(Color.WHITE);
        transactionDisplayArea.setForeground(textPrimary);

        JScrollPane scrollPane = new JScrollPane(transactionDisplayArea);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsPanel(Color bgLight, Color bgMedium, Color textPrimary) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Period Statistics"));
        panel.setBackground(bgMedium);

        JPanel periodSelectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        periodSelectPanel.setBackground(bgMedium);

        JLabel periodLabel = new JLabel("Time Period:");
        periodLabel.setForeground(textPrimary);
        periodSelectPanel.add(periodLabel);

        periodComboBox = new JComboBox<>(new String[]{
            "Last Day", "Last Week", "Last Month", "Last 6 Months", "Last Year"
        });
        periodComboBox.addActionListener(e -> updatePeriodStats());
        periodSelectPanel.add(periodComboBox);

        panel.add(periodSelectPanel, BorderLayout.NORTH);

        periodStatsArea = new JTextArea();
        periodStatsArea.setEditable(false);
        periodStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        periodStatsArea.setBackground(Color.WHITE);
        periodStatsArea.setForeground(textPrimary);

        JScrollPane scrollPane = new JScrollPane(periodStatsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel(Color bgLight, Color accent, Color accentDark, Color textInverse) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(bgLight);

        JButton addButton = new JButton("Add Transaction");
        stylePrimaryButton(addButton, accent, accentDark, textInverse);

        JButton refreshButton = new JButton("Refresh");
        stylePrimaryButton(refreshButton, accent, accentDark, textInverse);

        JButton statsButton = new JButton("Show/Hide Statistics");
        stylePrimaryButton(statsButton, accent, accentDark, textInverse);

        JButton clearButton = new JButton("Clear All");
        stylePrimaryButton(clearButton, accent, accentDark, textInverse);

        addButton.addActionListener(e -> addTransaction());
        refreshButton.addActionListener(e -> applyFiltersAndSort());
        statsButton.addActionListener(e -> toggleStatsPanel());
        clearButton.addActionListener(e -> clearAllTransactions());

        panel.add(addButton);
        panel.add(refreshButton);
        panel.add(statsButton);
        panel.add(clearButton);

        return panel;
    }

    private void stylePrimaryButton(JButton button, Color accent, Color accentDark, Color textInverse) {
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(accent);
        button.setForeground(textInverse);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        button.addChangeListener(e -> {
            ButtonModel m = button.getModel();
            if (m.isPressed() || m.isRollover()) {
                button.setBackground(accentDark);
            } else {
                button.setBackground(accent);
            }
        });
    }

    private void toggleStatsPanel() {
        statsPanel.setVisible(!statsPanel.isVisible());
        // Ak je skrytý, nastavíme divider na maximum, inak ho vrátíme na pôvodnú pozíciu
        if (statsPanel.isVisible()) {
            mainSplitPane.setDividerLocation(1000);
        } else {
            mainSplitPane.setDividerLocation(1);
        }
        mainSplitPane.revalidate();
        mainSplitPane.repaint();
    }

    private void addCustomCategory() {
        String newCategory = JOptionPane.showInputDialog(this, "Zadajte meno novej kategórie:");
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            String trimmed = newCategory.trim();
            
            boolean exists = false;
            for (String cat : DEFAULT_CATEGORIES) {
                if (cat.equalsIgnoreCase(trimmed)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                for (String cat : customCategories) {
                    if (cat.equalsIgnoreCase(trimmed)) {
                        exists = true;
                        break;
                    }
                }
            }
            
            if (!exists) {
                customCategories.add(trimmed);
                categoryManager.addCategory(trimmed);  // Uloží a synchronizuje
                updateCategoryComboBox();
                updateFilterCategoryComboBox();
                JOptionPane.showMessageDialog(this, "Kategória '" + trimmed + "' bola pridaná!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Táto kategória už existuje!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void saveCustomCategories() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATEGORIES_FILE))) {
            oos.writeObject(customCategories);
            System.out.println("Custom categories saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CATEGORIES_FILE))) {
            customCategories = (List<String>) ois.readObject();
            System.out.println("Custom categories loaded: " + customCategories);
        } catch (IOException | ClassNotFoundException e) {
            customCategories = new ArrayList<>();
            System.out.println("No custom categories file found or error reading it.");
        }
    }

    private void updateCategoryComboBox() {
        categoryComboBox.removeAllItems();
        for (String cat : DEFAULT_CATEGORIES) {
            categoryComboBox.addItem(cat);
        }
        for (String cat : customCategories) {
            categoryComboBox.addItem(cat);
        }
    }

    private void updateFilterCategoryComboBox() {
        Object selected = filterCategoryComboBox.getSelectedItem();
        filterCategoryComboBox.removeAllItems();
        filterCategoryComboBox.addItem("All");
        for (String cat : DEFAULT_CATEGORIES) {
            filterCategoryComboBox.addItem(cat);
        }
        for (String cat : customCategories) {
            filterCategoryComboBox.addItem(cat);
        }
        if (selected != null) {
            filterCategoryComboBox.setSelectedItem(selected);
        }
    }

    private void addTransaction() {
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String amountStr = amountField.getText().trim();
            String transactionType = (String) typeComboBox.getSelectedItem();
            String currency = (String) currencyComboBox.getSelectedItem();
            String name = nameField.getText().trim();
            String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();

            if (amountStr.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Prosím, vyplňte Meno a Sumu!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountStr);
            amount = Math.round(amount * 20.0) / 20.0;
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amountInEuro = CurrencyConverter.convertToEuro(amount, currency);
            amountInEuro = Math.round(amountInEuro * 20.0) / 20.0;

            String parsedDate = new Parser(date).getDate();
            String id = parsedDate + transactionCounter;

            String labeledName = name + " (" + paymentMethod + ")";

            Transaction transaction;
            if ("Expense".equals(transactionType)) {
                transaction = new Expense(date, amountInEuro, id, labeledName, category);
            } else {
                transaction = new Income(date, amountInEuro, id, labeledName, category);
            }

            manager.saveTransactions(transaction);
            transactionCounter++;
            updateIDLabel();

            nameField.setText("");
            amountField.setText("");

            applyFiltersAndSort();

            JOptionPane.showMessageDialog(this, "Transaction added successfully! (" + String.format("%.2f", amountInEuro) + " EUR)", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number!", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void applyFiltersAndSort() {
        List<Transaction> allTransactions = manager.loadTransactions();
        
        List<Transaction> filtered = new ArrayList<>(allTransactions);
        
        String filterType = (String) filterTypeComboBox.getSelectedItem();
        if (!"All".equals(filterType)) {
            filtered.removeIf(t -> !t.getType().equals(filterType));
        }

        String filterCategory = (String) filterCategoryComboBox.getSelectedItem();
        if (!"All".equals(filterCategory)) {
            filtered.removeIf(t -> !t.getCategory().equals(filterCategory));
        }

        String sortBy = (String) sortComboBox.getSelectedItem();
        if ("Date (Newest)".equals(sortBy)) {
            filtered.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        } else if ("Date (Oldest)".equals(sortBy)) {
            filtered.sort(Comparator.comparing(Transaction::getDate));
        } else if ("Amount (High to Low)".equals(sortBy)) {
            filtered.sort((t1, t2) -> Double.compare(t2.getSum(), t1.getSum()));
        } else if ("Amount (Low to High)".equals(sortBy)) {
            filtered.sort(Comparator.comparingDouble(Transaction::getSum));
        } else if ("ID".equals(sortBy)) {
            filtered.sort(Comparator.comparing(Transaction::getId));
        }

        displayTransactions(filtered);
        displayCategoryStats(allTransactions);
        updatePeriodStats();
    }

    private void displayTransactions(List<Transaction> transactions) {
        transactionDisplayArea.setText("");

        if (transactions.isEmpty()) {
            transactionDisplayArea.setText("No transactions found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(t.toString()).append("\n");
        }

        transactionDisplayArea.setText(sb.toString());
    }

    private void displayCategoryStats(List<Transaction> allTransactions) {
        StringBuilder sb = new StringBuilder();
        
        Set<String> allCategories = new HashSet<>();
        for (String cat : DEFAULT_CATEGORIES) {
            allCategories.add(cat);
        }
        for (String cat : customCategories) {
            allCategories.add(cat);
        }

        List<String> sortedCategories = new ArrayList<>(allCategories);
        sortedCategories.sort(String::compareTo);

        for (String category : sortedCategories) {
            double expenseSum = 0;
            double incomeSum = 0;

            for (Transaction t : allTransactions) {
                if (t.getCategory().equals(category)) {
                    if ("Expense".equals(t.getType())) {
                        expenseSum += t.getSum();
                    } else {
                        incomeSum += t.getSum();
                    }
                }
            }

            if (expenseSum > 0 || incomeSum > 0) {
                sb.append(category).append(":\n");
                if (expenseSum > 0) {
                    sb.append("  Výdaje: €").append(String.format("%.2f", expenseSum)).append("\n");
                }
                if (incomeSum > 0) {
                    sb.append("  Príjmy: €").append(String.format("%.2f", incomeSum)).append("\n");
                }
                sb.append("\n");
            }
        }

        if (sb.length() == 0) {
            categoryStatsArea.setText("Žiadne transakcie.");
        } else {
            categoryStatsArea.setText(sb.toString());
        }
    }

    private void updatePeriodStats() {
        List<Transaction> allTransactions = manager.loadTransactions();
        StringBuilder sb = new StringBuilder();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String period = (String) periodComboBox.getSelectedItem();
        LocalDate startDate = null;

        switch (period) {
            case "Last Day":
                startDate = today.minusDays(1);
                break;
            case "Last Week":
                startDate = today.minusWeeks(1);
                break;
            case "Last Month":
                startDate = today.minusMonths(1);
                break;
            case "Last 6 Months":
                startDate = today.minusMonths(6);
                break;
            case "Last Year":
                startDate = today.minusYears(1);
                break;
        }

        double expense = 0;
        double income = 0;

        for (Transaction t : allTransactions) {
            LocalDate transDate = LocalDate.parse(t.getDate(), formatter);
            if (!transDate.isBefore(startDate)) {
                if ("Expense".equals(t.getType())) {
                    expense += t.getSum();
                } else {
                    income += t.getSum();
                }
            }
        }

        sb.append("=== ").append(period.toUpperCase()).append(" ===\n\n");
        sb.append("Príjmy:   €").append(String.format("%.2f", income)).append("\n");
        sb.append("Výdaje:   €").append(String.format("%.2f", expense)).append("\n");
        sb.append("-".repeat(25)).append("\n");
        sb.append("Zůstatek: €").append(String.format("%.2f", income - expense)).append("\n");

        periodStatsArea.setText(sb.toString());
    }

    private void clearAllTransactions() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all transactions?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            manager.clearAllTransactions();
            transactionCounter = 0;
            updateIDLabel();
            applyFiltersAndSort();
            JOptionPane.showMessageDialog(this, "All transactions deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTransactionCounter() {
        List<Transaction> transactions = manager.loadTransactions();
        transactionCounter = transactions.size();
        updateIDLabel();
        applyFiltersAndSort();
    }

    private void updateIDLabel() {
        idCounterLabel.setText("Next ID: " + transactionCounter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}