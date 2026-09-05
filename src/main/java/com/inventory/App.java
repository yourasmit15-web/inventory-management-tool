package com.inventory;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.inventory.ui.LoginFrame;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            FlatIntelliJLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
