package ui;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE = 8;
    private static final String EMPTY = "   ";
    
    private static final String[][] INITIAL_BOARD = {
        {"R", "N", "B", "Q", "K", "B", "N", "R"},
        {"P", "P", "P", "P", "P", "P", "P", "P"},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {"p", "p", "p", "p", "p", "p", "p", "p"},
        {"r", "n", "b", "q", "k", "b", "n", "r"}
    };

    public static String draw(String perspective) {
        StringBuilder sb = new StringBuilder();
        boolean isWhite = "WHITE".equalsIgnoreCase(perspective);

        drawHeaders(sb, isWhite);
        for (int row = 0; row < BOARD_SIZE; row++) {
            int actualRow = isWhite ? row : (BOARD_SIZE - 1 - row);
            int displayRow = isWhite ? (BOARD_SIZE - row) : (row + 1);
            drawRow(sb, actualRow, displayRow, isWhite);
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

    private static void drawRow(StringBuilder sb, int actualRow, int displayRow, boolean isWhite) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(displayRow).append(" ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            int actualCol = isWhite ? col : (BOARD_SIZE - 1 - col);
            
            if ((actualRow + actualCol) % 2 == 0) {
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BLACK);
            }
            
            String piece = INITIAL_BOARD[actualRow][actualCol];
            if (piece.equals(" ")) {
                sb.append(EMPTY);
            } else {
                if (Character.isUpperCase(piece.charAt(0))) {
                    sb.append(SET_TEXT_COLOR_BLUE); // Black pieces (UpperCase in my array)
                } else {
                    sb.append(SET_TEXT_COLOR_RED); // White pieces (LowerCase in my array)
                }
                sb.append(" ").append(piece.toUpperCase()).append(" ");
            }
        }
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(displayRow).append(" ");
        sb.append(SET_BG_COLOR_DARK_GREY).append("\n");
    }

    public static void main(String[] args) {
        System.out.println("White Perspective:");
        System.out.println(draw("WHITE"));
        System.out.println("\nBlack Perspective:");
        System.out.println(draw("BLACK"));
    }
}
