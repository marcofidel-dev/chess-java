package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    private Pawn(Color color, boolean hasMoved) {
        super(color, PieceType.PAWN, hasMoved);
    }

    @Override
    public List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.WHITE) ? 1 : -1;
        int startRow  = (color == Color.WHITE) ? 1 : 6;

        // Forward 1
        Position oneForward = from.offset(direction, 0);
        if (oneForward != null && grid[oneForward.getRow()][oneForward.getCol()] == null) {
            moves.add(oneForward);

            // Forward 2 from starting row
            if (from.getRow() == startRow) {
                Position twoForward = from.offset(direction * 2, 0);
                if (twoForward != null && grid[twoForward.getRow()][twoForward.getCol()] == null) {
                    moves.add(twoForward);
                }
            }
        }

        // Diagonal captures
        for (int colDelta : new int[]{-1, 1}) {
            Position diag = from.offset(direction, colDelta);
            if (diag != null) {
                Piece target = grid[diag.getRow()][diag.getCol()];
                if (isEnemy(target)) {
                    moves.add(diag);
                }
                // En passant
                if (diag.equals(enPassantTarget)) {
                    moves.add(diag);
                }
            }
        }

        return moves;
    }

    @Override
    public Piece copy() {
        return new Pawn(color, hasMoved);
    }
}
