package invaders.observer;

import invaders.engine.GameWindow;
import invaders.engine.GameEngine;

public class ScoreSubscriber implements Subscriber {

    private int score = 0;
    private String displayScore;

    @Override
    public void update(GameWindow window) {
        GameEngine engine = window.getModel();
        score = engine.getScore();
        this.displayScore = "Score: " + score;
    }

    @Override
    public String getDisplayString(){
        return this.displayScore;
    }
}