package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentTurn = TeamColor.WHITE;
    ChessBoard currentBoard;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (currentBoard.getPiece(startPosition) == null) return null;
        Collection<ChessMove> availableMoveList = currentBoard.getPiece(startPosition).pieceMoves(currentBoard, startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();

        for (ChessMove move : availableMoveList) {
            ChessPiece endPositionPieceSave = currentBoard.getPiece(move.getEndPosition());

            currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
            currentBoard.addPiece(move.getStartPosition(), null);

            if (!isInCheck(currentTurn)) validMoveList.add(move);

            currentBoard.addPiece(move.getStartPosition(), currentBoard.getPiece(move.getEndPosition()));
            currentBoard.addPiece(move.getEndPosition(), endPositionPieceSave);
        }

        return validMoveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece pieceAtPosition = currentBoard.getPiece(move.getStartPosition());

        if (validMoves == null || validMoves.isEmpty() || !validMoves.contains(move) || pieceAtPosition.getTeamColor() != currentTurn) throw new InvalidMoveException();

        ChessPiece setPiece = move.getPromotionPiece() == null ? pieceAtPosition : new ChessPiece(pieceAtPosition.getTeamColor(), move.getPromotionPiece());
        currentBoard.addPiece(move.getEndPosition(), setPiece);
        currentBoard.addPiece(move.getStartPosition(), null);

        if (currentTurn == TeamColor.WHITE) {
            currentTurn = TeamColor.BLACK;
        } else {
            currentTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);

        if (kingPosition.getRow() == 0 && kingPosition.getColumn() == 0) return false;

        if (
                checkInDirection(kingPosition, ChessPiece.PieceType.BISHOP, 1, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.BISHOP, 1, -1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.BISHOP, -1, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.BISHOP, -1, -1)
        ) return true;
        if (
                checkInDirection(kingPosition, ChessPiece.PieceType.ROOK, 0, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.ROOK, 0, -1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.ROOK, 1, 0)
                || checkInDirection(kingPosition, ChessPiece.PieceType.ROOK, -1, 0)
        ) return true;
        if (
                checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, 1, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, 1, -1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, -1, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, -1, -1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, 0, 1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, 0, -1)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, 1, 0)
                || checkInDirection(kingPosition, ChessPiece.PieceType.QUEEN, -1, 0)
        ) return true;
        if (
                checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, 2, 1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, 1, 2)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, -2, 1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, -1, 2)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, 2, -1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, 1, -2)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, -2, -1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KNIGHT, -1, -2)
        ) return true;
        if (
                checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, 1, 1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, 1, -1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, -1, 1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, -1, -1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, 0, 1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, 0, -1)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, 1, 0)
                || checkSingleDirction(kingPosition, ChessPiece.PieceType.KING, -1, 0)
        ) return true;

        int direction = (currentTurn == ChessGame.TeamColor.WHITE) ? 1 : -1;

        return checkInDirection(kingPosition, ChessPiece.PieceType.PAWN, direction, 1) || checkInDirection(kingPosition, ChessPiece.PieceType.PAWN, direction, -1);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    private boolean checkInDirection(ChessPosition kingPosition, ChessPiece.PieceType threatPiece, int rowShift, int colShift) {
        int currentRow = kingPosition.getRow();
        int currentCol = kingPosition.getColumn();

        while (true) {
            currentRow += rowShift;
            currentCol += colShift;

            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) break;

            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtPosition = currentBoard.getPiece(newPosition);

            if (pieceAtPosition != null && pieceAtPosition.getPieceType() == threatPiece && pieceAtPosition.getTeamColor() != currentTurn) return true;
        }
        return false;
    }

    private boolean checkSingleDirction(ChessPosition kingPosition, ChessPiece.PieceType threatPiece, int rowShift, int colShift) {
        int checkRow = kingPosition.getRow() + rowShift;
        int checkCol = kingPosition.getColumn() + colShift;

        if (checkRow < 1 || checkRow > 8 || checkCol < 1 || checkCol > 8) {
            return false;
        }

        ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
        ChessPiece pieceAtPosition = currentBoard.getPiece(newPosition);

        if (pieceAtPosition == null) {
            return false;
        } else {
            if (pieceAtPosition.getTeamColor() != currentTurn && pieceAtPosition.getPieceType() == threatPiece) {
                return true;
            }
        }

        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = currentBoard.getPiece(currentPosition);
                if (pieceAtPosition != null && pieceAtPosition.getPieceType() == ChessPiece.PieceType.KING && pieceAtPosition.getTeamColor() == teamColor) {
                    return currentPosition;
                }
            }
        }

        return new ChessPosition(0, 0);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessGame that = (ChessGame) obj;

        return Objects.equals(currentTurn, that.currentTurn) && Objects.equals(currentBoard, that.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, currentBoard);
    }
}
