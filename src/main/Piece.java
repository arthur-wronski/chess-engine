package main;

import javax.swing.*;
import java.awt.*;

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

    public Image getPieceImage(){
        String name = this.colour.toString().toLowerCase() + "-" + this.type.toString().toLowerCase();
        return new ImageIcon("src/img/" + name + ".png").getImage();
    }
}
