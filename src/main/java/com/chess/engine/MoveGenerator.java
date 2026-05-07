package com.chess.engine;

import com.chess.board.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.MoveType;
import com.chess.model.Position;
import com.chess.model.PieceType;
import com.chess.piece.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates all legal moves for the current position.
 * A legal move is a pseudo-legal move that does not leave the moving side's king in check.
 */
public class MoveGenerator {

    /**
     * Returns all legal moves for the player whose turn it is.
     */
    public List<Move> generateLegalMoves(Board board) {
        return generateLegalMovesFor(board, board.getCurrentTurn());
    }

    public List<Move> generateLegalMovesFor(Board board, Color color) {
        List<Move> pseudoLegal = generatePseudoLegalMoves(board, color);
        List<Move> legal = new ArrayList<>();

        for (Move move : pseudoLegal) {
            Board copy = board.copy();
            applyMove(copy, move);
            if (!isKingInCheck(copy, color)) {
                legal.add(move);
            }
        }
        return legal;
    }

    // ---- Pseudo-legal move generation ----

    private List<Move> generatePseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();
        Piece[][] grid = board.getGrid();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = grid[row][col];
                if (piece == null || piece.getColor() != color) continue;

                Position from = Position.of(row, col);
                List<Position> destinations = piece.getPseudoLegalMoves(from, grid, board.getEnPassantTarget());

