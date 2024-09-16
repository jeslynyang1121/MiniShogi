package src;
import java.util.*;

public class GoldGeneral extends Piece {

    public GoldGeneral(String name, String owner, int currY, int currX) {
        super(name, owner, currY, currX);
    }

    /**
     * Generate all possible moves for gold general based on start(y, x)
     * Gold general can move 1 square in any direction except for backwards diagonal
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public Set<String> getMoves(int startY, int startX) {
        // check owner
        Set<String> m = new HashSet<>();
        int multiplier = -1;
        if (getOwner().equals(Player.upper)) {
            multiplier = 1;
        }

        int[][] addOns = {{0, 1}, {0, -1}, {1, 0}, {1, 1}, {1, -1}, {-1, 0}};
        for (int[] addOn : addOns) {
            int newPosY = startY + (multiplier * addOn[0]);
            int newPosX = startX + addOn[1];
            m.add("(" + newPosY + ", " + newPosX + ")");
        }
        return m;
    }

    public boolean promote(int startY, int startX) {
        // gold general cannot promote
        return false;
    }
}
