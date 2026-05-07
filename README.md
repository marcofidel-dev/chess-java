# Chess Java - Ajedrez Backend en Java

Implementación completa del juego de Ajedrez en Java 17, diseñada para el aprendizaje de desarrollo backend.

## Estructura del Proyecto

```
src/
├── main/java/com/chess/
│   ├── ChessApplication.java          # Punto de entrada
│   ├── model/                         # Modelos de dominio
│   │   ├── Color.java                 # WHITE / BLACK
│   │   ├── Position.java              # Casilla del tablero (fila, columna)
│   │   ├── PieceType.java             # KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
│   │   ├── MoveType.java              # NORMAL, CAPTURE, CASTLE, EN_PASSANT, PROMOTION
│   │   ├── Move.java                  # Un movimiento con origen, destino y tipo
│   │   └── GameState.java             # PLAYING, CHECK, CHECKMATE, STALEMATE, DRAW
│   ├── piece/                         # Jerarquía de piezas
│   │   ├── Piece.java                 # Clase abstracta base
│   │   ├── King.java
│   │   ├── Queen.java
│   │   ├── Rook.java
│   │   ├── Bishop.java
│   │   ├── Knight.java
│   │   └── Pawn.java
│   ├── board/
│   │   └── Board.java                 # Estado del tablero 8x8
│   ├── engine/
│   │   ├── MoveGenerator.java         # Generación de movimientos legales
│   │   └── GameEngine.java            # Control del juego
│   └── ui/
│       └── ConsoleUI.java             # Interfaz de consola
└── test/java/com/chess/               # Tests unitarios con JUnit 5
```

## Reglas Implementadas

| Regla | Estado |
|-------|--------|
| Movimientos básicos de todas las piezas | ✅ |
| Jaque (Check) | ✅ |
| Jaque Mate (Checkmate) | ✅ |
| Ahogado (Stalemate) | ✅ |
| Enroque corto y largo (Castling) | ✅ |
| Captura al paso (En Passant) | ✅ |
| Coronación de peón (Promotion) | ✅ |
| Regla de los 50 movimientos | ✅ |
| Material insuficiente (Draw) | ✅ |

## Cómo Ejecutar

### Requisitos
- Java 17+
- Maven 3.8+

### Compilar y ejecutar tests
```bash
mvn test
```

### Ejecutar el juego
```bash
mvn package -q
java -jar target/chess-java-1.0.0.jar
```

## Comandos del Juego

```
e2e4        → Mover pieza de e2 a e4
e7e8q       → Mover con promoción (q=reina, r=torre, b=alfil, n=caballo)
moves e2    → Ver movimientos legales desde e2
board       → Redibujar el tablero
history     → Ver historial de movimientos
help        → Mostrar ayuda
quit        → Salir
```

## Conceptos de Backend Practicados

- **Herencia y polimorfismo**: jerarquía de piezas con `Piece` abstracta
- **Patrones de diseño**: Factory (creación de piezas), Strategy (movimientos)
- **Inmutabilidad**: `Position` y `Move` son objetos de valor inmutables
- **Programación defensiva**: validación de límites, copia defensiva del tablero
- **Algoritmos**: detección de jaque por búsqueda inversa de ataques
- **Tests unitarios**: JUnit 5 con cobertura de reglas de ajedrez
- **Notación FEN**: serialización del estado del tablero
