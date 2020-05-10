package Main;

import javax.swing.*;

public class GraphicsManager extends Thread implements Runnable{
    private long lastUpdate  = System.nanoTime();
    private JComponent viewport;

    /**
     * Constructor.
     */
    public GraphicsManager(JComponent viewport) {
        this.setName("Graphics");
        this.viewport = viewport;
    }

    /**
     * Renders the current view in an endless loop.
     */
    @Override
    public void run() {
        while(true) {
            try {
                viewport.repaint();

                Thread.sleep(1);
            } catch(InterruptedException exception) {
                Thread.currentThread().interrupt();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
