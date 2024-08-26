// Need to set up all the starting bitboards for all the piece + colour combinations
// These will be 64 bits, first bit being A1 then A2 ...
// TODO: write up all the initial bitboards and render them on a chessboard to see

// A2, B2, ..., H2
let mut whitePawns: u64 = 0x00FF000000000000

// A7, B7, ..., H7
let mut blackPawns: u64 = 0x000000000000FF00

// A1 + H1
let mut whiteRook: u64 = 0x8100000000000000

// A8 + H8
let mut blackRook: u64 = 0x0000000000000081

// B1 + G1
let mut whiteKnight: u64 = 0x4200000000000000

// B8 + G8
let mut blackKnight: u64 = 0x0000000000000042

// C1 + F1
let mut whiteBishop: u64 = 0x2400000000000000

// C8 + F8
let mut blackBishop: u64 = 0x0000000000000024

// D1
let mut whiteQueen: u64 = 0x1000000000000000

// D8
let mut blackQueen: u64 = 0x0000000000000010

// E1
let mut whiteKing: u64 = 0x0800000000000000

// E8
let mut blackKing: u64 = 0x0000000000000008





