package ru.example.chess;

import java.util.*;
public final class Position {
    private final Map<Square, Piece> pieces;
    private final Color turn;
    private final Set<String> castling;
    private final Square enPassant;

    public Position(Map<Square, Piece> pieces, Color turn, Set<String> castling, Square enPassant) {
        Objects.requireNonNull(pieces); this.turn=Objects.requireNonNull(turn);
        this.pieces=Map.copyOf(pieces); this.castling=Set.copyOf(castling); this.enPassant=enPassant;
        long kings=this.pieces.values().stream().filter(p -> p.type()==PieceType.KING).count();
        if (kings > 2) throw new IllegalArgumentException("В позиции не может быть больше двух королей");
    }
    public static Position initial() {
        var m=new HashMap<Square,Piece>();
        for (int f=0;f<8;f++) { m.put(new Square(f,1),new Pawn(Color.WHITE)); m.put(new Square(f,6),new Pawn(Color.BLACK)); }
        Piece[] back={new Rook(Color.WHITE),new Knight(Color.WHITE),new Bishop(Color.WHITE),new Queen(Color.WHITE),new King(Color.WHITE),new Bishop(Color.WHITE),new Knight(Color.WHITE),new Rook(Color.WHITE)};
        Piece[] black={new Rook(Color.BLACK),new Knight(Color.BLACK),new Bishop(Color.BLACK),new Queen(Color.BLACK),new King(Color.BLACK),new Bishop(Color.BLACK),new Knight(Color.BLACK),new Rook(Color.BLACK)};
        for(int f=0;f<8;f++){m.put(new Square(f,0),back[f]);m.put(new Square(f,7),black[f]);}
        return new Position(m,Color.WHITE,Set.of("K","Q","k","q"),null);
    }
    public Piece pieceAt(Square s) { return pieces.get(s); }
    public Map<Square,Piece> pieces() { return pieces; }
    public Color turn() { return turn; }
    public boolean canCastle(String right) { return castling.contains(right); }
    public Square enPassant() { return enPassant; }
    Set<String> castlingRights() { return castling; }
}