package src;
import java.util.*;

public class Player {
    
    public final static String upper = "UPPER";
    public final static String lower = "lower";
    
    private List<Piece> capturedPieces;     // capturedPieces stores a player's current captured pieces
    private Set<Piece> myPieces;            // myPieces stores a player's current pieces on the board
    private String type;
    private int promotionRow;
    private List<String> allMoves;

    public Player(String type) {
        // upper player type = 1 and lower player type = 0
        this.type = type;
        if (type.equals(lower)) {
            promotionRow = 0;
        } else {
            promotionRow = 4;
        }
        capturedPieces = new ArrayList<>();
        myPieces = new HashSet<>();
        allMoves = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public int getPromotionRow() {
        return promotionRow;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }
    
    public Set<Piece> getMyPieces() {
        return myPieces;
    }
    
    public List<String> getAllMoves() {
        return allMoves;
    }

    public void addToAllMoves(String move) {
        allMoves.add(move);
    }

    /**
     * Get king's current position from myPieces
     * 
     * @return                  int[], current (y, x) coordinates of king
     */
    public int[] getKingPos() {
        King k = null;
        for (Piece p : myPieces) {
            String name = p.getName();
            if (name.equalsIgnoreCase("d")) {
                // found king
                k = (King) p;
            }
        }
        return k.getCurrPos();
    }

    public void addMyPieces(Piece p) {
        myPieces.add(p);
    }

    public void removeMyPieces(Piece p) {
        myPieces.remove(p);
    }

    /**
     * Get a specific piece from player's captured pieces based on String s
     * 
     * @param s                 String, String representation of piece
     * @return                  Piece, current (y, x) coordinates of king
     */
    public Piece getACapturedPiece(String s) {
        for (Piece p : capturedPieces) {
            if (p.getName().toLowerCase().equals(s)) {
                return p;
            }
        }
        return null;
    }

    public void addCapturedPieces(Piece p) {
        capturedPieces.add(p);
    }

    public void removeCapturedPieces(Piece p) {
        capturedPieces.remove(p);
    }

    public String capturedPiecesToString() {
        StringBuilder sb = new StringBuilder();
        for (Piece p : capturedPieces) {
            sb.append(" " + p.getName());
        }
        return sb.toString();
    }

}
