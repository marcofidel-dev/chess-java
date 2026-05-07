package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    @Test
    void rookOnEmptyBoardHas14Moves() {
        Piece[][] grid = new Piece[8][8];
        Rook rook = new Rook(Color.WHITE);
        grid[4][4] = rook;

        List<Position> moves = rook.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertEquals(14, moves.size());
    }

    @Test
    void rookBlockedByFriendlyPiece() {
        Piece[][] grid = new Piece[8][8];
        Rook rook = new Rook(Color.WHITE);
        grid[4][4] = rook;
        grid[4][6] = new Pawn(Color.WHITE); // Friendly piece in the way

        List<Position> moves = rook.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(4, 5)));
        assertFalse(moves.contains(Position.of(4, 6)));
        assertFalse(moves.contains(Position.of(4, 7)));
    }

    @Test
    void rookCanCaptureEnemy() {
        Piece[][] grid = new Piece[8][8];
        Rook rook = new Rook(Color.WHITE);
        grid[4][4] = rook;
        grid[4][6] = new Pawn(Color.BLACK); // Enemy

        List<Position> moves = rook.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(4, 6)));
        assertFalse(moves.contains(Position.of(4, 7))); // Can't go past the enemy
    }
}
