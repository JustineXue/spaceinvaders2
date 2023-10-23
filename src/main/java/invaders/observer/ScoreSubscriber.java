package invaders.observer;

import invaders.engine.GameWindow;

public class ScoreSubscriber implements Subscriber {

    private int score = 0;
    private String displayScore;

    @Override
    public void update(GameWindow window) {
        System.out.println("Updating");
        /*
        Logic TBD
         */

    }

    @Override
    public String getDisplayString(){
        return this.displayScore;
    }
}