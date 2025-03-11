package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    // on instantiation, create all the pieces and set up starting position
    // will have all the functions for moving and taking pieces, etc...

    // array of bitmaps in order: white pawns, black pawns, white knights, ..., black king.
    // Will use enum ordering in piece type + colour to quickly access correct bitmap
    // e.g. bishop = 2 and black = 1 => access 2 * 2 + 1 = 5th bitmap
    private final long[] bitboards;
    private Colour colourToPlay;
    private List<Move> allMoves;

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
        this.allMoves = getAllLegalMoves();
    }

    public long[] getBitboards(){
        return this.bitboards;
    }

    public List<Move> getAllMoves(){
        return this.allMoves;
    }

    public double evaluatePosition(){
        // how much the pieces are worth for eval: pawn = 1, knight = 3, bishop = 3.5, rook = 5, queen = 9
        double[] pieceWeights = new double[]{1, 3, 3.5, 5, 9};

        // eval will always give white a slight edge
        double evaluation = 0.2;

        // skipping king bitboards for evaluation
        for (int i = 0; i < bitboards.length - 2; i++){
            // will add or subtract based on if it's white or black's pieces
            boolean negative = i % 2 == 1;

            // get the total number of that particular piece on the board
            int pieceCount = Long.bitCount(bitboards[i]);
            double totalPieceWorth = pieceCount * pieceWeights[i / 2];

            evaluation += (negative ? -totalPieceWorth : totalPieceWorth);
        }

        return evaluation;
    }

    private List<Move> getAllLegalMoves(){
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
        Colour colour = piece.getColour();
        long allies = colour == Colour.White ? getWhiteBitboard() : getBlackBitboard();
        long enemies = colour == Colour.White ? getBlackBitboard() : getWhiteBitboard();

        switch (piece.getType()){
            case PieceType.Pawn:
                moves.addAll(getPawnMoves(piece, position));
                break;
            case PieceType.Knight:
                // iterate through all possible move indexes, must check that it is within board + no allies

                // can skip exploring a lot of moves if check position at beginning
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
        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();
        long enemies = piece.getColour() == Colour.White ? getBlackBitboard() : getWhiteBitboard();

        // only need to explore 4 directions, iterate until it finds enemy or ally
        int squaresUnder = position / 8;
        int squaresOver = 7 - squaresUnder;

        int squaresLeft = position % 8;
        int squaresRight = 7 - squaresLeft;

        for (int i = 1; i < Math.min(squaresOver, squaresRight); i++){
            int targetSquare = position + 9 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < Math.min(squaresOver, squaresLeft); i++){
            int targetSquare = position + 7 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < Math.min(squaresUnder, squaresLeft); i++){
            int targetSquare = position - 9 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < Math.min(squaresUnder, squaresRight); i++){
            int targetSquare = position - 7 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

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
        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();
        long enemies = piece.getColour() == Colour.White ? getBlackBitboard() : getWhiteBitboard();

        // only need to explore 4 directions, iterate until it finds enemy or ally
        int squaresUnder = position / 8;
        int squaresOver = 7 - squaresUnder;

        int squaresLeft = position % 8;
        int squaresRight = 7 - squaresLeft;

        for (int i = 1; i < squaresOver; i++){
            int targetSquare = position + 8 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < squaresUnder; i++){
            int targetSquare = position - 8 * i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < squaresLeft; i++){
            int targetSquare = position - i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

            if ((allies >> targetSquare & 1) == 1){
                break;
            }

            moves.add(new Move(piece, position, targetSquare));

            if ((enemies >> targetSquare & 1) == 1){
                break;
            }
        }

        for (int i = 1; i < squaresRight; i++){
            int targetSquare = position + i;
            // if ally detected -> exit loop
            // if enemy detected -> append and exit loop
            // else -> append and continue

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

    private List<Move> getKingMoves(Piece piece, int position){
        List<Move> moves = new ArrayList<>();
        long allies = piece.getColour() == Colour.White ? getWhiteBitboard() : getBlackBitboard();

        // need to check every position
        int row = position / 8;

        boolean canMoveUp = row < 7;
        boolean canMoveDown = row > 0;

        int column = position % 8;

        boolean canMoveLeft = column > 0;
        boolean canMoveRight = column < 7;

        if (canMoveUp){
            if (((allies >> (position + 8)) & 1) == 0){
                moves.add(new Move(piece, position, position + 8));
            }

            // if king can move left and no ally
            if (canMoveLeft && ((allies >> (position + 7)) & 1) == 0){
                moves.add(new Move(piece, position, position + 7));
            }

            // if king can move left and no ally
            if (canMoveRight && ((allies >> (position + 9)) & 1) == 0){
                moves.add(new Move(piece, position, position + 9));
            }
        }

        if (canMoveDown){
            if (((allies >> (position - 8)) & 1) == 0){
                moves.add(new Move(piece, position, position - 8));
            }

            // if king can move left and no ally
            if (canMoveLeft && ((allies >> (position - 9)) & 1) == 0){
                moves.add(new Move(piece, position, position - 9));
            }

            // if king can move left and no ally
            if (canMoveRight && ((allies >> (position - 7)) & 1) == 0){
                moves.add(new Move(piece, position, position - 7));
            }
        }

        if (canMoveLeft && ((allies >> position  - 1) == 0)){
            moves.add(new Move(piece, position, position - 1));
        }

        if (canMoveRight && ((allies >> position  + 1) == 0)){
            moves.add(new Move(piece, position, position + 1));
        }

        return moves;
    }
}
