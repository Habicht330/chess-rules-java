package ru.example.chess;

import java.util.*;
public class Main {
    public static void main(String[] args) throws Exception {
        var p=Position.initial();
        System.out.println("Шахматная модель. Фигур в начальной позиции: "+p.pieces().size());
        System.out.println("Очередь хода: "+p.turn());
        System.out.println("Легальных ходов белых: "+ChessRules.legalMoves(p).size());
        System.out.println("Фигуры на доске:");
        p.pieces().entrySet().stream().sorted(Comparator.comparing(e->e.getKey().toString())).forEach(e->System.out.println(e.getKey()+" -> "+e.getValue().color()+" "+e.getValue().type()));
        p=ChessRules.play(p,new Move(Square.parse("e2"),Square.parse("e4")));
        System.out.println("\nПосле e2-e4: очередь "+p.turn()+", поле en passant="+p.enPassant());
        System.out.println("Легальных ходов чёрных: "+ChessRules.legalMoves(p).size());
        try { ChessRules.play(p,new Move(Square.parse("h2"),Square.parse("h4"))); System.out.println("Ход h2-h4 выполнен."); }
        catch(InvalidMoveException ex){System.out.println("Ход отклонён: "+ex.getMessage());}
        System.out.println("Чёрный король под шахом: "+ChessRules.isInCheck(p,Color.BLACK));
    }
}