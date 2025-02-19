package javaapplication1;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class TeamDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField emailField;
    private DefaultListModel<User> membersListModel;
    private JList<User> membersList;
    private Team team;
    private User currentUser;
    private boolean confirmed = false;
    
    // Cores do tema Discord
    private final Color backgroundColor = new Color(54, 57, 63);
    private final Color fieldColor = new Color(64, 68, 75);
    private final Color buttonColor = new Color(88, 101, 242);
    private final Color textColor = Color.WHITE;
    private final Color errorColor = new Color(240, 71, 71);
    private final Color successColor = new Color(87, 242, 135);
    
    public TeamDialog(Window owner, User currentUser) {
        this(owner, currentUser, null);
    }
    
    public TeamDialog(Window owner, User currentUser, Team existingTeam) {
        super(owner, existingTeam == null ? "Nova Equipe" : "Editar Equipe", ModalityType.APPLICATION_MODAL);
        this.currentUser = currentUser;
        this.team = existingTeam;
        setupUI();
        
        if (existingTeam != null) {
            loadTeamData();
        }
    }
    
    private void setupUI() {
        setSize(500, 600);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Painel principal com padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome da equipe
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Nome da Equipe:");
        nameLabel.setForeground(textColor);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        nameField = new JTextField(20);
        styleField(nameField);
        formPanel.add(nameField, gbc);
        
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setForeground(textColor);
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        styleField(descriptionArea);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(fieldColor.darker()));
        formPanel.add(descScroll, gbc);
        
        // Adicionar membros
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Adicionar membro (email):");
        emailLabel.setForeground(textColor);
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        JPanel addMemberPanel = new JPanel(new BorderLayout(5, 0));
        addMemberPanel.setBackground(backgroundColor);
        
        emailField = new JTextField();
        styleField(emailField);
        addMemberPanel.add(emailField, BorderLayout.CENTER);
        
        JButton addButton = new JButton("Adicionar");
        styleButton(addButton);
        addButton.addActionListener(e -> addMember());
        addMemberPanel.add(addButton, BorderLayout.EAST);
        
        formPanel.add(addMemberPanel, gbc);
        
        // Lista de membros
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        membersListModel = new DefaultListModel<>();
        membersList = new JList<>(membersListModel);
        membersList.setCellRenderer(new MemberListCellRenderer());
        membersList.setBackground(fieldColor);
        membersList.setForeground(textColor);
        
        JScrollPane membersScroll = new JScrollPane(membersList);
        membersScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(fieldColor.darker()),
            "Membros da Equipe",
            0,
            0,
            null,
            textColor
        ));
        formPanel.add(membersScroll, gbc);
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        
        JButton cancelButton = new JButton("Cancelar");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        
        JButton confirmButton = new JButton(team == null ? "Criar" : "Salvar");
        styleButton(confirmButton);
        confirmButton.addActionListener(e -> confirm());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void styleField(JTextComponent field) {
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldColor.darker()),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void styleButton(JButton button) {
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(buttonColor);
            }
        });
    }
    
    private void addMember() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage("Digite um email válido", false);
            return;
        }
        
        // Verificar se o usuário existe
        User user = DatabaseManager.getUserByEmail(email);
        if (user == null) {
            showMessage("Usuário não encontrado", false);
            return;
        }
        
        // Verificar se já é membro
        for (int i = 0; i < membersListModel.size(); i++) {
            if (membersListModel.getElementAt(i).getEmail().equals(email)) {
                showMessage("Usuário já é membro da equipe", false);
                return;
            }
        }
        
        membersListModel.addElement(user);
        emailField.setText("");
        showMessage("Membro adicionado com sucesso", true);
        
        // Se estiver editando uma equipe existente, adiciona notificação
        if (team != null) {
            DatabaseManager.createTeamNotification(
                user.getId(),
                team.getId(),
                "Você foi adicionado à equipe " + team.getName(),
                TeamNotification.Type.ADDED
            );
        }
    }
    
    private void confirm() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty()) {
            showMessage("Digite um nome para a equipe", false);
            return;
        }
        
        if (team == null) {
            // Criar nova equipe
            int teamId = DatabaseManager.createTeam(name, description, currentUser.getId());
            if (teamId != -1) {
                // Adicionar membros
                for (int i = 0; i < membersListModel.size(); i++) {
                    User member = membersListModel.getElementAt(i);
                    DatabaseManager.addTeamMember(teamId, member.getId());
                    // Criar notificação para cada membro
                    DatabaseManager.createTeamNotification(
                        member.getId(),
                        teamId,
                        "Você foi adicionado à equipe " + name + " por " + currentUser.getName(),
                        TeamNotification.Type.ADDED
                    );
                }
                confirmed = true;
                dispose();
            } else {
                showMessage("Erro ao criar equipe", false);
            }
        } else {
            // Atualizar equipe existente
            if (DatabaseManager.updateTeam(team.getId(), name, description, team.getLeader().getId())) {
                // Remover todos os membros atuais
                for (User member : team.getMembers()) {
                    DatabaseManager.removeTeamMember(team.getId(), member.getId());
                    // Criar notificação para membros removidos
                    if (!isMemberInNewList(member)) {
                        DatabaseManager.createTeamNotification(
                            member.getId(),
                            team.getId(),
                            "Você foi removido da equipe " + team.getName(),
                            TeamNotification.Type.REMOVED
                        );
                    }
                }
                
                // Adicionar os novos membros
                for (int i = 0; i < membersListModel.size(); i++) {
                    User member = membersListModel.getElementAt(i);
                    DatabaseManager.addTeamMember(team.getId(), member.getId());
                    // Criar notificação para novos membros
                    if (!team.isMember(member)) {
                        DatabaseManager.createTeamNotification(
                            member.getId(),
                            team.getId(),
                            "Você foi adicionado à equipe " + team.getName(),
                            TeamNotification.Type.ADDED
                        );
                    }
                }
                confirmed = true;
                dispose();
            } else {
                showMessage("Erro ao atualizar equipe", false);
            }
        }
    }
    
    private void loadTeamData() {
        nameField.setText(team.getName());
        descriptionArea.setText(team.getDescription());
        
        for (User member : team.getMembers()) {
            membersListModel.addElement(member);
        }
    }
    
    private void showMessage(String message, boolean success) {
        JOptionPane.showMessageDialog(
            this,
            message,
            success ? "Sucesso" : "Erro",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    // Método auxiliar para verificar se um membro está na nova lista
    private boolean isMemberInNewList(User member) {
        for (int i = 0; i < membersListModel.size(); i++) {
            if (membersListModel.getElementAt(i).getId() == member.getId()) {
                return true;
            }
        }
        return false;
    }
    
    // Renderer personalizado para a lista de membros
    private class MemberListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        ) {
            User user = (User) value;
            JPanel panel = new JPanel(new BorderLayout(5, 0));
            panel.setBackground(isSelected ? fieldColor.darker() : fieldColor);
            
            JLabel label = new JLabel(user.getName() + " (" + user.getEmail() + ")");
            label.setForeground(textColor);
            panel.add(label, BorderLayout.CENTER);
            
            // Verifica se não é o líder da equipe
            if (team == null || team.getLeader().getId() != user.getId()) {
                JButton removeButton = new JButton("X");
                removeButton.setBackground(errorColor);
                removeButton.setForeground(textColor);
                removeButton.setBorderPainted(false);
                removeButton.setFocusPainted(false);
                removeButton.setPreferredSize(new Dimension(25, 25));
                removeButton.addActionListener(e -> membersListModel.removeElement(user));
                panel.add(removeButton, BorderLayout.EAST);
            }
            
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            return panel;
        }
    }
}
