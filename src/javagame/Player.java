// Java package declaration for the example Java game
package javagame;

// Importing necessary JavaFX classes for animations, geometry, and scenes

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javagame.Asteroid_Generators.Size;


// Player class that extends the Main class
public class Player {

    private AlienShip alienShip = null;
    private int currentLevel;

    private int lives;
    private final Text livesDisplay;
    private boolean invincible;
    private final Main main; // Add this line to declare a reference to the Main class
    public Polygon ship; // Player's ship as a Polygon object, made public to access it in the game loop
    private Point2D movement; // Movement vector for the player's ship
    private final Bullet[] bullets; // Array of Bullet objects for the player's ship
    private final int bulletLimit = 5; // Maximum number of bullets
    private boolean isReloading = false; // Flag to check if the player is reloading
    private final double reloadCooldown = 3; // Cooldown duration in seconds
    private boolean spaceBarPressed = false; // detects when player has fired a bullet
    private boolean shiftKeyPressed = false; // detects if player has used jump
    public Random rnd = new Random(); // used for the jump method so the player appears in a random location
    private static int score = 0; // Add 'static'
    private final Text scoreText;
    private final Stage stage;
    private High_Scores highScores;

    public void shootSound(){
        AudioClip shootingSoundEffect = new AudioClip(getClass().getResource("/media/shooting.mp3").toExternalForm());
        shootingSoundEffect.play();
    }

    // Player constructor that takes a Pane and Scene as arguments
    public Player(Pane pane, Scene scene, Main main, Stage stage) {
        this.score = 0; // Initialize the player's score to 0
        this.lives = 3; // initialize player lives to 3
        this.invincible = false;
        this.main = main;
        this.stage = stage;
        //this.scene = scene;

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

        this.scoreText = new Text("Score: 0");
        this.scoreText.setFont(Font.font("Courier New", 20));
        this.scoreText.setFill(Color.BLACK);
        this.scoreText.setX(10);
        this.scoreText.setY(40); // Y value is increased by 20 to account for the difference between the Label and Text positioning
        pane.getChildren().add(this.scoreText);

        // Create the HUD lives display
        livesDisplay = new Text("Lives: " + lives);
        livesDisplay.setFont(Font.font("Courier New", 20));
        livesDisplay.setFill(Color.BLACK);
        livesDisplay.setX(10);
        livesDisplay.setY(20);
        pane.getChildren().add(livesDisplay);


    }

    public void incrementScore(int points) {
        this.score += points;
        if (score < 0) {
            score = 0;
        }
        this.scoreText.setText("Score: " + this.score);
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
                if (asteroid != null) {
                    if (this.ship.getBoundsInParent().intersects(asteroid.getBoundsInParent())) {
                        shipIsSafe = false;
                        break;
                    }
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

                // Play the shooting sound
//                shootSound.setVolume(0.5); // Adjust the volume of the shooting sound
                shootSound();

                Bullet newBullet = new Bullet(this.ship.getTranslateX(), this.ship.getTranslateY(), this.ship.getRotate(), Color.BLACK);
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
                        Size currentSize = Size.fromScale(asteroids[j].getScaleX());
                        double x = asteroids[j].getLayoutX();
                        double y = asteroids[j].getLayoutY();

                        if (currentSize == Size.LARGE) { // Large asteroid
                            asteroidGenerators.addAsteroid(Asteroid_Generators.Size.MEDIUM, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                            asteroidGenerators.addAsteroid(Asteroid_Generators.Size.MEDIUM, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                        } else if (currentSize == Size.MEDIUM) { // Medium asteroid
                            asteroidGenerators.addAsteroid(Asteroid_Generators.Size.SMALL, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                            asteroidGenerators.addAsteroid(Asteroid_Generators.Size.SMALL, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                        }

                        // set asteroids collided with bullet to null after we've gotten the size of the asteroid in order to know
                        //which sized asteroid to spawn
                        asteroids[j] = null;

                        // Update the player's score based on the asteroid's size
                        if (currentSize == Size.LARGE) { // Large asteroid
                            incrementScore(100);
                        } else if (currentSize == Size.MEDIUM) { // Medium asteroid
                            incrementScore(50);
                        } else if (currentSize == Size.SMALL) { // Small asteroid
                            incrementScore(25);
                        }

                        break;
                    }
                }
            }
        }
        return collisionDetected;
    }

    // Method to check if the player collides with another object (Polygon)
    public boolean crashesWith(Polygon other) {
        // Return true if the players bounds intersect with the other object's bounds
        return this.ship.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    private void updateLivesDisplay() {
        livesDisplay.setText("Lives: " + lives);
    }

    public void showGameOverScreen(Pane pane, Main main) {
        // Remove the player's ship from the screen
        pane.getChildren().remove(ship);

        // Create and configure the game over text
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Courier New", 48));
        gameOverText.setFill(Color.RED);
        gameOverText.setX(pane.getWidth() / 2 - gameOverText.getLayoutBounds().getWidth() / 2);
        gameOverText.setY(pane.getHeight() / 2 - gameOverText.getLayoutBounds().getHeight() / 2);

        // Add the game over text to the pane
        pane.getChildren().add(gameOverText);

        highScores = new High_Scores();

        // Schedule a delay of 4 seconds
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            // Create a new instance of the HomePage class
            HomePage homePage = new HomePage(stage);

            // Set the stage with the created scene and display it
            stage.setScene(homePage.getScene());
        }));
        // Start the delay
        delay.play();

