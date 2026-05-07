package com.chess.board;

import com.chess.model.Color;
import com.chess.model.Position;
import com.chess.model.PieceType;
import com.chess.piece.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chess board state.
 *
 * Grid layout:
 *   grid[0][0] = a1 (white rook),  grid[7][0] = a8 (black rook)
 *   grid[row][col], row 0..7 = ranks 1..8
 */
public class Board {

    private final Piece[][] grid;
    private Color currentTurn;
    private Position enPassantTarget;   // Square a pawn can capture to via en passant

    // Castling rights: [whiteKingside, whiteQueenside, blackKingside, blackQueenside]
    private final boolean[] castlingRights;

    private int halfMoveClock;   // For 50-move rule
    private int fullMoveNumber;

    private final List<String> positionHistory; // For threefold repetition (FEN snapshots)

    public Board() {
        this.grid = new Piece[8][8];
        this.currentTurn = Color.WHITE;
        this.enPassantTarget = null;
        this.castlingRights = new boolean[]{true, true, true, true};
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        this.positionHistory = new ArrayList<>();
        setupInitialPosition();
    }

    private Board(Piece[][] grid, Color currentTurn, Position enPassantTarget,
                  boolean[] castlingRights, int halfMoveClock, int fullMoveNumber,
                  List<String> positionHistory) {
        this.grid = grid;
        this.currentTurn = currentTurn;
        this.enPassantTarget = enPassantTarget;
        this.castlingRights = castlingRights;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveNumber = fullMoveNumber;
        this.positionHistory = new ArrayList<>(positionHistory);
    }

    private void setupInitialPosition() {
        // White back rank (row 0)
        grid[0][0] = new Rook(Color.WHITE);
        grid[0][1] = new Knight(Color.WHITE);
        grid[0][2] = new Bishop(Color.WHITE);
        grid[0][3] = new Queen(Color.WHITE);
        grid[0][4] = new King(Color.WHITE);
        grid[0][5] = new Bishop(Color.WHITE);
        grid[0][6] = new Knight(Color.WHITE);
        grid[0][7] = new Rook(Color.WHITE);

        // White pawns (row 1)
        for (int col = 0; col < 8; col++) {
            grid[1][col] = new Pawn(Color.WHITE);
        }

        // Black pawns (row 6)
        for (int col = 0; col < 8; col++) {
            grid[6][col] = new Pawn(Color.BLACK);
        }

        // Black back rank (row 7)
        grid[7][0] = new Rook(Color.BLACK);
        grid[7][1] = new Knight(Color.BLACK);
        grid[7][2] = new Bishop(Color.BLACK);
        grid[7][3] = new Queen(Color.BLACK);
        grid[7][4] = new King(Color.BLACK);
        grid[7][5] = new Bishop(Color.BLACK);
        grid[7][6] = new Knight(Color.BLACK);
        grid[7][7] = new Rook(Color.BLACK);
    }

    // ---- Accessors ----

    public Piece getPieceAt(Position pos) {
        return grid[pos.getRow()][pos.getCol()];
    }

    public Piece getPieceAt(int row, int col) {
        return grid[row][col];
    }

    public Piece[][] getGrid() {
        return grid;
    }

    public Color getCurrentTurn() { return currentTurn; }
    public Position getEnPassantTarget() { return enPassantTarget; }
    public boolean[] getCastlingRights() { return castlingRights; }
    public int getHalfMoveClock() { return halfMoveClock; }
    public int getFullMoveNumber() { return fullMoveNumber; }

    // ---- Castling rights helpers ----

    public boolean canCastleKingside(Color color) {
        return color == Color.WHITE ? castlingRights[0] : castlingRights[2];
    }

    public boolean canCastleQueenside(Color color) {
        return color == Color.WHITE ? castlingRights[1] : castlingRights[3];
    }

