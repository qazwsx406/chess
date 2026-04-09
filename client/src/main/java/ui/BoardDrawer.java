package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE = 8;
    private static final String EMPTY = "   ";

    public static String draw(String perspective, ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        boolean isWhite = !"BLACK".equalsIgnoreCase(perspective);

        drawHeaders(sb, isWhite);
        if (isWhite) {
            for (int row = BOARD_SIZE; row >= 1; row--) {
                drawRow(sb, row, isWhite, board);
            }
        } else {
            for (int row = 1; row <= BOARD_SIZE; row++) {
                drawRow(sb, row, isWhite, board);
            }
        }
        drawHeaders(sb, isWhite);

        return sb.toString();
    }

    // Overload for when we don't have a board (e.g. initial setup)
    public static String draw(String perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return draw(perspective, board);
    }

    private static void drawHeaders(StringBuilder sb, boolean isWhite) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(EMPTY);
        for (int col = 1; col <= BOARD_SIZE; col++) {
            int actualCol = isWhite ? col : (BOARD_SIZE - col + 1);
            char displayCol = (char) ('a' + actualCol - 1);
            sb.append(" ").append(displayCol).append(" ");
        }
        sb.append(EMPTY).append(SET_BG_COLOR_DARK_GREY).append(RESET_ALL).append("\n");
    }

    private static void drawRow(StringBuilder sb, int row, boolean isWhite, ChessBoard board) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(row).append(" ");
        
        for (int col = 1; col <= BOARD_SIZE; col++) {
            int actualCol = isWhite ? col : (BOARD_SIZE - col + 1);

            if ((row + actualCol) % 2 == 0) {
                sb.append(SET_BG_COLOR_BLACK);
            } else {
                sb.append(SET_BG_COLOR_WHITE);
            }

            ChessPiece piece = board.getPiece(new ChessPosition(row, actualCol));
            if (piece == null) {
                sb.append(EMPTY);
            } else {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    sb.append(SET_TEXT_COLOR_RED);
                } else {
                    sb.append(SET_TEXT_COLOR_BLUE);
                }
                sb.append(getPieceString(piece));
            }
        }
        
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(row).append(" ");
        sb.append(SET_BG_COLOR_DARK_GREY).append(RESET_ALL).append("\n");
    }

    private static String getPieceString(ChessPiece piece) {
        String s = switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case ROOK -> " R ";
            case PAWN -> " P ";
        };
        return s;
    }
}