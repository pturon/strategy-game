package Main;

public class Main {
    public static void main(String[] args){
        GameFrame frame = new GameFrame();
        GraphicsManager manager = new GraphicsManager(frame.getViewport());
        manager.start();
    }
}
