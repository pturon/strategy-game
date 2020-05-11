package Game;

public class Unit {

    private String name;
    private int spritesheetPos = -1;

    private int posX;
    private int posY;

    private int movementRange = 4;
    private int attackRange = 1;

    public Unit(String name){
        this.name = name;
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
}
