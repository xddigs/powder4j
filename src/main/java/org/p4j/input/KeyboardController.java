package org.p4j.input;

import org.p4j.core.K;
import org.p4j.data.BrushShape;
import org.p4j.data.BrushType;
import org.p4j.data.ElementID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Handles keyboard input events for the simulation.
 * This class allows for element and brush shape selection.
 * Hold TAB to select the element, hold ALT to select the brush shape.
 */
public class KeyboardController extends KeyAdapter {
    private final Brush brush;
    private final List<ElementID> selectableElements;
    private final List<BrushShape> selectableShapes;
    private final List<BrushType> selectableTypes;
    private boolean wasTabPressed = false;
    private boolean wasAltPressed = false;
    private boolean wasEPressed = false;
    private boolean wasShiftPressed = false;
    private int selectedIndex = 0;
    private int selectedShapeIndex = 0;
    private int selectedTypeIndex = 0;
    private long lastEscapeTime = 0;

    public KeyboardController(Brush brush) {
        this.brush = brush;
        this.selectableElements = Arrays.stream(ElementID.values())
                .filter(ElementID::isSelectable)
                .collect(Collectors.toList());
        this.selectableShapes = List.of(BrushShape.values());
        this.selectableTypes = List.of(BrushType.values());
        for (int i = 0; i < selectableElements.size(); i++) {
            if (selectableElements.get(i) == brush.getElement()) {
                this.selectedIndex = i;
                break;
            }
        }

        for (int i = 0; i < selectableShapes.size(); i++) {
            if (selectableShapes.get(i) == brush.getShape()) {
                this.selectedShapeIndex = i;
                break;
            }
        }

        for (int i = 0; i < selectableTypes.size(); i++) {
            if (selectableTypes.get(i) == brush.getType()) {
                this.selectedTypeIndex = i;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            wasTabPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            wasAltPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            wasShiftPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
            wasEPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            toggleEscape();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            wasTabPressed = false;
            brush.setElement(selectableElements.get(selectedIndex));
        }

        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            wasAltPressed = false;
            brush.setShape(selectableShapes.get(selectedShapeIndex));
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            wasShiftPressed = false;
            brush.setType(selectableTypes.get(selectedTypeIndex));
        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
            wasEPressed = false;
        }
    }

    public boolean wasTabPressed() {
        return wasTabPressed;
    }

    public boolean wasAltPressed() {
        return wasAltPressed;
    }

    public boolean wasShiftPressed() {
        return wasShiftPressed;
    }

    public boolean wasEPressed() {
        return wasEPressed;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedShapeIndex() {
        return selectedShapeIndex;
    }

    public void setSelectedShapeIndex(int selectedShapeIndex) {
        this.selectedShapeIndex = selectedShapeIndex;
    }

    public int getSelectedTypeIndex() {
        return selectedTypeIndex;
    }

    public void setSelectedTypeIndex(int selectedTypeIndex) {
        this.selectedTypeIndex = selectedTypeIndex;
    }

    public List<ElementID> getSelectableElements() {
        return selectableElements;
    }

    public List<BrushShape> getSelectableShapes() {
        return selectableShapes;
    }

    public List<BrushType> getSelectableTypes() {
        return selectableTypes;
    }

    private void toggleEscape() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEscapeTime <
                K.ESCAPE_DOUBLE_PRESS_INTERVAL) {
            System.exit(0);
        }
        lastEscapeTime = currentTime;
        K.IS_RUNNING ^= true;
    }
}