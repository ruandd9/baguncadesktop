package javaapplication1;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
            // Ler o arquivo SQL
            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        DatabaseManager.class.getResourceAsStream("/database/kanban_db.sql"),
                        StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlScript.append(line).append("\n");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao ler arquivo SQL: " + e.getMessage(),
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
    public static int addTask(String taskName, String column, int userId) {
        String sql = "INSERT INTO tasks (name, column_name, created_by) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, taskName);
            pstmt.setString(2, column);
            pstmt.setInt(3, userId);
            
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
    public static boolean updateTask(int taskId, String newName) {
        String sql = "UPDATE tasks SET name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newName);
            pstmt.setInt(2, taskId);
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
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao excluir tarefa: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Carregar todas as tarefas
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, name, column_name FROM tasks ORDER BY created_at";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("column_name")
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
    
    // Registrar atividade
    public static void logActivity(int userId, int taskId, String actionType, String description, String oldValue, String newValue) {
        String sql = "INSERT INTO activity_log (user_id, task_id, action_type, description, old_value, new_value) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, taskId);
            pstmt.setString(3, actionType);
            pstmt.setString(4, description);
            pstmt.setString(5, oldValue);
            pstmt.setString(6, newValue);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao registrar atividade: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Carregar atividades recentes
    public static ArrayList<String> getRecentActivities() {
        ArrayList<String> activities = new ArrayList<>();
        String sql = "SELECT u.name, a.description, a.created_at " +
                    "FROM activity_log a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "ORDER BY a.created_at DESC LIMIT 10";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String activity = String.format("%s - %s (%s)",
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at").toString()
                );
                activities.add(activity);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao carregar atividades: " + e.getMessage(),
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
        return activities;
    }
}
