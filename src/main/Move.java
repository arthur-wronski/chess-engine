package main;

public class Move {
    private final Piece pieceToMove;
    private final int startingSquare;
    private final int targetSquare;

    public Move(Piece pieceToMove, int startingSquare, int targetSquare){
        this.pieceToMove = pieceToMove;
        this.startingSquare = startingSquare;
        this.targetSquare = targetSquare;
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
}
