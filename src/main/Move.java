package main;

import java.util.Optional;

public class Move {
    private final Piece pieceToMove;
    private final int startingSquare;
    private final int targetSquare;
    private final Optional<Piece> capturedPiece;

    public Move(Piece pieceToMove, int startingSquare, int targetSquare, Optional<Piece> capturedPiece){
        this.pieceToMove = pieceToMove;
        this.startingSquare = startingSquare;
        this.targetSquare = targetSquare;

        this.capturedPiece = capturedPiece;
    }

    public Piece getPieceToMove() {
        return pieceToMove;
    }

    public int getStartingSquare() {
        return startingSquare;
    }

    public int getTargetSquare() {
        return targetSquare;
    }

    public Optional<Piece> getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pieceToMove).append(" from ").append(BoardUtils.getSquareNameFromIndex(startingSquare))
                .append(" to ").append(BoardUtils.getSquareNameFromIndex(targetSquare));

        capturedPiece.ifPresent(piece -> sb.append(", captures ").append(piece));

        return sb.toString();
    }
}
