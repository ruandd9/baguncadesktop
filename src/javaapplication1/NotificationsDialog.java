package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotificationsDialog extends JDialog {
    private User currentUser;
    private DefaultListModel<TeamNotification> notificationsModel;
    private JList<TeamNotification> notificationsList;
    
    // Cores do tema Discord
    private final Color backgroundColor = new Color(54, 57, 63);
    private final Color fieldColor = new Color(64, 68, 75);
    private final Color textColor = Color.WHITE;
    private final Color unreadColor = new Color(88, 101, 242);
    
    public NotificationsDialog(Window owner, User currentUser) {
        super(owner, "Notificações", ModalityType.APPLICATION_MODAL);
        this.currentUser = currentUser;
        setupUI();
        loadNotifications();
    }
    
    private void setupUI() {
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Painel principal com padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Lista de notificações
        notificationsModel = new DefaultListModel<>();
        notificationsList = new JList<>(notificationsModel);
        notificationsList.setCellRenderer(new NotificationListCellRenderer());
        notificationsList.setBackground(fieldColor);
        notificationsList.setForeground(textColor);
        
        // Quando uma notificação é clicada, marca como lida
        notificationsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = notificationsList.locationToIndex(e.getPoint());
                if (index != -1) {
                    TeamNotification notification = notificationsModel.getElementAt(index);
                    if (!notification.isRead()) {
                        DatabaseManager.markNotificationAsRead(notification.getId());
                        notification.setRead(true);
                        notificationsList.repaint();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(notificationsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(fieldColor.darker()));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void loadNotifications() {
        notificationsModel.clear();
        ArrayList<TeamNotification> notifications = DatabaseManager.getUserNotifications(currentUser.getId());
        for (TeamNotification notification : notifications) {
            notificationsModel.addElement(notification);
        }
    }
    
    // Renderer personalizado para a lista de notificações
    private class NotificationListCellRenderer extends DefaultListCellRenderer {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        ) {
            TeamNotification notification = (TeamNotification) value;
            
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBackground(isSelected ? fieldColor.darker() : fieldColor);
            panel.setBorder(new EmptyBorder(8, 8, 8, 8));
            
            // Painel para o conteúdo da notificação
            JPanel contentPanel = new JPanel(new GridLayout(3, 1, 2, 2));
            contentPanel.setBackground(panel.getBackground());
            
            // Título (nome da equipe)
            JLabel titleLabel = new JLabel(notification.getTeamName());
            titleLabel.setForeground(notification.isRead() ? textColor : unreadColor);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Mensagem
            JLabel messageLabel = new JLabel(notification.getMessage());
            messageLabel.setForeground(textColor);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Data
            JLabel dateLabel = new JLabel(dateFormat.format(notification.getCreatedAt()));
            dateLabel.setForeground(new Color(185, 187, 190));
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            contentPanel.add(titleLabel);
            contentPanel.add(messageLabel);
            contentPanel.add(dateLabel);
            
            panel.add(contentPanel, BorderLayout.CENTER);
            
            // Indicador de não lida
            if (!notification.isRead()) {
                JPanel unreadIndicator = new JPanel();
                unreadIndicator.setPreferredSize(new Dimension(4, 0));
                unreadIndicator.setBackground(unreadColor);
                panel.add(unreadIndicator, BorderLayout.WEST);
            }
            
            return panel;
        }
    }
}
