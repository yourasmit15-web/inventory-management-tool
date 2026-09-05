package com.inventory.ui;

import com.inventory.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Name", "Category", "Stock", "Price (₹)", "Status"};
    private List<Product> rows = new ArrayList<>();

    public void setData(List<Product> products) {
        this.rows = new ArrayList<>(products);
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return rows.get(row);
    }

    @Override public int getRowCount()    { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) { case 0, 3 -> Integer.class; case 4 -> Double.class; default -> String.class; };
    }

    @Override
    public Object getValueAt(int row, int col) {
        Product p = rows.get(row);
        return switch (col) {
            case 0 -> p.getId();
            case 1 -> p.getName();
            case 2 -> p.getCategory();
            case 3 -> p.getQuantity();
            case 4 -> p.getPrice();
            case 5 -> p.getStatus();
            default -> "";
        };
    }
}
