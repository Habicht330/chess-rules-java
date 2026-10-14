package ru.example.chess;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ChessRulesTest {
    @Test void initialHasThirtyTwoPieces(){assertEquals(32,Position.initial().pieces().size());}
    @Test void initialWhiteHasTwentyLegalMoves(){assertEquals(20,ChessRules.legalMoves(Position.initial()).size());}
    @Test void squareRoundTrip(){assertEquals("h8",Square.parse("h8").toString());}
    @Test void squareRejectsOutOfRange(){assertThrows(IllegalArgumentException.class,()->new Square(8,0));}
    @Test void squareRejectsBadText(){assertThrows(IllegalArgumentException.class,()->Square.parse("z9"));}
    @Test void recordEqualsHashCodeContract(){var a=new Square(3,4);var b=new Square(3,4);assertEquals(a,b);assertEquals(a.hashCode(),b.hashCode());var values=new HashSet<Square>();values.add(a);values.add(b);assertEquals(1,values.size());}
    @Test void legalPawnMoveChangesTurn() throws Exception {var p=ChessRules.play(Position.initial(),new Move(Square.parse("e2"),Square.parse("e4")));assertEquals(Color.BLACK,p.turn());assertInstanceOf(Pawn.class,p.pieceAt(Square.parse("e4")));}
    @Test void emptyOriginThrows() {assertThrows(InvalidMoveException.class,()->ChessRules.play(Position.initial(),new Move(Square.parse("e4"),Square.parse("e5"))));}
    @Test void wrongSideThrows(){assertThrows(InvalidMoveException.class,()->ChessRules.play(Position.initial(),new Move(Square.parse("e7"),Square.parse("e5"))));}
    @Test void illegalPawnMoveThrows(){assertThrows(InvalidMoveException.class,()->ChessRules.play(Position.initial(),new Move(Square.parse("e2"),Square.parse("e5"))));}
    @Test void pawnDoubleMoveSetsEnPassant() throws Exception {var p=ChessRules.play(Position.initial(),new Move(Square.parse("e2"),Square.parse("e4")));assertEquals(Square.parse("e3"),p.enPassant());}
    @Test void pieceTypesPolymorphic(){assertEquals(PieceType.KNIGHT,new Knight(Color.WHITE).type());}
    @Test void positionDefensivelyCopiesInput(){var m=new HashMap<Square,Piece>();m.put(Square.parse("a1"),new Rook(Color.WHITE));var p=new Position(m,Color.WHITE,Set.of(),null);m.clear();assertEquals(1,p.pieces().size());}
    @Test void legalMovesAreImmutable(){assertThrows(UnsupportedOperationException.class,()->ChessRules.legalMoves(Position.initial()).clear());}
    @Test void moveRejectsSameSquare(){assertThrows(IllegalArgumentException.class,()->new Move(Square.parse("a1"),Square.parse("a1")));}

    @Test void detectsCheckmate() {
        var map=new HashMap<Square,Piece>();
        map.put(Square.parse("a8"),new King(Color.BLACK));
        map.put(Square.parse("c6"),new King(Color.WHITE));
        map.put(Square.parse("b7"),new Queen(Color.WHITE));
        var p=new Position(map,Color.BLACK,Set.of(),null);
        assertTrue(ChessRules.isCheckmate(p));
        assertFalse(ChessRules.isStalemate(p));
    }
    @Test void detectsStalemate() {
        var map=new HashMap<Square,Piece>();
        map.put(Square.parse("a8"),new King(Color.BLACK));
        map.put(Square.parse("c6"),new King(Color.WHITE));
        map.put(Square.parse("c7"),new Queen(Color.WHITE));
        var p=new Position(map,Color.BLACK,Set.of(),null);
        assertTrue(ChessRules.isStalemate(p));
        assertFalse(ChessRules.isCheckmate(p));
    }
}