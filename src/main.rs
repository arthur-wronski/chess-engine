// TODO: create an instance of the singleton board class that initiates bitboards array with all the starting positions,

// Index 0: White Pawns
// Index 1: White Knights
// Index 2: White Bishops
// Index 3: White Rooks
// Index 4: White Queen
// Index 5: White King
// Index 6: Black Pawns
// Index 7: Black Knights
// Index 8: Black Bishops
// Index 9: Black Rooks
// Index 10: Black Queen
// Index 11: Black King

mod board;

use board::Board;

fn main() {
    let white_pawns: u64 = 0x00FF000000000000;
    let white_knights: u64 = 0x4200000000000000;
    let white_bishops: u64 = 0x2400000000000000;
    let white_rooks: u64 = 0x8100000000000000;
    let white_queen: u64 = 0x1000000000000000;
    let white_king: u64 = 0x0800000000000000;
    let black_pawns: u64 = 0x000000000000FF00;
    let black_knights: u64 = 0x0000000000000042;
    let black_bishops: u64 = 0x0000000000000024;
    let black_rooks: u64 = 0x0000000000000081;
    let black_queen: u64 = 0x0000000000000010;
    let black_king: u64 = 0x0000000000000008;

    let mut chess_board = Board {
        bitboards : [white_pawns, white_knights, white_bishops, white_rooks, white_queen, white_king, black_pawns, black_knights, black_bishops, black_rooks, black_queen, black_king]
    };

    println!("{:?}", chess_board.bitboards);
}

