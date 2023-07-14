// Java package declaration for the example Java game
package javagame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Asteroid_Generators {
    private final int maxAsteroids = 100;
    public Random rnd = new Random();
    private final Polygon[] asteroids = new Polygon[maxAsteroids];
    private final double[] directionX = new double[maxAsteroids];
    private final double[] directionY = new double[maxAsteroids];

    // Add Pane as an instance variable
    private final Pane pane;

    private Level currentLevel;

    public Asteroid_Generators(Stage stage, Scene scene, Pane pane) {
        // Assign the passed pane to the instance variable
        this.pane = pane;


        // Initialize the current level
        currentLevel = new Level(1);
        currentLevel.handleLevel(this);

        // Position the level text
        currentLevel.getLevelText().setLayoutX(10);
        currentLevel.getLevelText().setLayoutY(50);

        // Add the level text to the pane
        pane.getChildren().add(currentLevel.getLevelText());

        // Create a Duration object to define the duration between frames
        Duration duration = Duration.seconds(0.01);

        // Define the minimum and maximum coordinates for the asteroids
        double minX = -140;
        double maxX = scene.getWidth() + 120;
        double minY = -140;
        double maxY = scene.getHeight() + 120;

        // Create a time line object for animating the asteroids
        Timeline timeline = new Timeline(
                // Create a KeyFrame object to define the actions for each frame
                new KeyFrame(duration, e -> {

                    // Loop through the asteroids
                    for (int j = 0; j < asteroids.length; j++) {

                        // Get the current asteroid
                        Polygon asteroid = asteroids[j];

                        //ensures asteroids dont freeze if one is destroyed
                        if (asteroid == null) {
                            continue;
                        }

                        // Calculate the new X and Y positions of the asteroid
                        double newX = asteroid.getLayoutX() + directionX[j];
                        double newY = asteroid.getLayoutY() + directionY[j];

                        // Check if the asteroid is outside the scene bounds in the X direction
                        if (newX < minX) {
                            asteroid.setLayoutX(maxX);
                        } else if (newX > maxX) {
                            asteroid.setLayoutX(minX);
                        } else {
                            asteroid.setLayoutX(newX);
                        }

                        // Check if the asteroid is outside the scene bounds in the Y direction
                        if (newY < minY) {
                            asteroid.setLayoutY(maxY);
                        } else if (newY > maxY) {
                            asteroid.setLayoutY(minY);
                        } else {
                            asteroid.setLayoutY(newY);
                        }

                        // Rotate the asteroid
                        asteroid.setRotate(asteroid.getRotate() + rnd.nextDouble());
                    }

                    if (currentLevel.checkLevelCleared(this, pane)) {
                        Text oldLevelText = currentLevel.getLevelText();
                        currentLevel = new Level(currentLevel.getLevel() + 1);
                        currentLevel.handleLevel(this);

                        // Remove old level text and add new level text
                        pane.getChildren().remove(oldLevelText);
                        currentLevel.getLevelText().setLayoutX(10);
                        currentLevel.getLevelText().setLayoutY(50);

                        // Add the new level text to the pane
                        pane.getChildren().add(currentLevel.getLevelText());
                    }
                })
        );

        // Set the timeline to loop indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Start the timeline animation
        timeline.play();
    }

    // Method to create an asteroid with given size, position, and direction
    private Polygon createAsteroid(Size size, double x, double y, double dx, double dy) {
        Polygon asteroid = new Polygon();
        asteroid.getPoints().addAll(
                50.0, 0.0,
                70.0, 10.0,
                100.0, 30.0,
                120.0, 60.0,
                100.0, 100.0,
                70.0, 120.0,
                30.0, 100.0,
                0.0, 70.0,
                0.0, 40.0,
                20.0, 20.0);
        double scale = size.getScale();
        asteroid.setScaleX(scale);
        asteroid.setScaleY(scale);
        asteroid.setLayoutX(x);
        asteroid.setLayoutY(y);

        return asteroid;
    }

    // Add a getter method for the asteroids array
    public Polygon[] getAsteroids() {
        return asteroids;
    }

    // Method to add a new asteroid to the game
    public void addAsteroid(Size size, double x, double y, double dx, double dy) {
        for (int i = 0; i < maxAsteroids; i++) {
            if (asteroids[i] == null) {
                Polygon asteroid = createAsteroid(size, x, y, dx, dy);
                asteroids[i] = asteroid;
                // Add the new asteroid to the pane only if it's not already a child of the pane
                if (!pane.getChildren().contains(asteroid)) {
                    pane.getChildren().add(asteroid);
                }
                // Assign asteroid speed based on size
                if (size == Size.LARGE) { // Large asteroid
                    directionX[i] = rnd.nextDouble() * 3 - 1.5; // Random value between -1.5 and 1.5
                    directionY[i] = rnd.nextDouble() * 3 - 1.5; // Random value between -1.5 and 1.5
                } else if (size == Size.MEDIUM) { // Medium asteroid
                    directionX[i] = rnd.nextDouble() * 5 - 2.5; // Random value between -2.5 and 2.5
                    directionY[i] = rnd.nextDouble() * 5 - 2.5; // Random value between -2.5 and 2.5
                } else { // Small asteroid
                    directionX[i] = rnd.nextDouble() * 7 - 3.5; // Random value between -3.5 and 3.5
                    directionY[i] = rnd.nextDouble() * 7 - 3.5; // Random value between -3.5 and 3.5
                }
                break;
            }
        }
    }

    public Pane getPane() {
        return pane;
    }

    public enum Size {
        LARGE(1.5),
        MEDIUM(1.0),
        SMALL(0.5);

        private final double scale;

        Size(double scale) {
            this.scale = scale;
        }

        public static Size fromScale(double scale) {
            for (Size size : Size.values()) {
                if (size.getScale() == scale) {
                    return size;
                }
            }
            return null;
        }

        public double getScale() {
            return scale;
        }

    }
}