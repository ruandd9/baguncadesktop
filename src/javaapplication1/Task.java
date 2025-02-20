package javaapplication1;

import java.util.Date;

public class Task {
    private int id;
    private String name;
    private String column;
    private Priority priority;
    private Date dueDate;
    private int userId;
    
    public enum Priority {
        ALTA,
        MEDIA,
        BAIXA
    }
    
    public Task(int id, String name, String column, Priority priority, Date dueDate, int userId) {
        this.id = id;
        this.name = name;
        this.column = column;
        this.priority = priority;
        this.dueDate = dueDate;
        this.userId = userId;
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
    
    public Priority getPriority() {
        return priority;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setName(String name) {
        this.name = name;
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
