package com.queensgambit.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.queensgambit.board.BoardState;
import com.queensgambit.pieces.PieceColor;
import com.queensgambit.pieces.PieceType;
import org.junit.jupiter.api.Test;

class MoveValidatorTest {

    private final MoveValidator validator = new MoveValidator();

    @Test
    void pawnCanAdvanceFromStartButCannotJumpOverPiece() {
        TestBoard board = new TestBoard();
        Spatial pawn = piece(PieceType.PAWN, PieceColor.WHITE, 4, 1);
        board.put(pawn);

        assertTrue(validator.isLegalMove(board, pawn, 4, 3));

        board.put(piece(PieceType.PAWN, PieceColor.WHITE, 4, 2));
        assertFalse(validator.isLegalMove(board, pawn, 4, 3));
    }

    @Test
    void pawnCapturesOnlyDiagonally() {
        TestBoard board = new TestBoard();
        Spatial pawn = piece(PieceType.PAWN, PieceColor.WHITE, 4, 4);
        board.put(pawn);
        board.put(piece(PieceType.KNIGHT, PieceColor.BLACK, 5, 5));

        assertTrue(validator.isLegalMove(board, pawn, 5, 5));
        assertFalse(validator.isLegalMove(board, pawn, 3, 5));
        board.put(piece(PieceType.KNIGHT, PieceColor.BLACK, 4, 5));
        assertFalse(validator.isLegalMove(board, pawn, 4, 5));
    }

    @Test
    void slidingPiecesCannotMoveThroughOccupiedSquares() {
        TestBoard board = new TestBoard();
        Spatial rook = piece(PieceType.ROOK, PieceColor.WHITE, 0, 0);
        board.put(rook);
        board.put(piece(PieceType.PAWN, PieceColor.WHITE, 0, 2));

        assertTrue(validator.isLegalMove(board, rook, 0, 1));
        assertFalse(validator.isLegalMove(board, rook, 0, 3));
    }

    @Test
    void knightJumpsAndCannotCaptureFriendlyPieces() {
        TestBoard board = new TestBoard();
        Spatial knight = piece(PieceType.KNIGHT, PieceColor.WHITE, 1, 0);
        board.put(knight);

        assertTrue(validator.isLegalMove(board, knight, 2, 2));

        board.put(piece(PieceType.PAWN, PieceColor.WHITE, 2, 2));
        assertFalse(validator.isLegalMove(board, knight, 2, 2));
    }

    private static Spatial piece(PieceType type, PieceColor color, int file, int rank) {
        Spatial piece = new Node(color + "_" + type);
        piece.setUserData("pieceType", type.name());
        piece.setUserData("pieceColor", color.name());
        piece.setUserData("file", file);
        piece.setUserData("rank", rank);
        return piece;
    }

    private static class TestBoard implements BoardState {
        private final Spatial[][] pieces = new Spatial[8][8];

        void put(Spatial piece) {
            int file = piece.getUserData("file");
            int rank = piece.getUserData("rank");
            pieces[file][rank] = piece;
        }

        @Override
        public Spatial pieceAt(int file, int rank) {
            return pieces[file][rank];
        }
    }
}
