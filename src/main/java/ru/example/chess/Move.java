package ru.example.chess;

import java.util.Objects;
public record Move(Square from, Square to, PieceType promotion) {
    public Move {
        Objects.requireNonNull(from); Objects.requireNonNull(to);
        if (from.equals(to)) throw new IllegalArgumentException("Начальная и конечная клетки совпадают");
        if (promotion != null && !java.util.Set.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).contains(promotion))
            throw new IllegalArgumentException("Пешка может превратиться только в ферзя, ладью, слона или коня");
    }
    public Move(Square from, Square to) { this(from, to, null); }
    @Override public String toString() { return from + "-" + to + (promotion == null ? "" : "=" + promotion); }
}