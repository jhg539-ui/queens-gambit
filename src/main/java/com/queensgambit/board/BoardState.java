package com.queensgambit.board;

import com.jme3.scene.Spatial;

public interface BoardState {
    Spatial pieceAt(int file, int rank);
}
