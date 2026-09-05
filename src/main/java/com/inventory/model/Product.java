package com.inventory.model;

public class Product {
    private int    id;
    private String name;
    private String category;
    private int    quantity;
    private int    lowStockThreshold;
    private double price;

    public Product() {}

    public Product(int id, String name, String category,
                   int quantity, int lowStockThreshold, double price) {
        this.id                = id;
        this.name              = name;
        this.category          = category;
        this.quantity          = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.price             = price;
    }

    public int    getId()                { return id; }
    public String getName()              { return name; }
    public String getCategory()          { return category; }
    public int    getQuantity()          { return quantity; }
    public int    getLowStockThreshold() { return lowStockThreshold; }
    public double getPrice()             { return price; }

    public void setId(int id)                              { this.id = id; }
    public void setName(String name)                       { this.name = name; }
    public void setCategory(String category)               { this.category = category; }
    public void setQuantity(int quantity)                  { this.quantity = quantity; }
    public void setLowStockThreshold(int lowStockThreshold){ this.lowStockThreshold = lowStockThreshold; }
    public void setPrice(double price)                     { this.price = price; }

    public String getStatus() {
        if (quantity == 0)                    return "Out of Stock";
        if (quantity <= lowStockThreshold)    return "Low Stock";
        return "In Stock";
    }

    public double getTotalValue() { return quantity * price; }
}
