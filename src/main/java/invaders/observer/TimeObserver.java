package invaders.observer;

import invaders.engine.GameEngine;

public class TimeObserver implements Observer{

    private double elapsedTime;
    private String displayTime;

    public TimeObserver(){
        this.elapsedTime = 0;
    }

    @Override
    public void update(GameEngine model) {
        this.elapsedTime = model.getElapsedTime();
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
