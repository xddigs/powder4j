package org.p4j.gui;

import org.p4j.core.K;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {

    private boolean isHovered = false;
    private boolean isPressed = false;

    private float currentScale = K.BUTTON_DEFAULT_SCALE;
    private float targetScale = K.BUTTON_DEFAULT_SCALE;
    private Timer animationTimer;

    public Button(String text) {
        super(text);
        setFocusPainted(false);
        setFocusable(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setForeground(K.GAME_BACKGROUND_COLOR);
        setFont(K.FONT_BIG);

        // Sumamos el margen al padding para mantener las dimensiones deseadas
        int topPadding = K.BUTTON_PADDING_TOP + K.BUTTON_MARGIN;
        int leftPadding = K.BUTTON_PADDING_LEFT + K.BUTTON_MARGIN;
        int bottomPadding = K.BUTTON_PADDING_BOTTOM + K.BUTTON_MARGIN;
        int rightPadding = K.BUTTON_PADDING_RIGHT + K.BUTTON_MARGIN;

        setBorder(BorderFactory.createEmptyBorder(
                topPadding, leftPadding, bottomPadding, rightPadding));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                targetScale = K.BUTTON_HOVER_SCALE;
                animate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                targetScale = K.BUTTON_DEFAULT_SCALE;
                animate();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                targetScale = K.BUTTON_PRESSED_SCALE;
                animate();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                targetScale = isHovered ? K.BUTTON_HOVER_SCALE :
                        K.BUTTON_DEFAULT_SCALE;
                animate();
            }
        });
    }

    private void animate() {
        if (animationTimer != null && animationTimer.isRunning()) return;

        animationTimer = new Timer(K.BUTTON_ANIMATION_DELAY_MS, e -> {
            float delta = targetScale - currentScale;
            if (Math.abs(delta) < K.BUTTON_ANIMATION_THRESHOLD) {
                currentScale = targetScale;
                ((Timer) e.getSource()).stop();
            } else {
                currentScale += delta * K.BUTTON_ANIMATION_LERP_FACTOR;
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        double centerX = width / K.BUTTON_CENTER_DIVISOR;
        double centerY = height / K.BUTTON_CENTER_DIVISOR;
        g2.translate(centerX, centerY);
        g2.scale(currentScale, currentScale);
        g2.translate(-centerX, -centerY);

        Color backGroundColor = K.TEXT_COLOR_UNSELECTED;
        if (isPressed) {
            backGroundColor = backGroundColor.darker();
        } else if (isHovered) {
            backGroundColor = K.HIGHLIGHT_COLOR;
        }

        int margin = K.BUTTON_MARGIN;
        int drawX = margin;
        int drawY = margin;
        int drawWidth = width - (margin * 2);
        int drawHeight = height - (margin * 2);

        g2.setColor(backGroundColor);
        g2.fillRoundRect(drawX, drawY, drawWidth, drawHeight,
                K.BUTTON_CORNER_RADIUS, K.BUTTON_CORNER_RADIUS);

        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}