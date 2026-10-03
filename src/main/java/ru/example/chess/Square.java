package ru.example.chess;

import java.util.Objects;
public record Square(int file, int rank) {
    public Square {
        if (file < 0 || file > 7 || rank < 0 || rank > 7)
            throw new IllegalArgumentException("Координаты клетки должны быть в диапазоне 0..7");
    }
    public static Square parse(String text) {
        Objects.requireNonNull(text, "Координата не должна быть null");
        if (!text.matches("[a-h][1-8]")) throw new IllegalArgumentException("Некорректная клетка: " + text);
        return new Square(text.charAt(0)-'a', text.charAt(1)-'1');
    }
    @Override public String toString() { return ""+(char)('a'+file)+(char)('1'+rank); }
}