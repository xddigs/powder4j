package org.p4j.gui;

import org.p4j.core.K;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    public Menu(Runnable onStartGame) {
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

        Button playButton = new Button("PLAY");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(_ -> onStartGame.run());

        Button exitButton = new Button("EXIT");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(_ -> System.exit(0));

        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 30)));
        container.add(playButton);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(exitButton);
        add(container);
    }
}
