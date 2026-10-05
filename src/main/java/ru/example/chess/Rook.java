package ru.example.chess;
public record Rook(Color color) implements Piece {
    public Rook { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.ROOK; }
}
