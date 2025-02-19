package javaapplication1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Team {
    private int id;
    private String name;
    private String description;
    private User leader;
    private List<User> members;
    private Date createdAt;
    private Date updatedAt;
    
    public Team(int id, String name, String description, User leader) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.members = new ArrayList<>();
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public User getLeader() {
        return leader;
    }
    
    public List<User> getMembers() {
        return new ArrayList<>(members); // Retorna uma cópia para evitar modificação externa
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setLeader(User leader) {
        this.leader = leader;
    }
    
    // Métodos de gestão de membros
    public boolean addMember(User user) {
        if (!members.contains(user)) {
            return members.add(user);
        }
        return false;
    }
    
    public boolean removeMember(User user) {
        return members.remove(user);
    }
    
    public boolean isMember(User user) {
        return members.contains(user);
    }
    
    public boolean isLeader(User user) {
        return leader != null && leader.getId() == user.getId();
    }
    
    public void setDates(Date createdAt, Date updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
