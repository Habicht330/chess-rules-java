package ru.example.chess;
public sealed interface Piece permits King, Queen, Rook, Bishop, Knight, Pawn {
    Color color();
    PieceType type();
}
