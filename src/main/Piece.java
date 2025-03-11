package main;

public class Piece {
    private final Colour colour;
    private final PieceType type;

    public Piece(Colour colour, PieceType type){
        this.colour = colour;
        this.type = type;
    }

    public Colour getColour() {
        return colour;
    }

    public PieceType getType() {
        return type;
    }

    @Override
    public String toString(){
        return colour.toString() + " " +  type.toString();
    }
}
