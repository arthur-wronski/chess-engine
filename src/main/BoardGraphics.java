package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

class BoardGraphics extends JPanel {
    Board board = new Board();
    long[] bitboards = board.getBitboards();


    public Dimension getPreferredSize() {
        return new Dimension(800,800);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBoard(g);
    }

    private void drawBoard(Graphics g){
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                drawSquare(g, row, column );

                drawPiece(g, row, column);
            }
        }
    }

    private void drawSquare(Graphics g, int row, int column){
        int SQUARE_SIZE = 100;

        // get square colour based on coordinate
        if ((row + column) % 2 == 1) {
            g.setColor(new Color(109, 77, 54));
        } else {
            g.setColor(new Color(181, 137, 105));
        }

        // draw square
        g.fillRect(column * SQUARE_SIZE,700 - row * SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
    }

    private void drawPiece(Graphics g, int row, int column){
        // given the coordinates of a chess square, check if any of the bitmaps have a 1 there
        // if so, print the piece out

        // will rename this to be smth to check if a piece is present on that square and have another
        // function that takes in the piece and coordinates and outputs it
        int squareIndex = row * 8 + column;

        for (int k = 0; k < bitboards.length; k++) {
            long bitboard = bitboards[k];
            if ((bitboard >> squareIndex & 1) != 0) {
                Piece piece = getPieceFromBitboardIndex(k);
                g.drawImage(piece.getPieceImage(), column * 100,700 - row * 100, 100, 100, this);
                // there will never be two pieces on the same square therefore we can stop iterating
                return;
            }
        }
    }

    private Piece getPieceFromBitboardIndex(int bitboardIndex){
        Colour pieceColour = (bitboardIndex % 2 == 0) ? Colour.White : Colour.Black;
        PieceType pieceType = PieceType.values()[bitboardIndex / 2];
        return new Piece(pieceColour, pieceType);
    }

    // need a separate class with the placement of all the pieces, this will get all the piece positions
    // and display the relevant image at said position
}
