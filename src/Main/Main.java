package Main;

import Game.GameBoard;

public class Main {
    public static void main(String[] args){
        GameFrame frame = new GameFrame();
        frame.setView(new GameBoard(5,5));
        GraphicsManager manager = new GraphicsManager(frame.getViewport());
        manager.start();
    }
}
