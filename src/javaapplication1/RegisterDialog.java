package javaapplication1;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean registrationSuccessful = false;
    
    // Cores do tema Discord
    private static final Color BACKGROUND_COLOR = new Color(54, 57, 63);
    private static final Color FIELD_COLOR = new Color(64, 68, 75);
    private static final Color BUTTON_COLOR = new Color(88, 101, 242);
    private static final Color BUTTON_HOVER_COLOR = new Color(71, 82, 196);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BORDER_COLOR = new Color(32, 34, 37);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public RegisterDialog(Window owner) {
        super(owner, "Criar Conta", ModalityType.APPLICATION_MODAL);
        setupUI();
    }
    
    private void setupUI() {
        // Configurar layout
        setLayout(new BorderLayout());
        
        // Painel principal com padding
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Container para centralizar o conteúdo
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(BACKGROUND_COLOR);
        centerContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        // Logo placeholder
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(150, 150));
        logoLabel.setMaximumSize(new Dimension(150, 150));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Comentado até ter a imagem real
        // ImageIcon logo = new ImageIcon("path/to/logo.png");
        // Image scaledImage = logo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        // logoLabel.setIcon(new ImageIcon(scaledImage));
        centerContainer.add(logoLabel);
        centerContainer.add(Box.createVerticalStrut(20));

        // Título
        JLabel titleLabel = new JLabel("Criar uma conta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(titleLabel);
        
        centerContainer.add(Box.createVerticalStrut(30));

        // Painel de campos
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(BACKGROUND_COLOR);
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        // Nome
        JLabel nameLabel = new JLabel("NOME");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(185, 187, 190));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(nameLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        
        nameField = createStyledField(new JTextField(20));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(nameField);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Email
        JLabel emailLabel = new JLabel("EMAIL");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(185, 187, 190));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emailLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        
        emailField = createStyledField(new JTextField(20));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emailField);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Senha
        JLabel passwordLabel = new JLabel("SENHA");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(185, 187, 190));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        
        passwordField = createStyledPasswordField(new JPasswordField(20));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Confirmar Senha
        JLabel confirmLabel = new JLabel("CONFIRMAR SENHA");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmLabel.setForeground(new Color(185, 187, 190));
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(confirmLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        
        confirmPasswordField = createStyledPasswordField(new JPasswordField(20));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(confirmPasswordField);
        
        centerContainer.add(fieldsPanel);
        centerContainer.add(Box.createVerticalStrut(25));
        
        // Botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        JButton registerButton = createStyledButton("Continuar", BUTTON_COLOR);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);
        
        buttonPanel.add(Box.createVerticalStrut(15));
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBackground(BACKGROUND_COLOR);
        loginPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        JLabel hasAccountLabel = new JLabel("Já tem uma conta?");
        hasAccountLabel.setForeground(new Color(185, 187, 190));
        hasAccountLabel.setFont(MAIN_FONT);
        loginPanel.add(hasAccountLabel);
        
        JButton loginButton = new JButton("Fazer Login");
        loginButton.setFont(MAIN_FONT);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setForeground(new Color(88, 101, 242));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> dispose());
        loginPanel.add(loginButton);
        
        buttonPanel.add(loginPanel);
        
        centerContainer.add(buttonPanel);
        
        // Adiciona o container centralizado ao painel principal
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(centerContainer);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Configurações finais
        setSize(400, 600);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Adicionar handler de tecla Enter
        getRootPane().setDefaultButton(registerButton);
    }
    
    private JTextField createStyledField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(300, 40));
        return field;
    }
    
    private JPasswordField createStyledPasswordField(JPasswordField field) {
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(300, 40));
        field.setEchoChar('●');
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(MAIN_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(300, 40));
        
        return button;
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
