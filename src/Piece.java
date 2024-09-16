package src;
import java.util.*;

/*
 * Description: 
 */

public abstract class Piece {
    private String name;
    private String owner;
    private boolean isPromoted;
    final int BOARD_SIZE = 5;
    public Set<String> moves;
    public Set<String> movesBehind;

    public Piece(String name, String owner, int currX, int currY) {
        this.name = name;
        this.owner = owner;
        
        // initialize piece's moves at starting x, y position
        moves = new HashSet<>();
        movesBehind = new HashSet<>();
        setMoves(getMoves(currX, currY));
    }

    public abstract Set<String> getMoves(int startX, int startY);

    public void setMoves(Set<String> moves) {
        this.moves = moves;
    }

    public void setMovesBehind(Set<String> movesBehind) {
        this.movesBehind = movesBehind;
    }

    public Set<String> getMovesBehind() {
        return movesBehind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean getIsPromoted() {
        return isPromoted;
    }

    public void setIsPromoted(boolean isPromoted) {
        this.isPromoted = isPromoted; 
    }

    public boolean canReach(int startX, int startY, int endX, int endY, Board board) {
        // check if piece can each end(y, x)
        String endXY = "(" + endX + ", " + endY + ")";
        return (moves.contains(endXY) || movesBehind.contains(endXY));
    }

    /**
     * Checks if end(y, x) is within piece's range of movement based on current location
     * 
     * @param endY              int, piece's ending y coordinate
     * @param endX              int, piece's ending x coordinate
     * @return                  boolean, true = move is legal, false = move is illegal
     */
    public boolean ifLegal(int endX, int endY) {
        String endXY = "(" + endX + ", " + endY + ")";
        boolean ifLegal = moves.contains(endXY) || movesBehind.contains(endXY);
        
        // get piece's moves at new location if move is legal
        if (ifLegal) {
            moves.clear();
            setMoves(getMoves(endX, endY));
            movesBehind.clear();
        }
        return ifLegal;
    }

    public void capture() {
        // switch ownership of piece
        if (getOwner().equals(Player.upper)) {
            setOwner(Player.lower);
            setName(getName().toLowerCase());
        } else {
            setOwner(Player.upper);
            setName(getName().toUpperCase());
        }

        // reset piece if promoted
        if (getIsPromoted()) {
            setName(getName().substring(1));
            setIsPromoted(false);
        }
    }

    public boolean promote(int startX, int startY) {
        if (getIsPromoted()) {
            // cannot double promote
            return false;
        }
        
        // promote + rename piece
        setName("+" + getName());
        setIsPromoted(true);
        
        // update piece's moves at curr location
        moves.clear();
        setMoves(getMoves(startX, startY));
        return true;
    }
    
    public String toString() {
        return name;
    }
}
