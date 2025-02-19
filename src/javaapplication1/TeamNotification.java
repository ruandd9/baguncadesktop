package javaapplication1;

import java.util.Date;

public class TeamNotification {
    private int id;
    private int userId;
    private int teamId;
    private String message;
    private String type;
    private Date createdAt;
    private boolean isRead;
    private String teamName; // Para exibição
    
    public TeamNotification(int id, int userId, int teamId, String message, String type, Date createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }
    
    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTeamId() { return teamId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Date getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
    public String getTeamName() { return teamName; }
    
    // Setters
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setRead(boolean read) { isRead = read; }
    
    // Tipos de notificação
    public static class Type {
        public static final String ADDED = "ADDED";
        public static final String REMOVED = "REMOVED";
        public static final String PROMOTED = "PROMOTED";
    }
}
