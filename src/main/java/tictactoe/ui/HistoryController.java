package tictactoe.ui;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tictactoe.model.TicTacToeState;

public final class HistoryController extends BoardController {

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;
    @FXML
    private Label stepLabel;
    private IntegerProperty stepNumber;

    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            super.initialize();

            stepLabel.setText("0");

            sizeSlider.setValue(20);
            sizeSlider.valueProperty().addListener((element, old, actual) -> {
                sizeSlider.setValue(actual.intValue());
                drawGrid();
            });

            stepNumber = new SimpleIntegerProperty(0);
            stepNumber.addListener((event, old, actual) -> {
                previousButton.setDisable(actual.intValue() == 0);
                nextButton.setDisable(actual.intValue() == properties.getHistory().size());
                stepLabel.setText(String.valueOf(actual));
                drawGrid();
            });

            previousButton.setDisable(true);

            drawGrid();
        });

    }

    @FXML
    private void onNextButton() {
        stepNumber.setValue(stepNumber.getValue() + 1);
        drawGrid();
    }

    @FXML
    private void onPreviousButton() {
        stepNumber.setValue(stepNumber.getValue() - 1);
        drawGrid();
    }

    private void drawGrid() {
        var model = new TicTacToeState(properties.getLength(), properties.getWidth(), properties.getHeight());
        for (int i = 0; i < stepNumber.getValue(); i++) {
            model = model.doMove(properties.getHistory().get(i).getRow(), properties.getHistory().get(i).getCol());
        }
        drawGrid(model);
    }
}
