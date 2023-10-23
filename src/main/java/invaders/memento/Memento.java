package invaders.memento;

import invaders.physics.Vector2D;
import java.util.Map;
import java.util.HashMap;
import invaders.engine.GameWindow;
import invaders.gameobject.Enemy;
import invaders.factory.EnemyProjectile;

public class Memento {

    private int score = 0;
    private double time = 0;
    private Map<Enemy, Vector2D> alienPositionList = new HashMap<Enemy, Vector2D>();
    private Map<EnemyProjectile, Vector2D> alienProjectilePositionList = new HashMap<EnemyProjectile, Vector2D>();

    public Memento(){}

    public void updateMemento(int score, double time, Map<Enemy, Vector2D> alienPositionList, Map<EnemyProjectile, Vector2D> alienProjectilePositionList){
        this.score = score;
        this.time = time;
        this.alienPositionList = alienPositionList;
        this.alienProjectilePositionList = alienProjectilePositionList;
    }

    public void restore(GameWindow model){
        System.out.println("Restoring previous saved state");
    }
    
}