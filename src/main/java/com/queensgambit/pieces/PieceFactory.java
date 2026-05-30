package com.queensgambit.pieces;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

/**
 * Builds chess pieces from primitive shapes. Good enough as placeholders;
 * swap in proper .glb/.obj models later via AssetManager#loadModel.
 */
public class PieceFactory {
    private final AssetManager assetManager;

    public PieceFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Spatial create(PieceType type, PieceColor color) {
        Node node = new Node(color + "_" + type);
        Material mat = makeMaterial(color.color);

        switch (type) {
            case PAWN:   buildPawn(node, mat);   break;
            case ROOK:   buildRook(node, mat);   break;
            case KNIGHT: buildKnight(node, mat); break;
            case BISHOP: buildBishop(node, mat); break;
            case QUEEN:  buildQueen(node, mat);  break;
            case KING:   buildKing(node, mat);   break;
        }
        // Knights face the centre of the board — flip black ones
        if (type == PieceType.KNIGHT && color == PieceColor.BLACK) {
            node.setLocalRotation(new Quaternion().fromAngles(0, FastMath.PI, 0));
        }
        node.setUserData("pieceType", type.name());
        node.setUserData("pieceColor", color.name());
        node.setUserData("isPiece", Boolean.TRUE);
        return node;
    }

    private Material makeMaterial(ColorRGBA color) {
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", color);
        m.setColor("Ambient", color.mult(0.6f));
        m.setColor("Specular", new ColorRGBA(0.55f, 0.50f, 0.40f, 1f));
        m.setFloat("Shininess", 64f);
        return m;
    }

