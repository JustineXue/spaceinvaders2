package invaders.observer;

import invaders.engine.GameEngine;

public interface Subscriber{

    public void update(GameEngine model);

    public String getDisplayString();

}
