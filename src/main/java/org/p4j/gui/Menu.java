package org.p4j.gui;

import org.p4j.core.K;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    public Menu(Runnable onStart2D, Runnable onStart3D) {
        super();
        setBackground(K.GAME_BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(K.APP_TITLE);
        titleLabel.setFont(K.FONT_BIG);
        titleLabel.setForeground(K.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Button play2DButton = new Button("PLAY 2D (CLASSIC)");
        play2DButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        play2DButton.addActionListener(_ -> onStart2D.run());

        Button play3DButton = new Button("PLAY 2.5D (ISOMETRIC)");
        play3DButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        play3DButton.addActionListener(_ -> onStart3D.run());

        Button exitButton = new Button("EXIT");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(_ -> System.exit(0));

        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 30)));
        container.add(play2DButton);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(play3DButton);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(exitButton);
        add(container);
    }
}