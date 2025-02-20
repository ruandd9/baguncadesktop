package javaapplication1;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends BasicScrollBarUI {
    private final Color THUMB_COLOR = new Color(64, 68, 75);
    private final Color TRACK_COLOR = new Color(47, 49, 54);
    private final int THUMB_SIZE = 8;

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = THUMB_COLOR;
        this.trackColor = TRACK_COLOR;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Ajusta o tamanho do thumb
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            thumbBounds.width = THUMB_SIZE;
            thumbBounds.x = scrollbar.getWidth() - thumbBounds.width;
        } else {
            thumbBounds.height = THUMB_SIZE;
            thumbBounds.y = scrollbar.getHeight() - thumbBounds.height;
        }

        // Desenha o thumb arredondado
        g2.setColor(THUMB_COLOR);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                        thumbBounds.width, thumbBounds.height, 
                        THUMB_SIZE, THUMB_SIZE);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(TRACK_COLOR);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }
}
