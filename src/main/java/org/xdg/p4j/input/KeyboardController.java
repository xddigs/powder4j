package org.xdg.p4j.input;

import org.xdg.p4j.data.ElementID;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Handles keyboard input events for the simulation.
 * This class allows for element selection.
 * Hold TAB to select the brush, use the mouse to select elements.
 */
public class KeyboardController extends KeyAdapter {

    private final Brush brush;
    private boolean tabPressed = false;
    private int selectedIndex = 0;

    public KeyboardController(Brush brush) {
        this.brush = brush;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            tabPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && tabPressed) {
            ElementID[] elements = ElementID.values();
            selectedIndex = (selectedIndex - 1 + elements.length) % elements.length;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && tabPressed) {
            ElementID[] elements = ElementID.values();
            selectedIndex = (selectedIndex + 1) % elements.length;
        }

        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
            int index = e.getKeyCode() - KeyEvent.VK_1;
            ElementID[] elements = ElementID.values();
            if (index < elements.length) {
                brush.setCurrentElement(elements[index]);
                selectedIndex = index;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            tabPressed = false;
            ElementID[] elements = ElementID.values();
            brush.setCurrentElement(elements[selectedIndex]);
        }
    }

    public boolean isTabPressed() {
        return tabPressed;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}