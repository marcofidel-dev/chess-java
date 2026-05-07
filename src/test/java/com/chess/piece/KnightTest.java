package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void knightInCenterHasEightMoves() {
        Piece[][] grid = new Piece[8][8];
        Knight knight = new Knight(Color.WHITE);
        grid[4][4] = knight;
        Position from = Position.of(4, 4);

        List<Position> moves = knight.getPseudoLegalMoves(from, grid, null);
        assertEquals(8, moves.size());
    }

    @Test
    void knightInCornerHasTwoMoves() {
        Piece[][] grid = new Piece[8][8];
        Knight knight = new Knight(Color.WHITE);
        grid[0][0] = knight;
        Position from = Position.of(0, 0);

        List<Position> moves = knight.getPseudoLegalMoves(from, grid, null);
        assertEquals(2, moves.size());
    }

    @Test
    void knightCannotLandOnFriendlyPiece() {
        Piece[][] grid = new Piece[8][8];
        Knight knight = new Knight(Color.WHITE);
        grid[4][4] = knight;
        grid[5][6] = new Pawn(Color.WHITE); // Friendly piece

        List<Position> moves = knight.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertFalse(moves.contains(Position.of(5, 6)));
        assertEquals(7, moves.size());
    }

    @Test
    void knightCanCaptureEnemy() {
        Piece[][] grid = new Piece[8][8];
        Knight knight = new Knight(Color.WHITE);
        grid[4][4] = knight;
        grid[5][6] = new Pawn(Color.BLACK); // Enemy piece

        List<Position> moves = knight.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(5, 6)));
    }

    @Test
    void knightJumpsOverPieces() {
        Piece[][] grid = new Piece[8][8];
        Knight knight = new Knight(Color.WHITE);
        grid[0][1] = knight;
        // Fill surrounding squares
        grid[0][0] = new Pawn(Color.WHITE);
        grid[0][2] = new Pawn(Color.WHITE);
        grid[1][0] = new Pawn(Color.WHITE);
        grid[1][1] = new Pawn(Color.WHITE);
        grid[1][2] = new Pawn(Color.WHITE);

        List<Position> moves = knight.getPseudoLegalMoves(Position.of(0, 1), grid, null);
        // Knight can still jump: (2,0) and (2,2)
        assertTrue(moves.contains(Position.of(2, 0)));
        assertTrue(moves.contains(Position.of(2, 2)));
    }
}
