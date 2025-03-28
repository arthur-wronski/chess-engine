package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    // array of bitmaps in order: white pawns, black pawns, white knights, ..., black king.
    // Will use enum ordering in piece type + colour to quickly access correct bitmap
    // e.g. bishop = 2 and black = 1 => access 2 * 2 + 1 = 5th bitmap
    private final long[] bitboards;
    private Colour colourToPlay;
    private List<Move> legalMoves;
    private double evaluation;

    public Board() {
        // starting position
        this.bitboards = new long[]{
                0x000000000000ff00L, // white pawns
                0x00ff000000000000L, // black pawns

                0x0000000000000042L, // white knights
                0x4200000000000000L, // black knights

                0x0000000000000024L, // white bishops
                0x2400000000000000L, // black bishops

                0x0000000000000081L, // white rooks
                0x8100000000000000L, // black rooks

                0x0000000000000008L, // white queen
                0x0800000000000000L, // black queen

                0x0000000000000010L, // white king
                0x1000000000000000L, // black king
        };

        this.colourToPlay = Colour.White;
        this.legalMoves = calculateAllLegalMoves();
        this.evaluation = 0.2;
    }

    public List<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public void evaluatePosition(){
        // how much the pieces are worth for eval: pawn = 1, knight = 3, bishop = 3.5, rook = 5, queen = 9
        double[] pieceWeights = new double[]{1, 3, 3.5, 5, 9};

        // eval will always give white a slight edge
        double evaluationTotal = 0.2;

        // skipping king bitboards for evaluation
        for (int i = 0; i < bitboards.length - 2; i++){
            // will add or subtract based on if it's white or black's pieces
            boolean negative = i % 2 == 1;

            // get the total number of that particular piece on the board
            int pieceCount = Long.bitCount(bitboards[i]);
            double totalPieceWorth = pieceCount * pieceWeights[i / 2];
            evaluationTotal += (negative ? -totalPieceWorth : totalPieceWorth);
        }

        System.out.println("Current evaluation: " + evaluationTotal);
        evaluation = evaluationTotal;
    }

    private List<Move> calculateAllLegalMoves(){
        List<Move> moves = new ArrayList<Move>();

        for (int i = 0; i < 64; i++){
            Optional<Piece> piecePresent = getPieceFromSquareIndex(i);

            if (piecePresent.isEmpty() || piecePresent.get().getColour() != colourToPlay){
                continue;
            }

            // need function that takes in a piece and its position and returns all legal moves
            moves.addAll(getAllPieceMoves(piecePresent.get(), i));

        }
        return moves;
    }

    private long getWhiteBitboard(){
        return bitboards[0] | bitboards[2] | bitboards[4] | bitboards[6] | bitboards[8] | bitboards[10];
    }

    private long getBlackBitboard(){
        return bitboards[1] | bitboards[3] | bitboards[5] | bitboards[7] | bitboards[9] | bitboards[11];
    }

    private long getBothBitboards(){
        return getWhiteBitboard() | getBlackBitboard();
    }

    private Piece getPieceFromBitboardIndex(int bitboardIndex){
        Colour pieceColour = (bitboardIndex % 2 == 0) ? Colour.White : Colour.Black;
        PieceType pieceType = PieceType.values()[bitboardIndex / 2];
        return new Piece(pieceColour, pieceType);
    }

    public Optional<Piece> getPieceFromSquareIndex(int squareIndex){
        for (int i = 0; i < bitboards.length; i++){
            long bitboard = bitboards[i];
            if ((bitboard >> squareIndex & 1) != 0){
                return Optional.of(getPieceFromBitboardIndex(i));
            }
        }
        return Optional.empty();
    }

    private List<Move> getAllPieceMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();

        switch (piece.getType()){
            case PieceType.Pawn:
                moves.addAll(getPawnMoves(piece, position));
                break;
            case PieceType.Knight:
                moves.addAll(getKnightMoves(piece, position));
                break;
            case PieceType.Bishop:
                moves.addAll(getBishopMoves(piece, position));
                break;
            case PieceType.Rook:
                moves.addAll(getRookMoves(piece, position));
                break;
            case PieceType.Queen:
                moves.addAll(getBishopMoves(piece, position));
                moves.addAll(getRookMoves(piece, position));
                break;
            case PieceType.King:
                moves.addAll(getKingMoves(piece, position));
        }

        return moves;
    }

    private boolean squareOnBoard(int squareIndex){
        return squareIndex >= 0 && squareIndex <= 63;
    }

    private List<Move> getPawnMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();
        Colour colour = piece.getColour();
        long enemies = colour == Colour.White ? getBlackBitboard() : getWhiteBitboard();

        // move up if no other piece and within board
        int forwardIndex = colour == Colour.White ? position + 8 : position - 8;

        // check if move forward is within board and target square is unoccupied
        if (squareOnBoard(forwardIndex) && (getBothBitboards() >> forwardIndex & 1) == 0){
            moves.add(new Move(piece, position, forwardIndex));

            int currentRow = position / 8;
            boolean hasNotMoved = colour == Colour.White ? currentRow == 1 : currentRow == 6;
            int twoForwardIndex = colour == Colour.White ? forwardIndex + 8 : forwardIndex - 8;

            // if pawn hasn't moved and square free
            if (hasNotMoved && (getBothBitboards() >> twoForwardIndex & 1) == 0 ){
                moves.add(new Move(piece, position, twoForwardIndex));
            }
        }

        // take diagonally if piece of opposite colour there and within board
        int leftTakeIndex = colour == Colour.White ? position + 7 : position - 9;

        // must check -> on board, pawn is not on left-most column and opposite coloured piece on target square
        if (squareOnBoard(leftTakeIndex) && (position % 8 != 0) && ((enemies >> leftTakeIndex & 1) == 1)){
            moves.add(new Move(piece, position, leftTakeIndex));
        }

        int rightTakeIndex = colour == Colour.White ? position + 9 : position - 7;

        if (squareOnBoard(rightTakeIndex) && (position % 8 != 7) && ((enemies >> rightTakeIndex & 1) == 1)){
            moves.add(new Move(piece, position, rightTakeIndex));
        }

        return moves;
    }

    private List<Move> getBishopMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();

        // only need to explore 4 directions, iterate until it finds enemy or ally
        int squaresUnder = position / 8;
        int squaresOver = 7 - squaresUnder;

        int squaresLeft = position % 8;
        int squaresRight = 7 - squaresLeft;

        moves.addAll(exploreDirection(piece, position, 9, Math.min(squaresOver, squaresRight)));
        moves.addAll(exploreDirection(piece, position, 7, Math.min(squaresOver, squaresLeft)));
        moves.addAll(exploreDirection(piece, position, -9, Math.min(squaresUnder, squaresLeft)));
        moves.addAll(exploreDirection(piece, position, -7, Math.min(squaresUnder, squaresRight)));

        return moves;
    }

    private List<Move> exploreDirection(Piece piece, int position, int direction, int maxMoves){
        List<Move> moves = new ArrayList<>();

        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();
        long enemies = piece.getColour() == Colour.White ? getBlackBitboard() : getWhiteBitboard();

        for (int i = 1; i <= maxMoves; i++){
            int targetSquare = position + direction * i;

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }
        return moves;
    }

    private List<Move> getRookMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();

        // only need to explore 4 directions, iterate until it finds enemy or ally
        int squaresUnder = position / 8;
        int squaresOver = 7 - squaresUnder;

        int squaresLeft = position % 8;
        int squaresRight = 7 - squaresLeft;

        moves.addAll(exploreDirection(piece, position, 8, squaresOver));
        moves.addAll(exploreDirection(piece, position, 1, squaresRight));
        moves.addAll(exploreDirection(piece, position, -1, squaresLeft));
        moves.addAll(exploreDirection(piece, position, -8, squaresUnder));

        return moves;
    }

    private List<Move> getKnightMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();

        int[] directions = {10, -10, 6, -6, 17, -17, 15, -15};

        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();

        int startingColumn = position % 8;

        for (int direction: directions){
            int targetSquare = position + direction;

            // if squareIndex outside of 0 and 63, skip move
            if (!squareOnBoard(targetSquare)){
                continue;
            }

            int targetColumn = targetSquare % 8;

            // if wrapped around board
            if (Math.abs(startingColumn -targetColumn) > 2){
                continue;
            }

            // if no allies
            if (((allies >> targetSquare) & 1) == 0){
                moves.add(new Move(piece, position, targetSquare));
            }
        }
        return moves;
    }

    private List<Move> getKingMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();

        int[] directions = {-1,1,8,-8,7,-7,9,-9};

        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();

        int startingColumn = position % 8;

        for (int direction: directions){
            int targetSquare = position + direction;

            // if squareIndex outside of 0 and 63, skip move
            if (!squareOnBoard(targetSquare)){
                continue;
            }

            int targetColumn = targetSquare % 8;

            // if wrapped around board
            if (Math.abs(startingColumn -targetColumn) > 1){
                continue;
            }

            // if no allies
            if (((allies >> targetSquare) & 1) == 0){
                moves.add(new Move(piece, position, targetSquare));
            }
        }
        return moves;
    }

    public void playMove(Move move){
        // play move, it should change the relevant bitmaps
        // get bitmap index from piece to move,
        int bitboardIndex = getBitboardIndexFromPiece(move.getPieceToMove());
        // flip bit on starting square
        bitboards[bitboardIndex] ^= 1L << move.getStartingSquare();

        long mask = ~(1L << move.getTargetSquare());

        // flip all other bitboard[targetSquare] to 0
        for (int i = 0; i < bitboards.length; i++){
            // sets bitboard[targetSquare] = 0
            bitboards[i] &= mask;
        }

        // flip bit on target square, (sets to 1 as was set to 0 just before)
        bitboards[bitboardIndex] ^= 1L << move.getTargetSquare();

        // trigger Eval recalculation
        evaluatePosition();
        colourToPlay = colourToPlay == Colour.White? Colour.Black : Colour.White;
        legalMoves = calculateAllLegalMoves();

    }

    private int getBitboardIndexFromPiece(Piece piece){
        int bitboardIndex = piece.getColour() == Colour.White ? 0 : 1;

        return switch (piece.getType()) {
            case PieceType.Pawn -> bitboardIndex;
            case PieceType.Knight -> bitboardIndex + 2;
            case PieceType.Bishop -> bitboardIndex + 4;
            case PieceType.Rook -> bitboardIndex + 6;
            case PieceType.Queen -> bitboardIndex + 8;
            case PieceType.King -> bitboardIndex + 10;
        };
    }
}
