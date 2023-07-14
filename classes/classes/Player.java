// Java package declaration for the example Java game
package com.example.javagame;

import com.example.javagame.Asteroid_Generators;

// Importing necessary JavaFX classes for animations, geometry, and scenes
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.*;


// Importing necessary Java classes for handling collections


// Player class that extends the Main class
public class Player{

    private Main main; // Add this line to declare a reference to the Main class

    private Polygon ship; // Player's ship as a Polygon object
    private Point2D movement; // Movement vector for the player's ship
    private Bullet bullet; // Bullet object for the player's ship

    private Bullet[] bullets; // Array of Bullet objects for the player's ship
    private int bulletLimit = 5; // Maximum number of bullets
    private boolean isReloading = false; // Flag to check if the player is reloading
    private double reloadCooldown = 3; // Cooldown duration in seconds

    private boolean spaceBarPressed = false; // detects when player has fired a bullet

    private boolean shiftKeyPressed = false; // detects if player has used jump

    public Random rnd = new Random(); // used for the jump method so the player appears in a random location

    private static int score = 0; // Add 'static'


    // Player constructor that takes a Pane and Scene as arguments
    public Player(Pane pane, Scene scene, Main main) {
        this.score = 0; // Initialize the player's score to 0

        this.main = main; // Set the reference to the Main class

        // Define the points of the ship's Polygon
        double[] points = {0, -20, 20, -10, 0, 0, 6, -10, 0, -20};
        this.ship = new Polygon(points);
        // Add the ship Polygon to the Pane
        pane.getChildren().add(this.ship);
        // Bind the ship's position to the center of the Scene
        this.ship.layoutXProperty().bind(scene.widthProperty().divide(2));
        this.ship.layoutYProperty().bind(scene.heightProperty().divide(2));
        // Initialize the movement vector to zero
        this.movement = new Point2D(0, 0);

        this.bullets = new Bullet[bulletLimit];
    }

    public void incrementScore(int points) {
        this.score += points;
        main.updateScore();
    }

    public static int getScore() { // Add 'static'
        return score;
    }



    // Method to turn the ship left
    public void turnLeft() {
        this.ship.setRotate(this.ship.getRotate() - 4);
    }

    // Method to turn the ship right
    public void turnRight() {
        this.ship.setRotate(this.ship.getRotate() + 4);
    }

    public void decellerate() {
        this.movement = this.movement.subtract(this.movement.multiply(0.015));
    }

    // Method for the ship to jump forward
    public void jump(Scene scene, Polygon[] asteroids) {
        boolean shipIsSafe = true; // Flag to indicate if the ship's new position is valid (not colliding with any asteroids)
        double newShipX; // New X-coordinate for the ship's position
        double newShipY; // New Y-coordinate for the ship's position

        do {
            shipIsSafe = true; // Assume the new position is valid until proven otherwise

            // Generate a random X-coordinate for the ship's new position within the scene's bounds
            newShipX = rnd.nextDouble() * (scene.getWidth() - this.ship.getBoundsInParent().getWidth()) - scene.getWidth() / 2;
            // Generate a random Y-coordinate for the ship's new position within the scene's bounds
            newShipY = rnd.nextDouble() * (scene.getHeight() - this.ship.getBoundsInParent().getHeight()) - scene.getHeight() / 2;

            // Set the ship's new X-coordinate
            this.ship.setTranslateX(newShipX);
            // Set the ship's new Y-coordinate
            this.ship.setTranslateY(newShipY);

            // Iterate through all asteroids to check for collisions
            for (Polygon asteroid : asteroids) {
                // If the ship's bounding box intersects an asteroid's bounding box, the new position is not valid
                if (this.ship.getBoundsInParent().intersects(asteroid.getBoundsInParent())) {
                    shipIsSafe = false;
                    break;
                }
            }
        } while (!shipIsSafe); // Continue generating new positions until a valid position is found

        this.movement = Point2D.ZERO; // Reset the ship's movement vector to zero
    }



    // Method to update the ship's position based on its movement vector
    public void move() {
        this.ship.setTranslateX(this.ship.getTranslateX() + this.movement.getX());
        this.ship.setTranslateY(this.ship.getTranslateY() + this.movement.getY());

        // Wrap the ship's position if it goes off-screen
        if (this.ship.getTranslateX() < -480) {
            this.ship.setTranslateX(480);
        }
        if (this.ship.getTranslateX() > 480) {
            this.ship.setTranslateX(-480);
        }
        if (this.ship.getTranslateY() < -360) {
            this.ship.setTranslateY(360);
        }
        if (this.ship.getTranslateY() > 360) {
            this.ship.setTranslateY(-360);
        }
    }