        // Use Platform.runLater to wait for the gameOverText to be displayed before prompting for the player's name
        Platform.runLater(() -> {
            try {
                // Add a delay before showing the prompt
                Thread.sleep(1000);

                // Get the player's name
                String playerName = main.promptPlayerName();

                // Save the player's name and score
                highScores.addScore(playerName, getScore());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // Check for collisions between the player's ship and asteroids, handle invincibility, lives, and spawning smaller asteroids.
    public boolean checkShipAsteroidCollision(Polygon[] asteroids, Pane pane, Asteroid_Generators asteroidGenerator) {
        boolean collisionDetected = false;
        Random rnd = new Random();

        for (int i = 0; i < asteroids.length; i++) {
            if (asteroids[i] != null && this.crashesWith(asteroids[i])) {
                collisionDetected = true;
                if (!invincible && lives > 0) {
                    lives--;
                    updateLivesDisplay(); // Update the HUD lives display

                    if (lives <= 0) {
                        showGameOverScreen(pane, main);

                        // Return to the main menu after 4 seconds
                        Timeline returnToMainMenu = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
                        }));
                        returnToMainMenu.play();
                    } else {
                        // Make the player invincible for 3 seconds and create a flashing effect
                        invincible = true;
                        // Make the player invincible for 3 seconds and create a flashing effect
                        Timeline invincibilityTimer = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                            invincible = false;
                        }));
                        invincibilityTimer.play();


                        // Create a FillTransition to make the player flash
                        FillTransition flashEffect = new FillTransition(Duration.seconds(0.2), this.ship, Color.BLACK, Color.TRANSPARENT);
                        flashEffect.setCycleCount(6);
                        flashEffect.setAutoReverse(true);
                        flashEffect.play();

                        // Set the ship to the center and set velocity to 0
                        ship.setTranslateX(0);
                        ship.setTranslateY(0);
                        movement = Point2D.ZERO;

                        // Deduct 10 points from the score
                        incrementScore(-10);
                        if (score < 0) {
                            score = 0;
                        }
                        System.out.println("HIT! You lose 10 points.");
                        System.out.println("Score: " + score);

                        // Generate smaller asteroids
                        double currentSize = asteroids[i].getScaleX();
                        double x = asteroids[i].getLayoutX();
                        double y = asteroids[i].getLayoutY();

                        if (currentSize == 1.5) { // Large asteroid
                            asteroidGenerator.addAsteroid(Asteroid_Generators.Size.MEDIUM, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                            asteroidGenerator.addAsteroid(Asteroid_Generators.Size.MEDIUM, x, y, rnd.nextDouble() + 1, rnd.nextDouble() + 1);
                        } else if (currentSize == 1.0) { // Medium asteroid
                            asteroidGenerator.addAsteroid(Asteroid_Generators.Size.SMALL, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                            asteroidGenerator.addAsteroid(Asteroid_Generators.Size.SMALL, x, y, rnd.nextDouble() + 2, rnd.nextDouble() + 2);
                        }

                        // Remove the asteroid from the pane and the array
                        pane.getChildren().remove(asteroids[i]);
                        asteroids[i] = null;

                    }
                }
                break;
            }
        }

