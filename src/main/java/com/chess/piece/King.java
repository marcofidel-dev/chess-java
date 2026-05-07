package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public King(Color color) {
        super(color, PieceType.KING);
    }

    private King(Color color, boolean hasMoved) {
        super(color, PieceType.KING, hasMoved);
    }

    /**
     * Returns normal king moves (1 square in any direction).
     * Castling is handled separately in MoveGenerator.
     */
    @Override
    public List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget) {
        List<Position> moves = new ArrayList<>();
        for (int[] d : DELTAS) {
            Position target = from.offset(d[0], d[1]);
            if (target != null && !isFriendly(grid[target.getRow()][target.getCol()])) {
                moves.add(target);
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new King(color, hasMoved);
    }
}