    // Method to accelerate the ship
    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.ship.getRotate())) * 0.05;
        double changeY = Math.sin(Math.toRadians(this.ship.getRotate())) * 0.05;

        this.movement = this.movement.add(changeX, changeY);
        double currentSpeed = this.movement.magnitude();
        double maxSpeed = 5;

        // Limit the ship's speed to the maximum speed

        if (currentSpeed < maxSpeed) {
            this.movement = this.movement.add(changeX, changeY);
        } else {
            this.movement = this.movement.normalize().multiply(maxSpeed);
        }
    }

    // Method to fire a bullet from the ship
    public void fire(Pane pane) {
        // Check if the player is not reloading
        if (!isReloading) {
            // Find an empty slot in the bullets array to store a new bullet
            int emptySlot = -1;
            for (int i = 0; i < bulletLimit; i++) {
                if (bullets[i] == null) {
                    emptySlot = i;
                    break;
                }
            }

            // If an empty slot is found, create a new bullet and store it in the array
            if (emptySlot != -1) {
                Bullet newBullet = new Bullet(this.ship.getTranslateX(), this.ship.getTranslateY(), this.ship.getRotate());
                bullets[emptySlot] = newBullet;
                pane.getChildren().add(newBullet); // Add the bullet to the game pane

                // Create a Timeline to update the bullet's position
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2), event -> newBullet.move()));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                // Store the current empty slot index to use it inside the PauseTransition event
                int finalEmptySlot = emptySlot;
                // Set up a PauseTransition for the bullet duration (2 seconds in this case)
                PauseTransition bulletDuration = new PauseTransition(Duration.seconds(2));
                // When the bullet duration is over, remove the bullet from the game pane and the bullets array
                bulletDuration.setOnFinished(event -> {
                    timeline.stop(); // Stop the bullet's timeline
                    pane.getChildren().remove(newBullet); // Remove the bullet from the game pane
                    bullets[finalEmptySlot] = null; // Set the bullet slot to null, making it available for future bullets
                });
                bulletDuration.play();

                // Start the reload cooldown if all bullets have been used
                int bulletCount = 0;
                for (Bullet bullet : bullets) {
                    if (bullet != null) {
                        bulletCount++;
                    }
                }
                if (bulletCount == bulletLimit) {
                    isReloading = true;
                    PauseTransition reloadTimer = new PauseTransition(Duration.seconds(reloadCooldown));
                    reloadTimer.setOnFinished(event -> isReloading = false);
                    reloadTimer.play();
                }
            }
        }
    }

    public boolean checkBulletAsteroidCollision(Polygon[] asteroids, Pane pane, Asteroid_Generators asteroidGenerators) {
        boolean collisionDetected = false;
        for (int i = 0; i < bulletLimit; i++) {
            if (bullets[i] != null) {
                for (int j = 0; j < asteroids.length; j++) {
                    if (asteroids[j] != null && bullets[i].collidesWith(asteroids[j])) {
                        collisionDetected = true;

                        // Remove the asteroid and the bullet from the game pane
                        pane.getChildren().remove(asteroids[j]);
                        pane.getChildren().remove(bullets[i]);

                        // Set the bullet array slot to null

                        bullets[i] = null;

                        // Split the asteroid if it's large or medium
                        double currentSize = asteroids[j].getScaleX();
                        double x = asteroids[j].getLayoutX();
                        double y = asteroids[j].getLayoutY();

                        if (currentSize == 1.5) { // Large asteroid
                            asteroidGenerators.addAsteroid(1.0, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                            asteroidGenerators.addAsteroid(1.0, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                        } else if (currentSize == 1.0) { // Medium asteroid
                            asteroidGenerators.addAsteroid(0.5, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                            asteroidGenerators.addAsteroid(0.5, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                        }

                        // You can add code here to update the game state, such as incrementing the player's score
                        // ...

                        // set asteroids collided with bullet to null after we've gotten the size of the asteroid in order to know
                        //which sized asteroid to spawn
                        asteroids[j] = null;

                        // Update the player's score based on the asteroid's size
                        if (currentSize == 1.5) { // Large asteroid
                            incrementScore(100);
                        } else if (currentSize == 1.0) { // Medium asteroid
                            incrementScore(50);
                        } else if (currentSize == 0.5) { // Small asteroid
                            incrementScore(25);
                        }

                        break;
                    }
                }
            }
        }
        return collisionDetected;
    }

    // Method to handle the ship's controls
    public void controlShip(Player player, Scene scene, Pane pane, Asteroid_Generators asteroidGenerator) {
        Set<KeyCode> pressedKeys = new HashSet<>();

        scene.setOnKeyPressed(keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            pressedKeys.add(keyCode);

            // Fire a bullet when the space bar is pressed
            if (keyCode == KeyCode.SPACE && !spaceBarPressed) {
                player.fire(pane);
                spaceBarPressed = true;
            }

            // Jump when the shift key is pressed
            if (keyCode == KeyCode.SHIFT && !shiftKeyPressed) {
                player.jump(scene, asteroidGenerator.getAsteroids());
                shiftKeyPressed = true;
            }
        });

        scene.setOnKeyReleased(keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            pressedKeys.remove(keyCode);

            // Reset the spaceBarPressed flag when the space bar is released
            if (keyCode == KeyCode.SPACE) {
                spaceBarPressed = false;
            }

            // Reset the shiftKeyPressed flag when the shift key is released
            if (keyCode == KeyCode.SHIFT) {
                shiftKeyPressed = false;
            }
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressedKeys.contains(KeyCode.LEFT)) {
                    player.turnLeft();
                }

                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    player.turnRight();
                }
                if (pressedKeys.contains(KeyCode.UP)) {
                    player.accelerate();
                }
                if (pressedKeys.contains(KeyCode.DOWN)) {
                    player.decellerate();
                }

                player.move();
                player.checkBulletAsteroidCollision(asteroidGenerator.getAsteroids(), pane, asteroidGenerator);
            }
        }.start();
    }
}

