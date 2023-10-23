package invaders.observer;

import invaders.engine.GameWindow;

// contains basic methods that all GameObjects must implement
public interface Subscriber{

    public void update(GameWindow window);

    public String getDisplayString();

}
