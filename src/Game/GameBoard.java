package Game;

import Main.Clock;
import Main.View;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

public class GameBoard implements View {

    private static final int TILE_WIDTH = 64;
    private static final int TILE_HEIGHT = 64;

    private int visible_width;
    private int visible_height;

    private int topLeftTileX = 0;
    private int topLeftTileY = 0;

    private int board_width;
    private int board_height;

    private int markerPosX = 0;
    private int markerPosY = 0;

    private Unit[][] units;
    private Unit selectedUnit;

    public GameBoard(int width, int height){
        board_width = width;
        board_height = height;
        units = new Unit[board_width][board_height];
        visible_width = 640 / TILE_WIDTH;
        visible_height = 512 / TILE_HEIGHT;
    }

    @Override
    public Image getCurrentImage() {
        BufferedImage image = new BufferedImage(640, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        //grid
        g.setColor(new Color(0,0,0));
        for(int x = 0; x < visible_width; x++){
            for(int y = 0; y < visible_height; y++){
                g.drawRect(x * TILE_WIDTH,y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }
        //cursor
        g.setColor(new Color(255,0,0));
        g.drawRect(markerPosX*TILE_WIDTH,markerPosY*TILE_HEIGHT,TILE_WIDTH,TILE_HEIGHT);
        //units

        return image;
    }

    @Override
    public Dimension getDimension() {
        return null;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void keyTyped(KeyEvent key) {

    }

    @Override
    public void keyPressed(KeyEvent key) {

    }

    @Override
    public void keyReleased(KeyEvent key) {

    }

    @Override
    public void mouseMoved(MouseEvent mouse){
        markerPosX = mouse.getX()/TILE_WIDTH;
        markerPosY = mouse.getY()/TILE_HEIGHT;
    }

    @Override
    public void mousePressed(MouseEvent mouse) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseClicked(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseExited(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheel) {

    }

    @Override
    public void setPaused(boolean isPaused) {

    }

    @Override
    public void step() {

    }
}
