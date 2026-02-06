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
                // diagonal single move
                singleMoveInDirection(moves, board, myPosition, 1, 1);
                singleMoveInDirection(moves, board, myPosition, 1, -1);
                singleMoveInDirection(moves, board, myPosition, -1, 1);
                singleMoveInDirection(moves, board, myPosition, -1, -1);
                // cross single move
                singleMoveInDirection(moves, board, myPosition, 0, 1);
                singleMoveInDirection(moves, board, myPosition, 0, -1);
                singleMoveInDirection(moves, board, myPosition, 1, 0);
                singleMoveInDirection(moves, board, myPosition, -1, 0);
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
                singleMoveInDirection(moves, board, myPosition, 2, 1);
                singleMoveInDirection(moves, board, myPosition, 2, -1);
                singleMoveInDirection(moves, board, myPosition, -2, 1);
                singleMoveInDirection(moves, board, myPosition, -2, -1);
                singleMoveInDirection(moves, board, myPosition, 1, 2);
                singleMoveInDirection(moves, board, myPosition, 1, -2);
                singleMoveInDirection(moves, board, myPosition, -1, 2);
                singleMoveInDirection(moves, board, myPosition, -1, -2);
                break;
            case PAWN:
                pawnMoves(moves, board, myPosition);
                break;
        }
        return moves;
    }

    private void pawnMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        ChessPosition oneStep = new ChessPosition(currentRow + direction, currentCol);
        if (oneStep.getRow() >= 1 && oneStep.getRow() <= 8) {
            if (board.getPiece(oneStep) == null) {
                if (oneStep.getRow() == promotionRow) {
                    addPromotionMoves(moves, myPosition, oneStep);
                } else {
                    moves.add(new ChessMove(myPosition, oneStep, null));
                }

                if (currentRow == startRow) {
                    ChessPosition twoStep = new ChessPosition(currentRow + 2 * direction, currentCol);
                    if (board.getPiece(twoStep) == null) {
                        moves.add(new ChessMove(myPosition, twoStep, null));
                    }
                }
            }
        }

        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int captureCol : captureCols) {
            if (captureCol >= 1 && captureCol <= 8) {
                ChessPosition capturePos = new ChessPosition(currentRow + direction, captureCol);
                if (capturePos.getRow() >= 1 && capturePos.getRow() <= 8) {
                    ChessPiece targetPiece = board.getPiece(capturePos);
                    if (targetPiece != null && targetPiece.getTeamColor() != pieceColor) {
                        if (capturePos.getRow() == promotionRow) {
                            addPromotionMoves(moves, myPosition, capturePos);
                        } else {
                            moves.add(new ChessMove(myPosition, capturePos, null));
                        }
                    }
                }
            }
        }
    }

    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }

    private void singleMoveInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, int rowShift, int colShift) {
        int checkRow = startPosition.getRow() + rowShift;
        int checkCol = startPosition.getColumn() + colShift;

        if (checkRow < 1 || checkRow > 8 || checkCol < 1 || checkCol > 8) {
            return;
        }

        ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
        ChessPiece pieceAtPosition = board.getPiece(newPosition);

        if (pieceAtPosition == null) {
            moves.add(new ChessMove(startPosition, newPosition, null));
        } else {
            if (pieceAtPosition.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
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
    public String toString() {
        return String.format("{%s %s}", pieceColor, type);
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
