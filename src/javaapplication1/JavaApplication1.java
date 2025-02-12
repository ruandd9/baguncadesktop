/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;

public class JavaApplication1 {
    public static void main(String[] args) {
        // Inicializar o banco de dados
        DatabaseManager.initializeDatabase();
        
        SwingUtilities.invokeLater(() -> {
            // Mostrar tela de login
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setVisible(true);
            
            LoginDialog loginDialog = new LoginDialog(frame);
            loginDialog.setVisible(true);
            
            frame.dispose();
            
            // Se o login for bem-sucedido, mostrar o quadro Kanban
            if (loginDialog.isLoginSuccessful()) {
                User loggedUser = loginDialog.getLoggedUser();
                KanbanBoard board = new KanbanBoard(loggedUser);
                board.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

class KanbanBoard extends JFrame {
    private JPanel todoPanel, doingPanel, donePanel;
    private ArrayList<JPanel> todoTasks, doingTasks, doneTasks;
    private User currentUser;
    private JTextArea activityLog;
    
    public KanbanBoard(User user) {
        this.currentUser = user;
        
        // Configuração básica da janela
        setTitle("Kanban To-Do List - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Tema escuro
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Cores do tema Discord
        Color backgroundColor = new Color(54, 57, 63);
        Color columnColor = new Color(47, 49, 54);
        Color buttonColor = new Color(88, 101, 242);
        Color textColor = new Color(255, 255, 255);
        
        // Configurar cores do frame
        this.getContentPane().setBackground(backgroundColor);
        
        // Inicialização das listas
        todoTasks = new ArrayList<>();
        doingTasks = new ArrayList<>();
        doneTasks = new ArrayList<>();
        
        // Configuração do layout principal
        setLayout(new BorderLayout());
        
        // Painel para as colunas
        JPanel columnsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        columnsPanel.setBackground(backgroundColor);
        
        // Criação das colunas com tema escuro
        todoPanel = createColumn("A Fazer", columnColor, textColor);
        doingPanel = createColumn("Fazendo", columnColor, textColor);
        donePanel = createColumn("Feito", columnColor, textColor);
        
        columnsPanel.add(todoPanel);
        columnsPanel.add(doingPanel);
        columnsPanel.add(donePanel);
        
        // Botão Adicionar Tarefa estilizado
        JButton addButton = new JButton("+ Nova Tarefa");
        addButton.setBackground(buttonColor);
        addButton.setForeground(textColor);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddTaskDialog());
        
        // Efeito hover no botão
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addButton.setBackground(buttonColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                addButton.setBackground(buttonColor);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(addButton);
        
        // Criar painel de atividades
        JPanel activityPanel = new JPanel(new BorderLayout(5, 5));
        activityPanel.setBackground(backgroundColor);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel activityTitle = new JLabel("Atividades Recentes");
        activityTitle.setForeground(textColor);
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        activityPanel.add(activityTitle, BorderLayout.NORTH);
        
        activityLog = new JTextArea();
        activityLog.setBackground(columnColor);
        activityLog.setForeground(textColor);
        activityLog.setEditable(false);
        activityLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activityLog.setLineWrap(true);
        activityLog.setWrapStyleWord(true);
        activityLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane activityScroll = new JScrollPane(activityLog);
        activityScroll.setPreferredSize(new Dimension(300, 0));
        activityScroll.setBorder(BorderFactory.createLineBorder(backgroundColor.darker()));
        activityScroll.getViewport().setBackground(columnColor);
        activityPanel.add(activityScroll, BorderLayout.CENTER);
        
        // Botão para atualizar atividades
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.setBackground(new Color(88, 101, 242));
        refreshButton.setForeground(textColor);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.addActionListener(e -> updateActivityLog());
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.setBackground(backgroundColor);
        buttonWrapper.add(refreshButton);
        activityPanel.add(buttonWrapper, BorderLayout.SOUTH);
        
        // Atualizar log de atividades
        updateActivityLog();
        
        // Timer para atualizar o log a cada 30 segundos
        Timer timer = new Timer(30000, e -> updateActivityLog());
        timer.start();
        
        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel central com botão e colunas
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(columnsPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(activityPanel, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    private JPanel createColumn(String title, Color bgColor, Color textColor) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(bgColor);
        column.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(bgColor.darker()),
            title, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), textColor));
        
        // Painel para as tarefas
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(bgColor);
        
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        column.add(scrollPane, BorderLayout.CENTER);
        return column;
    }
    
    private void showAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Adicionar Nova Tarefa", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Cores do tema Discord
        Color dialogBgColor = new Color(54, 57, 63);
        Color buttonColor = new Color(88, 101, 242);
        Color textColor = new Color(255, 255, 255);
        
        // Configurar cores do diálogo
        dialog.getContentPane().setBackground(dialogBgColor);
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(dialogBgColor);
        
        // Labels com texto branco
        JLabel taskLabel = new JLabel("Nome da Tarefa:");
        taskLabel.setForeground(textColor);
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel columnLabel = new JLabel("Coluna:");
        columnLabel.setForeground(textColor);
        columnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Campo de texto estilizado
        JTextField taskNameField = new JTextField();
        taskNameField.setBackground(new Color(64, 68, 75));
        taskNameField.setForeground(textColor);
        taskNameField.setCaretColor(textColor);
        taskNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(32, 34, 37)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // ComboBox estilizado
        String[] columns = {"A Fazer", "Fazendo", "Feito"};
        JComboBox<String> columnSelect = new JComboBox<>(columns);
        columnSelect.setBackground(new Color(64, 68, 75));
        columnSelect.setForeground(textColor);
        columnSelect.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        
        inputPanel.add(taskLabel);
        inputPanel.add(taskNameField);
        inputPanel.add(columnLabel);
        inputPanel.add(columnSelect);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(dialogBgColor);
        
        // Botões estilizados
        JButton confirmButton = new JButton("Confirmar");
        confirmButton.setBackground(buttonColor);
        confirmButton.setForeground(textColor);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(new Color(64, 68, 75));
        cancelButton.setForeground(textColor);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeitos hover nos botões
        confirmButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                confirmButton.setBackground(buttonColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                confirmButton.setBackground(buttonColor);
            }
        });
        
        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(71, 76, 84));
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(new Color(64, 68, 75));
            }
        });
        
        confirmButton.addActionListener(e -> {
            String taskName = taskNameField.getText().trim();
            if (!taskName.isEmpty()) {
                addTask(taskName, (String) columnSelect.getSelectedItem());
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Por favor, insira um nome para a tarefa.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(dialogBgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void addTask(String taskName, String column) {
        // Adicionar a tarefa ao banco de dados
        int taskId = DatabaseManager.addTask(taskName, column, currentUser.getId());
        if (taskId == -1) {
            JOptionPane.showMessageDialog(this,
                "Erro ao adicionar tarefa ao banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Registrar atividade
        DatabaseManager.logActivity(
            currentUser.getId(),
            taskId,
            "CREATE",
            "criou uma nova tarefa: " + taskName,
            null,
            column
        );
        
        // Criar um painel para a tarefa com tamanho fixo
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BorderLayout());
        taskPanel.setPreferredSize(new Dimension(180, 60));
        taskPanel.setMaximumSize(new Dimension(180, 60));
        taskPanel.setBackground(new Color(64, 68, 75));
        taskPanel.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37), 1));
        
        // Armazenar o ID da tarefa no painel
        taskPanel.putClientProperty("taskId", taskId);
        
        // Criar o label com o nome da tarefa
        JLabel taskLabel = new JLabel(taskName);
        taskLabel.setForeground(Color.WHITE);
        taskLabel.setHorizontalAlignment(SwingConstants.CENTER);
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taskPanel.add(taskLabel, BorderLayout.CENTER);
        
        // Efeito hover no card
        taskPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                taskPanel.setBackground(new Color(71, 76, 84));
            }
            public void mouseExited(MouseEvent e) {
                taskPanel.setBackground(new Color(64, 68, 75));
            }
        });
        
        // Criar menu de contexto
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(new Color(47, 49, 54));
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        
        // Opção de Editar
        JMenuItem editItem = new JMenuItem("Editar");
        styleMenuItem(editItem);
        editItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Novo nome da tarefa:", taskName);
            if (newName != null && !newName.trim().isEmpty()) {
                int id = (int) taskPanel.getClientProperty("taskId");
                if (DatabaseManager.updateTask(id, newName.trim())) {
                    taskLabel.setText(newName.trim());
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao atualizar tarefa no banco de dados.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Opção de Excluir
        JMenuItem deleteItem = new JMenuItem("Excluir");
        styleMenuItem(deleteItem);
        deleteItem.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir esta tarefa?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int id = (int) taskPanel.getClientProperty("taskId");
                if (DatabaseManager.deleteTask(id)) {
                    Container parent = taskPanel.getParent();
                    parent.remove(taskPanel);
                    parent.revalidate();
                    parent.repaint();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao excluir tarefa do banco de dados.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Submenu Mover Para
        JMenu moveToMenu = new JMenu("Mover para");
        styleMenuItem(moveToMenu);
        String[] columns = {"A Fazer", "Fazendo", "Feito"};
        for (String col : columns) {
            if (!col.equals(column)) {
                JMenuItem moveItem = new JMenuItem(col);
                styleMenuItem(moveItem);
                moveItem.addActionListener(e -> moveTask(taskPanel, col));
                moveToMenu.add(moveItem);
            }
        }
        
        // Adicionar itens ao menu
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.add(moveToMenu);
        
        // Adicionar listener para mostrar menu ao clicar com botão direito
        taskPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        JPanel targetPanel = todoPanel;
        ArrayList<JPanel> targetList = todoTasks;
        
        switch (column) {
            case "Fazendo":
                targetPanel = doingPanel;
                targetList = doingTasks;
                break;
            case "Feito":
                targetPanel = donePanel;
                targetList = doneTasks;
                break;
        }
        
        targetList.add(taskPanel);
        
        // Adicionar o card diretamente ao painel de tarefas
        JScrollPane scrollPane = (JScrollPane) targetPanel.getComponent(0);
        JPanel tasksPanel = (JPanel) scrollPane.getViewport().getView();
        
        tasksPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        tasksPanel.add(taskPanel);
        
        targetPanel.revalidate();
        targetPanel.repaint();
    }
    
    private void moveTask(JPanel taskPanel, String targetColumn) {
        int taskId = (int) taskPanel.getClientProperty("taskId");
        String taskName = ((JLabel) taskPanel.getComponent(0)).getText();
        String sourceColumn = getCurrentColumn(taskPanel);
        
        if (!DatabaseManager.moveTask(taskId, targetColumn)) {
            JOptionPane.showMessageDialog(this,
                "Erro ao mover tarefa no banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Registrar atividade
        DatabaseManager.logActivity(
            currentUser.getId(),
            taskId,
            "MOVE",
            "moveu a tarefa '" + taskName + "' para " + targetColumn,
            sourceColumn,
            targetColumn
        );
        
        // Remover da coluna atual
        Container sourcePanel = taskPanel.getParent();
        sourcePanel.remove(taskPanel);
        sourcePanel.revalidate();
        sourcePanel.repaint();
        
        // Adicionar à nova coluna
        JPanel targetPanel;
        ArrayList<JPanel> targetList;
        
        switch (targetColumn) {
            case "Fazendo":
                targetPanel = doingPanel;
                targetList = doingTasks;
                break;
            case "Feito":
                targetPanel = donePanel;
                targetList = doneTasks;
                break;
            default:
                targetPanel = todoPanel;
                targetList = todoTasks;
        }
        
        targetList.add(taskPanel);
        
        JScrollPane scrollPane = (JScrollPane) targetPanel.getComponent(0);
        JPanel tasksPanel = (JPanel) scrollPane.getViewport().getView();
        
        tasksPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        tasksPanel.add(taskPanel);
        
        targetPanel.revalidate();
        targetPanel.repaint();
    }
    
    private void updateActivityLog() {
        ArrayList<String> activities = DatabaseManager.getRecentActivities();
        StringBuilder sb = new StringBuilder();
        
        if (activities.isEmpty()) {
            sb.append("Nenhuma atividade registrada ainda.\n");
            sb.append("As atividades aparecerão aqui quando você:\n");
            sb.append("- Criar uma nova tarefa\n");
            sb.append("- Mover uma tarefa\n");
            sb.append("- Editar uma tarefa\n");
            sb.append("- Excluir uma tarefa");
        } else {
            for (String activity : activities) {
                sb.append(activity).append("\n\n");
            }
        }
        
        activityLog.setText(sb.toString());
        activityLog.setCaretPosition(0); // Rola para o topo
    }
    
    private String getCurrentColumn(JPanel taskPanel) {
        Container parent = taskPanel.getParent();
        while (parent != null && !(parent instanceof JScrollPane)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            parent = parent.getParent();
            if (parent == todoPanel) return "A Fazer";
            if (parent == doingPanel) return "Fazendo";
            if (parent == donePanel) return "Feito";
        }
        return "";
    }
    
    // Método auxiliar para estilizar itens do menu
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(new Color(47, 49, 54));
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Efeito hover
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(71, 76, 84));
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(47, 49, 54));
            }
        });
    }
}
