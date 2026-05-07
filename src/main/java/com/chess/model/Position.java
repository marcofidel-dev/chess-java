package com.chess.model;

import java.util.Objects;

/**
 * Represents a square on the chess board.
 * Row 0 = rank 1 (white's back rank), Row 7 = rank 8 (black's back rank).
 * Col 0 = file a, Col 7 = file h.
 */
public final class Position {

    private final int row;
    private final int col;

    public Position(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Position out of bounds: row=" + row + ", col=" + col);
        }
        this.row = row;
        this.col = col;
    }

    public static Position of(int row, int col) {
        return new Position(row, col);
    }

    /** Parses algebraic notation: "e4" -> row=3, col=4 */
    public static Position fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid notation: " + notation);
        }
        int col = notation.charAt(0) - 'a';
        int row = notation.charAt(1) - '1';
        return new Position(row, col);
    }

    public String toAlgebraic() {
        return "" + (char) ('a' + col) + (char) ('1' + row);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    /** Returns a new position offset by (rowDelta, colDelta), or null if out of bounds. */
    public Position offset(int rowDelta, int colDelta) {
        int r = row + rowDelta;
        int c = col + colDelta;
        if (r < 0 || r > 7 || c < 0 || c > 7) return null;
        return new Position(r, c);
    }

    public static boolean isValid(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position p)) return false;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}
