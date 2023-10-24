package invaders.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.rendering.Renderable;
import org.json.simple.JSONObject;
import invaders.singleton.DifficultyManager;
import invaders.factory.EnemyProjectile;
import invaders.physics.Vector2D;
import invaders.memento.Memento;
import invaders.memento.Caretaker;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();

	private Player player;

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;
	private int score = 0;
	private long startTime;

	private DifficultyManager levelManager = new DifficultyManager();
	private ConfigReader configReader;

	private List<Enemy> killedAlienList = new ArrayList<Enemy>();
	private Caretaker caretaker = new Caretaker(this);

	public GameEngine(String config){
		// Read the config here

		this.levelManager.changeInstance(config);
		this.configReader = levelManager.getInstance();

		// Get game width and height
		gameWidth = ((Long)((JSONObject) this.configReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) configReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(configReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:configReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}


		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:configReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){
		timer+=1;

		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				if((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						||(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))||
						(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);
						saveKilled(renderableA);
						saveKilled(renderableB);
						calculateScore(renderableA);
						calculateScore(renderableB);
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}

	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			this.saveMemento();
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	public void calculateScore(Renderable ro){
		String name = ro.getRenderableObjectName();
		if (name.equals("Enemy")){
			Enemy e = (Enemy) ro;
			String strategyName = e.getProjectileStrategyName();
			if (strategyName.equals("Slow")){
				updateScore(3);
			} else if (strategyName.equals("Fast")){
				updateScore(4);
			}
		} else if (name.equals("EnemyProjectile")){
			EnemyProjectile e = (EnemyProjectile) ro;
			String strategyName = e.getProjectileStrategyName();
			if (strategyName.equals("Slow")){
				updateScore(1);
			} else if (strategyName.equals("Fast")){
				updateScore(2);
			}
		} 
	}

	public void updateScore(int amount){
		this.score += amount;
	}

	public int getScore(){ return this.score; }

	public void saveKilled(Renderable ro){
		if (ro instanceof Enemy){
			this.killedAlienList.add((Enemy) ro);
		} 
	}

	public void restoreAlienProjectiles(List<Vector2D> positions, Enemy e){
		e.clearProjectiles(this);
		e.addProjectiles(this, positions);
	}

	public List<EnemyProjectile> makeSlowProjectilesList(){
		List<EnemyProjectile> projectiles = new ArrayList<EnemyProjectile>();
		for (GameObject go: gameObjects){
			if (go instanceof EnemyProjectile){
				EnemyProjectile e = (EnemyProjectile) go;
				if (e.getProjectileStrategyName().equals("Slow")){
					projectiles.add(e);
				}
			}
		}
		return projectiles;
	}

	public List<EnemyProjectile> makeFastProjectilesList(){
		List<EnemyProjectile> projectiles = new ArrayList<EnemyProjectile>();
		for (GameObject go: gameObjects){
			if (go instanceof EnemyProjectile){
				EnemyProjectile e = (EnemyProjectile) go;
				if (e.getProjectileStrategyName().equals("Fast")){
					projectiles.add(e);
				}
			}
		}
		return projectiles;
	}

	public void removeSlowProjectiles(){
		List<EnemyProjectile> projectiles = makeSlowProjectilesList();
		for (EnemyProjectile p: projectiles){
			p.takeDamage(1);
		}
	}

	public void removeFastProjectiles(){
		List<EnemyProjectile> projectiles = makeFastProjectilesList();
		for (EnemyProjectile p: projectiles){
			p.takeDamage(1);
		}
	}

	public void saveMemento(){
		Memento m = new Memento();
		Map<Enemy, Vector2D> alienPositionList = new HashMap<Enemy, Vector2D>();
		Map<Enemy, List<Vector2D>> projectilePositionList = new HashMap<Enemy, List<Vector2D>>();
		for (GameObject go: gameObjects){
			if (go instanceof Enemy){
				Enemy e = (Enemy) go;
				alienPositionList.put(e, e.getPosition());
				projectilePositionList.put(e, e.getProjectilePositionsList());
			}
		}
		m.updateMemento(score, getElapsedTime(), alienPositionList, projectilePositionList);
		this.caretaker.saveMemento(m);
		System.out.println("Memento saved");
	}

	public double getElapsedTime() {
        // Calculate and return the elapsed time since the game started
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - startTime;
        double elapsedTimeSeconds = (double) elapsedTimeMillis / 1000.0;
        return elapsedTimeSeconds;
    }

}
