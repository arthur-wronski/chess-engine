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

                0x0000000000000010L, // white queen
                0x1000000000000000L, // black queen

                0x0000000000000008L, // white king
                0x0800000000000000L, // black king

        };
    }

    public long[] getBitboards(){
        return this.bitboards;
    }
}
