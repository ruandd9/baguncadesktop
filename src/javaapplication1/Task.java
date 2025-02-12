package javaapplication1;

public class Task {
    private int id;
    private String name;
    private String column;
    
    public Task(int id, String name, String column) {
        this.id = id;
        this.name = name;
        this.column = column;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColumn() {
        return column;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
}
