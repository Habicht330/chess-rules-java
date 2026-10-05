package ru.example.chess;
public record King(Color color) implements Piece {
    public King { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.KING; }
}
