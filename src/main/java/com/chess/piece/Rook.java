package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    private Rook(Color color, boolean hasMoved) {
        super(color, PieceType.ROOK, hasMoved);
    }

    @Override
    public List<Position> getPseudoLegalMoves(Position from, Piece[][] grid, Position enPassantTarget) {
        List<Position> moves = new ArrayList<>();
        slide(from, grid, moves, 1, 0);
        slide(from, grid, moves, -1, 0);
        slide(from, grid, moves, 0, 1);
        slide(from, grid, moves, 0, -1);
        return moves;
    }

    @Override
    public Piece copy() {
        return new Rook(color, hasMoved);
    }
}
