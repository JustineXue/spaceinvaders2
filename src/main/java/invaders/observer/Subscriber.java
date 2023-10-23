package invaders.observer;

import invaders.engine.GameEngine;

// contains basic methods that all GameObjects must implement
public interface Subscriber {

    public void update(GameEngine model);

}
