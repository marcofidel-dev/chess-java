package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    private Bishop(Color color, boolean hasMoved) {
        super(color, PieceType.BISHOP, hasMoved);
    }

    @Override
    public List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget) {
        List<Position> moves = new ArrayList<>();
        slide(from, grid, moves, 1, 1);
        slide(from, grid, moves, 1, -1);
        slide(from, grid, moves, -1, 1);
        slide(from, grid, moves, -1, -1);
        return moves;
    }

    @Override
    public Piece copy() {
        return new Bishop(color, hasMoved);
    }
}
