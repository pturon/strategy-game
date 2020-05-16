package Main;

import Game.GameBoard;

public class Main {
    public static void main(String[] args){
        GameFrame frame = new GameFrame();
        frame.setView(new GameBoard(500,500));
        GraphicsManager manager = new GraphicsManager(frame.getViewport());
        ResourceManager.load();
        TextManager.init();
        manager.start();
    }
}
