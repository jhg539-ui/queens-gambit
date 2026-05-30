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

(If `./gradlew` isn't present yet, run `gradle wrapper` once or open the project
in IntelliJ / VS Code and let Gradle generate it.)

## Controls

- **Left-click a piece** to lift it.
- **Left-click a square** to drop the lifted piece there.
- **Left-click an enemy piece** to capture it.
- **Left-click another of your own pieces** to switch selection.

## Project layout

```
src/main/java/com/queensgambit
├── Main.java                       # entry point, camera, lighting
├── board/
│   └── ChessBoard.java             # builds squares, tracks piece occupancy
├── pieces/
│   ├── PieceFactory.java           # primitive-shape pieces (pawn..king)
│   ├── PieceType.java
│   └── PieceColor.java
└── game/
    └── GameController.java         # mouse picking + move handling
```

## Roadmap (suggested next steps)

1. **Chess rules** — add a `MoveValidator` that knows how each piece moves,
   check/checkmate, castling, en passant, promotion.
2. **Move highlighting** — when a piece is selected, glow its legal target
   squares. Easiest: keep a parallel grid of translucent overlay geometries
   on `Common/MatDefs/Misc/Unshaded.j3md` with `Color` alpha < 1.
3. **Turn order** — alternate white/black, reject moves out of turn.
4. **Better pieces** — swap `PieceFactory` for `assetManager.loadModel(...)`
   loading `.glb` or `.j3o` models. Staunton sets are easy to find under
   permissive licenses.
5. **Camera polish** — `ChaseCamera` orbiting the board centre, or a smooth
   flip when it becomes the other player's turn.
6. **Atmosphere** — post-processing: `FilterPostProcessor` + `BloomFilter` for
   the lamp glow; subtle `FogFilter` for depth.
7. **Opening trainer** — since this is *Queen's Gambit* themed, layer in a
   guided 1.d4 d5 2.c4 opening tutor that highlights expected moves.
