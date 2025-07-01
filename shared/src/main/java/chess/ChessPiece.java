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
    }

    private void addQueenMoves(ChessBoard board, int row, int col, Collection<ChessMove> moves) {
        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        addMovesInDirections(board, row, col, moves, directions, 7);
    }

    private void addBishopMoves(ChessBoard board, int row, int col, Collection<ChessMove> moves) {
        int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        addMovesInDirections(board, row, col, moves, directions, 7);
    }

    private void addRookMoves(ChessBoard board, int row, int col, Collection<ChessMove> moves) {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        addMovesInDirections(board, row, col, moves, directions, 7);
    }

    private void addKnightMoves(ChessBoard board, int row, int col, Collection<ChessMove> validMoves) {
        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol)) {
                ChessPosition endPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtPosition = board.getPiece(endPosition);
                if (pieceAtPosition == null || pieceAtPosition.getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(new ChessPosition(row, col), endPosition, null));
                }
            }
        }
    }

    private void addPawnMoves(ChessBoard board, int row, int col, Collection<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;

        int newRow = row + direction;
        if (isValidPosition(newRow, col)) {
            ChessPosition endPosition = new ChessPosition(newRow, col);
            if (board.getPiece(endPosition) == null) {
                addPawnMove(moves, row, col, newRow, col);

                if (row == startRow) {
                    newRow = row + 2 * direction;
                    endPosition = new ChessPosition(newRow, col);
                    if (board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col), endPosition, null));
                    }
                }
            }
        }

        for (int colOffset : new int[]{-1, 1}) {
            newRow = row + direction;
            int newCol = col + colOffset;
            if (isValidPosition(newRow, newCol)) {
                ChessPosition endPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtPosition = board.getPiece(endPosition);
                if (pieceAtPosition != null && pieceAtPosition.getTeamColor() != pieceColor) {
                    addPawnMove(moves, row, col, newRow, newCol);
                }
            }
        }
    }

    private void addPawnMove(Collection<ChessMove> moves, int startRow, int startCol, int endRow, int endCol) {
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);


        if (endRow == 8 || endRow == 1) {
            moves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
            moves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
            moves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
            moves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
    }

    private void addMovesInDirections(ChessBoard board, int row, int col, Collection<ChessMove> moves,
                                      int[][] directions, int maxSteps) {
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

    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}