        return collisionDetected;
    }

    // Check the collision between the player's bullets and the alien ship
    private boolean checkBulletAlienShipCollision(AlienShip alienShip, Pane pane) {
        for (Bullet bullet : bullets) {
            if (bullet != null && bullet.getBoundsInParent().intersects(alienShip.getBoundsInParent())) {
                pane.getChildren().remove(bullet);
                bullet.setUserData(null);
                // Here goes score increment and print statement for it
                return true;
            }
        }
        return false;
    }

    // Check the collision between the alien ship's bullets and the player
    private boolean checkAlienBulletPlayerCollision(Bullet alienBullet, Pane pane) {
        if (alienBullet != null && alienBullet.getBoundsInParent().intersects(this.ship.getBoundsInParent())) {
            pane.getChildren().remove(alienBullet);
            alienBullet = null;

            if (!invincible && lives > 0) {
                lives--;
                updateLivesDisplay(); // Update the HUD lives display

                if (lives <= 0) {
                    showGameOverScreen(pane, main);
                } else {
                    // Make the player invincible for 3 seconds and create a flashing effect
                    invincible = true;
                    Timeline invincibilityTimer = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                        invincible = false;
                    }));
                    invincibilityTimer.play();

                    // Create a flashing effect using the opacity of the ship
                    Timeline flashEffect = new Timeline(
                            new KeyFrame(Duration.seconds(0.2), e -> ship.setOpacity(0.0)),
                            new KeyFrame(Duration.seconds(0.4), e -> ship.setOpacity(1.0))
                    );
                    flashEffect.setCycleCount(6);
                    flashEffect.play();

                    // Deduct points from the score
                    incrementScore(-10);
                    System.out.println("HIT! You lose 15 points.");
                    System.out.println("Score: " + score);
                }
            }

            return true;
        }
        return false;
    }




    public void controlShip(Player player, Scene scene, Pane pane, Asteroid_Generators asteroidGenerator, Level level) {
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
                // Check if the current level has changed and start the alien ship spawn timer
                if (alienShip == null || level.getLevel() > currentLevel) {
                    currentLevel = level.getLevel();

                    // spawn alien
                    alienShip = new AlienShip(-40, pane.getHeight() / 2, pane);

                    // Check to prevent null pointer error
                    if (alienShip != null) {
                        alienShip.startAlienShipSpawnTimer(pane, ship);
                        alienShip.resumeShootingTimer(); // Resume the shooting timer when the alien ship respawns
                    }
                }

                if (pressedKeys.contains(KeyCode.LEFT)) {
                    player.turnLeft();
                }

                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    player.turnRight();
                }
                if (pressedKeys.contains(KeyCode.UP)) {
                    player.accelerate();
                }

                player.move();
                player.checkBulletAsteroidCollision(asteroidGenerator.getAsteroids(), pane, asteroidGenerator);
                player.checkShipAsteroidCollision(asteroidGenerator.getAsteroids(), pane, asteroidGenerator); // Check for ship-asteroid collision

                // Update the alien ship position
                if (alienShip != null) {
                    alienShip.move(pane);

                    // Check for collisions between the player's bullets and the alien ship
                    if (checkBulletAlienShipCollision(alienShip, pane)) {
                        pane.getChildren().remove(alienShip);
                        alienShip.removeAllBullets(pane); // Remove all bullets of the destroyed alien ship

                        // Pause the shooting timer
                        alienShip.pauseShootingTimer();
                        alienShip = null;
                        score++;
                    }

                    // Check for collisions between the alien ship's bullets and the player
                    if (alienShip != null && checkAlienBulletPlayerCollision(alienShip.getAlienBullet(), pane)) {
                        // Collision handled in the checkAlienBulletPlayerCollision()
                    }

                }
            }
        }.start();
    }
}
