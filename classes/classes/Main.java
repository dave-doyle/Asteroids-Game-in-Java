// Java package declaration for the example Java game
package com.example.javagame;

// Importing necessary JavaFX classes for application, scenes, and layouts
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

// Importing necessary Java classes for handling exceptions
import java.io.IOException;

// Main class that extends the JavaFX Application class
public class Main extends Application {

    private Text scoreText = new Text(); // Add this line to declare the scoreText variable

    // Overriding the start method from the Application class
    @Override
    public void start(Stage stage) throws IOException {
        // Create a new Pane layout with preferred dimensions
        Pane pane = new Pane();
        pane.setPrefSize(960, 720);

        // Create a new Scene with the pane layout
        Scene scene = new Scene(pane);

        // Set the stage with the created scene and display it
        stage.setScene(scene);
        stage.show();

        // Create a new Player object with the pane and scene
        Player player = new Player(pane, scene, this);

        // Initialize the Text object to display the player's score
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font("Verdana", 20));
        scoreText.setFill(Color.BLACK);
        scoreText.setX(10);
        scoreText.setY(30);
        pane.getChildren().add(scoreText);


        // Create a new Asteroid_Generators object with the stage, scene, and pane
        Asteroid_Generators asteroidGenerator = new Asteroid_Generators(stage, scene, pane);

        // Call the controlShip method on the player object to enable ship control
        player.controlShip(player, scene, pane, asteroidGenerator);
    }

    public Text getScoreText() {
        return scoreText;
    }


    // Main method that serves as the entry point of the application
    public static void main(String[] args) {
        // Call the launch method from the Application class to start the JavaFX application
        launch();
    }

    public void updateScore() {
        scoreText.setText("Score: " + Player.getScore()); // Use 'Player.getScore()' directly
    }

}
