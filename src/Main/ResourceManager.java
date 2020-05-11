package Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

    public static final String FOLDER = "res/";
    public static final int SPRITE_WIDTH = 32;
    public static final int SPRITE_HEIGHT = 48;

    private static BufferedImage arrows;

    private static BufferedImage background;
    private static ArrayList<BufferedImage> unitIdleSprites = new ArrayList<>();
    private static ArrayList<BufferedImage> unitWalkingSprites = new ArrayList<>();
    private static ArrayList<BufferedImage> unitAttackSprites = new ArrayList<>();

    public static void load(){
        try {
            arrows = ImageIO.read(new File(FOLDER+"arrows.png"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setBackground(String fileName){
        try{
            background = ImageIO.read(new File(FOLDER+fileName+".png"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static BufferedImage getBackground(int x, int y, int width, int height) {
        if(background!=null){
            return background.getSubimage(x,y,width,height);
        }
        return null;
    }

    public static int addIdleSpritesheet(String fileName){
        try{
            unitIdleSprites.add(ImageIO.read(new File(FOLDER+fileName+".png")));
            return unitIdleSprites.size()-1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static BufferedImage getIdleFrame(int unit, int frame) {
        return unitIdleSprites.get(unit).getSubimage(frame*SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    public static int addWalkingSpritesheet(String fileName){
        try{
            unitWalkingSprites.add(ImageIO.read(new File(FOLDER+fileName+"_walking.png")));
            return unitWalkingSprites.size()-1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static BufferedImage getWalkingFrame(int unit, int frame, int direction) {
        return unitWalkingSprites.get(unit).getSubimage(frame*SPRITE_WIDTH, SPRITE_HEIGHT*direction, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    public static BufferedImage getArrow(int width, int height, int type){
        return arrows.getSubimage(width*type,0,width,height);
    }
}
