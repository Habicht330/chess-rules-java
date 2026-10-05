package ru.example.chess;
public record Bishop(Color color) implements Piece {
    public Bishop { java.util.Objects.requireNonNull(color, "Цвет обязателен"); }
    @Override public PieceType type() { return PieceType.BISHOP; }
}
