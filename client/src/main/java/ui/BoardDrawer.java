package ui;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE = 8;
    private static final String EMPTY = "   ";

    public static String draw(String perspective) {
        StringBuilder sb = new StringBuilder();
        boolean isWhite = "WHITE".equalsIgnoreCase(perspective);

        drawHeaders(sb, isWhite);
        for (int row = 0; row < BOARD_SIZE; row++) {
            int displayRow = isWhite ? (BOARD_SIZE - row) : (row + 1);
            drawRow(sb, row, displayRow, isWhite);
        }
        drawHeaders(sb, isWhite);

        return sb.toString();
    }

    private static void drawHeaders(StringBuilder sb, boolean isWhite) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(EMPTY);
        for (int col = 0; col < BOARD_SIZE; col++) {
            char displayCol = (char) (isWhite ? ('a' + col) : ('h' - col));
            sb.append(" ").append(displayCol).append(" ");
        }
        sb.append(EMPTY).append(SET_BG_COLOR_DARK_GREY).append("\n");
    }

    private static void drawRow(StringBuilder sb, int row, int displayRow, boolean isWhite) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(displayRow).append(" ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            if ((row + col) % 2 == 0) {
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BLACK);
            }
            sb.append(EMPTY);
        }
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(displayRow).append(" ");
        sb.append(SET_BG_COLOR_DARK_GREY).append("\n");
    }
}
