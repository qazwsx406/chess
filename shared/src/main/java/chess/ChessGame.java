package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentTurn;
    ChessBoard currentBoard;
    boolean isFinished = false;

    public ChessGame() {
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        currentTurn = TeamColor.WHITE;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
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
        if (currentBoard.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> availableMoveList = currentBoard.getPiece(startPosition).pieceMoves(currentBoard, startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();

        for (ChessMove move : availableMoveList) {
            ChessPiece endPositionPieceSave = currentBoard.getPiece(move.getEndPosition());

            currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
            currentBoard.addPiece(move.getStartPosition(), null);

            if (!isInCheck(currentBoard.getPiece(move.getEndPosition()).getTeamColor())) {
                validMoveList.add(move);
            }

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
        if (isFinished) {
            throw new InvalidMoveException("Game is over");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());    
        ChessPiece pieceAtPosition = currentBoard.getPiece(move.getStartPosition());

        if (validMoves == null || validMoves.isEmpty() || !validMoves.contains(move)
                || pieceAtPosition.getTeamColor() != currentTurn) {
            throw new InvalidMoveException();
        }

        ChessPiece setPiece = move.getPromotionPiece() == null ? pieceAtPosition : 
                new ChessPiece(pieceAtPosition.getTeamColor(), move.getPromotionPiece());
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

        if (kingPosition.getRow() == 0 && kingPosition.getColumn() == 0) {
            return false;
        }

        int[][] slidingDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : slidingDirections) {
            if (checkInDirection(kingPosition, dir[0], dir[1])) {
                return true;
            }
        }

        int[][] knightMoves = {{2, 1}, {1, 2}, {-2, 1}, {-1, 2}, {2, -1}, {1, -2}, {-2, -1}, {-1, -2}};
        for (int[] move : knightMoves) {
            if (checkAtPosition(kingPosition, ChessPiece.PieceType.KNIGHT, move[0], move[1])) {
                return true;
            }
        }

        int[][] kingMoves = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] move : kingMoves) {
            if (checkAtPosition(kingPosition, ChessPiece.PieceType.KING, move[0], move[1])) {
                return true;
            }
        }

        int pawnDir = (teamColor == TeamColor.WHITE) ? 1 : -1;
        return checkAtPosition(kingPosition, ChessPiece.PieceType.PAWN, pawnDir, 1)
                || checkAtPosition(kingPosition, ChessPiece.PieceType.PAWN, pawnDir, -1);
    }

    private boolean checkInDirection(ChessPosition kingPos, int rowShift, int colShift) {
        int r = kingPos.getRow() + rowShift;
        int c = kingPos.getColumn() + colShift;
        TeamColor color = currentBoard.getPiece(kingPos).getTeamColor();

        while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPiece piece = currentBoard.getPiece(new ChessPosition(r, c));
            if (piece != null) {
                if (piece.getTeamColor() != color) {
                    ChessPiece.PieceType type = piece.getPieceType();
                    if (type == ChessPiece.PieceType.QUEEN) {
                        return true;
                    }
                    if ((rowShift != 0 && colShift != 0) && type == ChessPiece.PieceType.BISHOP) {
                        return true;
                    }
                    if ((rowShift == 0 || colShift == 0) && type == ChessPiece.PieceType.ROOK) {
                        return true;
                    }
                }
                break;
            }
            r += rowShift;
            c += colShift;
        }
        return false;
    }

    private boolean checkAtPosition(ChessPosition kingPos, ChessPiece.PieceType type, int rowShift, int colShift) {
        int r = kingPos.getRow() + rowShift;
        int c = kingPos.getColumn() + colShift;

        if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPiece piece = currentBoard.getPiece(new ChessPosition(r, c));
            return piece != null && piece.getTeamColor() != currentBoard.getPiece(kingPos).getTeamColor() 
                   && piece.getPieceType() == type;
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);       
                ChessPiece pieceAtPosition = currentBoard.getPiece(currentPosition);
                if (pieceAtPosition != null && pieceAtPosition.getPieceType() == ChessPiece.PieceType.KING
                        && pieceAtPosition.getTeamColor() == teamColor) {
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
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessGame that = (ChessGame) obj;

        return Objects.equals(currentTurn, that.currentTurn) && Objects.equals(currentBoard, that.currentBoard) && isFinished == that.isFinished;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, currentBoard, isFinished);
    }
}