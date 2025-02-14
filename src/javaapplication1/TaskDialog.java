package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.toedter.calendar.JDateChooser;

public class TaskDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<Task.Priority> priorityCombo;
    private JDateChooser dueDateChooser;
    private boolean confirmed = false;
    private String taskName;
    private Task.Priority priority;
    private Date dueDate;
    
    public TaskDialog(Window owner, String title, String initialName, 
                     Task.Priority initialPriority, Date initialDueDate) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setupUI(initialName, initialPriority, initialDueDate);
    }
    
    private void setupUI(String initialName, Task.Priority initialPriority, Date initialDueDate) {
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
        
        // Nome da tarefa
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(textColor);
        nameField = new JTextField(20);
        nameField.setText(initialName);
        styleField(nameField, fieldColor, textColor);
        
        // Prioridade
        JLabel priorityLabel = new JLabel("Prioridade:");
        priorityLabel.setForeground(textColor);
        priorityCombo = new JComboBox<>(Task.Priority.values());
        priorityCombo.setSelectedItem(initialPriority != null ? initialPriority : Task.Priority.MEDIA);
        styleComboBox(priorityCombo, fieldColor, textColor);
        
        // Data de vencimento
        JLabel dateLabel = new JLabel("Data de Vencimento:");
        dateLabel.setForeground(textColor);
        dueDateChooser = new JDateChooser();
        dueDateChooser.setDateFormatString("dd/MM/yyyy");
        if (initialDueDate != null) {
            dueDateChooser.setDate(initialDueDate);
        }
        styleDateChooser(dueDateChooser, fieldColor, textColor);
        
        // Botões
        JButton confirmButton = new JButton("Confirmar");
        styleButton(confirmButton, buttonColor, textColor);
        confirmButton.addActionListener(e -> confirm());
        
        JButton cancelButton = new JButton("Cancelar");
        styleButton(cancelButton, fieldColor, textColor);
        cancelButton.addActionListener(e -> dispose());
        
        // Adicionar componentes
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(priorityLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(priorityCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(dueDateChooser, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(confirmButton);
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
        
        // Enter para confirmar
        getRootPane().setDefaultButton(confirmButton);
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
    
    private void styleComboBox(JComboBox<?> combo, Color bgColor, Color fgColor) {
        combo.setBackground(bgColor);
        combo.setForeground(fgColor);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? bgColor.brighter() : bgColor);
                setForeground(fgColor);
                return this;
            }
        });
    }
    
    private void styleDateChooser(JDateChooser chooser, Color bgColor, Color fgColor) {
        chooser.setBackground(bgColor);
        chooser.setForeground(fgColor);
        Component[] components = chooser.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                styleField((JTextField) component, bgColor, fgColor);
            }
        }
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
    
    private void confirm() {
        taskName = nameField.getText().trim();
        
        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, insira um nome para a tarefa.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        priority = (Task.Priority) priorityCombo.getSelectedItem();
        dueDate = dueDateChooser.getDate();
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public Task.Priority getTaskPriority() {
        return priority;
    }
    
    public Date getTaskDueDate() {
        return dueDate;
    }
}
