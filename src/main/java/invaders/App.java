package invaders;

import javafx.application.Application;
import javafx.stage.Stage;
import invaders.engine.GameEngine;
import invaders.engine.GameWindow;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.util.Map;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create buttons for different difficulty levels
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");

        easyButton.setTranslateX(20); // X position
        easyButton.setTranslateY(10); // Y position
        mediumButton.setTranslateX(70); // X position
        mediumButton.setTranslateY(10); // Y position
        hardButton.setTranslateX(120); // X position
        hardButton.setTranslateY(10); // Y position

        // Set actions for the buttons to start the game with the selected difficulty
        easyButton.setOnAction(e -> startGame(primaryStage, "src/main/resources/config_easy.json"));
        mediumButton.setOnAction(e -> startGame(primaryStage, "src/main/resources/config_medium.json"));
        hardButton.setOnAction(e -> startGame(primaryStage, "src/main/resources/config_hard.json"));

        // Create a layout for the difficulty selection screen
        HBox buttonBox = new HBox(3);
        buttonBox.getChildren().addAll(easyButton, mediumButton, hardButton);

        // Set background color to black using CSS style
        buttonBox.setStyle("-fx-background-color: black;");

        // Create a scene for the difficulty selection screen
        Scene scene = new Scene(buttonBox, 300, 50);

        // Create a scene for the difficulty selection screen
        // Scene scene = new Scene(layout, 300, 200);
        // VBox layout = new VBox(10);
        // layout.getChildren().addAll(easyButton, mediumButton, hardButton);

        primaryStage.setTitle("Select Difficulty");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startGame(Stage primaryStage, String configString){
        GameEngine model = new GameEngine(configString);
        GameWindow window = new GameWindow(model);
        window.run();

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(window.getScene());
        primaryStage.show();
        window.run();
    }
}
