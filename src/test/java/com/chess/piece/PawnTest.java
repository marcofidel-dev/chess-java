package com.chess.piece;

import com.chess.model.Color;
import com.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void whitePawnMovesForwardOneFromStart() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[1][4] = pawn;

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(1, 4), grid, null);
        assertTrue(moves.contains(Position.of(2, 4)));
    }

    @Test
    void whitePawnCanMoveTwoSquaresFromStartRow() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[1][4] = pawn;

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(1, 4), grid, null);
        assertTrue(moves.contains(Position.of(3, 4)));
    }

    @Test
    void pawnCannotMoveTwoSquaresIfBlocked() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[1][4] = pawn;
        grid[2][4] = new Pawn(Color.BLACK); // Blocking piece

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(1, 4), grid, null);
        assertFalse(moves.contains(Position.of(2, 4)));
        assertFalse(moves.contains(Position.of(3, 4)));
    }

    @Test
    void pawnCapturesDiagonally() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[4][4] = pawn;
        grid[5][3] = new Pawn(Color.BLACK);
        grid[5][5] = new Pawn(Color.BLACK);

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertTrue(moves.contains(Position.of(5, 3)));
        assertTrue(moves.contains(Position.of(5, 5)));
    }

    @Test
    void pawnDoesNotCaptureFriendlyPiece() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[4][4] = pawn;
        grid[5][3] = new Pawn(Color.WHITE); // Friendly

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(4, 4), grid, null);
        assertFalse(moves.contains(Position.of(5, 3)));
    }

    @Test
    void whitePawnEnPassant() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.WHITE);
        grid[4][4] = pawn;
        grid[4][5] = new Pawn(Color.BLACK); // Black pawn that just moved 2 squares

        Position enPassantTarget = Position.of(5, 5); // Square behind black pawn
        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(4, 4), grid, enPassantTarget);
        assertTrue(moves.contains(Position.of(5, 5)));
    }

    @Test
    void blackPawnMovesBackward() {
        Piece[][] grid = new Piece[8][8];
        Pawn pawn = new Pawn(Color.BLACK);
        grid[6][3] = pawn;

        List<Position> moves = pawn.getPseudoLegalMoves(Position.of(6, 3), grid, null);
        assertTrue(moves.contains(Position.of(5, 3)));
        assertTrue(moves.contains(Position.of(4, 3))); // Two squares from start
    }
}
