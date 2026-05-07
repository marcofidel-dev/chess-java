package com.chess.engine;

import com.chess.board.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    @Test
    void initialPositionHas20LegalMoves() {
        Board board = new Board();
        MoveGenerator gen = new MoveGenerator();
        List<Move> moves = gen.generateLegalMoves(board);
        // 16 pawn moves (8 pawns × 2 each) + 4 knight moves
        assertEquals(20, moves.size());
    }

    @Test
    void kingIsNotInCheckAtStart() {
        Board board = new Board();
        MoveGenerator gen = new MoveGenerator();
        assertFalse(gen.isKingInCheck(board, Color.WHITE));
        assertFalse(gen.isKingInCheck(board, Color.BLACK));
    }

    @Test
    void castlingKingsidePossibleAfterClearingPieces() {
        Board board = new Board();
        MoveGenerator gen = new MoveGenerator();

        // Remove pieces between king and rook (white kingside: f1 and g1)
        board.removePiece(Position.of(0, 5)); // f1 Bishop
        board.removePiece(Position.of(0, 6)); // g1 Knight

        List<Move> moves = gen.generateLegalMoves(board);
        boolean hasCastle = moves.stream()
                .anyMatch(m -> m.getType() == com.chess.model.MoveType.CASTLE_KINGSIDE
                        && m.getPiece().getColor() == Color.WHITE);
        assertTrue(hasCastle, "White should be able to castle kingside");
    }

    @Test
    void castlingQueensidePossibleAfterClearingPieces() {
        Board board = new Board();
        MoveGenerator gen = new MoveGenerator();

        // Remove pieces between king and rook (white queenside: b1, c1, d1)
        board.removePiece(Position.of(0, 1)); // b1 Knight
        board.removePiece(Position.of(0, 2)); // c1 Bishop
        board.removePiece(Position.of(0, 3)); // d1 Queen

        List<Move> moves = gen.generateLegalMoves(board);
        boolean hasCastle = moves.stream()
                .anyMatch(m -> m.getType() == com.chess.model.MoveType.CASTLE_QUEENSIDE
                        && m.getPiece().getColor() == Color.WHITE);
        assertTrue(hasCastle, "White should be able to castle queenside");
    }

    @Test
    void squareAttackedByPawn() {
        Board board = new Board();
        MoveGenerator gen = new MoveGenerator();

        // e4 pawn attacks d5 and f5
        assertTrue(gen.isSquareAttacked(board, Position.fromAlgebraic("d3"), Color.WHITE));
        assertTrue(gen.isSquareAttacked(board, Position.fromAlgebraic("f3"), Color.WHITE));
    }
}