    /** Cylinder geometry standing on Y (jME cylinders are along Z by default). */
    private Geometry cyl(String name, float radius, float height, Material mat) {
        Cylinder c = new Cylinder(2, 24, radius, height, true);
        Geometry g = new Geometry(name, c);
        g.setMaterial(mat);
        g.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI, 0, 0));
        return g;
    }

    private Geometry sph(String name, float radius, Material mat) {
        Sphere s = new Sphere(16, 24, radius);
        Geometry g = new Geometry(name, s);
        g.setMaterial(mat);
        return g;
    }

    private Geometry box(String name, float x, float y, float z, Material mat) {
        Geometry g = new Geometry(name, new Box(x, y, z));
        g.setMaterial(mat);
        return g;
    }

    private void buildPawn(Node n, Material m) {
        Geometry base = cyl("base", 0.22f, 0.08f, m);
        base.setLocalTranslation(0, 0.04f, 0);
        Geometry collar = cyl("collar", 0.16f, 0.05f, m);
        collar.setLocalTranslation(0, 0.10f, 0);
        Geometry stem = cyl("stem", 0.10f, 0.28f, m);
        stem.setLocalTranslation(0, 0.27f, 0);
        Geometry head = sph("head", 0.14f, m);
        head.setLocalTranslation(0, 0.50f, 0);
        n.attachChild(base);
        n.attachChild(collar);
        n.attachChild(stem);
        n.attachChild(head);
    }

    private void buildRook(Node n, Material m) {
        Geometry base = cyl("base", 0.26f, 0.10f, m);
        base.setLocalTranslation(0, 0.05f, 0);
        Geometry body = cyl("body", 0.18f, 0.55f, m);
        body.setLocalTranslation(0, 0.38f, 0);
        Geometry top = cyl("top", 0.20f, 0.06f, m);
        top.setLocalTranslation(0, 0.69f, 0);
        n.attachChild(base);
        n.attachChild(body);
        n.attachChild(top);
        // Four crenellations
        for (int i = 0; i < 4; i++) {
            float angle = i * FastMath.HALF_PI + FastMath.PI / 4f;
            Geometry crenel = box("crenel", 0.05f, 0.06f, 0.05f, m);
            crenel.setLocalTranslation(
                FastMath.cos(angle) * 0.16f, 0.78f, FastMath.sin(angle) * 0.16f);
            n.attachChild(crenel);
        }
    }

    private void buildKnight(Node n, Material m) {
        Geometry base = cyl("base", 0.24f, 0.10f, m);
        base.setLocalTranslation(0, 0.05f, 0);
        Geometry collar = cyl("collar", 0.18f, 0.04f, m);
        collar.setLocalTranslation(0, 0.12f, 0);
        Geometry neck = box("neck", 0.10f, 0.22f, 0.10f, m);
        neck.setLocalTranslation(0, 0.30f, 0);
        // Forward-tilted head
        Geometry head = box("head", 0.09f, 0.10f, 0.20f, m);
        head.setLocalTranslation(0, 0.55f, 0.07f);
        head.setLocalRotation(new Quaternion().fromAngles(-0.35f, 0, 0));
        // Snout
        Geometry snout = box("snout", 0.07f, 0.06f, 0.10f, m);
        snout.setLocalTranslation(0, 0.50f, 0.20f);
        snout.setLocalRotation(new Quaternion().fromAngles(-0.45f, 0, 0));
        // Ears
        Geometry earL = box("earL", 0.025f, 0.07f, 0.025f, m);
        earL.setLocalTranslation(-0.06f, 0.66f, -0.04f);
        Geometry earR = box("earR", 0.025f, 0.07f, 0.025f, m);
        earR.setLocalTranslation( 0.06f, 0.66f, -0.04f);
        n.attachChild(base);
        n.attachChild(collar);
        n.attachChild(neck);
        n.attachChild(head);
        n.attachChild(snout);
        n.attachChild(earL);
        n.attachChild(earR);
    }

    private void buildBishop(Node n, Material m) {
        Geometry base = cyl("base", 0.24f, 0.10f, m);
        base.setLocalTranslation(0, 0.05f, 0);
        Geometry collar = cyl("collar", 0.18f, 0.04f, m);
        collar.setLocalTranslation(0, 0.12f, 0);
        Geometry body = cyl("body", 0.12f, 0.55f, m);
        body.setLocalTranslation(0, 0.40f, 0);
        Geometry mitre = sph("mitre", 0.13f, m);
        mitre.setLocalTranslation(0, 0.75f, 0);
        // Notch slit on the mitre — small dark wedge (uses same material; just a visual nub)
        Geometry tip = sph("tip", 0.04f, m);
        tip.setLocalTranslation(0, 0.90f, 0);
        n.attachChild(base);
        n.attachChild(collar);
        n.attachChild(body);
        n.attachChild(mitre);
        n.attachChild(tip);
    }

    private void buildQueen(Node n, Material m) {
        Geometry base = cyl("base", 0.27f, 0.10f, m);
        base.setLocalTranslation(0, 0.05f, 0);
        Geometry collar = cyl("collar", 0.20f, 0.04f, m);
        collar.setLocalTranslation(0, 0.12f, 0);
        Geometry body = cyl("body", 0.14f, 0.70f, m);
        body.setLocalTranslation(0, 0.47f, 0);
        Geometry crown = cyl("crown", 0.17f, 0.06f, m);
        crown.setLocalTranslation(0, 0.85f, 0);
        // 5 points on the coronet
        for (int i = 0; i < 5; i++) {
            float angle = i * (FastMath.TWO_PI / 5f);
            Geometry pt = sph("pt", 0.045f, m);
            pt.setLocalTranslation(
                FastMath.cos(angle) * 0.15f, 0.93f, FastMath.sin(angle) * 0.15f);
            n.attachChild(pt);
        }
        Geometry orb = sph("orb", 0.06f, m);
        orb.setLocalTranslation(0, 0.96f, 0);
        n.attachChild(base);
        n.attachChild(collar);
        n.attachChild(body);
        n.attachChild(crown);
        n.attachChild(orb);
    }

    private void buildKing(Node n, Material m) {
        Geometry base = cyl("base", 0.27f, 0.10f, m);
        base.setLocalTranslation(0, 0.05f, 0);
        Geometry collar = cyl("collar", 0.20f, 0.04f, m);
        collar.setLocalTranslation(0, 0.12f, 0);
        Geometry body = cyl("body", 0.14f, 0.78f, m);
        body.setLocalTranslation(0, 0.51f, 0);
        Geometry crown = cyl("crown", 0.17f, 0.06f, m);
        crown.setLocalTranslation(0, 0.93f, 0);
        // Cross
        Geometry vert = box("cross_v", 0.025f, 0.10f, 0.025f, m);
        vert.setLocalTranslation(0, 1.07f, 0);
        Geometry horiz = box("cross_h", 0.07f, 0.025f, 0.025f, m);
        horiz.setLocalTranslation(0, 1.06f, 0);
        n.attachChild(base);
        n.attachChild(collar);
        n.attachChild(body);
        n.attachChild(crown);
        n.attachChild(vert);
        n.attachChild(horiz);
    }
}
