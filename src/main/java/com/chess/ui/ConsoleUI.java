package com.chess.ui;

import com.chess.engine.GameEngine;
import com.chess.model.GameState;
import com.chess.model.Move;
import com.chess.model.PieceType;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based chess UI.
 *
 * Commands:
 *   <from><to>       - Move piece, e.g. "e2e4"
 *   <from><to><p>    - Move + promotion piece, e.g. "e7e8q"
 *   moves <sq>       - Show legal moves from a square, e.g. "moves e2"
 *   board            - Redraw the board
 *   history          - Show move history
 *   help             - Show commands
 *   quit             - Exit
 */
public class ConsoleUI {

    private final GameEngine engine;
    private final Scanner scanner;
    private boolean useUnicode = true;

    public ConsoleUI() {
        this.engine = new GameEngine();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Ajedrez en Java ===");
        System.out.println("Escribe 'help' para ver los comandos disponibles.");
        System.out.println();
        printBoard();

        while (!engine.isGameOver()) {
            System.out.println();
            System.out.println("Estado: " + engine.getStateDescription());
            System.out.print(engine.getBoard().getCurrentTurn() + " > ");

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty()) continue;

            switch (input) {
                case "quit", "exit", "q" -> {
                    System.out.println("Hasta luego.");
                    return;
                }
                case "board", "b" -> printBoard();
                case "history", "h" -> printHistory();
                case "help", "?" -> printHelp();
                case "ascii" -> { useUnicode = false; printBoard(); }
                case "unicode" -> { useUnicode = true; printBoard(); }
                default -> handleMoveOrQuery(input);
            }
        }

        System.out.println();
        printBoard();
        System.out.println();
        System.out.println("=== Fin de la partida ===");
        System.out.println(engine.getStateDescription());
    }

    private void handleMoveOrQuery(String input) {
        // "moves e2"
        if (input.startsWith("moves ") && input.length() == 8) {
            String square = input.substring(6);
            List<Move> moves = engine.getLegalMovesFrom(square);
            if (moves.isEmpty()) {
                System.out.println("Sin movimientos legales desde " + square);
            } else {
                System.out.println("Movimientos desde " + square + ":");
                moves.forEach(m -> System.out.print(" " + m.toAlgebraic()));
                System.out.println();
            }
            return;
        }

        // Move: "e2e4" or "e7e8q"
        if (input.length() >= 4) {
            String from = input.substring(0, 2);
            String to   = input.substring(2, 4);
            PieceType promo = PieceType.QUEEN;

            if (input.length() >= 5) {
                try {
                    promo = PieceType.fromChar(input.charAt(4));
                } catch (IllegalArgumentException e) {
                    System.out.println("Pieza de promoción inválida: " + input.charAt(4));
                    return;
                }
            }

            if (engine.makeMove(from, to, promo)) {
                printBoard();
                if (engine.getGameState() == GameState.CHECK) {
                    System.out.println("¡JAQUE!");
                }
            } else {
                System.out.println("Movimiento ilegal: " + input);
            }
            return;
        }

        System.out.println("Comando no reconocido. Escribe 'help' para ayuda.");
    }

    private void printBoard() {
        System.out.println();
        System.out.println(engine.getBoard().toDisplayString(useUnicode));
    }

    private void printHistory() {
        List<Move> history = engine.getMoveHistory();
        if (history.isEmpty()) {
            System.out.println("Sin movimientos aún.");
            return;
        }
        System.out.println("Historial de movimientos:");
        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) System.out.printf("%d. ", (i / 2) + 1);
            System.out.print(history.get(i).toAlgebraic() + " ");
            if (i % 2 == 1) System.out.println();
        }
        System.out.println();
    }

    private void printHelp() {
        System.out.println("""
                Comandos disponibles:
                  e2e4        - Mover pieza de e2 a e4
                  e7e8q       - Mover con promoción (q=reina, r=torre, b=alfil, n=caballo)
                  moves e2    - Ver movimientos legales desde e2
                  board       - Redibujar el tablero
                  history     - Ver historial de movimientos
                  ascii       - Cambiar a modo ASCII
                  unicode     - Cambiar a modo Unicode (por defecto)
                  help        - Mostrar esta ayuda
                  quit        - Salir del juego

                Notación de casillas: columna (a-h) + fila (1-8), ej: e4, d7, h1
                """);
    }
}
