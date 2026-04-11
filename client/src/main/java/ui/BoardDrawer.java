package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;
import chess.ChessMove;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE = 8;
    private static final String RESET = "\u001b[0m";

    public static String draw(String perspective, ChessBoard board, Collection<ChessMove> validMoves) {
        StringBuilder sb = new StringBuilder();
        boolean isWhite = !"BLACK".equalsIgnoreCase(perspective);

        Set<ChessPosition> endPositions = new HashSet<>();
        ChessPosition startPosition = null;
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                endPositions.add(move.getEndPosition());
                if (startPosition == null) {
                    startPosition = move.getStartPosition();
                }
            }
        }

        drawHeaders(sb, isWhite);
        if (isWhite) {
            for (int row = BOARD_SIZE; row >= 1; row--) {
                drawRow(sb, row, isWhite, board, startPosition, endPositions);
            }
        } else {
            for (int row = 1; row <= BOARD_SIZE; row++) {
                drawRow(sb, row, isWhite, board, startPosition, endPositions);
            }
        }
        drawHeaders(sb, isWhite);

        return sb.toString();
    }

    public static String draw(String perspective, ChessBoard board) {
        return draw(perspective, board, null);
    }

    public static String draw(String perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return draw(perspective, board, null);
    }

    private static void drawHeaders(StringBuilder sb, boolean isWhite) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(EMPTY).append(" ");
        for (int col = 1; col <= BOARD_SIZE; col++) {
            int actualCol = isWhite ? col : (BOARD_SIZE - col + 1);
            char displayCol = (char) ('a' + actualCol - 1);
            sb.append("  ").append(displayCol).append("  "); 
        }
        sb.append(" ").append(EMPTY).append(" ").append(SET_BG_COLOR_DARK_GREY).append(RESET).append("\n");
    }

    private static void drawRow(StringBuilder sb, int row, boolean isWhite, ChessBoard board, ChessPosition startPos, Set<ChessPosition> endPos) {
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append("  ").append(row).append("  ");
        
        for (int col = 1; col <= BOARD_SIZE; col++) {
            int actualCol = isWhite ? col : (BOARD_SIZE - col + 1);
            ChessPosition currentPos = new ChessPosition(row, actualCol);

            boolean isHighlight = endPos != null && endPos.contains(currentPos);
            boolean isStart = startPos != null && startPos.equals(currentPos);

            if (isStart) {
                sb.append(SET_BG_COLOR_YELLOW);
            } else if (isHighlight) {
                if ((row + actualCol) % 2 == 0) {
                    sb.append(SET_BG_COLOR_DARK_GREEN);
                } else {
                    sb.append(SET_BG_COLOR_GREEN);
                }
            } else {
                if ((row + actualCol) % 2 == 0) {
                    sb.append(SET_BG_COLOR_BLACK);
                } else {
                    sb.append(SET_BG_COLOR_WHITE);
                }
            }

            ChessPiece piece = board.getPiece(currentPos);
            if (piece == null) {
                sb.append(" ").append(EMPTY).append(" "); 
            } else {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    sb.append(SET_TEXT_COLOR_RED);
                } else {
                    sb.append(SET_TEXT_COLOR_BLUE);
                }
                sb.append(" ").append(getPieceString(piece)).append(" ");
            }
        }

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append("  ").append(row).append("  ");
        sb.append(SET_BG_COLOR_DARK_GREY).append(RESET).append("\n");
    }

    private static String getPieceString(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}