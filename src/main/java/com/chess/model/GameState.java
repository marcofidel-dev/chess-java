package com.chess.model;

public enum GameState {
    PLAYING,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW_FIFTY_MOVES,
    DRAW_INSUFFICIENT_MATERIAL
}
