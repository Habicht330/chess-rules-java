package ru.example.chess;
public record Queen(Color color) implements Piece {
    public Queen { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.QUEEN; }
}
