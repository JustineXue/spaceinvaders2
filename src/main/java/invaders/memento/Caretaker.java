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
            this.model.setScore(this.memento.getScore());
            this.model.setStartTime(getNewStartTime(this.memento.getTime()));      
            this.model.removeAllProjectiles();      
            this.model.restoreAliens(this.memento.getAlienPositionMap(), this.memento.getAlienProjectilesPositionMap());
        }
    }

    public void saveMemento(Memento m){
        this.memento = m;
        checkMemento();
    }

    public void checkMemento(){
        System.out.printf("Score is %d%n", this.memento.getScore());
        System.out.printf("Time is %f%n", this.memento.getTime());
        Map<Enemy, Vector2D> alienList = this.memento.getAlienPositionMap();
        System.out.printf("There are %d aliens%n", alienList.size());
        int alienCount = 0;
        for (Enemy e: alienList.keySet()){
            alienCount += 1;
            Vector2D position = alienList.get(e);
            System.out.printf("Alien %d has xVel %d position %f%, f%n", alienCount, e.getXVel(), position.getX(), position.getY());
        }
        Map<Enemy, List<Vector2D>> alienProjectileList = this.memento.getAlienProjectilesPositionMap();
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

    public long getNewStartTime(double elapsedTime) {
        long currentTime = System.currentTimeMillis();
        long newStartTime = currentTime - (long) (elapsedTime * 1000.0);
        return newStartTime;
    }

}