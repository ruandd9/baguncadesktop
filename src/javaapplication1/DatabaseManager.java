package javaapplication1;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/kanban_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "senac";
    
    // Testar conexão com o banco
    public static boolean testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS)) {
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
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS)) {
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
                    rs.getInt("created_by"),
                    priority,
                    dueDate
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
    public static ArrayList<String> getRecentActivities() {
        ArrayList<String> activities = new ArrayList<>();
        String sql = "SELECT a.description, a.created_at, u.name as user_name " +
                    "FROM activities a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "ORDER BY a.created_at DESC LIMIT 50";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs.next()) {
                String activity = String.format("%s - %s %s",
                    sdf.format(rs.getTimestamp("created_at")),
                    rs.getString("user_name"),
                    rs.getString("description")
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }
}
