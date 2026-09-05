package com.inventory.db;

import com.inventory.model.Product;
import com.inventory.model.User;
import com.inventory.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";
    private static DatabaseManager instance;
    private Connection conn;

    private DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            createTables();
            seedData();
        } catch (SQLException e) {
            throw new RuntimeException("Database init failed: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseManager get() {
        try {
            if (instance == null || instance.conn.isClosed())
                instance = new DatabaseManager();
        } catch (SQLException e) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ── Schema ────────────────────────────────────────────────────────────────
    private void createTables() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT    UNIQUE NOT NULL,
                password TEXT    NOT NULL,
                role     TEXT    NOT NULL DEFAULT 'user'
            )""");
        st.execute("""
            CREATE TABLE IF NOT EXISTS products (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                name          TEXT    NOT NULL,
                category      TEXT    NOT NULL,
                quantity      INTEGER NOT NULL DEFAULT 0,
                low_threshold INTEGER NOT NULL DEFAULT 5,
                price         REAL    NOT NULL DEFAULT 0.0
            )""");
    }

    // ── Seed ──────────────────────────────────────────────────────────────────
    private void seedData() throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) > 0) return;  // already seeded

        // Default accounts
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users(username,password,role) VALUES(?,?,?)")) {
            ps.setString(1, "admin"); ps.setString(2, PasswordUtil.hash("admin123")); ps.setString(3, "admin"); ps.executeUpdate();
            ps.setString(1, "user");  ps.setString(2, PasswordUtil.hash("user123"));  ps.setString(3, "user");  ps.executeUpdate();
        }

        // Sample products
        Object[][] products = {
            {"Wireless Keyboard",     "Electronics", 23,  5, 1299.00},
            {"USB-C Hub",             "Electronics",  4,  5, 2499.00},
            {"Notebook A5",           "Stationery",   0, 10,   49.00},
            {"Standing Desk Mat",     "Furniture",   12,  3,  899.00},
            {"Ballpoint Pens (12pk)", "Stationery",   2,  5,   79.00},
            {"Monitor Arm",           "Furniture",    7,  2, 3299.00},
            {"Webcam HD 1080p",       "Electronics",  8,  3, 1799.00},
            {"Sticky Notes (5pk)",    "Stationery",  30, 10,   39.00},
            {"Desk Lamp LED",         "Furniture",    5,  2,  549.00},
            {"Mechanical Mouse",      "Electronics",  3,  4,  999.00},
        };
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products(name,category,quantity,low_threshold,price) VALUES(?,?,?,?,?)")) {
            for (Object[] p : products) {
                ps.setString(1, (String) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setInt(3,    (int)    p[2]);
                ps.setInt(4,    (int)    p[3]);
                ps.setDouble(5, (double) p[4]);
                ps.executeUpdate();
            }
        }
    }

    // ── Authentication ────────────────────────────────────────────────────────
    public Optional<User> authenticate(String username, String password) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && PasswordUtil.verify(password, rs.getString("password"))) {
                return Optional.of(new User(rs.getInt("id"), rs.getString("username"),
                                            rs.getString("password"), rs.getString("role")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    // ── Products ──────────────────────────────────────────────────────────────
    public List<Product> getAllProducts() {
        return query("SELECT * FROM products ORDER BY name");
    }

    public List<Product> search(String q) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? ORDER BY name")) {
            String like = "%" + q.toLowerCase() + "%";
            ps.setString(1, like); ps.setString(2, like);
            return mapProducts(ps.executeQuery());
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Product> getByCategory(String cat) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM products WHERE category = ? ORDER BY name")) {
            ps.setString(1, cat);
            return mapProducts(ps.executeQuery());
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Product> getLowStock() {
        return query("SELECT * FROM products WHERE quantity <= low_threshold ORDER BY quantity");
    }

    public boolean addProduct(Product p) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products(name,category,quantity,low_threshold,price) VALUES(?,?,?,?,?)")) {
            ps.setString(1, p.getName()); ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity()); ps.setInt(4, p.getLowStockThreshold());
            ps.setDouble(5, p.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateProduct(Product p) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET name=?,category=?,quantity=?,low_threshold=?,price=? WHERE id=?")) {
            ps.setString(1, p.getName()); ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity()); ps.setInt(4, p.getLowStockThreshold());
            ps.setDouble(5, p.getPrice()); ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteProduct(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean restock(int id, int amount) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET quantity = quantity + ? WHERE id = ?")) {
            ps.setInt(1, amount); ps.setInt(2, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT DISTINCT category FROM products ORDER BY category")) {
            while (rs.next()) cats.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return cats;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    public int    totalProducts()   { return count("SELECT COUNT(*) FROM products"); }
    public double totalValue()      { return scalar("SELECT COALESCE(SUM(quantity*price),0) FROM products"); }
    public int    lowStockCount()   { return count("SELECT COUNT(*) FROM products WHERE quantity > 0 AND quantity <= low_threshold"); }
    public int    outOfStockCount() { return count("SELECT COUNT(*) FROM products WHERE quantity = 0"); }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private List<Product> query(String sql) {
        try { return mapProducts(conn.createStatement().executeQuery(sql)); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    private List<Product> mapProducts(ResultSet rs) throws SQLException {
        List<Product> list = new ArrayList<>();
        while (rs.next()) list.add(new Product(
            rs.getInt("id"), rs.getString("name"), rs.getString("category"),
            rs.getInt("quantity"), rs.getInt("low_threshold"), rs.getDouble("price")));
        return list;
    }

    private int count(String sql) {
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private double scalar(String sql) {
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) { return 0; }
    }
}
