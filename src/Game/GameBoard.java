package Game;

import Enums.Direction;
import Enums.UnitState;
import Main.Clock;
import Main.ResourceManager;
import Main.TextManager;
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

    private final int visible_width;
    private final int visible_height;

    private int topLeftTileX = 0;
    private int topLeftTileY = 0;

    private final int board_width;
    private final int board_height;

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

    private int selectedMenuPoint = 0;
    private final int LINE_HEIGHT = 8;
    private final int MENU_PADDING = 2;
    private final int MENU_LINE_PADDING = 5;

    private boolean unitMenuShown = false;
    private int unitMenuPosX = 0;
    private int unitMenuPosY = 0;
    private final int unitMenuWidth = 64;
    private final int unitMenuHeight = 128;

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
        drawArrows(g);
        //units
        for (Unit u : units) {
            if (u.getState() == UnitState.MOVING) {
                if (u.getMovementDirection() == Direction.TOP) {
                    g.drawImage(ResourceManager.getWalkingFrame(u.getSpritesheetPos(), u.getWalkingAnimationFrame(), 0), u.getPosX() * TILE_WIDTH + u.getOffsetX(), u.getPosY() * TILE_HEIGHT - 16 + u.getOffsetY(), null);
                } else if (u.getMovementDirection() == Direction.RIGHT) {
                    g.drawImage(ResourceManager.getWalkingFrame(u.getSpritesheetPos(), u.getWalkingAnimationFrame(), 1), u.getPosX() * TILE_WIDTH + u.getOffsetX(), u.getPosY() * TILE_HEIGHT - 16 + u.getOffsetY(), null);
                } else if (u.getMovementDirection() == Direction.BOTTOM) {
                    g.drawImage(ResourceManager.getWalkingFrame(u.getSpritesheetPos(), u.getWalkingAnimationFrame(), 2), u.getPosX() * TILE_WIDTH + u.getOffsetX(), u.getPosY() * TILE_HEIGHT - 16 + u.getOffsetY(), null);
                } else if (u.getMovementDirection() == Direction.LEFT) {
                    g.drawImage(ResourceManager.getWalkingFrame(u.getSpritesheetPos(), u.getWalkingAnimationFrame(), 3), u.getPosX() * TILE_WIDTH + u.getOffsetX(), u.getPosY() * TILE_HEIGHT - 16 + u.getOffsetY(), null);
                }
            } else {
                g.drawImage(ResourceManager.getIdleFrame(u.getSpritesheetPos(), idleAnimationFrame), u.getPosX() * TILE_WIDTH, u.getPosY() * TILE_HEIGHT - 16, null);
            }
        }
        //unitMenu
        if (unitMenuShown) {
            drawUnitMenu(g);
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
            if (selectedUnit.getState() != UnitState.MOVING) {
                if (getDistance(selectedUnit.getPosX(), selectedUnit.getPosY(), markerPosX, markerPosY) <= selectedUnit.getMovementLeft()) {
                    createArrowPath(markerPosX, markerPosY);
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
            if (selectedUnit.getState() != UnitState.MOVING) {
                if (getDistance(selectedUnit.getPosX(), selectedUnit.getPosY(), markerPosX, markerPosY) > selectedUnit.getMovementLeft()) {
                    selectedUnit = null;
                } else {
                    moveUnit(selectedUnit, markerPosX, markerPosY);
                    createArrowPath(markerPosX, markerPosY);
                }
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
                    case TOP:
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
                    case RIGHT:
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
                    case BOTTOM:
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
                    case LEFT:
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
            if (selectedUnit.getState() == UnitState.MOVING && selectedUnit.getNextTileFromPath() == null) {
                selectedUnit.setState(UnitState.IDLE);
            }
            if (selectedUnit.hasMovedThisRound() && selectedUnit.getState() == UnitState.IDLE) {
                unitMenuShown = true;
            } else {
                unitMenuShown = false;
            }
        } else {
            unitMenuShown = false;
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
        u.setState(UnitState.MOVING);
        u.setMovedThisRound(true);
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

    private void resetUnitPos(Unit u) {
        u.setState(UnitState.IDLE);
        u.setMovedThisRound(false);
        u.setPosX(u.getLastPosX());
        u.setPosY(u.getLastPosY());
    }

    private Direction getDirection(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 > y2) {
            return Direction.TOP;
        } else if (x1 < x2 && y1 == y2) {
            return Direction.RIGHT;
        } else if (x1 == x2 && y1 < y2) {
            return Direction.BOTTOM;
        } else {
            return Direction.LEFT;
        }
    }

    private void endRound() {
        for (Unit u : units) {
            u.setMovementLeft(u.getMovementRange());
            u.setState(UnitState.IDLE);
            u.setMovedThisRound(false);
            u.setLastPosX(u.getPosX());
            u.setLastPosY(u.getLastPosY());
        }
    }

    private boolean checkEndRound() {
        for (Unit u : units) {
            if (u.getState() != UnitState.SLEEP) {
                return false;
            }
        }
        return true;
    }

    private void drawUnitMenu(Graphics g) {
        if (selectedUnit != null) {
            g.setColor(new Color(50, 50, 50));
            if (selectedUnit.getPosX() - topLeftTileX >= visible_width - 5) {
                unitMenuPosX = (selectedUnit.getPosX() - topLeftTileX) * TILE_WIDTH - unitMenuWidth - TILE_WIDTH / 2;
            } else {
                unitMenuPosX = (selectedUnit.getPosX() - topLeftTileX) * TILE_WIDTH + TILE_WIDTH + TILE_WIDTH / 2;
            }
            if (selectedUnit.getPosY() - topLeftTileY == 0) {
                unitMenuPosY = 0;
            } else if (selectedUnit.getPosY() - topLeftTileY >= visible_height - 3) {
                unitMenuPosY = visible_height * TILE_HEIGHT - unitMenuHeight;
            } else {
                unitMenuPosY = selectedUnit.getPosY() * TILE_HEIGHT - TILE_HEIGHT;
            }
            g.fillRect(unitMenuPosX, unitMenuPosY, unitMenuWidth, unitMenuHeight);
            ArrayList<String> texts = TextManager.getUnitMenuTexts();
            g.setColor(new Color(255, 255, 255));
            for (int i = 0; i < texts.size(); i++) {
                int width = g.getFontMetrics().stringWidth(texts.get(i));
                g.drawString(texts.get(i), unitMenuPosX + (unitMenuWidth - width) / 2, unitMenuPosY + MENU_PADDING + LINE_HEIGHT + (MENU_LINE_PADDING + LINE_HEIGHT) * i);
            }
        }
    }

    private void drawUnitInfoPanel(Graphics g) {

    }

    private void drawArrows(Graphics g) {
        if (selectedUnit != null) {
            if (!path.isEmpty()) {
                Direction lastDirection = Direction.TOP;
                for (int i = 0; i < path.size(); i++) {
                    Direction direction;
                    int type = 0;
                    if (i == path.size() - 1) {
                        if (i == 0) {
                            direction = getDirection(selectedUnit.getPosX(), selectedUnit.getPosY(), path.get(i).getPosX(), path.get(i).getPosY());
                        } else {
                            direction = lastDirection;
                        }
                        switch (direction) {
                            case TOP:
                                type = 2;
                                break;
                            case RIGHT:
                                type = 3;
                                break;
                            case BOTTOM:
                                type = 4;
                                break;
                            case LEFT:
                                type = 5;
                                break;
                        }
                    } else {
                        if (i == 0) {
                            lastDirection = getDirection(selectedUnit.getPosX(), selectedUnit.getPosY(), path.get(i).getPosX(), path.get(i).getPosY());
                        }
                        direction = getDirection(path.get(i).getPosX(), path.get(i).getPosY(), path.get(i + 1).getPosX(), path.get(i + 1).getPosY());
                        if (direction == lastDirection) {
                            if (direction == Direction.TOP || direction == Direction.BOTTOM) {
                                type = 0;
                            } else {
                                type = 1;
                            }
                        } else {
                            switch (lastDirection) {
                                case TOP:
                                    switch (direction) {
                                        case RIGHT:
                                            type = 8;
                                            break;
                                        case LEFT:
                                            type = 7;
                                            break;
                                    }
                                    break;
                                case RIGHT:
                                    switch (direction) {
                                        case TOP:
                                            type = 9;
                                            break;
                                        case BOTTOM:
                                            type = 7;
                                            break;
                                    }
                                    break;
                                case BOTTOM:
                                    switch (direction) {
                                        case RIGHT:
                                            type = 6;
                                            break;
                                        case LEFT:
                                            type = 9;
                                            break;
                                    }
                                    break;
                                case LEFT:
                                    switch (direction) {
                                        case TOP:
                                            type = 6;
                                            break;
                                        case BOTTOM:
                                            type = 8;
                                            break;
                                    }
                                    break;
                            }

                        }
                        lastDirection = direction;
                    }
                    if (getUnitPosInPath() <= i) {
                        g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, type), path.get(i).getPosX() * TILE_WIDTH, path.get(i).getPosY() * TILE_HEIGHT, null);
                    }
                }
            }
        }
    }

    private void createArrowPath(int destinationX, int destinationY) {
        path = new LinkedList<>();
        int curX = selectedUnit.getPosX();
        int curY = selectedUnit.getPosY();
        int dist = getDistance(curX, curY, destinationX, destinationY);
        for (int i = 0; i < dist; i++) {
            if (Math.abs(curX - destinationX) > Math.abs(curY - destinationY)) {
                if (curX - destinationX < 0) {
                    curX++;
                } else {
                    curX--;
                }
            } else {
                if (curY - destinationY < 0) {
                    curY++;
                } else {
                    curY--;
                }
            }
            path.add(new Tile(curX, curY));
        }
    }

    private int getUnitPosInPath() {
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getPosX() == selectedUnit.getPosX()) {
                if (path.get(i).getPosY() == selectedUnit.getPosY()) {
                    return i + 1;
                }
            }
        }
        return -1;
    }
}
