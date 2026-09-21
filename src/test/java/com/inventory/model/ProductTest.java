package com.inventory.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void statusIsOutOfStockWhenQuantityIsZero() {
        Product product = new Product(1, "Test", "General", 0, 5, 10.0);
        assertEquals("Out of Stock", product.getStatus());
    }

    @Test
    void statusIsLowStockAtOrBelowThreshold() {
        Product product = new Product(1, "Test", "General", 5, 5, 10.0);
        assertEquals("Low Stock", product.getStatus());
    }

    @Test
    void statusIsInStockAboveThreshold() {
        Product product = new Product(1, "Test", "General", 6, 5, 10.0);
        assertEquals("In Stock", product.getStatus());
    }

    @Test
    void totalValueIsQuantityTimesPrice() {
        Product product = new Product(1, "Test", "General", 4, 5, 12.50);
        assertEquals(50.0, product.getTotalValue(), 0.0001);
    }
}
