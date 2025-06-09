package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.*;
import java.sql.*;

public class ChecklistDialog extends JDialog {
    // Cores do tema Discord
    private final Color BACKGROUND_COLOR = new Color(54, 57, 63);
    private final Color FIELD_COLOR = new Color(64, 68, 75);
    private final Color BUTTON_COLOR = new Color(88, 101, 242);
    private final Color BUTTON_HOVER_COLOR = new Color(71, 82, 196);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color ERROR_COLOR = new Color(240, 71, 71);
    private final Color SUCCESS_COLOR = new Color(87, 242, 135);
    private final Color MENU_BACKGROUND = new Color(47, 49, 54);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private int taskId;
    private String taskName;
    private DefaultListModel<ChecklistItem> itemsListModel;
    private JList<ChecklistItem> itemsList;
    private JTextField newItemField;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JPopupMenu contextMenu;

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

        // Configurar menu de contexto
        setupContextMenu();

        // Painel principal com tema escuro
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com barra de progresso
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        // Título da tarefa
        JLabel titleLabel = new JLabel(taskName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Barra de progresso estilizada
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(BUTTON_COLOR);
        progressBar.setBackground(FIELD_COLOR);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setFont(MAIN_FONT);
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 25)); // Aumentar altura

        progressLabel = new JLabel("0% Completo");
        progressLabel.setForeground(TEXT_COLOR);
        progressLabel.setFont(MAIN_FONT);
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Painel para a barra de progresso com padding
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setBackground(BACKGROUND_COLOR);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.EAST);

        topPanel.add(progressPanel, BorderLayout.CENTER);

        // Lista de itens estilizada
        itemsListModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsListModel);
        itemsList.setCellRenderer(new ChecklistItemRenderer());
        itemsList.setBackground(FIELD_COLOR);
        itemsList.setForeground(TEXT_COLOR);
        itemsList.setFont(MAIN_FONT);
        itemsList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Scroll pane estilizado
        JScrollPane scrollPane = new JScrollPane(itemsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        // Painel inferior para adicionar novos itens
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        newItemField = new JTextField();
        styleField(newItemField);

        JButton addButton = new JButton("Adicionar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_HOVER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2d.setColor(TEXT_COLOR);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        addButton.setFont(MAIN_FONT);
        addButton.setForeground(TEXT_COLOR);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addNewItem());

        bottomPanel.add(newItemField, BorderLayout.CENTER);
        bottomPanel.add(addButton, BorderLayout.EAST);

        // Adiciona os componentes ao painel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Adiciona listeners
        setupListeners();
    }

    private void setupContextMenu() {
        // Configurar UI Manager antes de criar o menu
        UIManager.put("PopupMenu.background", MENU_BACKGROUND);
        UIManager.put("PopupMenu.foreground", TEXT_COLOR);
        UIManager.put("MenuItem.background", MENU_BACKGROUND);
        UIManager.put("MenuItem.foreground", TEXT_COLOR);
        UIManager.put("MenuItem.selectionBackground", FIELD_COLOR);
        UIManager.put("MenuItem.selectionForeground", TEXT_COLOR);
        UIManager.put("MenuItem.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(32, 34, 37)));

        // Criar menu de contexto
        contextMenu = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(MENU_BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Criar itens do menu
        JMenuItem editItem = new JMenuItem("Editar");
        JMenuItem toggleItem = new JMenuItem("Marcar/Desmarcar");
        JMenuItem deleteItem = new JMenuItem("Excluir");

        // Estilizar cada item
        for (JMenuItem item : new JMenuItem[]{editItem, toggleItem, deleteItem}) {
            item.setFont(MAIN_FONT);
            item.setBackground(MENU_BACKGROUND);
            item.setForeground(TEXT_COLOR);
            item.setBorderPainted(false);
            item.setOpaque(true);
        }

        // Configurar ações
        editItem.addActionListener(e -> editSelectedItem());
        toggleItem.addActionListener(e -> toggleSelectedItem());
        deleteItem.addActionListener(e -> deleteSelectedItem());
        deleteItem.setForeground(ERROR_COLOR);

        // Adicionar itens ao menu
        contextMenu.add(editItem);
        contextMenu.add(toggleItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        // Estilizar separador
        contextMenu.getComponent(2).setBackground(MENU_BACKGROUND);
        contextMenu.getComponent(2).setForeground(FIELD_COLOR);

        // Garantir que o menu seja opaco
        contextMenu.setOpaque(true);
    }

  private void setupListeners() {
    itemsList.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            // --- INÍCIO DA MODIFICAÇÃO ---

            // Primeiro, verifica se foi um clique duplo para editar. Esta verificação deve vir antes.
            if (e.getClickCount() == 2) {
                editSelectedItem();

            // Depois, verifica se foi um clique único com o botão esquerdo para marcar/desmarcar.
            } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                // Pega o índice do item na posição onde o mouse clicou.
                int index = itemsList.locationToIndex(e.getPoint());
                if (index != -1) {
                    // Define o item clicado como o item selecionado na lista.
                    itemsList.setSelectedIndex(index);
                    // Chama o mesmo método que o menu de contexto usa para marcar/desmarcar.
                    toggleSelectedItem();
                }

            // --- FIM DA MODIFICAÇÃO ---

            // Por último, mantém a lógica para o clique com o botão direito (menu de contexto).
            } else if (SwingUtilities.isRightMouseButton(e)) {
                int row = itemsList.locationToIndex(e.getPoint());
                if (row >= 0) {
                    itemsList.setSelectedIndex(row);
                    contextMenu.show(itemsList, e.getX(), e.getY());
                }
            }
        }
    });

    newItemField.addActionListener(e -> addNewItem());
}

    private void styleField(JTextField field) {
        field.setBackground(FIELD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(32, 34, 37)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
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
            panel.setBackground(isSelected ? FIELD_COLOR.darker() : FIELD_COLOR);

            JCheckBox checkBox = new JCheckBox(item.getDescription());
            checkBox.setSelected(item.isCompleted());
            checkBox.setBackground(panel.getBackground());
            checkBox.setForeground(TEXT_COLOR);
            checkBox.setFont(MAIN_FONT);

            if (item.isCompleted()) {
                checkBox.setText("<html><strike>" + item.getDescription() + "</strike></html>");
            }

            panel.add(checkBox, BorderLayout.CENTER);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            return panel;
        }
    }
}
