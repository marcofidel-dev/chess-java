package com.chess.engine;

import com.chess.board.Board;
import com.chess.model.Color;
import com.chess.model.GameState;
import com.chess.model.Move;
import com.chess.model.Position;
import com.chess.model.PieceType;
import com.chess.piece.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controls the game flow: applies moves, updates state, detects end conditions.
 */
public class GameEngine {

    private final Board board;
    private final MoveGenerator moveGenerator;
    private GameState gameState;
    private final List<Move> moveHistory;

    public GameEngine() {
        this.board = new Board();
        this.moveGenerator = new MoveGenerator();
        this.gameState = GameState.PLAYING;
        this.moveHistory = new ArrayList<>();
    }

    // ---- Move handling ----

    /**
     * Attempts to make a move. Returns true if successful, false if illegal.
     */
    public boolean makeMove(Move move) {
        if (gameState == GameState.CHECKMATE || gameState == GameState.STALEMATE
                || gameState == GameState.DRAW_FIFTY_MOVES
                || gameState == GameState.DRAW_INSUFFICIENT_MATERIAL) {
            return false;
        }

        List<Move> legal = moveGenerator.generateLegalMoves(board);
        Optional<Move> matched = legal.stream().filter(m -> m.equals(move)).findFirst();
        if (matched.isEmpty()) return false;

        Move legalMove = matched.get();
        applyMoveToBoard(legalMove);
        moveHistory.add(legalMove);
        updateGameState();
        return true;
    }

    /**
     * Attempts a move by algebraic square notation (e.g. "e2" -> "e4").
     */
    public boolean makeMove(String from, String to) {
        return makeMove(from, to, PieceType.QUEEN);
    }

    public boolean makeMove(String from, String to, PieceType promotionPiece) {
        try {
            Position fromPos = Position.fromAlgebraic(from);
            Position toPos   = Position.fromAlgebraic(to);
            Piece piece = board.getPieceAt(fromPos);
            if (piece == null) return false;

            // Find matching legal move
            List<Move> legal = moveGenerator.generateLegalMoves(board);
            for (Move m : legal) {
                if (m.getFrom().equals(fromPos) && m.getTo().equals(toPos)) {
                    boolean isPromotion = m.getType() == com.chess.model.MoveType.PROMOTION
                            || m.getType() == com.chess.model.MoveType.PROMOTION_CAPTURE;
                    if (!isPromotion || m.getPromotedTo() == promotionPiece) {
                        applyMoveToBoard(m);
                        moveHistory.add(m);
                        updateGameState();
                        return true;
                    }
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void applyMoveToBoard(Move move) {
        boolean isCapture = move.isCapture();
        boolean isPawnMove = move.getPiece().getType() == PieceType.PAWN;

        moveGenerator.applyMove(board, move);

        // Half-move clock
        if (isCapture || isPawnMove) {
            board.resetHalfMoveClock();
        } else {
            board.incrementHalfMoveClock();
        }

        // Full-move number increments after Black's move
        if (board.getCurrentTurn() == Color.WHITE) {
            board.incrementFullMoveNumber();
        }

        board.setCurrentTurn(board.getCurrentTurn().opposite());
        board.addPositionToHistory(board.toFenPosition());
    }

    private void updateGameState() {
        Color current = board.getCurrentTurn();
        boolean inCheck = moveGenerator.isKingInCheck(board, current);
        List<Move> legal = moveGenerator.generateLegalMoves(board);

        if (legal.isEmpty()) {
            gameState = inCheck ? GameState.CHECKMATE : GameState.STALEMATE;
        } else if (board.getHalfMoveClock() >= 100) {
            gameState = GameState.DRAW_FIFTY_MOVES;
        } else if (hasInsufficientMaterial()) {
            gameState = GameState.DRAW_INSUFFICIENT_MATERIAL;
        } else if (inCheck) {
            gameState = GameState.CHECK;
        } else {
            gameState = GameState.PLAYING;
        }
    }

    private boolean hasInsufficientMaterial() {
        List<Piece> whitePieces = new ArrayList<>();
        List<Piece> blackPieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board.getPieceAt(row, col);
                if (p == null) continue;
                if (p.getColor() == Color.WHITE) whitePieces.add(p);
                else blackPieces.add(p);
            }
        }

        // King vs King
        if (whitePieces.size() == 1 && blackPieces.size() == 1) return true;

        // King + minor piece vs King
        if (isKingPlusMinor(whitePieces) && blackPieces.size() == 1) return true;
        if (isKingPlusMinor(blackPieces) && whitePieces.size() == 1) return true;

        // King + Bishop vs King + Bishop (same colored squares) - simplified
        return false;
    }

    private boolean isKingPlusMinor(List<Piece> pieces) {
        if (pieces.size() != 2) return false;
        return pieces.stream().anyMatch(p ->
                p.getType() == PieceType.BISHOP || p.getType() == PieceType.KNIGHT);
    }

    // ---- Queries ----

    public Board getBoard() { return board; }
    public GameState getGameState() { return gameState; }
    public List<Move> getMoveHistory() { return List.copyOf(moveHistory); }
    public MoveGenerator getMoveGenerator() { return moveGenerator; }

    public List<Move> getLegalMovesFrom(String position) {
        try {
            Position pos = Position.fromAlgebraic(position);
            return moveGenerator.generateLegalMoves(board).stream()
                    .filter(m -> m.getFrom().equals(pos))
                    .toList();
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public boolean isGameOver() {
        return gameState == GameState.CHECKMATE
                || gameState == GameState.STALEMATE
                || gameState == GameState.DRAW_FIFTY_MOVES
                || gameState == GameState.DRAW_INSUFFICIENT_MATERIAL;
    }

    public Color getWinner() {
        if (gameState == GameState.CHECKMATE) {
            return board.getCurrentTurn().opposite();
        }
        return null;
    }

    /** Returns a human-readable description of the current game state. */
    public String getStateDescription() {
        return switch (gameState) {
            case PLAYING  -> board.getCurrentTurn() + " to move";
            case CHECK    -> board.getCurrentTurn() + " is in CHECK";
            case CHECKMATE -> board.getCurrentTurn().opposite() + " wins by CHECKMATE";
            case STALEMATE -> "STALEMATE - Draw";
            case DRAW_FIFTY_MOVES -> "DRAW - Fifty-move rule";
            case DRAW_INSUFFICIENT_MATERIAL -> "DRAW - Insufficient material";
        };
    }
}
