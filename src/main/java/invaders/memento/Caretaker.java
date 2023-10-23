package invaders.memento;

import invaders.engine.GameWindow;

public class Caretaker {

    private Memento memento = new Memento();
    private GameWindow window;

    public Caretaker(GameWindow window){
        this.window = window;
    }

    public void restore(){

    }

    public void saveMemento(){
    }

}