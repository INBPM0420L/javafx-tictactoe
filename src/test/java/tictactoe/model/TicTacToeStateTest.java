package tictactoe.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import util.TestDataReader;

public class TicTacToeStateTest {
    private static TestDataReader<TicTacToeState.Value[][]> boardReader;

    private static final int BASE_LENGTH = 3;
    private static final int BASE_WIDTH = 3;
    private static final int BASE_HEIGHT = 3;

    private TicTacToeState buildTicTacToeState(TicTacToeState.Value[][] board, TicTacToeState.Value nextPlayer) {
        return TicTacToeState.builder()
                .length(BASE_LENGTH)
                .height(BASE_HEIGHT)
                .width(BASE_WIDTH)
                .board(board)
                .nextPlayer(nextPlayer)
                .build();
    }

    private TicTacToeState buildTicTacToeState(TicTacToeState.Value[][] board) {
        return buildTicTacToeState(board, TicTacToeState.Value.BLUE);
    }

    @BeforeAll
    static void setUp() {
        boardReader = new TestDataReader<>(TicTacToeState.Value[][].class);
    }

    @ParameterizedTest(name = "Constructor should initialize a {1}x{2} empty board with BLUE as starting player, and {0} length")
    @CsvSource({
            "0,0,0",
            "1,1,1",
            "3,3,3",
            "20,20,25",
            "100,99,99"
    })
    void test_constructorShouldCreateEmptyBoardWithBlueAsStartingPlayer(int length, int width, int height) {
        // given
        var expectedBoard = new TicTacToeState.Value[height][width];
        IntStream.range(0, height)
                .forEach(currentHeight -> Arrays.fill(expectedBoard[currentHeight], TicTacToeState.Value.EMPTY));

        // when
        var underTest = new TicTacToeState(length, width, height);

        // then
        Assertions.assertArrayEquals(expectedBoard, underTest.getBoard());
        Assertions.assertEquals(TicTacToeState.Value.BLUE, underTest.getNextPlayer());
    }

    @ParameterizedTest(name = "canMove() should return true for valid field: [{0},{1}]")
    @CsvSource({
            "0,0",
            "1,0",
            "0,1",
            "2,2"
    })
    void test_canMoveShouldReturnTrue_whenTheFieldIsValid(int row, int col) {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/canMove/inProgress.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertTrue(underTest.canMove(row, col));
    }

    @ParameterizedTest(name = "canMove() should return false for invalid field: [{0},{1}]")
    @CsvSource({
            "-1,-1",
            "-1,0",
            "0,-1",
            "3,3"
    })
    void test_canMoveShouldReturnFalse_whenTheFieldIsInvalid(int row, int col) {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/canMove/inProgress.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertFalse(underTest.canMove(row, col));
    }

    @ParameterizedTest(name = "canMove() should return false for [{0},{1}] when the winner is present")
    @CsvSource({
            "0,0",
            "1,1",
            "2,2"
    })
    void test_canMoveShouldReturnFalse_whenTheWinnerIsPresent(int row, int col) {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/canMove/winnerIsPresent.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertFalse(underTest.canMove(row, col));
    }

    @ParameterizedTest(name = "canMove() should return false when the field is not empty: row: {0}, col: {1}")
    @CsvSource({
            "0,0",
            "2,2"
    })
    void test_canMoveShouldReturnFalse_whenTheFieldIsNotEmpty(int row, int col) {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/canMove/fieldIsNotEmpty.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertFalse(underTest.canMove(row, col));
    }

    @Test
    @DisplayName("doMove() should place BLUE value to the board and switch to the RED player (when the next player is BLUE)")
    void test_doMoveShouldPlaceValueToBoardAndSwitchToRedPlayer_whenTheCurrentPlayerIsBlue() {
        // given
        TicTacToeState.Value[][] expectedBoard = boardReader.readTestData("tictactoe/model/doMove/valid_expected_blue.json");
        TicTacToeState.Value[][] initialBoard = boardReader.readTestData("tictactoe/model/doMove/valid_initial.json");
        var initialState = buildTicTacToeState(initialBoard, TicTacToeState.Value.BLUE);

        // when
        var underTest = initialState.doMove(0, 2);

        // then
        Assertions.assertArrayEquals(expectedBoard, underTest.getBoard());
        Assertions.assertEquals(TicTacToeState.Value.RED, underTest.getNextPlayer());
    }

