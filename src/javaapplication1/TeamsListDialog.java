package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class TeamsListDialog extends JDialog {
    private User currentUser;
    private DefaultListModel<Team> teamsListModel;
    private JList<Team> teamsList;
    
    // Cores do tema Discord
    private final Color backgroundColor = new Color(54, 57, 63);
    private final Color fieldColor = new Color(64, 68, 75);
    private final Color buttonColor = new Color(88, 101, 242);
    private final Color textColor = Color.WHITE;
    private final Color errorColor = new Color(240, 71, 71);
    
    public TeamsListDialog(Window owner, User currentUser) {
        super(owner, "Gerenciar Equipes", ModalityType.APPLICATION_MODAL);
        this.currentUser = currentUser;
        setupUI();
        loadTeams();
    }
    
    private void setupUI() {
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Painel principal com padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Botão Nova Equipe
        JButton newTeamButton = new JButton("+ Nova Equipe");
        styleButton(newTeamButton);
        newTeamButton.addActionListener(e -> createNewTeam());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(backgroundColor);
        topPanel.add(newTeamButton);
        
        // Lista de equipes
        teamsListModel = new DefaultListModel<>();
        teamsList = new JList<>(teamsListModel);
        teamsList.setCellRenderer(new TeamListCellRenderer());
        teamsList.setBackground(fieldColor);
        teamsList.setForeground(textColor);
        
        // Adiciona listener para cliques na lista
        teamsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = teamsList.locationToIndex(e.getPoint());
                if (index != -1) {
                    Team team = teamsListModel.getElementAt(index);
                    if (team.isLeader(currentUser)) {
                        Rectangle bounds = teamsList.getCellBounds(index, index);
                        // Área dos botões (assumindo 100 pixels de largura para os botões)
                        int buttonsWidth = 100;
                        int x = e.getX() - bounds.x;
                        
                        // Se o clique foi na área dos botões
                        if (x > bounds.width - buttonsWidth) {
                            // Divide a área dos botões em duas partes iguais
                            if (x > bounds.width - buttonsWidth/2) {
                                // Clique no botão excluir
                                deleteTeam(team);
                            } else {
                                // Clique no botão editar
                                editTeam(team);
                            }
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(teamsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(fieldColor.darker()));
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
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
    
    private void loadTeams() {
        teamsListModel.clear();
        ArrayList<Team> teams = DatabaseManager.getUserTeams(currentUser.getId());
        for (Team team : teams) {
            teamsListModel.addElement(team);
        }
    }
    
    private void createNewTeam() {
        TeamDialog dialog = new TeamDialog(this, currentUser);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadTeams(); // Recarrega a lista
        }
    }
    
    private void editTeam(Team team) {
        TeamDialog dialog = new TeamDialog(this, currentUser, team);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadTeams(); // Recarrega a lista
        }
    }
    
    private void deleteTeam(Team team) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a equipe '" + team.getName() + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Notificar todos os membros antes de excluir
            for (User member : team.getMembers()) {
                DatabaseManager.createTeamNotification(
                    member.getId(),
                    team.getId(),
                    "A equipe " + team.getName() + " foi excluída por " + currentUser.getName(),
                    TeamNotification.Type.REMOVED
                );
            }
            
            if (DatabaseManager.deleteTeam(team.getId())) {
                loadTeams(); // Recarrega a lista
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Erro ao excluir equipe",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    // Renderer personalizado para a lista de equipes
    private class TeamListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        ) {
            Team team = (Team) value;
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBackground(isSelected ? fieldColor.darker() : fieldColor);
            panel.setBorder(new EmptyBorder(8, 8, 8, 8));
            
            // Painel de informações da equipe
            JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
            infoPanel.setBackground(panel.getBackground());
            
            // Nome da equipe
            JLabel nameLabel = new JLabel(team.getName());
            nameLabel.setForeground(textColor);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Descrição e número de membros
            JLabel detailsLabel = new JLabel(String.format(
                "%d membros • Líder: %s",
                team.getMembers().size() + 1,
                team.getLeader().getName()
            ));
            detailsLabel.setForeground(new Color(185, 187, 190));
            detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            infoPanel.add(nameLabel);
            infoPanel.add(detailsLabel);
            panel.add(infoPanel, BorderLayout.CENTER);
            
            // Botões de ação (apenas se for o líder)
            if (team.isLeader(currentUser)) {
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                buttonsPanel.setBackground(panel.getBackground());
                
                // Botão Editar
                JLabel editButton = new JLabel("✎ Editar");
                editButton.setForeground(buttonColor);
                editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                editButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                editButton.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        editTeam(team);
                    }
                });
                
                // Botão Excluir
                JLabel deleteButton = new JLabel("🗑️");
                deleteButton.setForeground(errorColor);
                deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                deleteButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteButton.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        deleteTeam(team);
                    }
                });
                
                buttonsPanel.add(editButton);
                buttonsPanel.add(deleteButton);
                panel.add(buttonsPanel, BorderLayout.EAST);
            }
            
            return panel;
        }
    }
}
