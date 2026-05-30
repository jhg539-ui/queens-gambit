package com.queensgambit.game;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.queensgambit.board.ChessBoard;

/**
 * Click a piece to lift it, click a square (or enemy piece) to drop/capture.
 * Clicking your own piece while another is selected re-selects.
 * Basic chess movement is enforced, without turn order.
 */
public class GameController {

    private final SimpleApplication app;
    private final ChessBoard board;
    private final MoveValidator moveValidator = new MoveValidator();
    private Spatial selected = null;
    private Vector3f restPos = null;

    private static final float LIFT_HEIGHT = 0.35f;

    public GameController(SimpleApplication app, ChessBoard board) {
        this.app = app;
        this.board = board;
        registerInput();
    }

    private void registerInput() {
        app.getInputManager().addMapping("ChessClick",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(clickListener, "ChessClick");
    }

    private final ActionListener clickListener = (name, isPressed, tpf) -> {
        if (!isPressed) return;
        handleClick();
    };

    private void handleClick() {
        Spatial hit = pick();
        if (hit == null) return;

        Spatial pieceRoot = findAncestor(hit, "isPiece");
        if (pieceRoot != null) {
            handlePieceClick(pieceRoot);
            return;
        }

        Spatial square = findAncestor(hit, "isSquare");
        if (square != null && selected != null) {
            int file = square.getUserData("file");
            int rank = square.getUserData("rank");
            tryMove(file, rank);
        }
    }

    private void handlePieceClick(Spatial piece) {
        if (selected == null) {
            select(piece);
            return;
        }
        if (sameColor(selected, piece)) {
            // Re-select friendly piece
            deselect();
            select(piece);
        } else {
            // Capture
            Integer f = piece.getUserData("file");
            Integer r = piece.getUserData("rank");
            if (f != null && r != null) {
                tryMove(f, r);
            }
        }
    }

    private Spatial pick() {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = app.getInputManager().getCursorPosition();
        Vector3f near = app.getCamera()
                .getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f far = app.getCamera()
                .getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).clone();
        Vector3f dir = far.subtractLocal(near).normalizeLocal();
        Ray ray = new Ray(near, dir);
        app.getRootNode().collideWith(ray, results);
        if (results.size() == 0) return null;
        CollisionResult hit = results.getClosestCollision();
        return hit.getGeometry();
    }

    private Spatial findAncestor(Spatial s, String key) {
        Spatial cur = s;
        while (cur != null) {
            Boolean flag = cur.getUserData(key);
            if (Boolean.TRUE.equals(flag)) return cur;
            cur = cur.getParent();
        }
        return null;
    }

    private void select(Spatial piece) {
        selected = piece;
        restPos = piece.getLocalTranslation().clone();
        piece.setLocalTranslation(restPos.x, restPos.y + LIFT_HEIGHT, restPos.z);
    }

    private void deselect() {
        if (selected != null && restPos != null) {
            selected.setLocalTranslation(restPos);
        }
        selected = null;
        restPos = null;
    }

    private boolean tryMove(int file, int rank) {
        if (selected == null || !moveValidator.isLegalMove(board, selected, file, rank)) {
            return false;
        }

        Integer fromFile = selected.getUserData("file");
        Integer fromRank = selected.getUserData("rank");
        Spatial captured = board.pieceAt(file, rank);

        // Vacate origin square
        if (fromFile != null && fromRank != null) {
            board.clearSquare(fromFile, fromRank);
        }
        if (captured != null) {
            captured.removeFromParent();
        }
        Vector3f target = ChessBoard.squareToWorld(file, rank);
        selected.setLocalTranslation(target);
        board.setPieceAt(file, rank, selected);
        selected = null;
        restPos = null;
        return true;
    }

    private boolean sameColor(Spatial a, Spatial b) {
        return a.getUserData("pieceColor").equals(b.getUserData("pieceColor"));
    }
}
