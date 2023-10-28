package invaders.memento;

import java.util.Map;
import java.util.List;
import invaders.physics.Vector2D;
import invaders.gameobject.Enemy;

public class Caretaker {

    private Memento memento;

    public Caretaker(){}

    public Memento getMemento(){ return this.memento; }

    public void setMemento(Memento m){this.memento = m; }

}

