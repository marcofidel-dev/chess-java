package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    @Test
    void bishopOnEmptyBoardHas13Moves() {
        Piece[][] grid = new Piece[8][8];
        Bishop bishop = new Bishop(Color.WHITE);
        grid[4][4] = bishop;

        List<Position> moves = bishop.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertEquals(13, moves.size());
    }

    @Test
    void bishopBlockedDiagonally() {
        Piece[][] grid = new Piece[8][8];
        Bishop bishop = new Bishop(Color.WHITE);
        grid[4][4] = bishop;
        grid[6][6] = new Pawn(Color.WHITE); // Friendly

        List<Position> moves = bishop.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(5, 5)));
        assertFalse(moves.contains(Position.of(6, 6)));
        assertFalse(moves.contains(Position.of(7, 7)));
    }

    @Test
    void bishopCanCaptureEnemyDiagonally() {
        Piece[][] grid = new Piece[8][8];
        Bishop bishop = new Bishop(Color.WHITE);
        grid[4][4] = bishop;
        grid[6][6] = new Pawn(Color.BLACK); // Enemy

        List<Position> moves = bishop.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(6, 6)));
        assertFalse(moves.contains(Position.of(7, 7)));
    }
}
