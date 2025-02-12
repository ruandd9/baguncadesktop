package javaapplication1;

import java.util.Date;

public class Task {
    private int id;
    private String name;
    private String column;
    private int createdBy;
    private Priority priority;
    private Date dueDate;
    
    public enum Priority {
        ALTA("#FF4444"),
        MEDIA("#FFB74D"),
        BAIXA("#4CAF50");
        
        private final String color;
        
        Priority(String color) {
            this.color = color;
        }
        
        public String getColor() {
            return color;
        }
    }
    
    public Task(int id, String name, String column, int createdBy, Priority priority, Date dueDate) {
        this.id = id;
        this.name = name;
        this.column = column;
        this.createdBy = createdBy;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColumn() {
        return column;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public boolean isOverdue() {
        if (dueDate == null) return false;
        return new Date().after(dueDate);
    }
    
    public boolean isDueSoon() {
        if (dueDate == null) return false;
        long diff = dueDate.getTime() - new Date().getTime();
        long days = diff / (24 * 60 * 60 * 1000);
        return days <= 3 && days >= 0;
    }
}
