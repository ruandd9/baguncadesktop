/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
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
    private DefaultListModel<ActivityItem> activityModel;
    private JList<ActivityItem> activityList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
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

        // Barra superior com menu de usuário
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(columnColor);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Menu de usuário
        JPanel userMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userMenu.setBackground(columnColor);

        // Botão do menu de usuário
        JButton userButton = new JButton(user.getName());
        userButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userButton.setForeground(textColor);
        userButton.setBackground(columnColor);
        userButton.setBorderPainted(false);
        userButton.setFocusPainted(false);
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                userButton.setBackground(new Color(66, 69, 73));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                userButton.setBackground(columnColor);
            }
        });

        // Popup menu para o botão de usuário
        JPopupMenu userPopup = new JPopupMenu();
        userPopup.setBackground(columnColor);
        
        // Item de informações do usuário
        JMenuItem userInfoItem = new JMenuItem("Conectado como " + user.getName());
        userInfoItem.setEnabled(false);
        userInfoItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfoItem.setBackground(columnColor);
        userInfoItem.setForeground(new Color(185, 187, 190));
        userPopup.add(userInfoItem);
        
        userPopup.addSeparator();
        
        // Item de logout
        JMenuItem logoutItem = new JMenuItem("Sair da conta");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.setBackground(columnColor);
        logoutItem.setForeground(RED);
        logoutItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja sair da conta?",
                "Confirmar Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame();
                    frame.setUndecorated(true);
                    frame.setVisible(true);
                    
                    LoginDialog loginDialog = new LoginDialog(frame);
                    loginDialog.setVisible(true);
                    
                    frame.dispose();
                    
                    if (loginDialog.isLoginSuccessful()) {
                        User loggedUser = loginDialog.getLoggedUser();
                        KanbanBoard board = new KanbanBoard(loggedUser);
                        board.setVisible(true);
                    } else {
                        System.exit(0);
                    }
                });
            }
        });
        userPopup.add(logoutItem);

        userButton.addActionListener(e -> {
            userPopup.show(userButton, 0, userButton.getHeight());
        });

        userMenu.add(userButton);
        topBar.add(userMenu, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
        
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
        JButton addButton = createTaskButton("TODO");
        addButton.addActionListener(e -> showAddTaskDialog());
        
        // Botão Gerenciar Equipes
        JButton teamsButton = new JButton("Equipes");
        teamsButton.setBackground(buttonColor);
        teamsButton.setForeground(textColor);
        teamsButton.setFocusPainted(false);
        teamsButton.setBorderPainted(false);
        teamsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        teamsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        teamsButton.addActionListener(e -> showTeamsDialog());
        
        // Botão de Notificações
        JButton notificationsButton = new JButton("Notificações");
        notificationsButton.setBackground(buttonColor);
        notificationsButton.setForeground(textColor);
        notificationsButton.setFocusPainted(false);
        notificationsButton.setBorderPainted(false);
        notificationsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notificationsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationsButton.addActionListener(e -> showNotificationsDialog());
        
        // Timer para verificar notificações não lidas
        Timer notificationTimer = new Timer(5000, e -> {
            int unreadCount = DatabaseManager.getUnreadNotificationsCount(currentUser.getId());
            if (unreadCount > 0) {
                notificationsButton.setText("Notificações (" + unreadCount + ")");
                notificationsButton.setBackground(buttonColor.brighter());
            } else {
                notificationsButton.setText("Notificações");
                notificationsButton.setBackground(buttonColor);
            }
        });
        notificationTimer.start();
        
        // Efeito hover nos botões
        MouseAdapter buttonHover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton)e.getSource()).setBackground(buttonColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton)e.getSource();
                if (button == notificationsButton && button.getText().contains("(")) {
                    button.setBackground(buttonColor.brighter());
                } else {
                    button.setBackground(buttonColor);
                }
            }
        };
        
        addButton.addMouseListener(buttonHover);
        teamsButton.addMouseListener(buttonHover);
        notificationsButton.addMouseListener(buttonHover);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(addButton);
        buttonPanel.add(teamsButton);
        buttonPanel.add(notificationsButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(columnsPanel, BorderLayout.CENTER);
        
        // Criar painel de atividades
        JPanel activityPanel = new JPanel(new BorderLayout(5, 5));
        activityPanel.setBackground(backgroundColor);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel activityTitle = new JLabel("Atividades Recentes");
        activityTitle.setForeground(textColor);
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        activityPanel.add(activityTitle, BorderLayout.NORTH);
        
        // Inicializar modelo e lista de atividades
        activityModel = new DefaultListModel<>();
        activityList = new JList<>(activityModel);
        activityList.setCellRenderer(new ActivityListCellRenderer());
        activityList.setBackground(columnColor);
        activityList.setForeground(textColor);
        
        JScrollPane activityScroll = new JScrollPane(activityList);
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
        
        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(activityPanel, BorderLayout.EAST);
        
        add(mainPanel);

        // Atualizar tarefas e atividades ao iniciar
        SwingUtilities.invokeLater(() -> {
            refreshTasks();
            updateActivityLog();
        });
    }
    
    private JButton createTaskButton(String column) {
        JButton button = new JButton("+ Nova Tarefa");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(new Color(54, 57, 63));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JPanel createColumn(String title, Color bgColor, Color textColor) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(bgColor);
        column.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título da coluna
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        column.add(titleLabel, BorderLayout.NORTH);
        
        // Painel para as tarefas com BoxLayout vertical
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(bgColor);
        
        // Adicionar um painel "empurrador" que ocupa o espaço extra
        tasksPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        column.add(scrollPane, BorderLayout.CENTER);
        return column;
    }
    
    private void refreshTasks() {
        // Obter os painéis internos dos JScrollPane
        JPanel todoTasksPanel = (JPanel)((JScrollPane)todoPanel.getComponent(1)).getViewport().getView();
        JPanel doingTasksPanel = (JPanel)((JScrollPane)doingPanel.getComponent(1)).getViewport().getView();
        JPanel doneTasksPanel = (JPanel)((JScrollPane)donePanel.getComponent(1)).getViewport().getView();
        
        // Limpar todos os painéis
        todoTasksPanel.removeAll();
        doingTasksPanel.removeAll();
        doneTasksPanel.removeAll();
        
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
                case "TODO":
                    todoTasks.add(taskPanel);
                    todoTasksPanel.add(taskPanel);
                    break;
                case "DOING":
                    doingTasks.add(taskPanel);
                    doingTasksPanel.add(taskPanel);
                    break;
                case "DONE":
                    doneTasks.add(taskPanel);
                    doneTasksPanel.add(taskPanel);
                    break;
            }
        }
        
        // Atualizar interface
        todoTasksPanel.revalidate();
        todoTasksPanel.repaint();
        doingTasksPanel.revalidate();
        doingTasksPanel.repaint();
        doneTasksPanel.revalidate();
        doneTasksPanel.repaint();
        
        // Atualizar log de atividades
        updateActivityLog();
    }
    
    private JPanel createTaskPanel(int taskId, String taskName, Task.Priority priority, Date dueDate) {
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BorderLayout(5, 5));
        taskPanel.setPreferredSize(new Dimension(180, 80));
        taskPanel.setBackground(new Color(47, 49, 54));
        taskPanel.setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        taskPanel.putClientProperty("taskId", taskId);
        taskPanel.putClientProperty("taskName", taskName);
        taskPanel.putClientProperty("taskPriority", priority);
        taskPanel.putClientProperty("taskDueDate", dueDate);
        
        // Painel superior com nome e prioridade
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.setBackground(new Color(47, 49, 54));
        
        // Nome da tarefa
        JLabel nameLabel = new JLabel(taskName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        
        // Indicador de prioridade
        JPanel priorityIndicator = new JPanel();
        priorityIndicator.setName("priorityIndicator");
        priorityIndicator.setPreferredSize(new Dimension(10, 20));
        priorityIndicator.setBackground(getPriorityColor(priority));
        headerPanel.add(priorityIndicator, BorderLayout.WEST);
        
        // Painel inferior com data e ícones
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.setBackground(new Color(47, 49, 54));
        
        // Data de vencimento
        if (dueDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            JLabel dateLabel = new JLabel(sdf.format(dueDate));
            dateLabel.setForeground(Color.LIGHT_GRAY);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            bottomPanel.add(dateLabel, BorderLayout.WEST);
        }
        
        // Painel de ícones
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        iconsPanel.setBackground(new Color(47, 49, 54));
        
        // Ícone e barra de progresso da checklist
        JPanel checklistPanel = new JPanel(new BorderLayout(2, 0));
        checklistPanel.setBackground(new Color(47, 49, 54));  
        
        JButton checklistButton = createChecklistButton(taskId);
        checklistPanel.add(checklistButton, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(40, 8));
        progressBar.setBackground(new Color(64, 68, 75));
        progressBar.setForeground(new Color(88, 101, 242));
        progressBar.setBorderPainted(false);
        
        // Atualiza o progresso inicial
        try {
            double progress = DatabaseManager.getChecklistProgress(taskId);
            progressBar.setValue((int) progress);
            progressBar.setToolTipText(String.format("%.0f%% completo", progress));
        } catch ( SQLException e) {
            System.err.println("Erro ao carregar progresso da checklist: " + e.getMessage());
        }
        
        checklistPanel.add(progressBar, BorderLayout.EAST);
        iconsPanel.add(checklistPanel);
        
        bottomPanel.add(iconsPanel, BorderLayout.EAST);
        
        // Adiciona os painéis ao painel principal
        taskPanel.add(headerPanel, BorderLayout.NORTH);
        taskPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Mouse listener para arrastar e soltar
        taskPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Clique direito
                    showTaskContextMenu(e, taskPanel);
                }
            }
        });
        
        return taskPanel;
    }
    
    private JButton createChecklistButton(int taskId) {
        JButton button = new JButton("Lista");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(88, 101, 242)); // Azul Discord
        button.setBackground(null);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showChecklistDialog(taskId));
        return button;
    }
    
    private Color getPriorityColor(Task.Priority priority) {
        switch (priority) {
            case ALTA:
                return Color.RED;
            case MEDIA:
                return Color.YELLOW;
            case BAIXA:
                return Color.GREEN;
        }
        return Color.WHITE;
    }
    
    private void showChecklistDialog(int taskId) {
        // Obter o nome da tarefa do banco de dados
        String taskName = DatabaseManager.getTaskName(taskId);
        if (taskName != null) {
            ChecklistDialog dialog = new ChecklistDialog(this, taskId, taskName);
            dialog.setVisible(true);
            refreshTasks(); // Atualiza o progresso após fechar o diálogo
        } else {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar os dados da tarefa",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTaskContextMenu(MouseEvent e, JPanel taskPanel) {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Editar");
        JMenuItem deleteItem = new JMenuItem("Excluir");
        JMenuItem checklistItem = new JMenuItem("Checklist");
        
        // Submenu Mover Para
        JMenu moveToMenu = new JMenu("Mover para");
        String currentColumn = getCurrentColumn(taskPanel);
        
        if (!"TODO".equals(currentColumn)) {
            JMenuItem moveToTodoItem = new JMenuItem("A Fazer");
            moveToTodoItem.addActionListener(event -> moveTask(taskPanel, "TODO"));
            moveToMenu.add(moveToTodoItem);
        }
        
        if (!"DOING".equals(currentColumn)) {
            JMenuItem moveToDoingItem = new JMenuItem("Fazendo");
            moveToDoingItem.addActionListener(event -> moveTask(taskPanel, "DOING"));
            moveToMenu.add(moveToDoingItem);
        }
        
        if (!"DONE".equals(currentColumn)) {
            JMenuItem moveToDoneItem = new JMenuItem("Feito");
            moveToDoneItem.addActionListener(event -> moveTask(taskPanel, "DONE"));
            moveToMenu.add(moveToDoneItem);
        }
        
        // Submenu Prioridade
        JMenu priorityMenu = new JMenu("Prioridade");
        Task.Priority currentPriority = (Task.Priority) taskPanel.getClientProperty("taskPriority");
        
        for (Task.Priority priority : Task.Priority.values()) {
            if (priority != currentPriority) {
                JMenuItem priorityItem = new JMenuItem(priority.toString());
                priorityItem.addActionListener(event -> {
                    int taskId = (int) taskPanel.getClientProperty("taskId");
                    updateTaskPriority(taskId, priority, taskPanel);
                });
                priorityMenu.add(priorityItem);
            }
        }
        
        editItem.addActionListener(event -> {
            int taskId = (int) taskPanel.getClientProperty("taskId");
            String taskName = (String) taskPanel.getClientProperty("taskName");
            Task.Priority priority = (Task.Priority) taskPanel.getClientProperty("taskPriority");
            Date dueDate = (Date) taskPanel.getClientProperty("taskDueDate");
            showTaskDialog(taskId, taskName, priority, dueDate);
        });
        
        deleteItem.addActionListener(event -> {
            int taskId = (int) taskPanel.getClientProperty("taskId");
            String taskName = (String) taskPanel.getClientProperty("taskName");
            deleteTask(taskId, taskName, taskPanel);
        });
        
        checklistItem.addActionListener(event -> {
            int taskId = (int) taskPanel.getClientProperty("taskId");
            String taskName = (String) taskPanel.getClientProperty("taskName");
            ChecklistDialog dialog = new ChecklistDialog(this, taskId, taskName);
            dialog.setVisible(true);
        });
        
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.add(moveToMenu);
        popupMenu.add(priorityMenu);
        popupMenu.addSeparator();
        popupMenu.add(checklistItem);
        
        // Estilizar os itens do menu
        styleMenuItem(editItem);
        styleMenuItem(deleteItem);
        styleMenuItem(checklistItem);
        styleMenuItem(moveToMenu);
        styleMenuItem(priorityMenu);
        for (Component item : moveToMenu.getMenuComponents()) {
            if (item instanceof JMenuItem) {
                styleMenuItem((JMenuItem) item);
            }
        }
        for (Component item : priorityMenu.getMenuComponents()) {
            if (item instanceof JMenuItem) {
                styleMenuItem((JMenuItem) item);
            }
        }
        
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    // Método auxiliar para estilizar itens do menu
    private void styleMenuItem(JMenuItem item) {
        // Cores do Discord
        Color menuBackground = new Color(47, 49, 54);     // Fundo escuro
        Color menuHoverColor = new Color(71, 76, 84);    // Cor quando passa o mouse
        Color menuTextColor = new Color(220, 221, 222);  // Texto claro
        
        item.setBackground(menuBackground);
        item.setForeground(menuTextColor);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));  // Mais espaçamento
        item.setOpaque(true);  // Importante para ver a cor de fundo
        
        // Efeito hover mais suave
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(menuHoverColor);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(menuBackground);
            }
        });
        
        // Se for um JMenu (submenus), personalizar a popup
        if (item instanceof JMenu) {
            JMenu menu = (JMenu) item;
            menu.getPopupMenu().setBackground(menuBackground);
            menu.getPopupMenu().setBorder(BorderFactory.createLineBorder(new Color(32, 34, 37)));
        }
    }
    
    private void updateActivityLog() {
        ArrayList<Map<String, Object>> activities = DatabaseManager.getRecentActivities();
        activityModel.clear();
        
        if (activities.isEmpty()) {
            Map<String, Object> defaultActivity = new HashMap<>();
            defaultActivity.put("action", "INFO");
            defaultActivity.put("description", "Nenhuma atividade registrada ainda.\n" +
                "As atividades aparecerão aqui quando você:\n" +
                "- Criar uma nova tarefa\n" +
                "- Mover uma tarefa\n" +
                "- Editar uma tarefa\n" +
                "- Excluir uma tarefa");
            defaultActivity.put("source_column", "");
            defaultActivity.put("target_column", "");
            defaultActivity.put("created_at", new Date());
            activities.add(defaultActivity);
        }
        
        for (Map<String, Object> activity : activities) {
            activityModel.addElement(new ActivityItem(
                (String) activity.get("action"),
                (String) activity.get("description"),
                (String) activity.get("source_column"),
                (String) activity.get("target_column"),
                (Date) activity.get("created_at")
            ));
        }
        
        activityList.ensureIndexIsVisible(0); // Rola para o topo
    }
    
    private String getCurrentColumn(JPanel taskPanel) {
        // Obter os painéis internos dos JScrollPane
        JPanel todoTasksPanel = (JPanel)((JScrollPane)todoPanel.getComponent(1)).getViewport().getView();
        JPanel doingTasksPanel = (JPanel)((JScrollPane)doingPanel.getComponent(1)).getViewport().getView();
        JPanel doneTasksPanel = (JPanel)((JScrollPane)donePanel.getComponent(1)).getViewport().getView();
        
        // Verificar em qual painel a tarefa está
        if (todoTasksPanel.isAncestorOf(taskPanel)) {
            return "TODO";
        } else if (doingTasksPanel.isAncestorOf(taskPanel)) {
            return "DOING";
        } else if (doneTasksPanel.isAncestorOf(taskPanel)) {
            return "DONE";
        }
        
        return "TODO"; // Coluna padrão se não encontrar
    }
    
    private void moveTask(JPanel taskPanel, String targetColumn) {
        try {
            int taskId = (int) taskPanel.getClientProperty("taskId");
            String taskName = (String) taskPanel.getClientProperty("taskName");
            
            if (taskName == null || taskName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao obter nome da tarefa.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String sourceColumn = getCurrentColumn(taskPanel);
            
            // Não mover se já estiver na coluna de destino
            if (sourceColumn.equals(targetColumn)) {
                return;
            }
            
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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao mover tarefa: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTask(int taskId, String taskName, JPanel taskPanel) {
        try {
            String column = getCurrentColumn(taskPanel);
            
            // Confirmar exclusão
            int option = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a tarefa '" + taskName + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Registrar atividade antes de excluir
            DatabaseManager.logActivity(
                currentUser.getId(),
                taskId,
                "DELETE",
                "excluiu a tarefa '" + taskName + "'",
                column,
                null
            );
            
            // Excluir a tarefa
            if (DatabaseManager.deleteTask(taskId)) {
                // Atualizar interface
                refreshTasks();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir tarefa no banco de dados.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao excluir tarefa: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTeamsDialog() {
        // Mostrar diálogo de lista de equipes
        TeamsListDialog dialog = new TeamsListDialog(this, currentUser);
        dialog.setVisible(true);
    }
    
    private void showNotificationsDialog() {
        NotificationsDialog dialog = new NotificationsDialog(this, currentUser);
        dialog.setVisible(true);
    }
    
    private void showTaskDialog(int taskId, String taskName, Task.Priority priority, Date dueDate) {
        TaskDialog dialog = new TaskDialog(
            this,
            "Editar Tarefa",
            taskName,
            priority,
            dueDate
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            String newName = dialog.getTaskName();
            Task.Priority newPriority = dialog.getTaskPriority();
            Date newDueDate = dialog.getTaskDueDate();
            
            // Find the task panel by taskId
            JPanel taskPanel = findTaskPanelById(taskId);
            if (taskPanel == null) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao encontrar o painel da tarefa.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String currentColumn = getCurrentColumn(taskPanel);
            
            if (DatabaseManager.updateTask(taskId, newName, currentColumn, newPriority, newDueDate)) {
                // Registrar atividade
                DatabaseManager.logActivity(
                    currentUser.getId(),
                    taskId,
                    "EDIT",
                    "editou a tarefa '" + taskName + "'",
                    currentColumn,
                    currentColumn
                );
                
                // Atualizar interface
                refreshTasks();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar tarefa no banco de dados.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel findTaskPanelById(int taskId) {
        // Search in all columns
        for (JPanel taskPanel : todoTasks) {
            if ((int)taskPanel.getClientProperty("taskId") == taskId) {
                return taskPanel;
            }
        }
        for (JPanel taskPanel : doingTasks) {
            if ((int)taskPanel.getClientProperty("taskId") == taskId) {
                return taskPanel;
            }
        }
        for (JPanel taskPanel : doneTasks) {
            if ((int)taskPanel.getClientProperty("taskId") == taskId) {
                return taskPanel;
            }
        }
        return null;
    }
    
    private void updateTaskPriority(int taskId, Task.Priority newPriority, JPanel taskPanel) {
        try {
            if (DatabaseManager.updateTaskPriority(taskId, newPriority)) {
                // Atualizar o painel da tarefa
                taskPanel.putClientProperty("taskPriority", newPriority);
                
                // Procurar o painel de cabeçalho
                for (Component comp : taskPanel.getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel headerPanel = (JPanel) comp;
                        // Procurar o indicador de prioridade dentro do painel de cabeçalho
                        for (Component headerComp : headerPanel.getComponents()) {
                            if (headerComp instanceof JPanel && headerComp.getName() != null && 
                                headerComp.getName().equals("priorityIndicator")) {
                                ((JPanel) headerComp).setBackground(getPriorityColor(newPriority));
                                break;
                            }
                        }
                    }
                }
                
                // Registrar atividade
                String currentColumn = getCurrentColumn(taskPanel);
                DatabaseManager.logActivity(
                    currentUser.getId(),
                    taskId,
                    "PRIORITY",
                    "alterou a prioridade para " + newPriority,
                    currentColumn,
                    currentColumn
                );
                
                // Forçar repaint do painel
                taskPanel.revalidate();
                taskPanel.repaint();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atualizar prioridade: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
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
            addTask(dialog.getTaskName(), dialog.getColumn(), dialog.getTaskPriority(), dialog.getTaskDueDate());
        }
    }
    
    private void addTask(String taskName, String status, Task.Priority priority, Date dueDate) {
        int taskId = DatabaseManager.addTask(taskName, status, currentUser.getId(), priority, dueDate);
        if (taskId == -1) {
            JOptionPane.showMessageDialog(this,
                "Erro ao adicionar tarefa ao banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        refreshTasks();
    }
    
    // Classe interna para representar um item de atividade
    private class ActivityItem {
        private String action;
        private String description;
        private String sourceColumn;
        private String targetColumn;
        private Date createdAt;
        
        public ActivityItem(String action, String description, String sourceColumn, String targetColumn, Date createdAt) {
            this.action = action;
            this.description = description;
            this.sourceColumn = sourceColumn;
            this.targetColumn = targetColumn;
            this.createdAt = createdAt;
        }
        
        public String getAction() { return action; }
        public String getDescription() { return description; }
        public String getSourceColumn() { return sourceColumn; }
        public String getTargetColumn() { return targetColumn; }
        public Date getCreatedAt() { return createdAt; }
    }
    
    // Renderer personalizado para a lista de atividades
    private class ActivityListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        ) {
            ActivityItem activity = (ActivityItem) value;
            
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBackground(isSelected ? new Color(64, 68, 75).darker() : new Color(64, 68, 75));
            panel.setBorder(new EmptyBorder(8, 8, 8, 8));
            
            // Painel para o conteúdo da atividade
            JPanel contentPanel = new JPanel(new GridLayout(2, 1, 2, 2));
            contentPanel.setBackground(panel.getBackground());
            
            // Descrição da atividade
            JLabel descriptionLabel = new JLabel(activity.getDescription());
            descriptionLabel.setForeground(Color.WHITE);
            descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Data
            JLabel dateLabel = new JLabel(dateFormat.format(activity.getCreatedAt()));
            dateLabel.setForeground(new Color(185, 187, 190));
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            contentPanel.add(descriptionLabel);
            contentPanel.add(dateLabel);
            
            panel.add(contentPanel, BorderLayout.CENTER);
            
            // Indicador colorido baseado na ação
            Color actionColor;
            switch (activity.getAction()) {
                case "CREATE":
                    actionColor = new Color(87, 242, 135); // Verde
                    break;
                case "MOVE":
                    actionColor = new Color(88, 101, 242); // Azul Discord
                    break;
                case "DELETE":
                    actionColor = new Color(240, 71, 71); // Vermelho
                    break;
                default:
                    actionColor = new Color(255, 184, 108); // Laranja
            }
            
            JPanel actionIndicator = new JPanel();
            actionIndicator.setPreferredSize(new Dimension(4, 0));
            actionIndicator.setBackground(actionColor);
            panel.add(actionIndicator, BorderLayout.WEST);
            
            return panel;
        }
    }
}
