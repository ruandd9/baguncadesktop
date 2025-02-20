package javaapplication1;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean loginSuccessful = false;
    private User loggedUser = null;
    
    // Cores do tema Discord
    private static final Color BACKGROUND_COLOR = new Color(54, 57, 63);
    private static final Color FIELD_COLOR = new Color(64, 68, 75);
    private static final Color BUTTON_COLOR = new Color(88, 101, 242);
    private static final Color BUTTON_HOVER_COLOR = new Color(71, 82, 196);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BORDER_COLOR = new Color(32, 34, 37);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
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

        // Título
        JLabel titleLabel = new JLabel("Bem-vindo de volta!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(titleLabel);
        
        centerContainer.add(Box.createVerticalStrut(20));
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("Estamos felizes em ver você novamente!");
        subtitleLabel.setFont(MAIN_FONT);
        subtitleLabel.setForeground(new Color(185, 187, 190));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(subtitleLabel);
        
        centerContainer.add(Box.createVerticalStrut(30));

        // Painel de campos
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(BACKGROUND_COLOR);
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
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
        
        centerContainer.add(fieldsPanel);
        centerContainer.add(Box.createVerticalStrut(25));
        
        // Botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        JButton loginButton = createStyledButton("Entrar", BUTTON_COLOR);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);
        
        buttonPanel.add(Box.createVerticalStrut(15));
        
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(BACKGROUND_COLOR);
        registerPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        JLabel needAccountLabel = new JLabel("Precisa de uma conta?");
        needAccountLabel.setForeground(new Color(185, 187, 190));
        needAccountLabel.setFont(MAIN_FONT);
        registerPanel.add(needAccountLabel);
        
        JButton registerButton = new JButton("Registrar");
        registerButton.setFont(MAIN_FONT);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(88, 101, 242));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            RegisterDialog registerDialog = new RegisterDialog(SwingUtilities.getWindowAncestor(this));
            registerDialog.setVisible(true);
            if (registerDialog.isRegistrationSuccessful()) {
                emailField.setText(emailField.getText());
                passwordField.requestFocus();
            }
        });
        registerPanel.add(registerButton);
        
        buttonPanel.add(registerPanel);
        
        centerContainer.add(buttonPanel);
        
        // Adiciona o container centralizado ao painel principal
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(centerContainer);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Configurações finais
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Adicionar handler de tecla Enter
        getRootPane().setDefaultButton(loginButton);
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
