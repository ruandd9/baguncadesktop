package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean loginSuccessful = false;
    private User loggedUser = null;
    
    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setupUI();
    }
    
    private void setupUI() {
        // Cores do tema Discord
        Color backgroundColor = new Color(54, 57, 63);
        Color fieldColor = new Color(64, 68, 75);
        Color buttonColor = new Color(88, 101, 242);
        Color textColor = Color.WHITE;
        
        // Configurar layout
        setLayout(new BorderLayout(10, 10));
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Labels
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(textColor);
        
        // Campos
        emailField = new JTextField(20);
        styleField(emailField, fieldColor, textColor);
        
        passwordField = new JPasswordField(20);
        styleField(passwordField, fieldColor, textColor);
        
        // Botões
        JButton loginButton = new JButton("Entrar");
        styleButton(loginButton, buttonColor, textColor);
        loginButton.addActionListener(e -> login());
        
        JButton cancelButton = new JButton("Cancelar");
        styleButton(cancelButton, fieldColor, textColor);
        cancelButton.addActionListener(e -> dispose());
        
        // Adicionar componentes
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Adicionar painéis ao diálogo
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel);
        
        // Configurações finais
        pack();
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Enter para login
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void styleField(JTextField field, Color bgColor, Color fgColor) {
        field.setBackground(bgColor);
        field.setForeground(fgColor);
        field.setCaretColor(fgColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(32, 34, 37)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        loggedUser = DatabaseManager.authenticateUser(email, password);
        if (loggedUser != null) {
            loginSuccessful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Email ou senha incorretos.",
                "Erro de Login",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    public User getLoggedUser() {
        return loggedUser;
    }
}
