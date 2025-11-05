package fr.arnaud.aetherialdrift.game.state;

public interface GameState {

    void onEnter();

    void onUpdate(long elapsedTime);

    void onExit();

    String getName();
}