package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case KING:
                break;
            case QUEEN:
                // diagonal moves
                addMovesInDirection(moves, board, myPosition, 1, 1);
                addMovesInDirection(moves, board, myPosition, 1, -1);
                addMovesInDirection(moves, board, myPosition, -1, 1);
                addMovesInDirection(moves, board, myPosition, -1, -1);
                // cross moves
                addMovesInDirection(moves, board, myPosition, 0, 1);
                addMovesInDirection(moves, board, myPosition, 0, -1);
                addMovesInDirection(moves, board, myPosition, 1, 0);
                addMovesInDirection(moves, board, myPosition, -1, 0);
                break;
            case BISHOP:
                // diagonal moves
                addMovesInDirection(moves, board, myPosition, 1, 1);
                addMovesInDirection(moves, board, myPosition, 1, -1);
                addMovesInDirection(moves, board, myPosition, -1, 1);
                addMovesInDirection(moves, board, myPosition, -1, -1);
                break;
            case ROOK:
                // cross moves
                addMovesInDirection(moves, board, myPosition, 0, 1);
                addMovesInDirection(moves, board, myPosition, 0, -1);
                addMovesInDirection(moves, board, myPosition, 1, 0);
                addMovesInDirection(moves, board, myPosition, -1, 0);
                break;
            case KNIGHT:
                return List.of();
            case PAWN:
                return List.of();
        }
        return moves;
    }

    private void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, int rowShift, int colShift) {
        int currentRow = startPosition.getRow();
        int currentCol = startPosition.getColumn();

        while (true) {
            currentRow += rowShift;
            currentCol += colShift;

            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                break;
            }

            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtPosition = board.getPiece(newPosition);

            if (pieceAtPosition == null) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            } else {
                if (pieceAtPosition.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                }
                break;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessPiece that = (ChessPiece) obj;

        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
