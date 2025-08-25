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
    private List<Move> possibleMoves = new ArrayList<>();

    public void start(){
        javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    // need to on the click of a square, extract the square index and find all possible moves where
    // the starting square is square index
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("My Chess Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        frame.addMouseListener(new MouseAdapter() {
            private Point pressPoint;

            @Override
            public void mousePressed(MouseEvent me) {
                // Store the point where the mouse is pressed
                pressPoint = me.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                Point releasePoint = me.getPoint();
                // Check if the release point is close to the press point (within a few pixels)
                if (pressPoint.distance(releasePoint) < 10) {  // 10 pixels tolerance for slight movement
                    // Treat this as a valid click
                    int clickedSquare = getSquareClicked(me.getX(), me.getY());

                    for (Move possibleMove : possibleMoves){
                        if (possibleMove.getTargetSquare() == clickedSquare){
                            board.playMove(possibleMove);
                            possibleMoves = new ArrayList<>();
                            repaint();
                            if (board.colourToPlay == Colour.Black) {
                                new javax.swing.Timer(100, e -> {
                                    board.playMove(board.findBestMove(5));
                                    repaint();
                                    ((javax.swing.Timer)e.getSource()).stop();
                                }).start();
                            }
                            return;
                        }
                    }

                    possibleMoves = getPossibleMoves(clickedSquare);
                    repaint();
                }
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
        drawPossibleMoves(g);
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
            g.setColor(new Color(160, 82, 45));
        } else {
            g.setColor(new Color(222, 184, 135));
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

    private static List<Move> getPossibleMoves(int squareIndex){
        List<Move> possibleMoves = new ArrayList<>();
        // based on squareIndex, go through allMoves and get all moves which have
        // squareIndex as startingSquare and return a list of their targetSquares
        for (Move move : board.getLegalMoves()){
            if (move.getStartingSquare() == squareIndex){
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }

    private void drawPossibleMoves(Graphics g){
        int CIRCLE_SIZE = 20;
        g.setColor(new Color(180, 180, 180, 255));
        // for each squareIndex in possible moves
        // draw small circle?
        for (Move possibleMove : possibleMoves){
            int row = possibleMove.getTargetSquare() / 8;
            int column = possibleMove.getTargetSquare() % 8;
            g.fillOval(column * SQUARE_SIZE  + 40, 7 * SQUARE_SIZE - row * SQUARE_SIZE + 40, CIRCLE_SIZE, CIRCLE_SIZE);
        }
    }
}