                for (Position to : destinations) {
                    moves.addAll(buildMoves(board, from, to, piece, grid));
                }
            }
        }

        // Castling (handled separately because it requires complex checks)
        moves.addAll(generateCastlingMoves(board, color));

        return moves;
    }

    private List<Move> buildMoves(Board board, Position from, Position to, Piece piece, Piece[][] grid) {
        List<Move> moves = new ArrayList<>();
        Piece target = grid[to.getRow()][to.getCol()];

        if (piece.getType() == PieceType.PAWN) {
            int promotionRow = (piece.getColor() == Color.WHITE) ? 7 : 0;

            // En passant
            if (to.equals(board.getEnPassantTarget()) && target == null) {
                int capturedRow = (piece.getColor() == Color.WHITE) ? to.getRow() - 1 : to.getRow() + 1;
                Piece capturedPawn = grid[capturedRow][to.getCol()];
                moves.add(Move.enPassant(from, to, piece, capturedPawn));
                return moves;
            }

            // Promotion
            if (to.getRow() == promotionRow) {
                for (PieceType promo : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                    if (target != null) {
                        moves.add(Move.promotionCapture(from, to, piece, target, promo));
                    } else {
                        moves.add(Move.promotion(from, to, piece, promo));
                    }
                }
                return moves;
            }
        }

        // Normal capture or move
        if (target != null) {
            moves.add(Move.capture(from, to, piece, target));
        } else {
            moves.add(Move.normal(from, to, piece));
        }
        return moves;
    }

    // ---- Castling ----

    private List<Move> generateCastlingMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();
        Piece[][] grid = board.getGrid();
        int row = (color == Color.WHITE) ? 0 : 7;

        Piece king = grid[row][4];
        if (king == null || king.getType() != PieceType.KING || king.hasMoved()) return moves;

        // King must not be in check
        if (isKingInCheck(board, color)) return moves;

        // Kingside castling (files e-f-g)
        if (board.canCastleKingside(color)) {
            Piece rook = grid[row][7];
            if (rook != null && rook.getType() == PieceType.ROOK && !rook.hasMoved()
                    && grid[row][5] == null && grid[row][6] == null
                    && !isSquareAttacked(board, Position.of(row, 5), color.opposite())
                    && !isSquareAttacked(board, Position.of(row, 6), color.opposite())) {
                moves.add(Move.castleKingside(Position.of(row, 4), Position.of(row, 6), king));
            }
        }

        // Queenside castling (files e-d-c, b is just empty)
        if (board.canCastleQueenside(color)) {
            Piece rook = grid[row][0];
            if (rook != null && rook.getType() == PieceType.ROOK && !rook.hasMoved()
                    && grid[row][1] == null && grid[row][2] == null && grid[row][3] == null
                    && !isSquareAttacked(board, Position.of(row, 3), color.opposite())
                    && !isSquareAttacked(board, Position.of(row, 2), color.opposite())) {
                moves.add(Move.castleQueenside(Position.of(row, 4), Position.of(row, 2), king));
            }
        }

        return moves;
    }

    // ---- Check detection ----

    public boolean isKingInCheck(Board board, Color color) {
        Position kingPos = board.findKing(color);
        return isSquareAttacked(board, kingPos, color.opposite());
    }

    /**
     * Returns true if the given square is attacked by any piece of the given attacker color.
     */
    public boolean isSquareAttacked(Board board, Position square, Color attackerColor) {
        Piece[][] grid = board.getGrid();

        // Check pawn attacks
        int pawnDir = (attackerColor == Color.WHITE) ? -1 : 1; // direction from attacker to the square
        // A white pawn on (square.row - 1, square.col ± 1) attacks square
        // i.e., attacker pawn is one rank "behind" the square in pawn movement direction
        int pawnRow = square.getRow() + pawnDir; // row where an enemy pawn would be to attack this square
        // Actually: white pawn moves +row, so white pawn at (r, c) attacks (r+1, c±1)
        // To check if white pawn attacks `square`, look at (square.row - 1, square.col ± 1)
        int attackerPawnRow = square.getRow() - ((attackerColor == Color.WHITE) ? 1 : -1);
        for (int colDelta : new int[]{-1, 1}) {
            int aCol = square.getCol() + colDelta;
            if (Position.isValid(attackerPawnRow, aCol)) {
                Piece p = grid[attackerPawnRow][aCol];
                if (p != null && p.getType() == PieceType.PAWN && p.getColor() == attackerColor) {
                    return true;
                }
            }
        }

        // Knight attacks
        int[][] knightDeltas = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] d : knightDeltas) {
            Position p = square.offset(d[0], d[1]);
            if (p != null) {
                Piece piece = grid[p.getRow()][p.getCol()];
                if (piece != null && piece.getType() == PieceType.KNIGHT && piece.getColor() == attackerColor) {
                    return true;
                }
            }
        }

        // Sliding pieces (rook/queen on orthogonals, bishop/queen on diagonals)
        int[][] orthogonals = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : orthogonals) {
            if (isAttackedBySlider(grid, square, attackerColor, d[0], d[1],
                    PieceType.ROOK, PieceType.QUEEN)) return true;
        }

        int[][] diagonals = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : diagonals) {
            if (isAttackedBySlider(grid, square, attackerColor, d[0], d[1],
                    PieceType.BISHOP, PieceType.QUEEN)) return true;
        }

        // King attacks (to prevent kings from getting adjacent)
        int[][] kingDeltas = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : kingDeltas) {
            Position p = square.offset(d[0], d[1]);
            if (p != null) {
                Piece piece = grid[p.getRow()][p.getCol()];
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == attackerColor) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAttackedBySlider(Piece[][] grid, Position square, Color attackerColor,
                                        int rowDelta, int colDelta,
                                        PieceType sliderType1, PieceType sliderType2) {
        Position current = square.offset(rowDelta, colDelta);
        while (current != null) {
            Piece p = grid[current.getRow()][current.getCol()];
            if (p != null) {
                if (p.getColor() == attackerColor &&
                        (p.getType() == sliderType1 || p.getType() == sliderType2)) {
                    return true;
                }
                break; // Blocked by any piece
            }
            current = current.offset(rowDelta, colDelta);
        }
        return false;
    }

    // ---- Move application (used only for legality checking on copies) ----

    public void applyMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to   = move.getTo();
        Piece piece   = board.getPieceAt(from);

        board.setEnPassantTarget(null);

        switch (move.getType()) {
            case NORMAL, CAPTURE -> {
                movePiece(board, from, to);
                updateCastlingRights(board, from, to, piece);
                // Set en passant target for double pawn push
                if (piece.getType() == PieceType.PAWN && Math.abs(to.getRow() - from.getRow()) == 2) {
                    int epRow = (from.getRow() + to.getRow()) / 2;
                    board.setEnPassantTarget(Position.of(epRow, from.getCol()));
                }
            }
            case EN_PASSANT -> {
                board.removePiece(from);
                board.setPiece(to, piece);
                piece.setHasMoved(true);
                // Remove captured pawn
                int capturedRow = (piece.getColor() == Color.WHITE) ? to.getRow() - 1 : to.getRow() + 1;
                board.removePiece(Position.of(capturedRow, to.getCol()));
            }
            case CASTLE_KINGSIDE -> {
                int row = from.getRow();
                movePiece(board, from, to);                          // King e->g
                movePiece(board, Position.of(row, 7), Position.of(row, 5)); // Rook h->f
                board.revokeBothCastlingRights(piece.getColor());
            }
            case CASTLE_QUEENSIDE -> {
                int row = from.getRow();
                movePiece(board, from, to);                          // King e->c
                movePiece(board, Position.of(row, 0), Position.of(row, 3)); // Rook a->d
                board.revokeBothCastlingRights(piece.getColor());
            }
            case PROMOTION -> {
                board.removePiece(from);
                board.setPiece(to, createPromotedPiece(piece.getColor(), move.getPromotedTo()));
                updateCastlingRights(board, from, to, piece);
            }
            case PROMOTION_CAPTURE -> {
                board.removePiece(from);
                board.setPiece(to, createPromotedPiece(piece.getColor(), move.getPromotedTo()));
                updateCastlingRights(board, from, to, piece);
            }
        }
    }

    private void movePiece(Board board, Position from, Position to) {
        Piece piece = board.getPieceAt(from);
        board.removePiece(from);
        board.setPiece(to, piece);
        piece.setHasMoved(true);
    }

    private void updateCastlingRights(Board board, Position from, Position to, Piece piece) {
        // King moved
        if (piece.getType() == PieceType.KING) {
            board.revokeBothCastlingRights(piece.getColor());
        }
        // Rook moved or captured
        if (from.equals(Position.of(0, 0)) || to.equals(Position.of(0, 0)))
            board.revokeCastlingRights(Color.WHITE, false);
        if (from.equals(Position.of(0, 7)) || to.equals(Position.of(0, 7)))
            board.revokeCastlingRights(Color.WHITE, true);
        if (from.equals(Position.of(7, 0)) || to.equals(Position.of(7, 0)))
            board.revokeCastlingRights(Color.BLACK, false);
        if (from.equals(Position.of(7, 7)) || to.equals(Position.of(7, 7)))
            board.revokeCastlingRights(Color.BLACK, true);
    }

    private Piece createPromotedPiece(Color color, PieceType type) {
        return switch (type) {
            case QUEEN  -> new Queen(color);
            case ROOK   -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            default -> throw new IllegalArgumentException("Invalid promotion: " + type);
        };
    }
}
