package com.chess.model;

import com.chess.piece.Piece;

import java.util.Objects;

public final class Move {

    private final Position from;
    private final Position to;
    private final Piece piece;
    private final Piece capturedPiece;
    private final MoveType type;
    private final PieceType promotedTo;

    public Move(Position from, Position to, Piece piece, Piece capturedPiece, MoveType type, PieceType promotedTo) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.piece = Objects.requireNonNull(piece);
        this.capturedPiece = capturedPiece;
        this.type = Objects.requireNonNull(type);
        this.promotedTo = promotedTo;
    }

    public static Move normal(Position from, Position to, Piece piece) {
        return new Move(from, to, piece, null, MoveType.NORMAL, null);
    }

    public static Move capture(Position from, Position to, Piece piece, Piece captured) {
        return new Move(from, to, piece, captured, MoveType.CAPTURE, null);
    }

    public static Move castleKingside(Position from, Position to, Piece king) {
        return new Move(from, to, king, null, MoveType.CASTLE_KINGSIDE, null);
    }

    public static Move castleQueenside(Position from, Position to, Piece king) {
        return new Move(from, to, king, null, MoveType.CASTLE_QUEENSIDE, null);
    }

    public static Move enPassant(Position from, Position to, Piece pawn, Piece capturedPawn) {
        return new Move(from, to, pawn, capturedPawn, MoveType.EN_PASSANT, null);
    }

    public static Move promotion(Position from, Position to, Piece pawn, PieceType promotedTo) {
        return new Move(from, to, pawn, null, MoveType.PROMOTION, promotedTo);
    }

    public static Move promotionCapture(Position from, Position to, Piece pawn, Piece captured, PieceType promotedTo) {
        return new Move(from, to, pawn, captured, MoveType.PROMOTION_CAPTURE, promotedTo);
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getPiece() { return piece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public MoveType getType() { return type; }
    public PieceType getPromotedTo() { return promotedTo; }

    public boolean isCapture() {
        return type == MoveType.CAPTURE || type == MoveType.EN_PASSANT || type == MoveType.PROMOTION_CAPTURE;
    }

    /** Algebraic notation (e.g. "e2e4", "e7e8Q" for promotion) */
    public String toAlgebraic() {
        String move = from.toAlgebraic() + to.toAlgebraic();
        if (promotedTo != null) {
            move += Character.toLowerCase(promotedTo.getSymbol());
        }
        return move;
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move m)) return false;
        return from.equals(m.from) && to.equals(m.to)
                && Objects.equals(promotedTo, m.promotedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotedTo);
    }
}
