package ru.example.chess;

import java.util.*;
public final class ChessRules {
    private ChessRules() {}
    private static final int[][] KN={{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};
    private static final int[][] KING={{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
    private static final int[][] ORTH={{1,0},{-1,0},{0,1},{0,-1}};
    private static final int[][] DIAG={{1,1},{1,-1},{-1,1},{-1,-1}};
    public static List<Move> legalMoves(Position p) {
        var out=new ArrayList<Move>();
        for(var e:p.pieces().entrySet()) if(e.getValue().color()==p.turn())
            for(var m:pseudo(p,e.getKey(),e.getValue())) {
                var next=applyUnchecked(p,m);
                if(!isInCheck(next,p.turn())) out.add(m);
            }
        return List.copyOf(out);
    }

    public static boolean isInCheck(Position p, Color color) {
        Square king=null;
        for(var e:p.pieces().entrySet()) if(e.getValue().type()==PieceType.KING && e.getValue().color()==color) {king=e.getKey();break;}
        if(king==null) return false;
        for(var e:p.pieces().entrySet()) if(e.getValue().color()!=color && attacks(p,e.getKey(),e.getValue(),king)) return true;
        return false;
    }

    public static Position play(Position p, Move m) throws InvalidMoveException {
        if(!p.pieces().containsKey(m.from())) throw new InvalidMoveException("На "+m.from()+" нет фигуры");
        if(p.pieceAt(m.from()).color()!=p.turn()) throw new InvalidMoveException("Сейчас ходит "+p.turn());
        if(!legalMoves(p).contains(m)) throw new InvalidMoveException("Ход "+m+" недопустим: нарушает правила или оставляет короля под шахом");
        return applyUnchecked(p,m);
    }

    public static boolean isCheckmate(Position p) {
        return isInCheck(p, p.turn()) && legalMoves(p).isEmpty();
    }
    public static boolean isStalemate(Position p) {
        return !isInCheck(p, p.turn()) && legalMoves(p).isEmpty();
    }
    public static boolean isGameOver(Position p) {
        return isCheckmate(p) || isStalemate(p);
    }

    private static boolean inside(int f,int r){return f>=0&&f<8&&r>=0&&r<8;}
    private static Square sq(int f,int r){return inside(f,r)?new Square(f,r):null;}
    private static List<Move> pseudo(Position p,Square from,Piece pc) {
        var out=new ArrayList<Move>(); int f=from.file(),r=from.rank();
        if(pc.type()==PieceType.PAWN) {
            int d=pc.color()==Color.WHITE?1:-1, start=pc.color()==Color.WHITE?1:6;
            Square one=sq(f,r+d);
            if(one!=null&&p.pieceAt(one)==null) {
                addPawnMove(out,from,one,pc.color());
                Square two=sq(f,r+2*d);
                if(r==start&&two!=null&&p.pieceAt(two)==null) out.add(new Move(from,two));
            }
            for(int df:new int[]{-1,1}) {
                Square to=sq(f+df,r+d); if(to==null)continue;
                Piece target=p.pieceAt(to);
                if(target!=null&&target.color()!=pc.color()&&target.type()!=PieceType.KING) addPawnMove(out,from,to,pc.color());
                if(to.equals(p.enPassant())) out.add(new Move(from,to));
            }
            return out;
        }
        int[][] steps=switch(pc.type()){case KNIGHT->KN;case KING->KING;case ROOK->ORTH;case BISHOP->DIAG;case QUEEN->new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};default->new int[0][0];};
        boolean slide=pc.type()==PieceType.ROOK||pc.type()==PieceType.BISHOP||pc.type()==PieceType.QUEEN;
        for(var v:steps) for(int n=1;n<= (slide?7:1);n++) {
            Square to=sq(f+v[0]*n,r+v[1]*n); if(to==null)break;
            Piece target=p.pieceAt(to);
            if(target==null)out.add(new Move(from,to));
            else {if(target.color()!=pc.color()&&target.type()!=PieceType.KING)out.add(new Move(from,to));break;}
        }
        if(pc.type()==PieceType.KING) addCastles(p,from,pc.color(),out);
        return out;
    }
    private static void addPawnMove(List<Move> out,Square from,Square to,Color c) {
        if(to.rank()==(c==Color.WHITE?7:0)) for(var t:List.of(PieceType.QUEEN,PieceType.ROOK,PieceType.BISHOP,PieceType.KNIGHT)) out.add(new Move(from,to,t));
        else out.add(new Move(from,to));
    }
    private static void addCastles(Position p,Square from,Color c,List<Move> out) {
        int r=c==Color.WHITE?0:7; String k=c==Color.WHITE?"K":"k", q=c==Color.WHITE?"Q":"q";
        if(!from.equals(new Square(4,r))||isInCheck(p,c))return;
        if(p.canCastle(k)&&p.pieceAt(new Square(7,r)) instanceof Rook rook&&rook.color()==c&&p.pieceAt(new Square(5,r))==null&&p.pieceAt(new Square(6,r))==null
          &&!attacked(p,new Square(5,r),c)&&!attacked(p,new Square(6,r),c))out.add(new Move(from,new Square(6,r)));
        if(p.canCastle(q)&&p.pieceAt(new Square(0,r)) instanceof Rook rook&&rook.color()==c&&p.pieceAt(new Square(1,r))==null&&p.pieceAt(new Square(2,r))==null&&p.pieceAt(new Square(3,r))==null
          &&!attacked(p,new Square(3,r),c)&&!attacked(p,new Square(2,r),c))out.add(new Move(from,new Square(2,r)));
    }
    private static boolean attacked(Position p,Square s,Color by){for(var e:p.pieces().entrySet())if(e.getValue().color()!=by&&attacks(p,e.getKey(),e.getValue(),s))return true;return false;}
    private static boolean attacks(Position p,Square from,Piece pc,Square to) {
        int dx=to.file()-from.file(),dy=to.rank()-from.rank(),ax=Math.abs(dx),ay=Math.abs(dy);
        return switch(pc.type()) {
            case PAWN -> ay==1&&ax==1&&dy==(pc.color()==Color.WHITE?1:-1);
            case KNIGHT -> ax*ay==2;
            case KING -> Math.max(ax,ay)==1;
            case ROOK -> (dx==0||dy==0)&&clear(p,from,to);
            case BISHOP -> ax==ay&&ax>0&&clear(p,from,to);
            case QUEEN -> (dx==0||dy==0||ax==ay)&&!(dx==0&&dy==0)&&clear(p,from,to);
        };
    }
    private static boolean clear(Position p,Square a,Square b) {
        int dx=Integer.compare(b.file(),a.file()),dy=Integer.compare(b.rank(),a.rank());int f=a.file()+dx,r=a.rank()+dy;
        while(f!=b.file()||r!=b.rank()){if(p.pieceAt(new Square(f,r))!=null)return false;f+=dx;r+=dy;}return true;
    }
    private static Position applyUnchecked(Position p,Move m) {
        var map=new HashMap<>(p.pieces()); Piece pc=map.remove(m.from());
        if(pc.type()==PieceType.PAWN&&m.to().equals(p.enPassant())&&m.from().file()!=m.to().file()) map.remove(new Square(m.to().file(),m.to().rank()+(pc.color()==Color.WHITE?-1:1)));
        Piece placed=pc;
        if(pc.type()==PieceType.PAWN&&m.to().rank()==(pc.color()==Color.WHITE?7:0))
            placed=switch(m.promotion()==null?PieceType.QUEEN:m.promotion()){case QUEEN->new Queen(pc.color());case ROOK->new Rook(pc.color());case BISHOP->new Bishop(pc.color());case KNIGHT->new Knight(pc.color());default->throw new IllegalArgumentException();};
        map.remove(m.to()); map.put(m.to(),placed);
        if(pc.type()==PieceType.KING&&Math.abs(m.to().file()-m.from().file())==2) {
            int r=m.from().rank(); if(m.to().file()==6){map.put(new Square(5,r),map.remove(new Square(7,r)));}else{map.put(new Square(3,r),map.remove(new Square(0,r)));}
        }
        var rights=new HashSet<>(p.castlingRights());
        if(pc.type()==PieceType.KING){rights.remove(pc.color()==Color.WHITE?"K":"k");rights.remove(pc.color()==Color.WHITE?"Q":"q");}
        if(pc.type()==PieceType.ROOK){if(m.from().equals(new Square(0,0)))rights.remove("Q");if(m.from().equals(new Square(7,0)))rights.remove("K");if(m.from().equals(new Square(0,7)))rights.remove("q");if(m.from().equals(new Square(7,7)))rights.remove("k");}
        Square ep=pc.type()==PieceType.PAWN&&Math.abs(m.to().rank()-m.from().rank())==2?new Square(m.from().file(),(m.from().rank()+m.to().rank())/2):null;
        return new Position(map,p.turn().opposite(),rights,ep);
    }
}