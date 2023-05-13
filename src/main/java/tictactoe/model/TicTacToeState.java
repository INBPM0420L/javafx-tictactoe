package tictactoe.model;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class TicTacToeState {
    int length;
    int width;
    int height;
    @Getter(AccessLevel.PRIVATE)
    State[][] board;
    State nextPlayer;


    public TicTacToeState(
            final int length,
            final int width,
            final int height) {

        this.length = length;
        this.width = width;
        this.height = height;
        this.board = new State[height][width];
        this.nextPlayer = State.BLUE;

        for (int i = 0; i < height; i++) {
            Arrays.fill(this.board[i], State.EMPTY);
        }
    }

    public State getCell(final int row, final int col) {
        return board[row][col];
    }

    public boolean canMove(final int row, final int col) {
        try {
            if (getWinner().isPresent() || board[row][col] != State.EMPTY) {
                return false;
            }
        } catch (final ArrayIndexOutOfBoundsException ignored) {
            return false;
        }
        return true;
    }

    public TicTacToeState doMove(final int row, final int col) {
        final var newBoard = new State[height][];
        for (int i = 0; i < height; i++) {
            newBoard[i] = Arrays.copyOf(board[i], width);
        }
        newBoard[row][col] = nextPlayer;

        return this.toBuilder()
                .nextPlayer(switch (nextPlayer) {
                    case RED -> State.BLUE;
                    case BLUE -> State.RED;
                    default -> throw new IllegalStateException();
                })
                .board(newBoard)
                .build();
    }

    public Optional<State> getWinner() {
        var hasEmpty = false;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (board[row][col] == State.EMPTY) {
                    hasEmpty = true;
                    continue;
                }

                for (final var checkFunction : List.<BiPredicate<Integer, Integer>>of(
                        this::checkHorizontal, this::checkVertical, this::checkDiagonal1, this::checkDiagonal2)) {
                    try {
                        if (checkFunction.test(row, col)) {
                            return Optional.of(board[row][col]);
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }
        }

        return hasEmpty ? Optional.empty() : Optional.of(State.EMPTY);
    }

    private boolean checkHorizontal(final int row, final int col) {
        for (int i = 1; i < length; i++) {
            if (board[row][col] != board[row][col + i]) {
                return false;
            }
        }
        return true;
    }

    private boolean checkVertical(final int row, final int col) {
        for (int i = 1; i < length; i++) {
            if (board[row][col] != board[row + i][col]) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonal1(final int row, final int col) {
        for (int i = 1; i < length; i++) {
            if (board[row][col] != board[row + i][col + i]) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonal2(final int row, final int col) {
        for (int i = 1; i < length; i++) {
            if (board[row][col] != board[row - i][col + i]) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        final var sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(board[i][j].ordinal()).append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public enum State {
        EMPTY, RED, BLUE;
    }
}
