package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.ConfigReader;
import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.json.simple.JSONObject;
import invaders.observer.Subscriber;
import invaders.observer.TimeSubscriber;
import invaders.observer.ScoreSubscriber;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

    private long startTime;

    private Subscriber timeSubscriber = new TimeSubscriber();
    private Subscriber scoreSubscriber = new ScoreSubscriber();

    private Text timeText = new Text();

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

        timeText = new Text("Time: 00:00");
        timeText.setX(10); // Adjust X position as needed
        timeText.setY(30); // Adjust Y position as needed
        timeText.setFill(Color.WHITE);
        Font font = Font.font("Arial", 18); // Font family and font size
        timeText.setFont(font);
        pane.getChildren().add(timeText);
    }

	public void run() {
        this.startTime = System.currentTimeMillis();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> {
            this.timeSubscriber.update(this);
            this.updateTime();
            this.draw();
            System.out.println(this.getTimeDisplay());
        }));
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

    public double getElapsedTime() {
        // Calculate and return the elapsed time since the game started
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - startTime;
        double elapsedTimeSeconds = (double) elapsedTimeMillis / 1000.0;
        return elapsedTimeSeconds;
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

}
