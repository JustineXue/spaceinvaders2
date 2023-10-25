package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import invaders.observer.Subscriber;
import invaders.observer.TimeSubscriber;
import invaders.observer.ScoreSubscriber;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import invaders.gameobject.Enemy;

public class GameWindow {
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private Renderable background;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    // private static final double VIEWPORT_MARGIN = 280.0;

    private Subscriber timeSubscriber = new TimeSubscriber();
    private Subscriber scoreSubscriber = new ScoreSubscriber();

    private Text timeText = new Text();
    private Text scoreText = new Text();
    private Text instructionsText = new Text();
    private Text gameOverText = new Text();

    private boolean gameOverDisplayed = false;

	public GameWindow(GameEngine model){
        this.model = model;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();

        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(model, pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        timeText = new Text(10, 30, "Time: 00:00");
        timeText.setFill(Color.WHITE);
        Font font = Font.font("Arial", 16); 
        timeText.setFont(font);
        pane.getChildren().add(timeText);

        scoreText = new Text(width - 80, 30, "Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(font);
        pane.getChildren().add(scoreText);

        Font fontSmall = Font.font("Arial", 13); 
        instructionsText = new Text(115, 30, "S: Remove Slow Projectiles | F: Remove Fast Projectiles | U: Undo");
        instructionsText.setFill(Color.YELLOW);
        instructionsText.setFont(fontSmall);
        pane.getChildren().add(instructionsText);
    }

	public void run() {
        this.model.setStartTime(System.currentTimeMillis());
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> {
            if (!gameOverDisplayed && this.model.isGameOver()){
                if (this.model.getIsGameWon()){
                    System.out.println("You won");
                    displayGameOverText(true);
                } else {
                    System.out.println("Game over");
                    displayGameOverText(false);
                }
                gameOverDisplayed = true;
            } else if (!gameOverDisplayed) {
                this.timeSubscriber.update(this);
                this.scoreSubscriber.update(this);
                this.draw();
                this.updateTime();
                this.updateScore();
            } 
        }
        ));
         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }


    private void draw(){
        model.update();

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }

        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

	public Scene getScene() {
        return scene;
    }

    public String getTimeDisplay(){
        return this.timeSubscriber.getDisplayString();
    }

    public String getScoreDisplay(){
        return this.scoreSubscriber.getDisplayString();
    }

    public void updateTime(){
        this.timeText.setText(getTimeDisplay());
    }

    public void updateScore(){
        this.scoreText.setText(getScoreDisplay());
    }

    public GameEngine getModel(){ return this.model; }

    public void displayGameOverText(boolean isGameWon){
        if (isGameWon){
            gameOverText = new Text(width/2 - 50, height/2 - 30, "YOU WON");
        } else {
            gameOverText = new Text(width/2 - 50, height/2 - 30, "GAME OVER");
        }
        gameOverText.setFill(Color.WHITE);
        Font font = Font.font("Arial", 24); 
        gameOverText.setFont(font);
        pane.getChildren().add(gameOverText);

        timeText.setX(width/2 - 50);
        timeText.setY(height/2);
        timeText.setFont(font);

        scoreText.setX(width/2 - 50);
        scoreText.setY(height/2 + 30);
        scoreText.setFont(font);

        pane.getChildren().remove(instructionsText);
    }

}
