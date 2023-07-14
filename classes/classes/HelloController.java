// Java package declaration for the example Java game
package com.example.javagame;

// Importing necessary JavaFX classes for FXML and UI controls
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// HelloController class for handling the UI interactions
public class HelloController {
    // FXML annotation to reference the 'welcomeText' Label in the associated FXML file
    @FXML
    private Label welcomeText;

    // FXML annotation for binding the 'onHelloButtonClick' method to the button click event in the FXML file
    @FXML
    protected void onHelloButtonClick() {
        // Set the text of the 'welcomeText' Label when the button is clicked
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
