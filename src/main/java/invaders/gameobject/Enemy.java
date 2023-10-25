package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectileFactory;
import invaders.factory.Projectile;
import invaders.factory.ProjectileFactory;
import invaders.physics.Collider;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class Enemy implements GameObject, Renderable {
    private Vector2D position;
    private int lives = 1;
    private Image image;
    private int xVel = -1;

    private ArrayList<Projectile> enemyProjectile;
    private ArrayList<Projectile> pendingToDeleteEnemyProjectile;
    private ProjectileStrategy projectileStrategy;
    private ProjectileFactory projectileFactory;
    private Image projectileImage;
    private Random random = new Random();

    public Enemy(Vector2D position) {
        this.position = position;
        this.projectileFactory = new EnemyProjectileFactory();
        this.enemyProjectile = new ArrayList<>();
        this.pendingToDeleteEnemyProjectile = new ArrayList<>();
    }

    @Override
    public void start() {}

    @Override
    public void update(GameEngine engine) {
        if(enemyProjectile.size()<3){
            if(this.isAlive() &&  random.nextInt(120)==20){
                Projectile p = projectileFactory.createProjectile(new Vector2D(position.getX() + this.image.getWidth() / 2, position.getY() + image.getHeight() + 2),projectileStrategy, projectileImage);
                enemyProjectile.add(p);
                engine.getPendingToAddGameObject().add(p);
                engine.getPendingToAddRenderable().add(p);
            }
        }else{
            pendingToDeleteEnemyProjectile.clear();
            for(Projectile p : enemyProjectile){
                if(!p.isAlive()){
                    engine.getPendingToRemoveGameObject().add(p);
                    engine.getPendingToRemoveRenderable().add(p);
                    pendingToDeleteEnemyProjectile.add(p);
                }
            }

            for(Projectile p: pendingToDeleteEnemyProjectile){
                enemyProjectile.remove(p);
            }
        }

        if(this.position.getX()<=this.image.getWidth() || this.position.getX()>=(engine.getGameWidth()-this.image.getWidth()-1)){
            this.position.setY(this.position.getY()+25);
            xVel*=-1;
        }

        this.position.setX(this.position.getX() + xVel);

        if((this.position.getY()+this.image.getHeight())>=engine.getPlayer().getPosition().getY()){
            engine.getPlayer().takeDamage(Integer.MAX_VALUE);
        }

        /*
        Logic TBD
         */

    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return this.image.getWidth();
    }

    @Override
    public double getHeight() {
       return this.image.getHeight();
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setProjectileImage(Image projectileImage) {
        this.projectileImage = projectileImage;
    }

    @Override
    public void takeDamage(double amount) {
        this.lives-=1;
    }

    @Override
    public double getHealth() {
        return this.lives;
    }

    @Override
    public String getRenderableObjectName() {
        return "Enemy";
    }

    @Override
    public boolean isAlive() {
        return this.lives>0;
    }

    public void setProjectileStrategy(ProjectileStrategy projectileStrategy) {
        this.projectileStrategy = projectileStrategy;
    }

    public String getProjectileStrategyName(){ return this.projectileStrategy.getName(); }

    public void clearProjectiles(GameEngine engine){
        pendingToDeleteEnemyProjectile.clear();
        for(Projectile p : enemyProjectile){
            p.takeDamage(1);
            engine.getPendingToRemoveGameObject().add(p);
            engine.getPendingToRemoveRenderable().add(p);
            pendingToDeleteEnemyProjectile.add(p);
        }
        for(Projectile p: pendingToDeleteEnemyProjectile){
            enemyProjectile.remove(p);
        }
    }

    public void addProjectiles(GameEngine engine, List<Vector2D> positions){
        for (Vector2D po: positions){
            Projectile p = projectileFactory.createProjectile(new Vector2D(po.getX() + this.image.getWidth() / 2, po.getY() + image.getHeight() + 2),projectileStrategy, projectileImage);
            enemyProjectile.add(p);
            engine.getPendingToAddGameObject().add(p);
            engine.getPendingToAddRenderable().add(p);
        }
    }

    public List<Vector2D> getProjectilesPositionsList(){
        List<Vector2D> projectiles = new ArrayList<Vector2D>();
        for (Projectile p: enemyProjectile){
            projectiles.add(new Vector2D(p.getPosition().getX(), p.getPosition().getY()));
        }
        return projectiles;
    }

    public int getXVel(){ return this.xVel; }

    public void resetPosition(double x, double y){
        this.position.setX(x);
        this.position.setY(y);
    }
}
