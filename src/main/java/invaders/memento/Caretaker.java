package invaders.memento;

import java.util.Map;
import java.util.List;
import invaders.engine.GameEngine;
import invaders.physics.Vector2D;
import invaders.gameobject.Enemy;

public class Caretaker {

    private Memento memento;
    private GameEngine model;

    public Caretaker(GameEngine model){
        this.model = model;
    }

    public void restore(){
        if (memento == null){
            System.out.println("No memento stored");
        } else {
            System.out.println("Restoring");
            checkMemento();
        }
    }

    public void saveMemento(Memento m){
        this.memento = m;
        // checkMemento();
    }

    public void checkMemento(){
        System.out.printf("Score is %d%n", this.memento.getScore());
        System.out.printf("Time is %f%n", this.memento.getTime());
        Map<Enemy, Vector2D> alienList = this.memento.getAlienPositionList();
        System.out.printf("There are %d aliens%n", alienList.size());
        int alienCount = 0;
        for (Enemy e: alienList.keySet()){
            alienCount += 1;
            Vector2D position = alienList.get(e);
            System.out.printf("Alien %d has position %f%, f%n", alienCount, position.getX(), position.getY());
        }
        Map<Enemy, List<Vector2D>> alienProjectileList = this.memento.getAlienProjectilePositionList();
        alienCount = 0;
        for (Enemy e: alienProjectileList.keySet()){
            alienCount += 1;
            List<Vector2D> positions = alienProjectileList.get(e);
            System.out.printf("Alien %d has %d projectiles%n", alienCount, positions.size());
            int projectileCount = 0;
            for (Vector2D p: positions){
                projectileCount += 1;
                System.out.printf("Projectile %d has position %f, %f%n", projectileCount, p.getX(), p.getY());
            }
        }
    }

}