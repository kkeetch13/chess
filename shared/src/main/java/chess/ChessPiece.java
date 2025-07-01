package chess;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (type) {
            case KING:
                addKingMoves(board, row, col, moves);
                break;
            case QUEEN:
                addQueenMoves(board, row, col, moves);
                break;
            case BISHOP:
                addBishopMoves(board, row, col, moves);
                break;
            case KNIGHT:
                addKnightMoves(board, row, col, moves);
                break;
            case ROOK:
                addRookMoves(board, row, col, moves);
                break;
            case PAWN:
                addPawnMoves(board, row, col, moves);
                break;
        }

        return moves;
    }

    private void addKingMoves(ChessBoard board, int row, int col, Collection<ChessMove> moves) {
        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        addMovesInDirections(board, row, col, moves, directions, 1);
        private void addMovesInDirections (ChessBoard board,int row, int col, Collection<ChessMove > moves,
        int[][] directions, int maxSteps){
            for (int[] direction : directions) {
                int newRow = row;
                int newCol = col;
                for (int step = 0; step < maxSteps; step++) {
                    newRow += direction[0];
                    newCol += direction[1];
                    if (!isValidPosition(newRow, newCol)) break;

                    ChessPosition endPosition = new ChessPosition(newRow, newCol);
                    ChessPiece pieceAtPosition = board.getPiece(endPosition);

                    if (pieceAtPosition == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col), endPosition, null));
                    } else {
                        if (pieceAtPosition.getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(new ChessPosition(row, col), endPosition, null));
                        }
                        break;
                    }
                }
            }
        }

        private boolean isValidPosition ( int row, int col){
            return row >= 1 && row <= 8 && col >= 1 && col <= 8;
        }
    }
}
