package tictactoe.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;

import java.io.IOException;
import java.util.Objects;

public final class TicTacToeApplication extends Application {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void start(
            @NonNull final Stage stage) throws IOException {
        stage.setTitle("JavaFX TicTacToe");
        final var scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/opening.fxml"))));
        stage.setScene(scene);
        stage.show();
    }
}
