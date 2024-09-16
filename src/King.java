package src;
import java.util.*;

public class King extends Piece {

    int[] currPos;      // keep track of king's current location

    public King(String name, String owner, int currY, int currX) {
        super(name, owner, currY, currX);
        currPos = new int[] {currY, currX};
    }

    /**
     * Generate all possible moves for king based on start(y, x)
     * King can move 1 square in any direction
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public Set<String> getMoves(int startY, int startX) {
        // do not need to check owner, movement is the same regardless
        Set<String> m = new HashSet<>();
        int[][] addOns = {{0, 1}, {0, -1}, {1, 0}, {1, 1}, {1, -1}, {-1, 0}, {-1, 1}, {-1, -1}};
        
        for (int[] addOn : addOns) {
            m.add("(" + (startY + addOn[0]) + ", " + (startX + addOn[1]) + ")");
        }
        return m;
    }
    
    public int[] getCurrPos() {
        return currPos;
    }
    
    public void setCurrPos(int newY, int newX) {
        currPos = new int[] {newY, newX};
    }

    public boolean promote(int startY, int startX) {
        // king cannot promote
        return false;
    }
}
