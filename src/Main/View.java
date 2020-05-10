package Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface View {

    /**
     * Delivers the current image of the view.
     * @return The current image of the view.
     */
    public abstract Image getCurrentImage();

    /**
     * Delivers the size of the view, which is used to update the size of the window.
     * @return The size of the view.
     */
    public abstract Dimension getDimension();

    /**
     * Returns the icon of the view, which is shown in the taskbar.
     * @return The icon of the view, which is shown in the taskbar.
     */
    public abstract Image getIcon();

    /**
     * Returns the title of the view, which is shown in the taskbar.
     * @return The title of the view, which is shown in the taskbar.
     */
    public abstract String getTitle();

    /**
     * This method can be used to reset games before they are started for the second time.
     */
    public default void init() {

    }

    /**
     * Forwards a keyTyped-event to the view.
     * @param key The keyTyped-event
     */
    public default void keyTyped(KeyEvent key) {

    }

    /**
     * Forwards a keyPressed-event to the view.
     * @param key The keyPressed-event
     */
    public default void keyPressed(KeyEvent key) {

    }

    /**
     * Forwards a keyReleased-event to the view.
     * @param key The keyReleased-event
     */
    public default void keyReleased(KeyEvent key) {

    }

    /**
     * Forwards a mouseMoved-event to the view.
     * @param mouse The mouseMoved-event
     */
    public default void mouseMoved(MouseEvent mouse) {

    }

    /**
     * Forwards a mousePressed-event to the view.
     * @param mouse The mousePressed-event
     */
    public default void mousePressed(MouseEvent mouse) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseReleased(MouseEvent mouseWheel) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseClicked(MouseEvent mouseWheel) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseEntered(MouseEvent mouseWheel) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseExited(MouseEvent mouseWheel) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseDragged(MouseEvent mouseWheel) {

    }

    /**
     * Forwards a mouseWheelMoved-event to the view.
     * @param mouseWheel The mouseWheelMoved-event
     */
    public default void mouseWheelMoved(MouseWheelEvent mouseWheel) {

    }

    /**
     *
     * @param isPaused
     */
    public default void setPaused(boolean isPaused){

    }

    /**
     * This method is called periodically by the Clock.
     */
    public default void step() {

    }
}
