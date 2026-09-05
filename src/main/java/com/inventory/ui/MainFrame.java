package com.inventory.ui;

import com.inventory.db.DatabaseManager;
import com.inventory.model.Product;
import com.inventory.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainFrame extends JFrame {

    // ── Palette ──
    private static final Color PRIMARY   = new Color(25, 118, 210);
    private static final Color TOPBAR_BG = new Color(13, 71, 161);
    private static final Color CONTENT   = new Color(245, 247, 250);
    private static final Color GREEN     = new Color(46, 125, 50);
    private static final Color ORANGE    = new Color(230, 81, 0);
    private static final Color RED       = new Color(198, 40, 40);
    private static final Color CARD_BG   = Color.WHITE;

    private final User              currentUser;
    private final DatabaseManager   db = DatabaseManager.get();
    private final ProductTableModel tableModel = new ProductTableModel();

    private JTable  table;
    private JLabel  statusLabel;
    private JLabel  statsTotal, statsValue, statsLow, statsOut;
    private JTextField searchField;
    private JComboBox<String> catFilter;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Inventory Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        buildUI();
        loadData(db.getAllProducts());
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildCenter(),   BorderLayout.CENTER);
        root.add(buildStatusBar(),BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Top Bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(TOPBAR_BG);
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("📦  Inventory Manager");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel userLbl = new JLabel("👤  " + currentUser.getUsername()
            + "  [" + currentUser.getRole().toUpperCase() + "]");
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(new Color(200, 220, 255));

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.setForeground(Color.WHITE);
        logout.setBackground(new Color(198, 40, 40));
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setOpaque(true);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        right.add(userLbl);
        right.add(logout);
        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(CONTENT);
        center.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(buildStatsRow());
        top.add(Box.createVerticalStrut(14));
        top.add(buildToolbar());
        top.add(Box.createVerticalStrut(10));

        center.add(top,            BorderLayout.NORTH);
        center.add(buildTable(),   BorderLayout.CENTER);
        return center;
    }

    // ── Stats Cards ───────────────────────────────────────────────────────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        statsTotal = new JLabel("0");
        statsValue = new JLabel("₹0");
        statsLow   = new JLabel("0");
        statsOut   = new JLabel("0");

        row.add(statCard("Total Products",   statsTotal, PRIMARY));
        row.add(statCard("Inventory Value",  statsValue, GREEN));
        row.add(statCard("Low Stock",        statsLow,   ORANGE));
        row.add(statCard("Out of Stock",     statsOut,   RED));
        return row;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(14, 18, 14, 18)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(4, 0));

        card.add(bar,        BorderLayout.WEST);
        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);

        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search products...");
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.addActionListener(e -> doSearch());

        catFilter = new JComboBox<>();
        catFilter.addItem("All Categories");
        db.getCategories().forEach(catFilter::addItem);
        catFilter.setPreferredSize(new Dimension(160, 32));
        catFilter.addActionListener(e -> doFilter());

        JButton searchBtn  = toolBtn("🔍 Search",  PRIMARY,              e -> doSearch());
        JButton addBtn     = toolBtn("➕ Add",      new Color(46,125,50), e -> doAdd());
        JButton editBtn    = toolBtn("✏️ Edit",     PRIMARY,              e -> doEdit());
        JButton deleteBtn  = toolBtn("🗑 Delete",   RED,                  e -> doDelete());
        JButton restockBtn = toolBtn("📦 Restock",  new Color(230,81,0),  e -> doRestock());
        JButton refreshBtn = toolBtn("↺ Refresh",   new Color(96,96,96),  e -> loadData(db.getAllProducts()));

        bar.add(searchField); bar.add(catFilter); bar.add(searchBtn);
        bar.add(new JSeparator(JSeparator.VERTICAL));
        bar.add(addBtn); bar.add(editBtn); bar.add(deleteBtn); bar.add(restockBtn);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(refreshBtn);
        return bar;
    }

    private JButton toolBtn(String text, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 12, 32));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(227, 242, 253));
        table.setSelectionForeground(new Color(13, 71, 161));
        table.setGridColor(new Color(235, 235, 235));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(227, 242, 253));
        header.setForeground(new Color(25, 118, 210));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));

        // Column widths
        int[] widths = {55, 230, 130, 70, 110, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status column color renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(CENTER);
                String status = val.toString();
                if (!sel) {
                    switch (status) {
                        case "Out of Stock" -> { lbl.setBackground(new Color(255, 235, 238)); lbl.setForeground(RED); }
                        case "Low Stock"    -> { lbl.setBackground(new Color(255, 248, 225)); lbl.setForeground(ORANGE); }
                        default             -> { lbl.setBackground(new Color(232, 245, 233)); lbl.setForeground(GREEN); }
                    }
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                }
                return lbl;
            }
        });

        // Center-align qty column
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) doEdit();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)));
        bar.setBackground(new Color(250, 250, 250));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        bar.add(statusLabel);
        return bar;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    private void loadData(List<Product> products) {
        tableModel.setData(products);

        // Refresh stats from DB
        statsTotal.setText(String.valueOf(db.totalProducts()));
        statsValue.setText(String.format("₹%,.0f", db.totalValue()));
        statsLow.setText(String.valueOf(db.lowStockCount()));
        statsOut.setText(String.valueOf(db.outOfStockCount()));

        // Refresh category filter (keep selection)
        String selected = (String) catFilter.getSelectedItem();
        catFilter.removeAllItems();
        catFilter.addItem("All Categories");
        db.getCategories().forEach(catFilter::addItem);
        catFilter.setSelectedItem(selected);

        status("Showing " + products.size() + " product(s)");
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadData(db.getAllProducts()); return; }
        List<Product> results = db.search(q);
        tableModel.setData(results);
        status("Search: \"" + q + "\" — " + results.size() + " result(s)");
    }

    private void doFilter() {
        String cat = (String) catFilter.getSelectedItem();
        if (cat == null || cat.equals("All Categories")) { loadData(db.getAllProducts()); return; }
        List<Product> results = db.getByCategory(cat);
        tableModel.setData(results);
        status("Category: " + cat + " — " + results.size() + " product(s)");
    }

    private void doAdd() {
        ProductDialog dlg = new ProductDialog(this, "Add Product", null, db.getCategories());
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            db.addProduct(dlg.getProduct(0));
            loadData(db.getAllProducts());
            status("Product added successfully.");
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { alert("Select a product to edit."); return; }
        Product p = tableModel.getProductAt(table.convertRowIndexToModel(row));
        ProductDialog dlg = new ProductDialog(this, "Edit Product", p, db.getCategories());
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            db.updateProduct(dlg.getProduct(p.getId()));
            loadData(db.getAllProducts());
            status("Product '" + p.getName() + "' updated.");
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { alert("Select a product to delete."); return; }
        Product p = tableModel.getProductAt(table.convertRowIndexToModel(row));
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete \"" + p.getName() + "\"?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            db.deleteProduct(p.getId());
            loadData(db.getAllProducts());
            status("Product '" + p.getName() + "' deleted.");
        }
    }

    private void doRestock() {
        int row = table.getSelectedRow();
        if (row < 0) { alert("Select a product to restock."); return; }
        Product p = tableModel.getProductAt(table.convertRowIndexToModel(row));
        String input = JOptionPane.showInputDialog(this,
            "Restock: " + p.getName() + "\nCurrent Stock: " + p.getQuantity() + "\n\nAdd quantity:",
            "Restock Product", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) return;
        try {
            int add = Integer.parseInt(input.trim());
            if (add <= 0) { alert("Quantity must be positive."); return; }
            db.restock(p.getId(), add);
            loadData(db.getAllProducts());
            status("Restocked '" + p.getName() + "' by +" + add);
        } catch (NumberFormatException e) { alert("Enter a valid number."); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void status(String msg) { statusLabel.setText("  " + msg); }
    private void alert(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.INFORMATION_MESSAGE);
    }
}
