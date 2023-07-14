module javagame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens javagame to javafx.fxml;
    exports javagame;
}