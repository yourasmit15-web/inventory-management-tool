package com.inventory.ui;

import com.inventory.db.DatabaseManager;
import com.inventory.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private static final Color PRIMARY    = new Color(25, 118, 210);
    private static final Color BG_LEFT    = new Color(13, 71, 161);
    private static final Color BG_RIGHT   = new Color(245, 247, 250);
    private static final Color ERROR_RED  = new Color(211, 47, 47);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JButton        loginButton;

    public LoginFrame() {
        setTitle("Inventory Manager — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(340);
        split.setDividerSize(0);
        split.setEnabled(false);
        setContentPane(split);
    }

    // ── Left branding panel ───────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_LEFT, 0, getHeight(), new Color(21, 101, 192));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(340, 520));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel icon = new JLabel("📦", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Manager", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.BOLD, 30));
        sub.setForeground(new Color(187, 222, 251));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Track, manage &amp; control<br>your inventory with ease</center></html>", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(200, 225, 255));
        tagline.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(200, 1));

        JLabel hint = new JLabel("<html><center><b>Default Accounts</b><br>"
            + "admin / admin123<br>user / user123</center></html>", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(new Color(180, 210, 255));
        hint.setAlignmentX(CENTER_ALIGNMENT);

        inner.add(Box.createVerticalGlue());
        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(title);
        inner.add(sub);
        inner.add(Box.createVerticalStrut(20));
        inner.add(tagline);
        inner.add(Box.createVerticalStrut(24));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(24));
        inner.add(hint);
        inner.add(Box.createVerticalGlue());

        panel.add(inner);
        return panel;
    }

    // ── Right form panel ──────────────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_RIGHT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(40, 40, 40, 40)));

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(30, 30, 30));
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subHeading = new JLabel("Sign in to your account");
        subHeading.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subHeading.setForeground(new Color(120, 120, 120));
        subHeading.setAlignmentX(LEFT_ALIGNMENT);

        usernameField = styledField("Username");
        passwordField = new JPasswordField();
        styleField(passwordField, "Password");

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());

        // Enter key triggers login
        getRootPane().setDefaultButton(loginButton);

        card.add(heading);
        card.add(Box.createVerticalStrut(4));
        card.add(subHeading);
        card.add(Box.createVerticalStrut(28));
        card.add(label("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(label("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginButton);

        card.setMaximumSize(new Dimension(320, 400));
        panel.add(card);
        return panel;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<Optional<User>, Void> worker = new SwingWorker<>() {
            @Override protected Optional<User> doInBackground() {
                return DatabaseManager.get().authenticate(username, password);
            }
            @Override protected void done() {
                try {
                    Optional<User> user = get();
                    if (user.isPresent()) {
                        dispose();
                        SwingUtilities.invokeLater(() -> new MainFrame(user.get()).setVisible(true));
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        passwordField.setText("");
                        loginButton.setEnabled(true);
                        loginButton.setText("Sign In");
                    }
                } catch (Exception ex) {
                    errorLabel.setText("Login error: " + ex.getMessage());
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                }
            }
        };
        worker.execute();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        styleField(f, placeholder);
        return f;
    }

    private void styleField(JTextField f, String placeholder) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(240, 36));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.putClientProperty("JTextField.placeholderText", placeholder);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(80, 80, 80));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
