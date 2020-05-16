package Game;

import Enums.Direction;
import Enums.UnitState;
import Enums.Menu;
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
import java.util.Stack;

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

    private ArrayList<Unit> units;
    private Unit selectedUnit;
    private String nextUnitAction = "none";

    private final boolean[][] obstacles;
    private final int[][] distanceToTarget;
    private Stack<Tile> path;
    private int[] arrows;
    private LinkedList<Tile> pathQueue;

    private int idleAnimationFrame = 0;
    private int idleAnimationTimer = 0;
    private final int IDLE_ANIMATION_FRAME_DURATION = 10;

    private static final int MOVEMENT_PER_FRAME = 2;
    private int walkingAnimationTimer = 0;
    private final int WALKING_ANIMATION_FRAME_DURATION = 10;

    private int selectedMenuPoint = -1;

    private Menu shownMenu = Menu.NONE;

    private int actionMenuPosX = 0;
    private int actionMenuPosY = 0;
    private final int actionMenuWidth = 64;
    private final int actionMenuLineHeight = 32;
    private int actionMenuSize = 2;

    private int round = 0;

    public GameBoard(int width, int height) {
        board_width = width;
        board_height = height;
        obstacles = new boolean[board_width][board_height];
        obstacles[7][5] = true;
        distanceToTarget = new int[board_width][board_height];
        units = new ArrayList<>();
        visible_width = 640 / TILE_WIDTH;
        visible_height = 512 / TILE_HEIGHT;
        path = new Stack<>();

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
            showRadius(g);
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
        //menus
        switch (shownMenu){
            case NONE:
                break;
            case UNIT_INFO:
                drawUnitInfoPanel(g);
                break;
            case ACTION_MENU:
                drawActionMenu(g);
                break;
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
        System.out.println("typed");
    }

    @Override
    public void keyPressed(KeyEvent key) {
        System.out.println("pressed");
    }

    @Override
    public void keyReleased(KeyEvent key) {
        System.out.println("released");
    }

    @Override
    public void mouseMoved(MouseEvent mouse) {
        markerPosX = mouse.getX() / TILE_WIDTH;
        markerPosY = mouse.getY() / TILE_HEIGHT;
        if (selectedUnit != null) {
            if(shownMenu==Menu.NONE){
                if (selectedUnit.getState() != UnitState.MOVING) {
                    Tile t = new Tile(markerPosX, markerPosY);
                    if (getDistance(t) <= selectedUnit.getMovementLeft() && getDistance(t) != -1) {
                        generatePath(markerPosX, markerPosY, selectedUnit.getPosX(), selectedUnit.getPosY());
                    }
                }
            }
        }
        if(shownMenu==Menu.ACTION_MENU){
            if(actionMenuPosX<=mouse.getX()){
                if(actionMenuPosX+actionMenuWidth>mouse.getX()){
                    if(actionMenuPosY<=mouse.getY()){
                        if(actionMenuPosY+(actionMenuLineHeight*actionMenuSize)>mouse.getY()){
                            selectedMenuPoint = (mouse.getY()-actionMenuPosY)/actionMenuLineHeight;
                        } else {
                            selectedMenuPoint = -1;
                        }
                    } else {
                        selectedMenuPoint = -1;
                    }
                } else {
                    selectedMenuPoint = -1;
                }
            } else {
                selectedMenuPoint = -1;
            }
        } else {
            selectedMenuPoint = -1;
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
                if(shownMenu==Menu.NONE){
                    if (getDistance(selectedUnit.getPosX(), selectedUnit.getPosY(), markerPosX, markerPosY) > selectedUnit.getMovementLeft()) {
                        selectedUnit = null;
                        path = null;
                        arrows = null;
                    } else {
                        generatePath(markerPosX, markerPosY, selectedUnit.getPosX(), selectedUnit.getPosY());
                        shownMenu = Menu.ACTION_MENU;
                    }
                } else if(shownMenu==Menu.ACTION_MENU){
                    if(selectedMenuPoint==0){
                        shownMenu = Menu.NONE;
                        generatePath(markerPosX, markerPosY, selectedUnit.getPosX(), selectedUnit.getPosY());
                        nextUnitAction = "none";
                    } else if(selectedMenuPoint==actionMenuSize-1){
                        System.out.println("test");
                        moveUnit(selectedUnit);
                        shownMenu=Menu.NONE;
                        nextUnitAction = "sleep";
                    } else {
                    }
                }
            }
        } else {
            for (Unit u : units) {
                if (markerPosX == u.getPosX() - topLeftTileX) {
                    if (markerPosY == u.getPosY() - topLeftTileY) {
                        if(u.getState()==UnitState.IDLE){
                            selectedUnit = u;
                            radiusCenterX = markerPosX;
                            radiusCenterY = markerPosY;
                            radiusSize = u.getMovementLeft();
                            generateDistance(markerPosX, markerPosY);
                        }
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
            if (selectedUnit.getState() == UnitState.MOVING) {
                shownMenu = Menu.NONE;
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
                selectedUnit.setMovementDirection(getDirection(new Tile(selectedUnit.getPosX(), selectedUnit.getPosY()), getNextTile()));
                switch (selectedUnit.getMovementDirection()) {
                    case TOP:
                        if (Math.abs(selectedUnit.getOffsetY()) == TILE_HEIGHT) {
                            selectedUnit.setPosY(selectedUnit.getPosY() - 1);
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft()-1);
                            path.pop();
                        } else {
                            selectedUnit.setOffsetY(selectedUnit.getOffsetY() - MOVEMENT_PER_FRAME);
                        }
                        break;
                    case RIGHT:
                        if (Math.abs(selectedUnit.getOffsetX()) == TILE_WIDTH) {
                            selectedUnit.setPosX(selectedUnit.getPosX() + 1);
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft()-1);
                            path.pop();
                        } else {
                            selectedUnit.setOffsetX(selectedUnit.getOffsetX() + MOVEMENT_PER_FRAME);
                        }
                        break;
                    case BOTTOM:
                        if (Math.abs(selectedUnit.getOffsetY()) == TILE_HEIGHT) {
                            selectedUnit.setPosY(selectedUnit.getPosY() + 1);
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft()-1);
                            path.pop();
                        } else {
                            selectedUnit.setOffsetY(selectedUnit.getOffsetY() + MOVEMENT_PER_FRAME);
                        }
                        break;
                    case LEFT:
                        if (Math.abs(selectedUnit.getOffsetX()) == TILE_WIDTH) {
                            selectedUnit.setPosX(selectedUnit.getPosX() - 1);
                            selectedUnit.setOffsetX(0);
                            selectedUnit.setOffsetY(0);
                            selectedUnit.setMovementLeft(selectedUnit.getMovementLeft()-1);
                            path.pop();
                        } else {
                            selectedUnit.setOffsetX(selectedUnit.getOffsetX() - MOVEMENT_PER_FRAME);
                        }
                        break;
                }
            }
            if (selectedUnit.getState() == UnitState.MOVING && path.isEmpty()) {
                if(nextUnitAction.equals("sleep")){
                    selectedUnit.setState(UnitState.SLEEP);
                    if(checkEndRound()){
                        endRound();
                    }
                } else {
                    selectedUnit.setState(UnitState.IDLE);
                    generateDistance(selectedUnit.getPosX(), selectedUnit.getPosY());
                }
            }
        }
    }

    private Tile getNextTile() {
        return path.get(path.size() - 1);
    }

    private int getDistance(int posX, int posY, int x, int y) {
        return Math.abs(posX - x) + Math.abs(posY - y);
    }

    private void showRadius(Graphics g) {
        int max = radiusSize;
        for (int x = radiusCenterX - max; x <= radiusCenterX + max; x++) {
            for (int y = radiusCenterY - max; y <= radiusCenterY + max; y++) {
                if (x >= 0 && y >= 0 && x < board_width && y < board_height) {
                    if (x >= topLeftTileX && x < topLeftTileX + visible_width && y >= topLeftTileY && y < topLeftTileY + visible_width) {
                        if (distanceToTarget[x][y] <= max && distanceToTarget[x][y] != -1) {
                            g.setColor(new Color(0, 0, 255));
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            g.setColor(new Color(0, 0, 0));
                            g.drawString(Integer.toString(distanceToTarget[x][y]), x * TILE_WIDTH, y * TILE_HEIGHT + 30);
                        }
                    }
                }
            }
        }
    }

    private void resetDistance(){
        for (int x = 0; x < distanceToTarget.length; x++) {
            for (int y = 0; y < distanceToTarget[x].length; y++) {
                distanceToTarget[x][y] = -1;
            }
        }
    }

    private void generateDistance(int startX, int startY) {
        int max = selectedUnit.getMovementLeft();
        resetDistance();
        pathQueue = new LinkedList<>();
        pathQueue.add(new Tile(startX, startY));
        distanceToTarget[startX][startY] = 0;
        while (!pathQueue.isEmpty()) {
            Tile top = pathQueue.peek();
            int x = top.getPosX();
            int y = top.getPosY();
            if (x > 0 && x < board_width - 1) {
                if (y > 0 && y < board_height - 1) {
                    if (distanceToTarget[x][y] < max) {
                        if (obstacles[x + 1][y]) {
                            distanceToTarget[x + 1][y] = 99;
                        } else {
                            if (distanceToTarget[x + 1][y] == -1) {
                                distanceToTarget[x + 1][y] = distanceToTarget[x][y] + 1;
                                pathQueue.add(new Tile(x + 1, y));
                            }
                        }
                        if (obstacles[x - 1][y]) {
                            distanceToTarget[x - 1][y] = 99;
                        } else {
                            if (distanceToTarget[x - 1][y] == -1) {
                                distanceToTarget[x - 1][y] = distanceToTarget[x][y] + 1;
                                pathQueue.add(new Tile(x - 1, y));
                            }
                        }
                        if (obstacles[x][y + 1]) {
                            distanceToTarget[x][y + 1] = 99;
                        } else {
                            if (distanceToTarget[x][y + 1] == -1) {
                                distanceToTarget[x][y + 1] = distanceToTarget[x][y] + 1;
                                pathQueue.add(new Tile(x, y + 1));
                            }
                        }
                        if (obstacles[x][y - 1]) {
                            distanceToTarget[x][y - 1] = 99;
                        } else {
                            if (distanceToTarget[x][y - 1] == -1) {
                                distanceToTarget[x][y - 1] = distanceToTarget[x][y] + 1;
                                pathQueue.add(new Tile(x, y - 1));
                            }
                        }
                    }
                }
            }
            pathQueue.pop();
        }
    }

    private void generatePath(int startX, int startY, int targetX, int targetY) {
        int length = getDistance(new Tile(startX, startY));
        int dist = length;
        path = new Stack<>();
        arrows = new int[length];
        Tile currentTile = new Tile(startX, startY);
        while (dist > 0) {
            path.add(currentTile);
            Tile bestTile = currentTile;
            for (Tile t : getNeighbourTiles(currentTile)) {
                if (getDistance(t) != -1 && getDistance(t) < getDistance(bestTile)) {
                    bestTile = t;
                }
            }
            currentTile = bestTile;
            dist = getDistance(bestTile);
        }
        generateArrows();
    }

    private void generateArrows() {
        if (path.size() > 0) {
            int i = 0;
            Direction lastDirection = getDirection(new Tile(selectedUnit.getPosX(), selectedUnit.getPosY()), path.peek());
            for (int j = path.size() - 1; j >= 0; j--, i++) {
                if (j == 0) {
                    switch (lastDirection) {
                        case TOP:
                            arrows[i] = 2;
                            break;
                        case RIGHT:
                            arrows[i] = 3;
                            break;
                        case BOTTOM:
                            arrows[i] = 4;
                            break;
                        case LEFT:
                            arrows[i] = 5;
                            break;
                    }
                } else {
                    Direction direction = getDirection(path.get(j), path.get(j - 1));
                    if (lastDirection == direction) {
                        if (direction == Direction.TOP || direction == Direction.BOTTOM) {
                            arrows[i] = 0;
                        } else {
                            arrows[i] = 1;
                        }
                    } else {
                        switch (lastDirection) {
                            case TOP:
                                switch (direction) {
                                    case RIGHT:
                                        arrows[i] = 8;
                                        break;
                                    case LEFT:
                                        arrows[i] = 7;
                                        break;
                                }
                                break;
                            case RIGHT:
                                switch (direction) {
                                    case TOP:
                                        arrows[i] = 9;
                                        break;
                                    case BOTTOM:
                                        arrows[i] = 7;
                                        break;
                                }
                                break;
                            case BOTTOM:
                                switch (direction) {
                                    case RIGHT:
                                        arrows[i] = 6;
                                        break;
                                    case LEFT:
                                        arrows[i] = 9;
                                        break;
                                }
                                break;
                            case LEFT:
                                switch (direction) {
                                    case TOP:
                                        arrows[i] = 6;
                                        break;
                                    case BOTTOM:
                                        arrows[i] = 8;
                                        break;
                                }
                                break;
                        }
                    }
                    lastDirection = direction;
                }
            }
        }
    }

    private void drawArrows(Graphics g) {
        if (arrows != null && arrows.length > 0) {
            int j = path.size()-1;
            for (int i = arrows.length-path.size(); i < arrows.length; i++) {
                Tile t = path.get(j);
                g.drawImage(ResourceManager.getArrow(TILE_WIDTH, TILE_HEIGHT, arrows[i]), t.getPosX()*TILE_WIDTH, t.getPosY()*TILE_HEIGHT, null);
                j--;
            }
        }
    }

    private int getDistance(Tile t) {
        return distanceToTarget[t.getPosX()][t.getPosY()];
    }

    private ArrayList<Tile> getNeighbourTiles(Tile t) {
        ArrayList<Tile> val = new ArrayList<>();
        int x = t.getPosX();
        int y = t.getPosY();
        if (x < board_width - 1) {
            val.add(new Tile(x + 1, y));
        }
        if (x > 0) {
            val.add(new Tile(x - 1, y));
        }
        if (y < board_height - 1) {
            val.add(new Tile(x, y + 1));
        }
        if (y > 0) {
            val.add(new Tile(x, y - 1));
        }
        return val;
    }

    private void moveUnit(Unit u) {
        u.setState(UnitState.MOVING);
        u.setMovedThisRound(true);
    }

    private void resetUnitPos(Unit u) {
        u.setState(UnitState.IDLE);
        u.setMovedThisRound(false);
        u.setPosX(u.getLastPosX());
        u.setPosY(u.getLastPosY());
    }

    private Direction getDirection(Tile start, Tile end) {
        int x1 = start.getPosX();
        int y1 = start.getPosY();
        int x2 = end.getPosX();
        int y2 = end.getPosY();
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
        selectedUnit = null;
        resetDistance();
        round ++;
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

    private void drawUnitInfoPanel(Graphics g) {

    }

    private void drawActionMenu(Graphics g){
        actionMenuSize = 2;
        if(enemyInRange()){
            actionMenuSize = 3;
        }
        int x = path.get(0).getPosX();
        int y = path.get(0).getPosY();
        if(x-topLeftTileX>=visible_width-3){
            actionMenuPosX = (x-(actionMenuWidth/TILE_WIDTH)) * TILE_WIDTH;
        } else {
            actionMenuPosX = (x+1) * TILE_WIDTH;
        }
        if(y-topLeftTileY==0){
            actionMenuPosY = y * TILE_HEIGHT;
        } else {
            if(y-topLeftTileY>=visible_height-actionMenuSize+1){
                System.out.println("moin");
                actionMenuPosY = (visible_height-actionMenuSize) * TILE_HEIGHT;
            } else {
                actionMenuPosY = (y - 1) * TILE_HEIGHT;
            }
        }
        g.drawImage(ResourceManager.getActionMenuBackground(actionMenuWidth, actionMenuLineHeight, actionMenuSize), actionMenuPosX,actionMenuPosY, null);

        if(selectedMenuPoint!=-1){
            g.setColor(new Color(255,255,255, 100));
            g.fillRect(actionMenuPosX, actionMenuPosY+actionMenuLineHeight*selectedMenuPoint, actionMenuWidth, actionMenuLineHeight);
        }
        ArrayList<String> text = TextManager.getActionMenuTexts();
        g.setColor(new Color(0,0,0));
        int width = g.getFontMetrics().stringWidth(text.get(0));
        int height = g.getFontMetrics().getHeight();
        g.drawString(text.get(0), actionMenuPosX + (actionMenuWidth-width)/2, actionMenuPosY + actionMenuLineHeight - (actionMenuLineHeight-height)/2);
        width = g.getFontMetrics().stringWidth(text.get(text.size()-1));
        g.drawString(text.get(text.size()-1), actionMenuPosX + (actionMenuWidth-width)/2, actionMenuPosY + (actionMenuLineHeight*(text.size()-1)) - (actionMenuLineHeight-height)/2);
    }

    private boolean enemyInRange(){
        return false;
    }
}
