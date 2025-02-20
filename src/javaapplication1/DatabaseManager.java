package javaapplication1;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kanban_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";
    
    // Testar conexão com o banco
    public static boolean testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS)) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver MySQL não encontrado.\nPor favor, adicione o mysql-connector-java.jar ao projeto.",
                "Erro de Driver",
                JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            String message = "Erro ao conectar ao MySQL.\n\n";
            message += "Verifique se:\n";
            message += "1. O XAMPP está instalado e o MySQL está rodando\n";
            message += "2. A porta 3306 está correta\n";
            message += "3. O usuário e senha estão corretos\n\n";
            message += "Erro: " + e.getMessage();
            
            JOptionPane.showMessageDialog(null,
                message,
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Inicializar o banco de dados usando o arquivo SQL
    public static void initializeDatabase() {
        if (!testConnection()) {
            return;
        }
        
        try {
            // Obter o caminho do projeto
            String projectPath = System.getProperty("user.dir");
            String sqlFilePath = projectPath + "/src/database/kanban_db.sql";
            
            // Ler o arquivo SQL
            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        new java.io.FileInputStream(sqlFilePath),
                        StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlScript.append(line).append("\n");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao ler arquivo SQL: " + e.getMessage() + "\n" +
                    "Caminho tentado: " + sqlFilePath,
                    "Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Executar o script SQL
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS)) {
                // Dividir o script em comandos individuais
                String[] commands = sqlScript.toString().split(";");
                
                for (String command : commands) {
                    if (!command.trim().isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(command);
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(null,
                    "Banco de dados inicializado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao executar script SQL: " + e.getMessage(),
                "Erro de Inicialização",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Obter conexão com o banco
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    // Adicionar uma nova tarefa
    public static int addTask(String taskName, String column, int userId, Task.Priority priority, Date dueDate) {
        String sql = "INSERT INTO tasks (name, column_name, created_by, priority, due_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, taskName);
            pstmt.setString(2, column);
            pstmt.setInt(3, userId);
            pstmt.setString(4, priority.name());
            pstmt.setDate(5, dueDate != null ? new java.sql.Date(dueDate.getTime()) : null);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao adicionar tarefa: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
    
    // Atualizar uma tarefa
    public static boolean updateTask(int taskId, String taskName, String column, Task.Priority priority, Date dueDate) {
        String sql = "UPDATE tasks SET name = ?, column_name = ?, priority = ?, due_date = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, taskName);
            pstmt.setString(2, column);
            pstmt.setString(3, priority.name());
            pstmt.setDate(4, dueDate != null ? new java.sql.Date(dueDate.getTime()) : null);
            pstmt.setInt(5, taskId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao atualizar tarefa: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Atualizar prioridade de uma tarefa
    public static boolean updateTaskPriority(int taskId, Task.Priority priority) throws SQLException {
        String sql = "UPDATE tasks SET priority = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, priority.toString());
            pstmt.setInt(2, taskId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Mover uma tarefa para outra coluna
    public static boolean moveTask(int taskId, String newColumn) {
        String sql = "UPDATE tasks SET column_name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newColumn);
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao mover tarefa: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Excluir uma tarefa
    public static boolean deleteTask(int taskId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Primeiro atualizar as atividades para remover a referência à tarefa
                String updateActivities = "UPDATE activities SET task_id = NULL WHERE task_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateActivities)) {
                    pstmt.setInt(1, taskId);
                    pstmt.executeUpdate();
                }
                
                // Depois excluir a tarefa
                String deleteTasks = "DELETE FROM tasks WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteTasks)) {
                    pstmt.setInt(1, taskId);
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao excluir tarefa: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Carregar tarefas
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, name, column_name, created_by, priority, due_date FROM tasks ORDER BY due_date ASC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task.Priority priority = Task.Priority.valueOf(rs.getString("priority"));
                Date dueDate = rs.getDate("due_date");
                
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("column_name"),
                    priority,
                    dueDate,
                    rs.getInt("created_by")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao carregar tarefas: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
        return tasks;
    }
    
    // Autenticar usuário
    public static User authenticateUser(String email, String password) {
        String sql = "SELECT id, name, email FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao autenticar usuário: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    // Registrar novo usuário
    public static boolean registerUser(String name, String email, String password) {
        // Verificar se o email já existe
        String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                    "Este email já está cadastrado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Inserir novo usuário
            String insertSql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao registrar usuário: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    // Registrar uma atividade
    public static void logActivity(int userId, int taskId, String action, String description, String sourceColumn, String targetColumn) {
        String sql = "INSERT INTO activities (user_id, task_id, action, description, source_column, target_column, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, taskId);
            pstmt.setString(3, action);
            pstmt.setString(4, description);
            pstmt.setString(5, sourceColumn);
            pstmt.setString(6, targetColumn);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Apenas log o erro, não mostrar para o usuário pois é uma operação secundária
            e.printStackTrace();
        }
    }
    
    // Obter atividades recentes
    public static ArrayList<Map<String, Object>> getRecentActivities() {
        ArrayList<Map<String, Object>> activities = new ArrayList<>();
        String sql = "SELECT a.*, u.name as user_name " +
                    "FROM activities a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "ORDER BY a.created_at DESC " +
                    "LIMIT 50";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("action", rs.getString("action"));
                activity.put("description", rs.getString("description"));
                activity.put("source_column", rs.getString("source_column"));
                activity.put("target_column", rs.getString("target_column"));
                activity.put("created_at", rs.getTimestamp("created_at"));
                activity.put("user_name", rs.getString("user_name"));
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }
    
    // Métodos para gerenciamento de equipes
    
    public static int createTeam(String name, String description, int leaderId) {
        String sql = "INSERT INTO teams (name, description, leader_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, leaderId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static boolean addTeamMember(int teamId, int userId) {
        String sql = "INSERT INTO team_members (team_id, user_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeTeamMember(int teamId, int userId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Team getTeam(int teamId) {
        String sql = "SELECT t.*, u.id as leader_user_id, u.name as leader_name, u.email as leader_email " +
                    "FROM teams t " +
                    "JOIN users u ON t.leader_id = u.id " +
                    "WHERE t.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User leader = new User(
                    rs.getInt("leader_user_id"),
                    rs.getString("leader_name"),
                    rs.getString("leader_email")
                );
                
                Team team = new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    leader
                );
                
                team.setDates(rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"));
                loadTeamMembers(team);
                return team;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void loadTeamMembers(Team team) {
        String sql = "SELECT u.* FROM users u " +
                    "JOIN team_members tm ON u.id = tm.user_id " +
                    "WHERE tm.team_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, team.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User member = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
                team.addMember(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<Team> getUserTeams(int userId) {
        ArrayList<Team> teams = new ArrayList<>();
        String sql = "SELECT DISTINCT t.* FROM teams t " +
                    "LEFT JOIN team_members tm ON t.id = tm.team_id " +
                    "WHERE t.leader_id = ? OR tm.user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Team team = getTeam(rs.getInt("id"));
                if (team != null) {
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    
    public static boolean updateTeam(int teamId, String name, String description, int leaderId) {
        String sql = "UPDATE teams SET name = ?, description = ?, leader_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, leaderId);
            pstmt.setInt(4, teamId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteTeam(int teamId) {
        String sql = "DELETE FROM teams WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Métodos para notificações de equipe
    
    public static void createTeamNotification(int userId, int teamId, String message, String type) {
        String sql = "INSERT INTO team_notifications (user_id, team_id, message, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, teamId);
            pstmt.setString(3, message);
            pstmt.setString(4, type);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<TeamNotification> getUserNotifications(int userId) {
        ArrayList<TeamNotification> notifications = new ArrayList<>();
        String sql = "SELECT n.*, t.name as team_name FROM team_notifications n " +
                    "JOIN teams t ON n.team_id = t.id " +
                    "WHERE n.user_id = ? " +
                    "ORDER BY n.created_at DESC LIMIT 50";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TeamNotification notification = new TeamNotification(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("team_id"),
                    rs.getString("message"),
                    rs.getString("type"),
                    rs.getTimestamp("created_at"),
                    rs.getBoolean("is_read")
                );
                notification.setTeamName(rs.getString("team_name"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public static void markNotificationAsRead(int notificationId) {
        String sql = "UPDATE team_notifications SET is_read = TRUE WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static int getUnreadNotificationsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM team_notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Métodos para gerenciamento de checklists
    public static ArrayList<ChecklistItem> getChecklistItems(int taskId) throws SQLException {
        ArrayList<ChecklistItem> items = new ArrayList<>();
        String sql = "SELECT ci.* FROM checklist_items ci " +
                    "INNER JOIN checklists c ON ci.checklist_id = c.id " +
                    "WHERE c.task_id = ? " +
                    "ORDER BY ci.position";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(new ChecklistItem(
                    rs.getInt("id"),
                    rs.getInt("checklist_id"),
                    rs.getString("description"),
                    rs.getBoolean("is_completed"),
                    rs.getInt("position")
                ));
            }
        }
        return items;
    }
    
    public static ChecklistItem addChecklistItem(int taskId, String description, int position) throws SQLException {
        // Primeiro, verifica se já existe uma checklist para a tarefa
        int checklistId = getOrCreateChecklist(taskId);
        
        String sql = "INSERT INTO checklist_items (checklist_id, description, position) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, checklistId);
            pstmt.setString(2, description);
            pstmt.setInt(3, position);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return new ChecklistItem(
                    rs.getInt(1),
                    checklistId,
                    description,
                    false,
                    position
                );
            }
        }
        throw new SQLException("Falha ao criar item da checklist");
    }
    
    private static int getOrCreateChecklist(int taskId) throws SQLException {
        // Verifica se já existe uma checklist
        String selectSql = "SELECT id FROM checklists WHERE task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        // Se não existe, cria uma nova
        String insertSql = "INSERT INTO checklists (task_id, name) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, taskId);
            pstmt.setString(2, "Checklist Principal");
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Falha ao criar checklist");
    }
    
    public static void updateChecklistItem(int itemId, String description) throws SQLException {
        String sql = "UPDATE checklist_items SET description = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        }
    }
    
    public static void deleteChecklistItem(int itemId) throws SQLException {
        String sql = "DELETE FROM checklist_items WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
        }
    }
    
    public static void toggleChecklistItem(int itemId) throws SQLException {
        String sql = "UPDATE checklist_items SET is_completed = NOT is_completed WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
        }
    }
    
    public static double getChecklistProgress(int taskId) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN is_completed = 1 THEN 1 ELSE 0 END) as completed " +
                    "FROM checklist_items ci " +
                    "INNER JOIN checklists c ON ci.checklist_id = c.id " +
                    "WHERE c.task_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int completed = rs.getInt("completed");
                return total > 0 ? (completed * 100.0) / total : 0;
            }
        }
        return 0;
    }
    
    // Obter nome da tarefa pelo ID
    public static String getTaskName(int taskId) {
        String sql = "SELECT name FROM tasks WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter nome da tarefa: " + e.getMessage());
        }
        
        return null;
    }
}
