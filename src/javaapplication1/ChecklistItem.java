package javaapplication1;

public class ChecklistItem {
    private int id;
    private int checklistId;
    private String description;
    private boolean completed;
    private int position;
    
    public ChecklistItem(int id, int checklistId, String description, boolean completed, int position) {
        this.id = id;
        this.checklistId = checklistId;
        this.description = description;
        this.completed = completed;
        this.position = position;
    }
    
    public int getId() {
        return id;
    }
    
    public int getChecklistId() {
        return checklistId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
