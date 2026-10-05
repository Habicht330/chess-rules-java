package ru.example.chess;
public record Knight(Color color) implements Piece {
    public Knight { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.KNIGHT; }
}
