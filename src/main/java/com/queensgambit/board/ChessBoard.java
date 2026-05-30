package com.queensgambit.board;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.queensgambit.pieces.PieceColor;
import com.queensgambit.pieces.PieceFactory;
import com.queensgambit.pieces.PieceType;

/**
 * The 8x8 chess board. Files 0..7 == a..h, ranks 0..7 == 1..8.
 * World coordinates: board centred on origin, lying on XZ plane, Y up.
 */
public class ChessBoard implements BoardState {

    public static final float SQUARE_SIZE = 1.0f;
    public static final float SQUARE_TOP_Y = 0.10f; // top surface of squares

    private final Node node;
    private final AssetManager assetManager;
    private final Spatial[][] pieces = new Spatial[8][8]; // [file][rank]

    public ChessBoard(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.node = new Node("ChessBoard");
        buildFrame();
        buildSquares();
    }

    public Node getNode() {
        return node;
    }

    /** Convert (file, rank) to world position at the top centre of that square. */
    public static Vector3f squareToWorld(int file, int rank) {
        float x = (file - 3.5f) * SQUARE_SIZE;
        float z = (rank - 3.5f) * SQUARE_SIZE;
        return new Vector3f(x, SQUARE_TOP_Y, z);
    }

    public Spatial pieceAt(int file, int rank) {
        return pieces[file][rank];
    }

    public void setPieceAt(int file, int rank, Spatial piece) {
        pieces[file][rank] = piece;
        if (piece != null) {
            piece.setUserData("file", file);
            piece.setUserData("rank", rank);
        }
    }

    public void clearSquare(int file, int rank) {
        pieces[file][rank] = null;
    }

    private void buildFrame() {
        // Dark walnut frame
        Box frameBox = new Box(4.6f, 0.10f, 4.6f);
        Geometry frame = new Geometry("Frame", frameBox);
        Material frameMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        frameMat.setBoolean("UseMaterialColors", true);
        ColorRGBA walnut = new ColorRGBA(0.14f, 0.08f, 0.04f, 1f);
        frameMat.setColor("Diffuse", walnut);
        frameMat.setColor("Ambient", walnut.mult(0.6f));
        frameMat.setColor("Specular", new ColorRGBA(0.35f, 0.25f, 0.15f, 1f));
        frameMat.setFloat("Shininess", 24f);
        frame.setMaterial(frameMat);
        frame.setLocalTranslation(0, -0.05f, 0);
        node.attachChild(frame);
    }

    private void buildSquares() {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Box squareBox = new Box(SQUARE_SIZE / 2f, 0.05f, SQUARE_SIZE / 2f);
                Geometry square = new Geometry("Square_" + file + "_" + rank, squareBox);

                boolean isLight = (file + rank) % 2 == 1;
                Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                mat.setBoolean("UseMaterialColors", true);

                ColorRGBA col = isLight
                        ? new ColorRGBA(0.82f, 0.72f, 0.55f, 1f)  // warm cream
                        : new ColorRGBA(0.22f, 0.13f, 0.07f, 1f); // deep walnut
                mat.setColor("Diffuse", col);
                mat.setColor("Ambient", col.mult(0.5f));
                mat.setColor("Specular", new ColorRGBA(0.15f, 0.15f, 0.15f, 1f));
                mat.setFloat("Shininess", 16f);
                square.setMaterial(mat);

                Vector3f pos = squareToWorld(file, rank);
                square.setLocalTranslation(pos.x, 0.05f, pos.z);
                square.setUserData("file", file);
                square.setUserData("rank", rank);
                square.setUserData("isSquare", Boolean.TRUE);
                node.attachChild(square);
            }
        }
    }

    public void setupStartingPosition(PieceFactory factory) {
        PieceType[] back = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };
        for (int file = 0; file < 8; file++) {
            place(factory.create(back[file], PieceColor.WHITE), file, 0);
            place(factory.create(PieceType.PAWN, PieceColor.WHITE), file, 1);
            place(factory.create(PieceType.PAWN, PieceColor.BLACK), file, 6);
            place(factory.create(back[file], PieceColor.BLACK), file, 7);
        }
    }

    private void place(Spatial piece, int file, int rank) {
        Vector3f pos = squareToWorld(file, rank);
        piece.setLocalTranslation(pos.x, SQUARE_TOP_Y, pos.z);
        setPieceAt(file, rank, piece);
        node.attachChild(piece);
    }
}
