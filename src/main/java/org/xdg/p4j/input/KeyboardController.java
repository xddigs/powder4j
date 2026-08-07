package org.xdg.p4j.input;

import org.xdg.p4j.data.ElementID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Handles keyboard input events for the simulation.
 * This class allows for element selection.
 * Hold TAB to select the brush, use the mouse to select elements.
 */
public class KeyboardController extends KeyAdapter {

    private final Brush brush;
    private final List<ElementID> selectableElements;
    private boolean tabPressed = false;
    private int selectedIndex = 0;

    public KeyboardController(Brush brush) {
        this.brush = brush;
        this.selectableElements = Arrays.stream(ElementID.values())
                .filter(ElementID::isSelectable)
                .collect(Collectors.toList());
        
        // Find initial index of current brush element if it exists in selectable list
        for (int i = 0; i < selectableElements.size(); i++) {
            if (selectableElements.get(i) == brush.getCurrentElement()) {
                this.selectedIndex = i;
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            tabPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && tabPressed) {
            selectedIndex = (selectedIndex - 1 + selectableElements.size()) % selectableElements.size();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && tabPressed) {
            selectedIndex = (selectedIndex + 1) % selectableElements.size();
        }

        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
            int index = e.getKeyCode() - KeyEvent.VK_1;
            if (index < selectableElements.size()) {
                brush.setCurrentElement(selectableElements.get(index));
                selectedIndex = index;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            tabPressed = false;
            brush.setCurrentElement(selectableElements.get(selectedIndex));
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

    public List<ElementID> getSelectableElements() {
        return selectableElements;
    }
}