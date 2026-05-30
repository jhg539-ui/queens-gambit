# Queen's Gambit

A moody chess game built with [jMonkeyEngine 3](https://jmonkeyengine.org/). The
aesthetic leans into the *Queen's Gambit* mood: a single warm key light over a
dark walnut and ivory board, on a near-black backdrop.

## Run

Requires JDK 17+. From the project root:

```bash
./gradlew run        # macOS/Linux
gradlew.bat run      # Windows
```

## Controls

- **Left-click a piece** to lift it.
- **Left-click a legal square** to drop the lifted piece there.
- **Left-click an enemy piece** to capture it if the selected piece can legally move there.
- **Left-click another of your own pieces** to switch selection.

Basic piece movement rules are enforced, but turn order, check, checkmate,
castling, en passant, and promotion are not implemented yet.

## Project layout

```text
src/main/java/com/queensgambit
├── Main.java                       # entry point, camera, lighting
├── board/
│   ├── BoardState.java             # small board interface for validation/tests
│   └── ChessBoard.java             # builds squares, tracks piece occupancy
├── pieces/
│   ├── PieceFactory.java           # primitive-shape pieces (pawn..king)
│   ├── PieceType.java
│   └── PieceColor.java
└── game/
    ├── GameController.java         # mouse picking + move handling
    └── MoveValidator.java          # basic chess movement rules
```

## Roadmap

1. Add check/checkmate and prevent moves that leave the king in check.
2. Add castling, en passant, and promotion.
3. Add move highlighting for legal target squares.
4. Add turn order and game state.
5. Replace primitive piece shapes with proper 3D models.
