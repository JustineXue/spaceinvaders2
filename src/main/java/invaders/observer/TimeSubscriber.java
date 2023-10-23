package invaders.observer;

import invaders.engine.GameEngine;
import invaders.engine.GameWindow;

public class TimeSubscriber implements Subscriber{

    private double elapsedTime;
    private String displayTime;

    public TimeSubscriber(){
        this.elapsedTime = 0;
    }

    @Override
    public void update(GameWindow window) {
        GameEngine engine = window.getModel();
        this.elapsedTime = engine.getElapsedTime();
        this.displayTime = "Time: " + formatElapsedTime(elapsedTime);
    }

    @Override
    public String getDisplayString(){
        return this.displayTime;
    }

    private String formatElapsedTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

}
