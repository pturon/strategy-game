package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame {

    private static final int FPS = 60;

    private static final int WIDTH = 640;
    private static final int HEIGHT = 512;

    private static View currentView;
    private JComponent viewport;

    public GameFrame(){
        super("Title");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(currentView!=null){
                    currentView.setPaused(false);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(currentView!=null){
                    currentView.setPaused(true);
                }
            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(currentView!=null){
                    currentView.keyTyped(e);
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if(currentView!=null){
                    currentView.keyPressed(e);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(currentView!=null){
                    currentView.keyReleased(e);
                }
            }
        });

        viewport = createViewport();
        add(viewport);
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getSize().width) / 2, (d.height - getSize().height) / 2);
        setVisible(true);
    }

    private JComponent createViewport() {
        JComponent createdViewport = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                if(currentView!=null) {
                    Image currentImage = currentView.getCurrentImage();
                    if (currentImage != null) {
                        g.drawImage(currentImage, 0, 0, null);
                    }
                }
            }
        };
        createdViewport.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent mouse) {
                if(currentView != null)currentView.mousePressed(mouse);
            }
            public void mouseReleased(MouseEvent mouse) {
                if(currentView != null)currentView.mouseReleased(mouse);
            }
            public void mouseClicked(MouseEvent mouse) {
                if(currentView != null)currentView.mouseClicked(mouse);
            }
            public void mouseEntered(MouseEvent mouse) {
                if(currentView != null)currentView.mouseEntered(mouse);
            }
            public void mouseExited(MouseEvent mouse) {
                if(currentView != null)currentView.mouseExited(mouse);
            }
        });
        createdViewport.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent mouse) {
                if(currentView != null)currentView.mouseDragged(mouse);
            }
            public void mouseMoved(MouseEvent mouse) {
                if(currentView != null)currentView.mouseMoved(mouse);
            }
        });
        createdViewport.addMouseWheelListener((MouseWheelEvent mouseWheel) -> {
            if(currentView != null)currentView.mouseWheelMoved(mouseWheel);
        });
        createdViewport.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        return createdViewport;
    }

    public static void setView(final View view) {
        Clock.stop();
        Clock.setStepsPerSecond(FPS);
        currentView = view;
        currentView.init();
        Clock.setCurrentView(currentView);
        Clock.start();
    }

    public JComponent getViewport(){
        return viewport;
    }
}