    public void revokeCastlingRights(Color color, boolean kingside) {
        if (color == Color.WHITE) {
            if (kingside) castlingRights[0] = false;
            else          castlingRights[1] = false;
        } else {
            if (kingside) castlingRights[2] = false;
            else          castlingRights[3] = false;
        }
    }

    public void revokeBothCastlingRights(Color color) {
        if (color == Color.WHITE) {
            castlingRights[0] = false;
            castlingRights[1] = false;
        } else {
            castlingRights[2] = false;
            castlingRights[3] = false;
        }
    }

    // ---- Mutation ----

    public void setPiece(Position pos, Piece piece) {
        grid[pos.getRow()][pos.getCol()] = piece;
    }

    public void removePiece(Position pos) {
        grid[pos.getRow()][pos.getCol()] = null;
    }

    public void setCurrentTurn(Color turn) {
        this.currentTurn = turn;
    }

    public void setEnPassantTarget(Position target) {
        this.enPassantTarget = target;
    }

    public void setHalfMoveClock(int clock) {
        this.halfMoveClock = clock;
    }

    public void setFullMoveNumber(int number) {
        this.fullMoveNumber = number;
    }

    public void incrementHalfMoveClock() { halfMoveClock++; }
    public void resetHalfMoveClock()     { halfMoveClock = 0; }

    public void incrementFullMoveNumber() { fullMoveNumber++; }

    public void addPositionToHistory(String fen) {
        positionHistory.add(fen);
    }

    public int countPositionOccurrences(String fen) {
        return (int) positionHistory.stream().filter(fen::equals).count();
    }

    // ---- Utility: find king ----

    public Position findKing(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = grid[row][col];
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return Position.of(row, col);
                }
            }
        }
        throw new IllegalStateException("King not found for " + color);
    }

    // ---- Deep copy ----

    public Board copy() {
        Piece[][] newGrid = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] != null) {
                    newGrid[row][col] = grid[row][col].copy();
                }
            }
        }
        boolean[] newRights = castlingRights.clone();
        return new Board(newGrid, currentTurn, enPassantTarget, newRights, halfMoveClock, fullMoveNumber, positionHistory);
    }

    // ---- Board display ----

    public String toDisplayString(boolean useUnicode) {
        StringBuilder sb = new StringBuilder();
        sb.append("  a b c d e f g h\n");
        for (int row = 7; row >= 0; row--) {
            sb.append((row + 1)).append(" ");
            for (int col = 0; col < 8; col++) {
                Piece piece = grid[row][col];
                if (piece == null) {
                    sb.append(((row + col) % 2 == 0) ? "." : "·");
                } else {
                    sb.append(useUnicode ? piece.getUnicodeSymbol() : piece.toString());
                }
                sb.append(" ");
            }
            sb.append((row + 1)).append("\n");
        }
        sb.append("  a b c d e f g h");
        return sb.toString();
    }

    /** Minimal FEN position string (pieces + turn + castling + en passant). */
    public String toFenPosition() {
        StringBuilder sb = new StringBuilder();

        // Piece placement
        for (int row = 7; row >= 0; row--) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece p = grid[row][col];
                if (p == null) {
                    empty++;
                } else {
                    if (empty > 0) { sb.append(empty); empty = 0; }
                    sb.append(p.toString());
                }
            }
            if (empty > 0) sb.append(empty);
            if (row > 0) sb.append('/');
        }

        // Turn
        sb.append(' ').append(currentTurn == Color.WHITE ? 'w' : 'b');

        // Castling
        String castling = (castlingRights[0] ? "K" : "") +
                          (castlingRights[1] ? "Q" : "") +
                          (castlingRights[2] ? "k" : "") +
                          (castlingRights[3] ? "q" : "");
        sb.append(' ').append(castling.isEmpty() ? "-" : castling);

        // En passant
        sb.append(' ').append(enPassantTarget != null ? enPassantTarget.toAlgebraic() : "-");

        return sb.toString();
    }
}
