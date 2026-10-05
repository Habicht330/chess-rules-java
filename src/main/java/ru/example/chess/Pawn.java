package ru.example.chess;
public record Pawn(Color color) implements Piece {
    public Pawn { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.PAWN; }
}
