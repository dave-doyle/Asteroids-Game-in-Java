package javagame;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javagame.Asteroid_Generators.Size;

import java.util.Random;

public class Level {
    private final Random rnd = new Random();
    private final int level;
    private final Text levelText;

    // Constructor for the Level class
    public Level(int level) {
        this.level = level;
        levelText = new Text("Level: " + level);
        levelText.setFont(Font.font("Courier New", 20));
        this.levelText.setFill(Color.BLACK);
        this.levelText.setX(1);
        this.levelText.setY(12);
    }

    // Updates the level text to display the current level
    public void updateLevelText() {
        levelText.setText("Level: " + level);
    }

    // Getter for levelText
    public Text getLevelText() {
        return levelText;
    }

    // Getter for level
    public int getLevel() {
        return level;
    }

    // Handles levels
    public void handleLevel(Asteroid_Generators game) {
        // Clear existing asteroids
        for (int i = 0; i < game.getAsteroids().length; i++) {
            game.getAsteroids()[i] = null;
        }

        double baseDx = rnd.nextDouble();
        double baseDy = rnd.nextDouble();

        generateAsteroids(game, baseDx, baseDy);
    }

    // Levels' logic
    private void generateAsteroids(Asteroid_Generators game, double baseDx, double baseDy) {
        int numAsteroids = level;
        double speedIncreaseFactor = 1 + (level - 1) * 0.1;
        double safeRadius = 500; // Define a safe radius around the player's ship

        // Use the center of the screen as the player's ship position
        Pane pane = game.getPane();
        double playerX = pane.getWidth() / 2;
        double playerY = pane.getHeight() / 2;

        for (int i = 0; i < numAsteroids; i++) {
            Size size = Size.LARGE; // Fixed size of LARGE for all asteroids
            double x, y;
            double distance;

            do {
                // Generate random positions for the asteroid
                x = game.rnd.nextDouble() * (pane.getWidth() - size.getScale() * 50);
                y = game.rnd.nextDouble() * (pane.getHeight() - size.getScale() * 50);
                distance = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2));
            } while (distance < safeRadius); // Ensure the asteroid is generated outside the safe radius

            // Calculating the random velocities of the asteroids
            double dx = (baseDx * 3 - 1.5) * speedIncreaseFactor;
            double dy = (baseDy * 3 - 1.5) * speedIncreaseFactor;

            game.addAsteroid(size, x, y, dx, dy);
        }
    }


    // Checks if the level is cleared by confirming all asteroids are destroyed
    public boolean checkLevelCleared(Asteroid_Generators game, Pane pane) {
        for (Polygon currentAsteroid : game.getAsteroids()) {
            if (currentAsteroid != null) {
                return false;
            }
        }
        pane.getChildren().remove(levelText);
        updateLevelText();
        pane.getChildren().add(levelText);
        return true;
    }
}

