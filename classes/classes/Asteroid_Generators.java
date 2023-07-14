// Java package declaration for the example Java game
package com.example.javagame;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import javafx.animation.Timeline;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class Asteroid_Generators extends Main {
    public Random rnd = new Random();


    // Add a Text object to display the score
    private int maxAsteroids = 10;
    private Polygon[] asteroids = new Polygon[maxAsteroids];
    private double[] directionX = new double[maxAsteroids];
    private double[] directionY = new double[maxAsteroids];

    // Add Pane as an instance variable
    private Pane pane;

    public Asteroid_Generators(Stage stage, Scene scene, Pane pane) {
        // Assign the passed pane to the instance variable
        this.pane = pane;





        // Create initial asteroids
        addAsteroid(1.5, 50, 50, rnd.nextDouble(), rnd.nextDouble());
        addAsteroid(1.0, 250, 50, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
        addAsteroid(0.5, 450, 50, rnd.nextDouble() + 2, rnd.nextDouble() + 2);



        // Create a Duration object to define the duration between frames
        Duration duration = Duration.seconds(0.01);

        // Define the minimum and maximum coordinates for the asteroids
        double minX = 0;
        double maxX = scene.getWidth();
        double minY = 0;
        double maxY = scene.getHeight();

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
                })
        );

        // Set the timeline to loop indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Start the timeline animation
        timeline.play();
    }



    // Method to create an asteroid with given size, position, and direction
    private Polygon createAsteroid(double size, double x, double y, double dx, double dy) {
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

        asteroid.setScaleX(size);
        asteroid.setScaleY(size);
        asteroid.setLayoutX(x);
        asteroid.setLayoutY(y);

        return asteroid;
    }

    // Add a getter method for the asteroids array
    public Polygon[] getAsteroids() {
        return asteroids;
    }

    // Method to add a new asteroid to the game
    public void addAsteroid(double size, double x, double y, double dx, double dy) {
        for (int i = 0; i < maxAsteroids; i++) {
            if (asteroids[i] == null) {
                Polygon asteroid = createAsteroid(size, x, y, dx, dy);
                asteroids[i] = asteroid;
                // Add the new asteroid to the pane only if it's not already a child of the pane
                if (!pane.getChildren().contains(asteroid)) {
                    pane.getChildren().add(asteroid);
                }
                // Assign asteroid speed based on size
                if (size == 1.5) { // Large asteroid
                    directionX[i] = rnd.nextDouble() * 3 - 1.5; // Random value between -1.5 and 1.5
                    directionY[i] = rnd.nextDouble() * 3 - 1.5; // Random value between -1.5 and 1.5
                } else if (size == 1.0) { // Medium asteroid
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




    // Method to remove an asteroid from the game
    public void removeAsteroid(Polygon asteroid) {
        for (int i = 0; i < maxAsteroids; i++) {
            if (asteroids[i] == asteroid) {
                double currentSize = asteroid.getScaleX();
                double x = asteroid.getLayoutX();
                double y = asteroid.getLayoutY();


                // Remove the asteroid
                asteroids[i] = null;
                directionX[i] = 0;
                directionY[i] = 0;
                pane.getChildren().remove(asteroid);

                // Split the asteroid if it's large or medium
                if (currentSize == 1.5) { // Large asteroid
                    addAsteroid(1.0, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                    addAsteroid(1.0, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                } else if (currentSize == 1.0) { // Medium asteroid
                    addAsteroid(0.5, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                    addAsteroid(0.5, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                }
                break;
            }
        }
    }



}