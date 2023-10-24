package invaders.memento;

import invaders.physics.Vector2D;
import java.util.Map;
import java.util.HashMap;
import invaders.engine.GameWindow;
import invaders.engine.GameEngine;
import invaders.gameobject.Enemy;
import java.util.List;
import java.util.ArrayList;

public class Memento {

    private int score = 0;
    private double time = 0;
    private Map<Enemy, Vector2D> alienPositionList = new HashMap<Enemy, Vector2D>();
    private Map<Enemy, List<Vector2D>> alienProjectilePositionList = new HashMap<Enemy, List<Vector2D>>();

    public Memento(){}

    public void updateMemento(int score, double time, Map<Enemy, Vector2D> alienPositionList, Map<Enemy, List<Vector2D>> alienProjectilePositionList){
        this.score = score;
        this.time = time;
        this.alienPositionList = alienPositionList;
        this.alienProjectilePositionList = alienProjectilePositionList;
    }

    public void restore(GameWindow model, GameEngine engine){
        System.out.println("Restoring previous saved state");
    }

    public int getScore(){ return this.score; }
    public double getTime(){ return this.time; }
    public Map<Enemy, Vector2D> getAlienPositionList(){ return this.alienPositionList; }
    public Map<Enemy, List<Vector2D>> getAlienProjectilePositionList(){ return this.alienProjectilePositionList; }
    
}