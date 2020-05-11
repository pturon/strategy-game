package Game;

import Main.Clock;
import Main.ResourceManager;
import Main.View;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameBoard implements View {

    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private int visible_width;
    private int visible_height;

    private int topLeftTileX = 0;
    private int topLeftTileY = 0;

    private int board_width;
    private int board_height;

    private int markerPosX = 0;
    private int markerPosY = 0;

    private int radiusCenterX = 0;
    private int radiusCenterY = 0;
    private int radiusSize = 0;

    private LinkedList<Tile> path;

    private ArrayList<Unit> units;
    private Unit selectedUnit;

    private int idleAnimationFrame = 0;
    private int idleAnimationTimer = 0;
    private final int IDLE_ANIMATION_FRAME_DURATION = 10;

    private static final int MOVEMENT_PER_FRAME = 2;
    private int walkingAnimationTimer = 0;
    private final int WALKING_ANIMATION_FRAME_DURATION = 10;

    public GameBoard(int width, int height) {
        board_width = width;
        board_height = height;
        units = new ArrayList<>();
        visible_width = 640 / TILE_WIDTH;
        visible_height = 512 / TILE_HEIGHT;
        path = new LinkedList<>();

        units.add(new Unit("test_sprite"));
        for (Unit u : units) {
            u.setSpritesheetPos(ResourceManager.addIdleSpritesheet(u.getName()));
            ResourceManager.addWalkingSpritesheet(u.getName());
        }
        units.get(0).setPosX(5);
        units.get(0).setPosY(5);
    }

    @Override
    public Image getCurrentImage() {
        BufferedImage image = new BufferedImage(640, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        if (selectedUnit != null) {
            showRadius(selectedUnit, g);
        }

        //grid
        g.setColor(new Color(0, 0, 0));
        for (int x = 0; x < visible_width; x++) {
            for (int y = 0; y < visible_height; y++) {
                g.drawRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }
        //cursor
        g.setColor(new Color(255, 0, 0));
        g.drawRect(markerPosX * TILE_WIDTH, markerPosY * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        //arrows
        if (!path.isEmpty()) {
            int lastDirection = -1;
            for (int i = 0; i < path.size(); i++) {
                int dir = getDirection(selectedUnit.getPosX(), selectedUnit.getPosY(), path.get(i).getPosX(), path.get(i).getPosY());
                if (dir == lastDirection || lastDirection == -1) {
                    switch (dir) {
                        case 0:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 0), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 1:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 1), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 2:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 0), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 3:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 1), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                    }
                } else if (i == path.size() - 1) {
                    switch (lastDirection) {
                        case 0:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 2), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 1:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 3), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 2:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 4), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                        case 3:
                            g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 5), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                            break;
                    }
                } else {
                    switch (lastDirection) {
                        case 0:
                            switch (dir) {
                                case 1:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 8), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                                case 3:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 7), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                            }
                            break;
                        case 1:
                            switch (dir) {
                                case 0:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 9), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                                case 2:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 7), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                            }
                            break;
                        case 2:
                            switch (dir) {
                                case 1:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 6), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                                case 3:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 9), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                            }
                            break;
                        case 3:
                            switch (dir) {
                                case 0:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 6), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                                case 2:
                                    g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, 8), path.get(i).getPosX()*TILE_WIDTH, path.get(i).getPosY()*TILE_HEIGHT, null);
                                    break;
                            }
                            break;
                    }
                }
                lastDirection = dir;
            }
        }
        //units
        for (Unit u : units) {
            if (u.isMoving()) {
                g.drawImage(ResourceManager.getWalkingFrame(u.getSpritesheetPos(), u.getWalkingAnimationFrame(), u.getMovementDirection()), u.getPosX() * TILE_WIDTH + u.getOffsetX(), u.getPosY() * TILE_HEIGHT - 16 + u.getOffsetY(), null);
            } else {
                g.drawImage(ResourceManager.getIdleFrame(u.getSpritesheetPos(), idleAnimationFrame), u.getPosX() * TILE_WIDTH, u.getPosY() * TILE_HEIGHT - 16, null);
            }
        }
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
    public void mouseMoved(MouseEvent mouse) {
        markerPosX = mouse.getX() / TILE_WIDTH;
        markerPosY = mouse.getY() / TILE_HEIGHT;
        if (selectedUnit != null) {
            if(!selectedUnit.isMoving()){
                if (getDistance(markerPosX, markerPosY, selectedUnit.getPosX(), selectedUnit.getPosY()) <= selectedUnit.getMovementLeft()) {
                    createArrowPath();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouse) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseWheel) {

    }

    @Override
    public void mouseClicked(MouseEvent mouse) {
        if (selectedUnit != null) {
            if (getDistance(selectedUnit.getPosX(), selectedUnit.getPosY(), markerPosX, markerPosY) > selectedUnit.getMovementLeft()) {
                selectedUnit = null;
            } else {
                moveUnit(selectedUnit, markerPosX, markerPosY);
            }
        } else {
            for (Unit u : units) {
                if (markerPosX == u.getPosX() - topLeftTileX) {
                    if (markerPosY == u.getPosY() - topLeftTileY) {
                        selectedUnit = u;
                        radiusCenterX = markerPosX;
                        radiusCenterY = markerPosY;
                        radiusSize = u.getAttackRange() + u.getMovementLeft();
                    }
                }
            }
        }
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
        if (idleAnimationTimer < IDLE_ANIMATION_FRAME_DURATION) {
            idleAnimationTimer++;
        } else {
            idleAnimationTimer = 0;
            if (idleAnimationFrame == 3) {
                idleAnimationFrame = 0;
            } else {
                idleAnimationFrame++;
            }
        }
        if (selectedUnit != null) {
            if (selectedUnit.isMoving()) {
                if (walkingAnimationTimer < WALKING_ANIMATION_FRAME_DURATION) {
                    walkingAnimationTimer++;
                } else {
                    walkingAnimationTimer = 0;
                    if (selectedUnit.getWalkingAnimationFrame() == 3) {
                        selectedUnit.setWalkingAnimationFrame(0);
                    } else {
                        selectedUnit.setWalkingAnimationFrame(selectedUnit.getWalkingAnimationFrame() + 1);
                    }
                }
                switch (selectedUnit.getMovementDirection()) {
                    case 0:
                        if (Math.abs(selectedUnit.getOffsetY()) == TILE_HEIGHT) {
                            selectedUnit.setPosY(selectedUnit.getPosY() - 1);
                            selectedUnit.removeTileFromPath();
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft() - 1);
                            if (!selectedUnit.isMoving()) {
                                radiusCenterX = selectedUnit.getPosX();
                                radiusCenterY = selectedUnit.getPosY();
                                radiusSize = selectedUnit.getMovementLeft() + selectedUnit.getAttackRange();
                            }
                        } else {
                            selectedUnit.setOffsetY(selectedUnit.getOffsetY() - MOVEMENT_PER_FRAME);
                        }
                        break;
                    case 1:
                        if (Math.abs(selectedUnit.getOffsetX()) == TILE_WIDTH) {
                            selectedUnit.setPosX(selectedUnit.getPosX() + 1);
                            selectedUnit.removeTileFromPath();
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft() - 1);
                            if (!selectedUnit.isMoving()) {
                                radiusCenterX = selectedUnit.getPosX();
                                radiusCenterY = selectedUnit.getPosY();
                                radiusSize = selectedUnit.getMovementLeft() + selectedUnit.getAttackRange();
                            }
                        } else {
                            selectedUnit.setOffsetX(selectedUnit.getOffsetX() + MOVEMENT_PER_FRAME);
                        }
                        break;
                    case 2:
                        if (Math.abs(selectedUnit.getOffsetY()) == TILE_HEIGHT) {
                            selectedUnit.setPosY(selectedUnit.getPosY() + 1);
                            selectedUnit.removeTileFromPath();
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft() - 1);
                            if (!selectedUnit.isMoving()) {
                                radiusCenterX = selectedUnit.getPosX();
                                radiusCenterY = selectedUnit.getPosY();
                                radiusSize = selectedUnit.getMovementLeft() + selectedUnit.getAttackRange();
                            }
                        } else {
                            selectedUnit.setOffsetY(selectedUnit.getOffsetY() + MOVEMENT_PER_FRAME);
                        }
                        break;
                    case 3:
                        if (Math.abs(selectedUnit.getOffsetX()) == TILE_WIDTH) {
                            selectedUnit.setPosX(selectedUnit.getPosX() - 1);
                            selectedUnit.removeTileFromPath();
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft() - 1);
                            if (!selectedUnit.isMoving()) {
                                radiusCenterX = selectedUnit.getPosX();
                                radiusCenterY = selectedUnit.getPosY();
                                radiusSize = selectedUnit.getMovementLeft() + selectedUnit.getAttackRange();
                            }
                        } else {
                            selectedUnit.setOffsetX(selectedUnit.getOffsetX() - MOVEMENT_PER_FRAME);
                        }
                        break;
                }
            }
        }
    }

    private int getDistance(int posX, int posY, int x, int y) {
        return Math.abs(posX - x) + Math.abs(posY - y);
    }

    private void showRadius(Unit u, Graphics g) {
        for (int x = radiusCenterX - radiusSize; x <= radiusCenterX + radiusSize; x++) {
            for (int y = radiusCenterY - radiusSize; y <= radiusCenterY + radiusSize; y++) {
                if (getDistance(radiusCenterX, radiusCenterY, x, y) == radiusSize) {
                    g.setColor(new Color(255, 0, 0));
                    g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                }
                if (getDistance(radiusCenterX, radiusCenterY, x, y) < radiusSize) {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                }
            }
        }
        if (selectedUnit.isMoving()) {

        }
    }

    private void moveUnit(Unit u, int x, int y) {
        path = new LinkedList<>();
        int curX = u.getPosX();
        int curY = u.getPosY();
        int dist = getDistance(curX, curY, x, y);
        for (int i = 0; i < dist; i++) {
            if (Math.abs(curX - x) > Math.abs(curY - y)) {
                if (curX - x < 0) {
                    curX++;
                } else {
                    curX--;
                }
            } else {
                if (curY - y < 0) {
                    curY++;
                } else {
                    curY--;
                }
            }
            u.addTileToPath(curX, curY);
        }
    }

    private void createArrowPath() {
        path = new LinkedList<>();
        int curX = selectedUnit.getPosX();
        int curY = selectedUnit.getPosY();
        int dist = getDistance(curX, curY, markerPosX, markerPosY);
        for (int i = 0; i < dist; i++) {
            if (Math.abs(curX - markerPosX) > Math.abs(curY - markerPosY)) {
                if (curX - markerPosX < 0) {
                    curX++;
                } else {
                    curX--;
                }
            } else {
                if (curY - markerPosY < 0) {
                    curY++;
                } else {
                    curY--;
                }
            }
            path.add(new Tile(curX, curY));
        }
    }

    private int getDirection(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 > y2) {
            return 0;
        } else if (x1 < x2 && y1 == y2) {
            return 1;
        } else if (x1 == x2 && y1 < y2) {
            return 2;
        } else {
            return 3;
        }
    }
}
