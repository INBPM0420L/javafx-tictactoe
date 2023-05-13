package tictactoe.ui;

import tictactoe.model.TicTacToeState;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class MyProperties {
    String playerBlue;
    String playerRed;
    List<Entry> history;
    TicTacToeState.State firstPlayer;
    int width;
    int height;
    int length;


    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Entry {
        int col;
        int row;

        public static Entry of(
                final int row,
                final int col) {

            return builder()
                    .row(row)
                    .col(col)
                    .build();
        }
    }
}
