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
import invaders.factory.EnemyProjectile;
import invaders.physics.Vector2D;
import invaders.memento.Memento;
import invaders.observer.ScoreObserver;
import invaders.observer.Observer;
import invaders.observer.TimeObserver;
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

	private List<Enemy> killedAlienList = new ArrayList<Enemy>();
	private Caretaker caretaker = new Caretaker();

	private boolean isGameWon;
	private boolean aliensReachBottom = false;
	private boolean aliensTouchPlayer = false;

	private List<Observer> observers = new ArrayList<>();

	public GameEngine(String config){
		// Read the config here
		ConfigReader configReader = ConfigReader.getInstance();
		configReader.parse(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) configReader.getGameInfo().get("size")).get("x")).intValue();
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
        setStartTime(System.currentTimeMillis());
		attach((Observer) new TimeObserver());
		attach((Observer) new ScoreObserver());
		notifyObservers();
	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){
		notifyObservers();
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
						if (renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("Player") ||
						renderableA.getRenderableObjectName().equals("Player") && renderableB.getRenderableObjectName().equals("Enemy")){
							aliensTouchPlayer = true;
						} else {
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
				if (ro instanceof Enemy){
					aliensReachBottom = true;
				}
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

	public long getStartTime(){ return this.startTime; }

	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	public void setScore(int score){ this.score = score; }

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

	public void removeAllProjectiles(){
		for (GameObject go: gameObjects){
			if (go instanceof EnemyProjectile){
				EnemyProjectile p = (EnemyProjectile) go;
				p.takeDamage(1); 
			}
		}
	}

	public void saveMemento(){
		Memento m = new Memento();
		Map<Enemy, Vector2D> alienPositionMap = new HashMap<Enemy, Vector2D>();
		Map<Enemy, List<Vector2D>> projectilesPositionMap = new HashMap<Enemy, List<Vector2D>>();
		for (GameObject go: gameObjects){
			if (go instanceof Enemy){
				Enemy e = (Enemy) go;
				if (!killedAlienList.contains(e)){
					alienPositionMap.put(e, new Vector2D(e.getPosition().getX(), e.getPosition().getY()));
					projectilesPositionMap.put(e, e.getProjectilesPositionsList());
				}
			}
		}
		m.updateMemento(score, getElapsedTime(), alienPositionMap, projectilesPositionMap);
		this.caretaker.setMemento(m);
	}

	public double getElapsedTime() {
        // Calculate and return the elapsed time since the game started
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - startTime;
        double elapsedTimeSeconds = (double) elapsedTimeMillis / 1000.0;
        return elapsedTimeSeconds;
    }

	public List<Enemy> makeAlienList(){
		List<Enemy> enemyList = new ArrayList<Enemy>();
		for (GameObject go: gameObjects){
			if (go instanceof Enemy){
				enemyList.add((Enemy) go);
			}
		}
		return enemyList;
	}

	public void restoreAlienProjectiles(List<Vector2D> positions, Enemy e){
		e.clearProjectiles(this);
		e.addProjectiles(this, positions);
	}

	public void restoreAliens(Map<Enemy, Vector2D> alienPositionMap, Map<Enemy, List<Vector2D>> alienProjectilesPositionMap){
		List<Enemy> enemyList = makeAlienList();
		for (Enemy e: alienPositionMap.keySet()){
			if (enemyList.contains(e)){
				e.resetPosition(alienPositionMap.get(e).getX(), alienPositionMap.get(e).getY());
				e.setLives(1);
				restoreAlienProjectiles(alienProjectilesPositionMap.get(e), e);
			} else if (killedAlienList.contains(e)) {
				killedAlienList.remove(e);
				gameObjects.add((GameObject) e);
				renderables.add((Renderable) e);
				e.resetPosition(alienPositionMap.get(e).getX(), alienPositionMap.get(e).getY());
				e.setLives(1);
				restoreAlienProjectiles(alienProjectilesPositionMap.get(e), e);
			} 
		}
	}

	public boolean aliensKilled(){
		for (GameObject go: gameObjects){
			if (go instanceof Enemy){
				Enemy e = (Enemy) go;
				if (e.isAlive()){
					return false;
				}
			}
		}
		return true;
	}

	public boolean isGameOver(){
		if (aliensKilled()){
			this.isGameWon = true;
			return true;
		} else if (aliensReachBottom || !player.isAlive() || aliensTouchPlayer){
			this.isGameWon = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean getIsGameWon(){ return this.isGameWon; }

	public void restoreMemento(){
		Memento m = this.caretaker.getMemento();
        if (m != null){
            setScore(m.getScore());
            setStartTime(getNewStartTime(m.getTime()));      
            removeAllProjectiles();      
            restoreAliens(m.getAlienPositionMap(), m.getAlienProjectilesPositionMap());
        }
	}

    public long getNewStartTime(double elapsedTime) {
        long currentTime = System.currentTimeMillis();
        long newStartTime = currentTime - (long) (elapsedTime * 1000.0);
        return newStartTime;
    }

	public List<Observer> getObservers(){ return this.observers; }

	public void attach(Observer observer){
		observers.add(observer);
	}

	public void notifyObservers(){
		for (Observer observer: observers){
			observer.update(this);
		}
	}
}


