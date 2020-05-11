package Game;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Unit {

    private String name;
    private int spritesheetPos = -1;

    private int posX;
    private int posY;

    private int lastPosX;
    private int lastPosY;

    private int offsetX = 0;
    private int offsetY = 0;

    private int movementRange = 4;
    private int attackRange = 1;
    private int movementLeft = movementRange;

    private int walkingAnimationFrame = 0;

    private Queue<Tile> path;

    public Unit(String name){
        this.name = name;
        path = new LinkedList<Tile>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getSpritesheetPos() {
        return spritesheetPos;
    }

    public void setSpritesheetPos(int spritesheetPos) {
        this.spritesheetPos = spritesheetPos;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public void setMovementRange(int movementRange) {
        this.movementRange = movementRange;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public int getMovementLeft() {
        return movementLeft;
    }

    public void setMovementLeft(int movementLeft) {
        this.movementLeft = movementLeft;
    }

    public void addTileToPath(int x, int y){
        path.add(new Tile(x,y));
    }

    public void removeTileFromPath(){
        path.poll();
    }

    public Tile getNextTileFromPath(){
        return path.peek();
    }

    public boolean isMoving(){
        return !path.isEmpty();
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getMovementDirection(){
        if(getNextTileFromPath()==null){
            return -1;
        } else if(posX==getNextTileFromPath().getPosX()&&posY>getNextTileFromPath().getPosY()){
            return 0;
        } else if(posX<getNextTileFromPath().getPosX()&&posY==getNextTileFromPath().getPosY()){
            return 1;
        } else if(posX==getNextTileFromPath().getPosX()&&posY<getNextTileFromPath().getPosY()){
            return 2;
        } else {
            return 3;
        }

    }

    public int getLastPosX() {
        return lastPosX;
    }

    public void setLastPosX(int lastPosX) {
        this.lastPosX = lastPosX;
    }

    public int getLastPosY() {
        return lastPosY;
    }

    public void setLastPosY(int lastPosY) {
        this.lastPosY = lastPosY;
    }

    public int getWalkingAnimationFrame() {
        return walkingAnimationFrame;
    }

    public void setWalkingAnimationFrame(int walkingAnimationFrame) {
        this.walkingAnimationFrame = walkingAnimationFrame;
    }
}