    @Test
    @DisplayName("doMove() should place RED value to the board and switch to the BLUE player (when the next player is RED)")
    void test_doMoveShouldPlaceValueToBoardAndSwitchToBluePlayer_whenTheCurrentPlayerIsRed() {
        // given
        TicTacToeState.Value[][] expectedBoard = boardReader.readTestData("tictactoe/model/doMove/valid_expected_red.json");
        TicTacToeState.Value[][] initialBoard = boardReader.readTestData("tictactoe/model/doMove/valid_initial.json");
        var initialState = buildTicTacToeState(initialBoard, TicTacToeState.Value.RED);

        // when
        var underTest = initialState.doMove(0, 2);

        // then
        Assertions.assertArrayEquals(expectedBoard, underTest.getBoard());
        Assertions.assertEquals(TicTacToeState.Value.BLUE, underTest.getNextPlayer());
    }

    @Test
    @DisplayName("doMove() should throw IllegalStateException when the next player is EMPTY")
    void test_doMoveShouldThrowIllegalStateException_whenNextPlayerIsEmpty() {
        // given
        TicTacToeState.Value[][] initialBoard = boardReader.readTestData("tictactoe/model/doMove/valid_initial.json");
        var underTest = buildTicTacToeState(initialBoard, TicTacToeState.Value.EMPTY);

        // when, then
        Assertions.assertThrows(IllegalStateException.class, () -> underTest.doMove(0, 2));
    }

    @Test
    @DisplayName("getWinner() should return the winner player when there are three same adjacent values horizontally")
    void test_getWinnerShouldReturnWinner_whenThereAreThreeSameAdjacentValuesHorizontally() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/horizontal.json");
        var underTest = buildTicTacToeState(board, TicTacToeState.Value.BLUE);

        // when, then
        Assertions.assertEquals(Optional.of(TicTacToeState.Value.BLUE), underTest.getWinner());
    }

    @Test
    @DisplayName("getWinner() should return the winner player when there are three same adjacent values vertically")
    void test_getWinnerShouldReturnWinner_whenThereAreThreeSameAdjacentValuesVertically() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/vertical.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertEquals(Optional.of(TicTacToeState.Value.RED), underTest.getWinner());
    }

    @Test
    @DisplayName("getWinner() should return the winner player when there are three same adjacent values left diagonally")
    void test_getWinnerShouldReturnWinner_whenThereAreThreeSameAdjacentValuesLeftDiagonally() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/diagonal1.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertEquals(Optional.of(TicTacToeState.Value.BLUE), underTest.getWinner());
    }

    @Test
    @DisplayName("getWinner() should return the winner player when there are three same adjacent values right diagonally")
    void test_getWinnerShouldReturnWinner_whenThereAreThreeSameAdjacentValuesRightDiagonally() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/diagonal2.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertEquals(Optional.of(TicTacToeState.Value.RED), underTest.getWinner());
    }

    @Test
    @DisplayName("getWinner() should return empty Optional when there is no winner yet")
    void test_getWinnerShouldReturnEmptyOptional_whenThereIsNoWinnerYet() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/inProgress.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertEquals(Optional.empty(), underTest.getWinner());
    }

    @Test
    @DisplayName("getWinner() should return EMPTY when the game state is draw")
    void test_getWinnerShouldReturnEmpty_whenTheStateIsDraw() {
        // given
        TicTacToeState.Value[][] board = boardReader.readTestData("tictactoe/model/getWinner/draw.json");
        var underTest = buildTicTacToeState(board);

        // when, then
        Assertions.assertEquals(Optional.of(TicTacToeState.Value.EMPTY), underTest.getWinner());
    }
}
