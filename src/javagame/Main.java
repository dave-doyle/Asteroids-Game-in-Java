package javagame;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {
    MediaPlayer mediaPlayer;

    // Main method that serves as the entry point of the application
    public static void main(String[] args) {
        // Call the launch method from the Application class to start the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a home page
        HomePage homePage = new HomePage(stage);

        // Set the scene to the home page scene
        stage.setScene(homePage.getScene());
        stage.setTitle("Asteroids"); // Add a title to the stage
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResource("/media/icon.jpg").toExternalForm())); // add icon
        stage.show();

        // Start the background music
        startBackgroundMusic();
    }

    private void startBackgroundMusic() {
        // Stop the current music if it's playing
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        // Start the new music
        Media backgroundMusic = new Media(getClass().getResource("/media/backgroundMusic.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(backgroundMusic);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public String promptPlayerName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Your Name");
        dialog.setHeaderText("Please enter your name:");
        dialog.setContentText("Name:");

        String playerName = "";
        boolean validName = false;
        while (!validName) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                playerName = result.get().trim();
                if (!playerName.isEmpty()) {
                    validName = true;
                } else {
                    showAlert("Error", "Please enter a non-empty name.");
                }
            } else {
                break;
            }
        }

        return playerName;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

