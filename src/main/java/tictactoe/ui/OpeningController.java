package tictactoe.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.NonNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class OpeningController {
    @FXML
    private TextField playerRedTextField;

    @FXML
    private TextField playerBlueTextField;

    @FXML
    private Label errorLabel;

    public void onLoadButton(
            @NonNull final ActionEvent actionEvent) {
        final var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json documents", "*.json"));
        Optional.ofNullable(fileChooser.showOpenDialog(((Button) actionEvent.getSource()).getScene().getWindow()))
                .ifPresent(file -> {
                    try {
                        final var root = FXMLLoader.<Parent>load(Objects.requireNonNull(getClass().getResource("/fxml/history.fxml")));
                        final var stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        stage.setUserData(TicTacToeApplication.MAPPER.readValue(file, MyProperties.class));
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @FXML
    public void onStartButton(
            @NonNull final ActionEvent actionEvent) throws IOException {
        if (playerRedTextField.getText().isBlank() || playerBlueTextField.getText().isBlank()) {
            errorLabel.setText("Please enter your name!");
        } else {
            final var root = FXMLLoader.<Parent>load(Objects.requireNonNull(getClass().getResource("/fxml/game.fxml")));
            final var stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setUserData(MyProperties.builder()
                    .playerBlue(playerBlueTextField.getText())
                    .playerRed(playerRedTextField.getText())
                    .build());
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

}
