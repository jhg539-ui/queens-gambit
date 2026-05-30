package com.queensgambit;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.queensgambit.board.ChessBoard;
import com.queensgambit.game.GameController;
import com.queensgambit.pieces.PieceFactory;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Queen's Gambit");
        settings.setResolution(1280, 720);
        settings.setSamples(4); // MSAA — softens piece edges
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Deep, almost-black room
        viewPort.setBackgroundColor(new ColorRGBA(0.04f, 0.04f, 0.06f, 1f));

        // Camera — angled three-quarter view, white side at the bottom
        cam.setLocation(new Vector3f(0f, 9f, 8.5f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);

        // Board + pieces
        ChessBoard board = new ChessBoard(assetManager);
        rootNode.attachChild(board.getNode());

        PieceFactory factory = new PieceFactory(assetManager);
        board.setupStartingPosition(factory);

        setupLighting();

        // Interaction
        new GameController(this, board);
    }

    /**
     * Moody, single-warm-key-light setup reminiscent of a late-night
     * tournament hall. Tweak these to taste.
     */
    private void setupLighting() {
        // Very low ambient — keeps the shadows deep
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.12f));
        rootNode.addLight(ambient);

        // Warm tungsten lamp above the board
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0f, 7f, 0f));
        lamp.setColor(new ColorRGBA(1.00f, 0.82f, 0.55f, 1f).mult(2.5f));
        lamp.setRadius(18f);
        rootNode.addLight(lamp);

        // Cool fill from the side so the dark pieces aren't a black hole
        DirectionalLight fill = new DirectionalLight();
        fill.setDirection(new Vector3f(-0.6f, -0.4f, -0.5f).normalizeLocal());
        fill.setColor(new ColorRGBA(0.20f, 0.28f, 0.45f, 1f));
        rootNode.addLight(fill);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Hook for animation, clocks, etc.
    }
}
