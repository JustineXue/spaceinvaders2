package invaders.memento;

import invaders.engine.GameEngine;

public class Caretaker {

    private Memento memento = new Memento();
    private GameEngine model;

    public Caretaker(GameEngine model){
        this.model = model;
    }

    public void restore(){

    }

    public void saveMemento(Memento m){
        this.memento = memento;
    }

}