package main;

public class Board {
    // on instantiation, create all the pieces and set up starting position
    // will have all the functions for moving and taking pieces, etc...

    // array of bitmaps in order: white pawns, black pawns, white knights, ..., black king.
    // Will use enum ordering in piece type + colour to quickly access correct bitmap
    // e.g. bishop = 2 and black = 1 => access 2 * 2 + 1 = 5th bitmap
    private final long[] bitboards;

    public Board() {
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
    }

    public long[] getBitboards(){
        return this.bitboards;
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
}
