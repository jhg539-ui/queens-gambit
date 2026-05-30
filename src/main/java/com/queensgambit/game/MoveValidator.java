package com.queensgambit.game;

import com.jme3.scene.Spatial;
import com.queensgambit.board.BoardState;
import com.queensgambit.pieces.PieceColor;
import com.queensgambit.pieces.PieceType;

public class MoveValidator {

    public boolean isLegalMove(BoardState board, Spatial piece, int toFile, int toRank) {
        Integer fromFile = piece.getUserData("file");
        Integer fromRank = piece.getUserData("rank");
        if (fromFile == null || fromRank == null || !isInside(toFile, toRank)) {
            return false;
        }
        if (fromFile == toFile && fromRank == toRank) {
            return false;
        }

        PieceType type = PieceType.valueOf(piece.getUserData("pieceType"));
        PieceColor color = PieceColor.valueOf(piece.getUserData("pieceColor"));
        Spatial target = board.pieceAt(toFile, toRank);
        if (target != null && color == PieceColor.valueOf(target.getUserData("pieceColor"))) {
            return false;
        }

        int dx = toFile - fromFile;
        int dy = toRank - fromRank;
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        return switch (type) {
            case PAWN -> isLegalPawnMove(board, color, fromFile, fromRank, toFile, toRank, dx, dy, target);
            case KNIGHT -> (absDx == 1 && absDy == 2) || (absDx == 2 && absDy == 1);
            case BISHOP -> absDx == absDy && isPathClear(board, fromFile, fromRank, toFile, toRank);
            case ROOK -> (dx == 0 || dy == 0) && isPathClear(board, fromFile, fromRank, toFile, toRank);
            case QUEEN -> (dx == 0 || dy == 0 || absDx == absDy)
                    && isPathClear(board, fromFile, fromRank, toFile, toRank);
            case KING -> absDx <= 1 && absDy <= 1;
        };
    }

    private boolean isLegalPawnMove(
            BoardState board,
            PieceColor color,
            int fromFile,
            int fromRank,
            int toFile,
            int toRank,
            int dx,
            int dy,
            Spatial target) {
        int direction = color == PieceColor.WHITE ? 1 : -1;
        int startRank = color == PieceColor.WHITE ? 1 : 6;

        if (dx == 0 && target == null) {
            if (dy == direction) {
                return true;
            }
            int middleRank = fromRank + direction;
            return fromRank == startRank
                    && dy == 2 * direction
                    && board.pieceAt(fromFile, middleRank) == null;
        }

        return Math.abs(dx) == 1 && dy == direction && target != null;
    }

    private boolean isPathClear(BoardState board, int fromFile, int fromRank, int toFile, int toRank) {
        int fileStep = Integer.compare(toFile, fromFile);
        int rankStep = Integer.compare(toRank, fromRank);

        int file = fromFile + fileStep;
        int rank = fromRank + rankStep;
        while (file != toFile || rank != toRank) {
            if (board.pieceAt(file, rank) != null) {
                return false;
            }
            file += fileStep;
            rank += rankStep;
        }
        return true;
    }

    private boolean isInside(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }
}
