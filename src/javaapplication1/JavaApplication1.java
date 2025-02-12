/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

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
        TaskDialog dialog = new TaskDialog(
            this,
            "Nova Tarefa",
            "",
            Task.Priority.MEDIA,
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            addTask(
                dialog.getTaskName(),
                "A Fazer",
                dialog.getTaskPriority(),
                dialog.getTaskDueDate()
            );
        }
    }
    
    private void addTask(String taskName, String column, Task.Priority priority, Date dueDate) {
        // Adicionar a tarefa ao banco de dados
        int taskId = DatabaseManager.addTask(taskName, column, currentUser.getId(), priority, dueDate);
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
        JPanel taskPanel = createTaskPanel(taskId, taskName, priority, dueDate);
        
        // Adicionar à coluna apropriada
        if (column.equals("A Fazer")) {
            todoTasks.add(taskPanel);
            todoPanel.add(taskPanel);
        } else if (column.equals("Fazendo")) {
            doingTasks.add(taskPanel);
            doingPanel.add(taskPanel);
        } else {
            doneTasks.add(taskPanel);
            donePanel.add(taskPanel);
        }
        
        // Atualizar a interface
        revalidate();
        repaint();
    }
    
    private JPanel createTaskPanel(int taskId, String taskName, Task.Priority priority, Date dueDate) {
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BorderLayout(5, 5));
        taskPanel.setPreferredSize(new Dimension(200, 100));
        taskPanel.setBackground(new Color(47, 49, 54));
        taskPanel.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        taskPanel.putClientProperty("taskId", taskId);
        
        // Painel superior com nome e prioridade
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.setBackground(new Color(47, 49, 54));
        
        JLabel nameLabel = new JLabel(taskName);
        nameLabel.setForeground(Color.WHITE);
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        
        // Indicador de prioridade
        JPanel priorityIndicator = new JPanel();
        priorityIndicator.setPreferredSize(new Dimension(10, 20));
        priorityIndicator.setBackground(Color.decode(priority.getColor()));
        headerPanel.add(priorityIndicator, BorderLayout.WEST);
        
        taskPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Data de vencimento
        if (dueDate != null) {
            JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            datePanel.setBackground(new Color(47, 49, 54));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            JLabel dateLabel = new JLabel(sdf.format(dueDate));
            dateLabel.setForeground(Color.LIGHT_GRAY);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            // Ícone de alerta se estiver atrasado ou próximo
            if (new Date().after(dueDate)) {
                dateLabel.setForeground(Color.decode("#FF4444"));
                dateLabel.setText("ATRASADO: " + dateLabel.getText());
            } else {
                long diff = dueDate.getTime() - new Date().getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                if (days <= 3) {
                    dateLabel.setForeground(Color.decode("#FFB74D"));
                }
            }
            
            datePanel.add(dateLabel);
            taskPanel.add(datePanel, BorderLayout.CENTER);
        }
        
        // Menu de contexto
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(new Color(47, 49, 54));
        
        // Opções de prioridade
        JMenu priorityMenu = new JMenu("Prioridade");
        styleMenuItem(priorityMenu);
        
        for (Task.Priority p : Task.Priority.values()) {
            JMenuItem priorityItem = new JMenuItem(p.name());
            styleMenuItem(priorityItem);
            priorityItem.setBackground(Color.decode(p.getColor()));
            priorityItem.addActionListener(e -> {
                DatabaseManager.updateTask(taskId, taskName, getCurrentColumn(taskPanel), p, dueDate);
                refreshTasks();
            });
            priorityMenu.add(priorityItem);
        }
        
        // Opção de data
        JMenuItem dateItem = new JMenuItem("Alterar Data");
        styleMenuItem(dateItem);
        dateItem.addActionListener(e -> {
            JDateChooser chooser = new JDateChooser();
            chooser.setDate(dueDate);
            
            int result = JOptionPane.showConfirmDialog(
                this,
                chooser,
                "Selecione a Data",
                JOptionPane.OK_CANCEL_OPTION
            );
            
            if (result == JOptionPane.OK_OPTION) {
                DatabaseManager.updateTask(
                    taskId,
                    taskName,
                    getCurrentColumn(taskPanel),
                    priority,
                    chooser.getDate()
                );
                refreshTasks();
            }
        });
        
        // Adicionar itens ao menu
        popupMenu.add(priorityMenu);
        popupMenu.add(dateItem);
        popupMenu.addSeparator();
        
        // Opções de mover
        JMenu moveMenu = new JMenu("Mover para");
        styleMenuItem(moveMenu);
        
        JMenuItem moveToTodo = new JMenuItem("A Fazer");
        styleMenuItem(moveToTodo);
        moveToTodo.addActionListener(e -> moveTask(taskPanel, "A Fazer"));
        
        JMenuItem moveToDoing = new JMenuItem("Fazendo");
        styleMenuItem(moveToDoing);
        moveToDoing.addActionListener(e -> moveTask(taskPanel, "Fazendo"));
        
        JMenuItem moveToDone = new JMenuItem("Feito");
        styleMenuItem(moveToDone);
        moveToDone.addActionListener(e -> moveTask(taskPanel, "Feito"));
        
        moveMenu.add(moveToTodo);
        moveMenu.add(moveToDoing);
        moveMenu.add(moveToDone);
        
        popupMenu.add(moveMenu);
        popupMenu.addSeparator();
        
        // Opção de excluir
        JMenuItem deleteItem = new JMenuItem("Excluir");
        styleMenuItem(deleteItem);
        deleteItem.setForeground(new Color(255, 99, 71));
        deleteItem.addActionListener(e -> deleteTask(taskPanel));
        
        popupMenu.add(deleteItem);
        
        // Adicionar menu ao painel
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
        
        return taskPanel;
    }
    
    private void refreshTasks() {
        // Limpar todos os painéis
        todoPanel.removeAll();
        doingPanel.removeAll();
        donePanel.removeAll();
        
        todoTasks.clear();
        doingTasks.clear();
        doneTasks.clear();
        
        // Recarregar tarefas do banco
        ArrayList<Task> tasks = DatabaseManager.loadTasks();
        for (Task task : tasks) {
            JPanel taskPanel = createTaskPanel(
                task.getId(),
                task.getName(),
                task.getPriority(),
                task.getDueDate()
            );
            
            switch (task.getColumn()) {
                case "A Fazer":
                    todoTasks.add(taskPanel);
                    todoPanel.add(taskPanel);
                    break;
                case "Fazendo":
                    doingTasks.add(taskPanel);
                    doingPanel.add(taskPanel);
                    break;
                case "Feito":
                    doneTasks.add(taskPanel);
                    donePanel.add(taskPanel);
                    break;
            }
        }
        
        // Atualizar interface
        revalidate();
        repaint();
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
    
    private void moveTask(JPanel taskPanel, String targetColumn) {
        int taskId = (int) taskPanel.getClientProperty("taskId");
        String taskName = ((JLabel) ((JPanel) taskPanel.getComponent(0)).getComponent(1)).getText();
        String sourceColumn = getCurrentColumn(taskPanel);
        
        if (DatabaseManager.moveTask(taskId, targetColumn)) {
            // Registrar atividade
            DatabaseManager.logActivity(
                currentUser.getId(),
                taskId,
                "MOVE",
                "moveu a tarefa '" + taskName + "' para " + targetColumn,
                sourceColumn,
                targetColumn
            );
            
            // Atualizar interface
            refreshTasks();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erro ao mover tarefa no banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTask(JPanel taskPanel) {
        int taskId = (int) taskPanel.getClientProperty("taskId");
        String taskName = ((JLabel) ((JPanel) taskPanel.getComponent(0)).getComponent(1)).getText();
        String column = getCurrentColumn(taskPanel);
        
        int option = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir esta tarefa?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteTask(taskId)) {
                // Registrar atividade
                DatabaseManager.logActivity(
                    currentUser.getId(),
                    taskId,
                    "DELETE",
                    "excluiu a tarefa '" + taskName + "'",
                    column,
                    null
                );
                
                // Atualizar interface
                refreshTasks();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir tarefa do banco de dados.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
