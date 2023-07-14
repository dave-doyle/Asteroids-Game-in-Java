package javagame;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class AlienShip extends Polygon {

    private Timeline alienShipSpawnTimer;

    private Point2D velocity;
    private final double speed = 2.0;
    private Bullet bullet;
    private Random random;
    private int counter = 0;
    private final int updateInterval = 60; // Change direction every 60 frames

    private Timeline shootingTimer;

    private long lastShotTime = 0;
    private final long shotInterval = 3000; // 3000 milliseconds or 3 seconds

    double shipMinY = -17.5; // The highest point of the ship
    double shipMaxY = 10; // The lowest point of the ship


    public AlienShip(double x, double y, Pane pane) {
        super(-30, 0, -20, 10, 20, 10, 30, 0, 20, -10, 15, -10, 10, -17.5, -10, -17.5, -15, -10, -20, -10);
        setTranslateX(x);
        setTranslateY(y);
        random = new Random();

        // Set initial velocity
        velocity = new Point2D(speed, 0);
    }


    public void move(Pane pane) {
        counter++;
        if (counter % updateInterval == 0) {
            double randomAngle = random.nextDouble() * Math.PI - Math.PI / 2;
            double newX = speed * Math.cos(randomAngle);
            double newY = speed * Math.sin(randomAngle);
            velocity = new Point2D(newX, newY);
        }
        setTranslateX(getTranslateX() + velocity.getX());
        setTranslateY(getTranslateY() + velocity.getY());
        double shipWidth = getBoundsInLocal().getWidth();
        double shipHeight = getBoundsInLocal().getHeight();
        if (getParent() != null) {
            if (getTranslateX() < -shipWidth) {
                setTranslateX(pane.getWidth()); // Wrap around to the right side of the screen
            } else if (getTranslateX() > pane.getWidth()) {
                setTranslateX(-shipWidth); // Wrap around to the left side of the screen
            }

            if (getTranslateY() < (shipMinY - shipHeight)) {
                setTranslateY(pane.getHeight() - shipMaxY); // Wrap around to the bottom of the screen
            } else if (getTranslateY() > (pane.getHeight() - shipMinY)) {
                setTranslateY(-shipHeight + shipMaxY); // Wrap around to the top of the screen
            }
        }
    }


    public void startAlienShipSpawnTimer(Pane pane, Polygon ship) {
        if (alienShipSpawnTimer != null) {
            alienShipSpawnTimer.stop();
        }
        // An alien ship appears every 10 seconds.
        alienShipSpawnTimer = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            if (this.getParent() == null) {
                this.setTranslateX(-40);
                this.setTranslateY(pane.getHeight() / 2);
                pane.getChildren().add(this);

                // Start the shooting timer when the alien ship is added to the pane
                if (shootingTimer == null) {
                    startShootingTimer(pane, ship);
                } else {
                    resumeShootingTimer();
                }
            }
        }));
        alienShipSpawnTimer.setCycleCount(Timeline.INDEFINITE);
        alienShipSpawnTimer.play();
    }



    public void shoot(Pane pane, Polygon ship) {
        //double clock = 0;
        // Check if the alien ship is within the screen boundaries
        if (getTranslateX() < 0 || getTranslateX() > pane.getWidth() || getTranslateY() < 0 || getTranslateY() > pane.getHeight()) {
            return;
        }
        // ensure alien only fires one bullet every three seconds
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < shotInterval) {
            return;
        }

        lastShotTime = currentTime;

        if (this.bullet == null) {
            // The alienship shoot at the player ship
            double directionX = ship.getTranslateX() - getTranslateX() + 480;
            double directionY = ship.getTranslateY() - getTranslateY() + 360;

            // Calculate the angle between the player's ship and the alien ship, passing the value to the Bullet class.
            double angle = Math.toDegrees(Math.atan2(directionY, directionX));
            this.bullet = new Bullet(getTranslateX() - 480, getTranslateY() - 360, angle, Color.RED);
            pane.getChildren().add(bullet);

            // Create a Timeline to update the bullet's position
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2), event -> {
                if(bullet != null) {
                    bullet.move();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            // Set the bullet to disappear after two seconds.
            PauseTransition bulletDuration = new PauseTransition(Duration.seconds(2));

            // When the bullet duration is over, remove the bullet from the game pane and the bullets array
            bulletDuration.setOnFinished(event -> {
                timeline.stop(); // Stop the bullet's timeline
                pane.getChildren().remove(bullet); // Remove the bullet from the game pane
                bullet = null; // Set the bullet to null, making it available for future bullet
            });
            bulletDuration.play();
        }
    }


    // Clear the bullet when the alien ship is destroyed.
    public void removeAllBullets(Pane pane) {
        if (bullet != null) {
            pane.getChildren().remove(bullet);
            bullet = null;
        }
    }

    //    This method can be used in other classes to obtain the Bullet object
    public Bullet getAlienBullet() {
        return bullet;
    }

    //    Set up a timeline to make the alien ship shoot a bullet at the player every three seconds.
    public void startShootingTimer(Pane pane, Polygon ship) {
        shootingTimer = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            shoot(pane, ship);
        }));
        shootingTimer.setCycleCount(Timeline.INDEFINITE);
        shootingTimer.play();
    }

    public void pauseShootingTimer() {
        if (shootingTimer != null) {
            shootingTimer.pause();
        }
    }

    public void resumeShootingTimer() {
        if (shootingTimer != null) {
            shootingTimer.play();
        }
    }


}