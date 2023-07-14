// Java package declaration for the example Java game
package com.example.javagame;

// Importing necessary JavaFX classes for geometry and shapes
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

// Bullet class that inherits from the JavaFX Circle class
public class Bullet extends Circle {
    // Speed and angle of the bullet
    private final double speed;
    private final double angle;

    // Bullet constructor that takes initial position and angle as arguments
    public Bullet(double x, double y, double angle) {
        // Call the superclass constructor to create a Circle with given position and radius
        super(x + 480, y + 360, 3);
        System.out.println(x);
        // Set the angle of the bullet
        this.angle = angle;
        // Set the speed of the bullet
        this.speed = 1;
    }

    // Method to check if the bullet collides with another object (Polygon)
    public boolean collidesWith(Polygon other) {
        // Return true if the bullet's bounds intersect with the other object's bounds
        return this.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    // Method to move the bullet
    public void move() {
        // Update the bullet's x and y position based on its angle and speed
        this.setCenterX(getCenterX() + Math.cos(Math.toRadians(angle)) * speed);
        this.setCenterY(getCenterY() + Math.sin(Math.toRadians(angle)) * speed);

        // Check if the bullet's x position is outside the screen bounds and wrap its position accordingly
        if (this.getCenterX() < 0) {
            this.setCenterX(960);
        }
        if (this.getCenterX() > 960) {
            this.setCenterX(0);
        }

        // Check if the bullet's y position is outside the screen bounds and wrap its position accordingly
        if (this.getCenterY() < 0) {
            this.setCenterY(720);
        }
        if (this.getCenterY() > 720) {
            this.setCenterY(0);
        }
    }
}
