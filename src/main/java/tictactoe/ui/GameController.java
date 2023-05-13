package tictactoe.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.NonNull;
import tictactoe.model.TicTacToeState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GameController extends BoardController {

    @FXML
    private Slider widthSlider;
    @FXML
    private Slider heightSlider;
    @FXML
    private Slider lengthSlider;

    @FXML
    private Button startButton;
    @FXML
    private Button finishButton;

    @FXML
    private Label lengthDisplay;
    @FXML
    private Label widthDisplay;
    @FXML
    private Label heightDisplay;

    private ObjectProperty<TicTacToeState> model;
    private BooleanProperty inGame;


    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            super.initialize();

            inGame = new SimpleBooleanProperty(false);
            widthSlider.setValue(3);
            heightSlider.setValue(3);
            sizeSlider.setValue(20);
            lengthSlider.maxProperty().set(3);
            lengthSlider.setValue(3);

            lengthDisplay.textProperty().bind(lengthSlider.valueProperty().asString());
            widthDisplay.textProperty().bind(widthSlider.valueProperty().asString());
            heightDisplay.textProperty().bind(heightSlider.valueProperty().asString());

            model = new SimpleObjectProperty<>(new TicTacToeState(3, 3, 3));
            List.of(heightSlider.valueProperty(), widthSlider.valueProperty())
                    .forEach(s -> s.addListener((element, old, actual) -> {
                        s.setValue(actual.intValue());
                        lengthSlider.maxProperty().set(Math.min(widthSlider.getValue(), heightSlider.getValue()));
                        lengthSlider.setValue(Math.min(lengthSlider.getValue(), lengthSlider.maxProperty().getValue()));
                        model.set(new TicTacToeState((int) lengthSlider.getValue(), (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                    }));

            sizeSlider.valueProperty().addListener((element, old, actual) -> {
                sizeSlider.setValue(actual.intValue());
                drawGrid(model.get());
            });

            lengthSlider.valueProperty().addListener((element, old, actual) -> {
                lengthSlider.setValue(actual.intValue());
            });

            model.addListener((element, old, actual) -> {
                drawGrid(model.get());
            });

            inGame.addListener((element, old, actual) -> {
                List.of(startButton, widthSlider, heightSlider, lengthSlider).forEach(b -> b.setDisable(actual));
                finishButton.setDisable(!actual);
            });

            drawGrid(model.get());
            finishButton.setDisable(true);
        });

    }


    @FXML
    private void onStartGame() {
        inGame.set(true);
        properties.history = new ArrayList<>();
        model.set(new TicTacToeState((int) lengthSlider.getValue(), (int) widthSlider.getValue(), (int) heightSlider.getValue()));
        properties.setFirstPlayer(model.get().getNextPlayer());
        properties.setLength(model.get().getLength());
        properties.setWidth(model.get().getWidth());
        properties.setHeight(model.get().getHeight());
        drawGrid(model.get());
    }

    @FXML
    private void onFinishGame() {
        inGame.set(false);
        model.set(new TicTacToeState((int) lengthSlider.getValue(), (int) widthSlider.getValue(), (int) heightSlider.getValue()));
        drawGrid(model.get());
    }

    @FXML
    private void onSaveButton(ActionEvent actionEvent) throws IOException {
        final var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json documents", "*.json"));
        Optional.ofNullable(fileChooser.showSaveDialog(((Button) actionEvent.getSource()).getScene().getWindow()))
                .ifPresent(file -> {
                    try {
                        TicTacToeApplication.MAPPER.writeValue(file, properties);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

    }


    protected StackPane createSquare(
            final double size,
            @NonNull final TicTacToeState.State value) {
        final var square = super.createSquare(size, value);
        if (inGame.get()) {
            square.getStyleClass().add("inGame");
            square.setOnMouseClicked(this::handleMouseClick);
        }
        return square;
    }

    @FXML
    private void handleMouseClick(
            final @NonNull MouseEvent event) {
        final var row = GridPane.getRowIndex((StackPane) event.getSource());
        final var col = GridPane.getColumnIndex((StackPane) event.getSource());

        Optional.of(model.get())
                .filter(model -> model.canMove(row, col))
                .map(model -> model.doMove(row, col))
                .ifPresent(model -> {
                    properties.getHistory()
                            .add(MyProperties.Entry.of(col, row));

                    model.getWinner()
                            .ifPresent(winner -> {
                                inGame.set(false);
                                drawGrid(model);
                            });
                });
    }
}
