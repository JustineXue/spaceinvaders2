package invaders.observer;

import invaders.engine.GameEngine;

public interface Observer{

    public void update(GameEngine model);

    public String getDisplayString();

}
