package Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

    public static final String FOLDER = "res/";
    public static final int SPRITE_WIDTH = 32;
    public static final int SPRITE_HEIGHT = 48;

    private static BufferedImage background;
    private static ArrayList<BufferedImage> unitSprites = new ArrayList<>();
    private static ArrayList<BufferedImage> unitAttackSprites = new ArrayList<>();

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

    public static int addSpritesheet(String fileName){
        try{
            unitSprites.add(ImageIO.read(new File(FOLDER+fileName+".png")));
            return unitSprites.size()-1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static BufferedImage getSpriteAnimationFrame(int unit, int frame) {
        return unitSprites.get(unit).getSubimage(frame*SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
    }


}
