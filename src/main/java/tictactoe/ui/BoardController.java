package tictactoe.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.NonNull;
import tictactoe.model.TicTacToeState;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.IntStream;

public abstract class BoardController {
    @FXML
    protected GridPane board;
    @FXML
    protected Slider sizeSlider;
    @FXML
    protected Label sizeDisplay;
    @FXML
    protected Label bluePlayerLabel;
    @FXML
    protected Label redPlayerLabel;
    protected MyProperties properties;

    @FXML
    protected final void onExitButton(ActionEvent actionEvent) throws IOException {
        final var stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/opening.fxml")))));
        stage.show();
    }

    protected void initialize() {
        properties = (MyProperties) board.getScene().getWindow().getUserData();
        redPlayerLabel.setText(properties.getPlayerRed());
        bluePlayerLabel.setText(properties.getPlayerBlue());
        sizeDisplay.textProperty().bind(sizeSlider.valueProperty().asString());
    }

    protected void drawGrid(
            @NonNull final TicTacToeState model) {
        board.setHgap(sizeSlider.getValue() / 10.0);
        board.setVgap(sizeSlider.getValue() / 10.0);
        board.setPrefWidth(model.getWidth() * sizeSlider.getValue() + (model.getWidth() - 1) * (sizeSlider.getValue() / 10.0));
        board.setPrefHeight(model.getHeight() * sizeSlider.getValue() + (model.getHeight() - 1) * (sizeSlider.getValue() / 10.0));

        board.getChildren().clear();

        board.getRowConstraints().setAll(
                IntStream.range(0, model.getHeight())
                        .mapToObj(i -> new RowConstraints())
                        .toList());

        board.getColumnConstraints().setAll(
                IntStream.range(0, model.getWidth())
                        .mapToObj(i -> new ColumnConstraints())
                        .toList());

        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                board.add(createSquare(sizeSlider.getValue(), model.getCell(row, col)), col, row);
            }
        }

    }

    protected StackPane createSquare(
            final double size,
            @NonNull final TicTacToeState.State value) {
        final var square = new StackPane();
        square.setPrefWidth(size);
        square.setPrefHeight(size);
        square.getStyleClass().add("square");

        final var piece = new Circle(size / 2.3);
        piece.fillProperty().set(switch (value) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case EMPTY -> Color.TRANSPARENT;
        });
        square.getChildren().add(piece);

        return square;
    }
}
