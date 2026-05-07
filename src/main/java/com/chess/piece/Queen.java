package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    private Queen(Color color, boolean hasMoved) {
        super(color, PieceType.QUEEN, hasMoved);
    }

    @Override
    public List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget) {
        List<Position> moves = new ArrayList<>();
        // Orthogonal (rook directions)
        slide(from, grid, moves, 1, 0);
        slide(from, grid, moves, -1, 0);
        slide(from, grid, moves, 0, 1);
        slide(from, grid, moves, 0, -1);
        // Diagonal (bishop directions)
        slide(from, grid, moves, 1, 1);
        slide(from, grid, moves, 1, -1);
        slide(from, grid, moves, -1, 1);
        slide(from, grid, moves, -1, -1);
        return moves;
    }

    @Override
    public Piece copy() {
        return new Queen(color, hasMoved);
    }
}
