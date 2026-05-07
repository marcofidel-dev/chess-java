package com.chess.model;

public enum PieceType {
    KING('K'),
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N'),
    PAWN('P');

    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static PieceType fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'K' -> KING;
            case 'Q' -> QUEEN;
            case 'R' -> ROOK;
            case 'B' -> BISHOP;
            case 'N' -> KNIGHT;
            case 'P' -> PAWN;
            default -> throw new IllegalArgumentException("Unknown piece: " + c);
        };
    }
}
