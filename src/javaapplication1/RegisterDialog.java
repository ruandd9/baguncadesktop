package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean registrationSuccessful = false;
    
    public RegisterDialog(Window owner) {
        super(owner, "Criar Conta", ModalityType.APPLICATION_MODAL);
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels e campos
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(textColor);
        nameField = new JTextField(20);
        styleField(nameField, fieldColor, textColor);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        emailField = new JTextField(20);
        styleField(emailField, fieldColor, textColor);
        
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(textColor);
        passwordField = new JPasswordField(20);
        styleField(passwordField, fieldColor, textColor);
        
        JLabel confirmLabel = new JLabel("Confirmar Senha:");
        confirmLabel.setForeground(textColor);
        confirmPasswordField = new JPasswordField(20);
        styleField(confirmPasswordField, fieldColor, textColor);
        
        // Botões
        JButton registerButton = new JButton("Criar Conta");
        styleButton(registerButton, buttonColor, textColor);
        registerButton.addActionListener(e -> register());
        
        JButton cancelButton = new JButton("Cancelar");
        styleButton(cancelButton, fieldColor, textColor);
        cancelButton.addActionListener(e -> dispose());
        
        // Adicionar componentes
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(registerButton);
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
        
        // Enter para registrar
        getRootPane().setDefaultButton(registerButton);
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
    
    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validações
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Por favor, insira um email válido.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "As senhas não coincidem.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "A senha deve ter pelo menos 6 caracteres.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tentar registrar
        if (DatabaseManager.registerUser(name, email, password)) {
            registrationSuccessful = true;
            JOptionPane.showMessageDialog(this,
                "Conta criada com sucesso!\nVocê já pode fazer login.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}
