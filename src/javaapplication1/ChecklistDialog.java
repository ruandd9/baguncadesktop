package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.*;
import java.sql.*;

public class ChecklistDialog extends JDialog {
    private final Color backgroundColor = new Color(54, 57, 63);
    private final Color fieldColor = new Color(64, 68, 75);
    private final Color buttonColor = new Color(88, 101, 242);
    private final Color textColor = Color.WHITE;
    private final Color errorColor = new Color(240, 71, 71);
    private final Color successColor = new Color(87, 242, 135);
    
    private int taskId;
    private String taskName;
    private DefaultListModel<ChecklistItem> itemsListModel;
    private JList<ChecklistItem> itemsList;
    private JTextField newItemField;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    
    public ChecklistDialog(Window owner, int taskId, String taskName) {
        super(owner, "Checklist da Tarefa: " + taskName, ModalityType.APPLICATION_MODAL);
        this.taskId = taskId;
        this.taskName = taskName;
        setupUI();
        loadChecklist();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior com barra de progresso
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(backgroundColor);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(buttonColor);
        progressBar.setBackground(fieldColor);
        
        progressLabel = new JLabel("0% Completo");
        progressLabel.setForeground(textColor);
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topPanel.add(progressBar, BorderLayout.CENTER);
        topPanel.add(progressLabel, BorderLayout.EAST);
        
        // Lista de itens
        itemsListModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsListModel);
        itemsList.setCellRenderer(new ChecklistItemRenderer());
        itemsList.setBackground(fieldColor);
        itemsList.setForeground(textColor);
        
        JScrollPane scrollPane = new JScrollPane(itemsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        
        // Painel inferior para adicionar novos itens
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(backgroundColor);
        
        newItemField = new JTextField();
        styleField(newItemField);
        
        JButton addButton = new JButton("Adicionar");
        styleButton(addButton, buttonColor);
        addButton.addActionListener(e -> addNewItem());
        
        bottomPanel.add(newItemField, BorderLayout.CENTER);
        bottomPanel.add(addButton, BorderLayout.EAST);
        
        // Adiciona os componentes ao painel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Adiciona listeners
        itemsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedItem();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            private void showPopupMenu(MouseEvent e) {
                if (itemsList.getSelectedIndex() != -1) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editItem = new JMenuItem("Editar");
                    JMenuItem deleteItem = new JMenuItem("Excluir");
                    JMenuItem toggleItem = new JMenuItem("Marcar/Desmarcar");
                    
                    styleMenuItem(editItem);
                    styleMenuItem(deleteItem);
                    styleMenuItem(toggleItem);
                    
                    editItem.addActionListener(e1 -> editSelectedItem());
                    deleteItem.addActionListener(e1 -> deleteSelectedItem());
                    toggleItem.addActionListener(e1 -> toggleSelectedItem());
                    
                    popupMenu.add(editItem);
                    popupMenu.add(deleteItem);
                    popupMenu.addSeparator();
                    popupMenu.add(toggleItem);
                    
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        newItemField.addActionListener(e -> addNewItem());
    }
    
    private void styleField(JTextField field) {
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(32, 34, 37)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
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
    
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(backgroundColor);
        item.setForeground(textColor);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(fieldColor);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(backgroundColor);
            }
        });
    }
    
    private void loadChecklist() {
        itemsListModel.clear();
        try {
            ArrayList<ChecklistItem> items = DatabaseManager.getChecklistItems(taskId);
            for (ChecklistItem item : items) {
                itemsListModel.addElement(item);
            }
            updateProgress();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar checklist: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewItem() {
        String description = newItemField.getText().trim();
        if (!description.isEmpty()) {
            try {
                ChecklistItem item = DatabaseManager.addChecklistItem(taskId, description, itemsListModel.getSize());
                itemsListModel.addElement(item);
                newItemField.setText("");
                updateProgress();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao adicionar item: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedItem() {
        int selectedIndex = itemsList.getSelectedIndex();
        if (selectedIndex != -1) {
            ChecklistItem item = itemsListModel.getElementAt(selectedIndex);
            String newDescription = JOptionPane.showInputDialog(this,
                "Editar item:",
                item.getDescription());
            
            if (newDescription != null && !newDescription.trim().isEmpty()) {
                try {
                    DatabaseManager.updateChecklistItem(item.getId(), newDescription.trim());
                    item.setDescription(newDescription.trim());
                    itemsListModel.setElementAt(item, selectedIndex);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao editar item: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSelectedItem() {
        int selectedIndex = itemsList.getSelectedIndex();
        if (selectedIndex != -1) {
            ChecklistItem item = itemsListModel.getElementAt(selectedIndex);
            int option = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este item?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                try {
                    DatabaseManager.deleteChecklistItem(item.getId());
                    itemsListModel.remove(selectedIndex);
                    updateProgress();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao excluir item: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void toggleSelectedItem() {
        int selectedIndex = itemsList.getSelectedIndex();
        if (selectedIndex != -1) {
            ChecklistItem item = itemsListModel.getElementAt(selectedIndex);
            try {
                DatabaseManager.toggleChecklistItem(item.getId());
                item.setCompleted(!item.isCompleted());
                itemsListModel.setElementAt(item, selectedIndex);
                updateProgress();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar item: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateProgress() {
        int total = itemsListModel.getSize();
        int completed = 0;
        
        for (int i = 0; i < total; i++) {
            if (itemsListModel.getElementAt(i).isCompleted()) {
                completed++;
            }
        }
        
        int percentage = total > 0 ? (completed * 100) / total : 0;
        progressBar.setValue(percentage);
        progressLabel.setText(percentage + "% Completo");
    }
    
    private class ChecklistItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            ChecklistItem item = (ChecklistItem) value;
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(isSelected ? fieldColor.darker() : fieldColor);
            
            JCheckBox checkBox = new JCheckBox(item.getDescription());
            checkBox.setSelected(item.isCompleted());
            checkBox.setBackground(panel.getBackground());
            checkBox.setForeground(textColor);
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            if (item.isCompleted()) {
                checkBox.setText("<html><strike>" + item.getDescription() + "</strike></html>");
            }
            
            panel.add(checkBox, BorderLayout.CENTER);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            return panel;
        }
    }
}
