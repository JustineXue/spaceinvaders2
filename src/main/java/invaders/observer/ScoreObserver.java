package invaders.observer;

import invaders.engine.GameEngine;

public class ScoreObserver implements Observer {

    private int score = 0;
    private String displayScore;

    @Override
    public void update(GameEngine model) {
        score = model.getScore();
        this.displayScore = "Score: " + score;
    }

    @Override
    public String getDisplayString(){
        return this.displayScore;
    }
}