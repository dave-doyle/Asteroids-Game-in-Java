package javagame;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javagame.High_Scores.ScoreEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class HomePage {
    public static final int SCREEN_HEIGHT = 720;
    private static final int MAX_LINES = 5;
    private static final int SCREEN_WIDTH = 960;
    private static final String FILE_NAME = "highscores.txt";
    private final Scene scene;


    public HomePage(Stage stage) {
        Pane pane = new Pane();
        pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        scene = new Scene(pane);
        stage.setScene(scene);

        // Add a welcome slogan
        Text welcomeText = new Text("Welcome to Asteroid！");
        welcomeText.setFont(Font.font("Courier New", FontWeight.BOLD, 48));
        welcomeText.setX(230);
        welcomeText.setY(250);
        pane.getChildren().add(welcomeText);

        // Add a "Start Game" button
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        startButton.setLayoutX(390);
        startButton.setLayoutY(400);
        startButton.setPrefWidth(200); // Set the width of the button to 200 pixels
        startButton.setPrefHeight(60); // Set the height of the button to 80 pixels
        pane.getChildren().add(startButton);

        startButton.setOnAction(e -> {
            // Create a new page to ask the user to press enter to start the game
            Pane startPage = new Pane();
            startPage.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            Scene startScene = new Scene(startPage);
            stage.setScene(startScene);

            // Add instructions for the user to press enter to start the game
            Text startText = new Text("Press Enter to Start the Game");
            startText.setFont(Font.font("Courier New", FontWeight.BOLD, 33));
            startText.setX(200);
            startText.setY(360);
            startPage.getChildren().add(startText);

            // Add an event handler to start the game when the user presses enter
            startScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    // remove start text
                    startPage.getChildren().remove(startText);
                    // Start the game
                    Player player = new Player(startPage, startScene, new Main(), stage);
                    Level level = new Level(1);
                    Asteroid_Generators asteroid = new Asteroid_Generators(stage, startScene, startPage);
                    player.controlShip(player, startScene, startPage, asteroid, level);
                }
            });
        });

        // Add a "How to Control" button
        Button controlButton = new Button("Control Tips");
        controlButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        controlButton.setLayoutX(390);
        controlButton.setLayoutY(500);
        controlButton.setPrefWidth(200); // Set the width of the button to 200 pixels
        controlButton.setPrefHeight(60); // Set the height of the button to 80 pixels
        pane.getChildren().add(controlButton);

        // Add an event handler to the "How to Control" button
        controlButton.setOnAction(e -> {
            // Create a new page with instructions on how to control the game
            Pane controlPage = new Pane();
            controlPage.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

            Scene controlScene = new Scene(controlPage);
            stage.setScene(controlScene);

            String rules = """
                    Speed Control:
                    Accelerate - press the up arrow key;
                    Decelerate - press the down arrow key.

                    Direction Control:
                    Turn left - press the left + up arrow key;
                    Turn right - press the right + up arrow key.

                    Fire Bullet:
                    Press the space bar.

                    Hyperspace:
                    Press the shift key.

                    !!!Don't crash into the asteroids!!!""";

// Add instructions on how to control the game
            Text controlText = new Text(rules);
            controlText.setFont(Font.font("Courier New", FontWeight.BOLD, 25));
            controlText.setX(200);
            controlText.setY(120);
            controlPage.getChildren().add(controlText);

            // Add a button to return to the home page
            Button backButton = new Button("Back to Home");
            backButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
            backButton.setLayoutX(390);
            backButton.setLayoutY(600);
            backButton.setPrefWidth(200); // Set the width of the button to 200 pixels
            backButton.setPrefHeight(60); // Set the height of the button to 80 pixels
            controlPage.getChildren().add(backButton);

            // Add an event handler to the "Back to Home Page" button
            backButton.setOnAction(event -> stage.setScene(scene));
        });

        // Add a "score rank" button
        Button rankButton = new Button("Score Rank");
        rankButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        rankButton.setLayoutX(390);
        rankButton.setLayoutY(600);
        rankButton.setPrefWidth(200); // Set the width of the button to 200 pixels
        rankButton.setPrefHeight(60); // Set the height of the button to 80 pixels
        pane.getChildren().add(rankButton);

        rankButton.setOnAction(e -> {
            List<ScoreEntry> scoresList = readScoresFromFile();
            scoresList.sort(Collections.reverseOrder()); // Sort in descending order

            BorderPane rankingPane = new BorderPane();
            rankingPane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            Label titleLabel = new Label("Top 5");
            titleLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 50));
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setPrefSize(rankingPane.getPrefWidth(), 250);
            rankingPane.setTop(titleLabel);

            VBox scoreBox = new VBox();
            scoreBox.setAlignment(Pos.CENTER);
            scoreBox.setSpacing(50);
            rankingPane.setCenter(scoreBox);

            for (int i = 0; i < scoresList.size() && i < MAX_LINES; i++) {
                High_Scores.ScoreEntry entry = scoresList.get(i);
                Label scoreLabel = new Label(entry.getPlayerName() + " : " + entry.getScore());
                scoreLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 30));
                scoreLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
                scoreBox.getChildren().add(scoreLabel);
            }

            Scene rankingScene = new Scene(rankingPane);
            stage.setScene(rankingScene);

            Button backButton = new Button("Back to Home");
            backButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
            backButton.setLayoutX(390);
            backButton.setLayoutY(600);
            backButton.setPrefWidth(200); // Set the width of the button to 200 pixels
            backButton.setPrefHeight(60); // Set the height of the button to 80 pixels
            scoreBox.getChildren().add(backButton);
            // Add an event handler to the "Back to Home Page" button
            backButton.setOnAction(event -> stage.setScene(scene));
        });

    }

    private List<High_Scores.ScoreEntry> readScoresFromFile() {
        List<High_Scores.ScoreEntry> scores = new ArrayList<>();
        File scoreFile = new File(FILE_NAME);

        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create scores file: " + e.getMessage());
            }
            return scores; // Return an empty list
        }

        try (Scanner scanner = new Scanner(scoreFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Invalid score entry format: " + line);
                    continue;
                }
                String playerName = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());
                scores.add(new High_Scores.ScoreEntry(playerName, score));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Failed to read scores from file: " + e.getMessage());
        }
        return scores;
    }

    public Scene getScene() {
        return scene;
    }
}