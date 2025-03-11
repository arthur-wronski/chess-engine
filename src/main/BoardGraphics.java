package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

class BoardGraphics extends JPanel {
    static Board board = new Board();
    static int SQUARE_SIZE = 100;
    private static List<Integer> possibleMoves= new ArrayList<>();

    public void start(){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // need to on the click of a square, extract the square index and find all possible moves where
    // the starting square is square index
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("My Chess Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        frame.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                int clickedSquare = getSquareClicked(me.getX(), me.getY());
                possibleMoves = getPossibleMoves(clickedSquare);
                repaint();
            }
        });

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


    public Dimension getPreferredSize() {
        return new Dimension(8 * SQUARE_SIZE,8 * SQUARE_SIZE);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPossibleMoves(g, possibleMoves);
    }

    private void drawBoard(Graphics g){
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                drawSquare(g, row, column );

                int squareIndex = row * 8 + column;
                Optional<Piece> pieceOnSquare = board.getPieceFromSquareIndex(squareIndex);

                if (pieceOnSquare.isPresent()){
                    drawPiece(g, pieceOnSquare.get(), row, column);
                }

            }
        }
    }

    private void drawSquare(Graphics g, int row, int column){
        // get square colour based on coordinate
        if ((row + column) % 2 == 0) {
            g.setColor(new Color(109, 77, 54));
        } else {
            g.setColor(new Color(181, 137, 105));
        }

        // draw square
        g.fillRect(column * SQUARE_SIZE,7 * SQUARE_SIZE - row * SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
    }

    private void drawPiece(Graphics g, Piece piece, int row, int column){
        g.drawImage(piece.getPieceImage(), column * SQUARE_SIZE,7 * SQUARE_SIZE - row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, this);
    }

    private static int getSquareClicked(int x, int y){
        // given (x,y) coordinates of user click
        // get the index of the square that was clicked

        int columnClicked = x / SQUARE_SIZE;
        int rowClicked = (8 * SQUARE_SIZE -y) / SQUARE_SIZE;

        return rowClicked * 8 + columnClicked;
    }

    private static List<Integer> getPossibleMoves(int squareIndex){
        List<Integer> targetSquares = new ArrayList<>();
        // based on squareIndex, go through allMoves and get all moves which have
        // squareIndex as startingSquare and return a list of their targetSquares
        for (Move move : board.getAllMoves()){
            if (move.getStartingSquare() == squareIndex){
                targetSquares.add(move.getTargetSquare());
            }
        }
        return targetSquares;
    }

    private static void drawPossibleMoves(Graphics g, List<Integer> possibleMoves){
        int CIRCLE_SIZE = 20;
        g.setColor(new Color(180, 180, 180, 255));
        // for each squareIndex in possible moves
        // draw small circle?
        for (int possibleMove : possibleMoves){
            int row = possibleMove / 8;
            int column = possibleMove % 8;
            g.fillOval(column * SQUARE_SIZE  + 40, 7 * SQUARE_SIZE - row * SQUARE_SIZE + 40, CIRCLE_SIZE, CIRCLE_SIZE);
        }
    }
}
