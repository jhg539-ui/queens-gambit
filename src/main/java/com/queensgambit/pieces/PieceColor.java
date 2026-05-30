package com.queensgambit.pieces;

import com.jme3.math.ColorRGBA;

public enum PieceColor {
    WHITE(new ColorRGBA(0.95f, 0.92f, 0.82f, 1f)),   // cream ivory
    BLACK(new ColorRGBA(0.10f, 0.09f, 0.09f, 1f));   // deep onyx

    public final ColorRGBA color;

    PieceColor(ColorRGBA c) {
        this.color = c;
    }

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
