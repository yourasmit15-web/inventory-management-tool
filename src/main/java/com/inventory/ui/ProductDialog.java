package com.inventory.ui;

import com.inventory.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ProductDialog extends JDialog {

    private JTextField  nameField, categoryField, qtyField, lowField, priceField;
    private JComboBox<String> categoryCombo;
    private boolean     confirmed = false;

    public ProductDialog(Frame parent, String title, Product existing, List<String> categories) {
        super(parent, title, true);
        setSize(440, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI(existing, categories);
    }

    private void buildUI(Product existing, List<String> categories) {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Color.WHITE);

        // ── Header ──
        JPanel header = new JPanel();
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel(existing == null ? "Add New Product" : "Edit Product");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // ── Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST; lc.insets = new Insets(6, 0, 2, 12); lc.gridx = 0; lc.fill = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL; fc.insets = new Insets(6, 0, 2, 0); fc.gridx = 1; fc.weightx = 1.0;

        nameField     = new JTextField(existing != null ? existing.getName()     : "");
        qtyField      = new JTextField(existing != null ? String.valueOf(existing.getQuantity())          : "0");
        lowField      = new JTextField(existing != null ? String.valueOf(existing.getLowStockThreshold()) : "5");
        priceField    = new JTextField(existing != null ? String.format("%.2f", existing.getPrice())     : "0.00");

        String[] catOptions = categories.toArray(new String[0]);
        categoryCombo = new JComboBox<>(catOptions);
        categoryCombo.setEditable(true);
        if (existing != null) categoryCombo.setSelectedItem(existing.getCategory());

        addRow(form, lc, fc, 0, "Product Name *", nameField);
        addRow(form, lc, fc, 1, "Category *",     categoryCombo);
        addRow(form, lc, fc, 2, "Quantity *",     qtyField);
        addRow(form, lc, fc, 3, "Low Threshold",  lowField);
        addRow(form, lc, fc, 4, "Price (₹) *",    priceField);

        JLabel req = new JLabel("* Required fields");
        req.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        req.setForeground(new Color(150, 150, 150));
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.gridy = 5; rc.gridwidth = 2; rc.insets = new Insets(10, 0, 0, 0); rc.anchor = GridBagConstraints.WEST;
        form.add(req, rc);

        root.add(form, BorderLayout.CENTER);

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(new Color(245, 247, 250));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton cancel = new JButton("Cancel");
        cancel.setFocusPainted(false);
        cancel.addActionListener(e -> dispose());

        JButton save = new JButton(existing == null ? "Add Product" : "Save Changes");
        save.setBackground(new Color(25, 118, 210));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setBorderPainted(false);
        save.setOpaque(true);
        save.addActionListener(e -> doSave());

        btnPanel.add(cancel);
        btnPanel.add(save);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(save);
    }

    private void addRow(JPanel form, GridBagConstraints lc, GridBagConstraints fc,
                        int row, String labelText, JComponent field) {
        lc.gridy = row; fc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(label, lc);
        styleInput(field);
        form.add(field, fc);
    }

    private void styleInput(JComponent c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(0, 32));
    }

    private void doSave() {
        String name = nameField.getText().trim();
        String cat  = (categoryCombo.getEditor().getItem().toString()).trim();
        if (name.isEmpty()) { error("Product name is required."); return; }
        if (cat.isEmpty())  { error("Category is required.");    return; }
        try {
            int    qty   = Integer.parseInt(qtyField.getText().trim());
            int    low   = Integer.parseInt(lowField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            if (qty < 0 || low < 0 || price < 0) { error("Values must be ≥ 0."); return; }
            confirmed = true;
            dispose();
        } catch (NumberFormatException e) { error("Qty, Low, and Price must be valid numbers."); }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }

    public Product getProduct(int existingId) {
        return new Product(
            existingId,
            nameField.getText().trim(),
            categoryCombo.getEditor().getItem().toString().trim(),
            Integer.parseInt(qtyField.getText().trim()),
            Integer.parseInt(lowField.getText().trim()),
            Double.parseDouble(priceField.getText().trim())
        );
    }
}
