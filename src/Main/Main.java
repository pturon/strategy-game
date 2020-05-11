package Main;

import Game.GameBoard;

public class Main {
    public static void main(String[] args){
        GameFrame frame = new GameFrame();
        GraphicsManager manager = new GraphicsManager(frame.getViewport());
        frame.setView(new GameBoard(5,5));
        manager.start();
    }
}
