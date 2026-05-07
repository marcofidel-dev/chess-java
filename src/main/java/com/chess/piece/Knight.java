package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    private static final int[][] DELTAS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    private Knight(Color color, boolean hasMoved) {
        super(color, PieceType.KNIGHT, hasMoved);
    }

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
        return new Knight(color, hasMoved);
    }
}
