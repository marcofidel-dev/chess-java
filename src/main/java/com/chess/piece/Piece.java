package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.List;

public abstract class Piece {

    protected final Color color;
    protected final PieceType type;
    protected boolean hasMoved;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    protected Piece(Color color, PieceType type, boolean hasMoved) {
        this.color = color;
        this.type = type;
        this.hasMoved = hasMoved;
    }

    /**
     * Returns all pseudo-legal destination squares (without considering check).
     * En passant target square is provided for pawns.
     */
    public abstract List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget);

    /** Creates a deep copy of this piece. */
    public abstract Piece copy();

    public Color getColor() { return color; }
    public PieceType getType() { return type; }
    public boolean hasMoved() { return hasMoved; }
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }

    public boolean isEnemy(Piece other) {
        return other != null && other.color != this.color;
    }

    public boolean isFriendly(Piece other) {
        return other != null && other.color == this.color;
    }

    public String getUnicodeSymbol() {
        return switch (type) {
            case KING   -> color == Color.WHITE ? "♔" : "♚";
            case QUEEN  -> color == Color.WHITE ? "♕" : "♛";
            case ROOK   -> color == Color.WHITE ? "♖" : "♜";
            case BISHOP -> color == Color.WHITE ? "♗" : "♝";
            case KNIGHT -> color == Color.WHITE ? "♘" : "♞";
            case PAWN   -> color == Color.WHITE ? "♙" : "♟";
        };
    }

    @Override
    public String toString() {
        char symbol = type.getSymbol();
        return color == Color.WHITE ? String.valueOf(Character.toUpperCase(symbol))
                                    : String.valueOf(Character.toLowerCase(symbol));
    }

    /** Shared utility: slide in a direction until blocked or board edge. */
    protected void slide(Position from, Piece[][] grid, List<Position> moves, int rowDelta, int colDelta) {
        Position current = from.offset(rowDelta, colDelta);
        while (current != null) {
            Piece target = grid[current.getRow()][current.getCol()];
            if (target == null) {
                moves.add(current);
            } else {
                if (isEnemy(target)) moves.add(current);
                break;
            }
            current = current.offset(rowDelta, colDelta);
        }
    }
}
