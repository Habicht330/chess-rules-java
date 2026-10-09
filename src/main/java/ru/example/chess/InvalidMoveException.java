package ru.example.chess;
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) { super(message); }
}