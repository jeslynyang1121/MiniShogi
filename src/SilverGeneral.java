package src;

import java.util.*;

public class SilverGeneral extends Piece {

    public SilverGeneral(String name, String owner, int currY, int currX) {
        super(name, owner, currY, currX);
    }

    /**
     * Generate all possible moves for silver general based on start(y, x)
     * Silver general can move 1 square in any direction except for left/right/back
     * Promoted silver general moves like a gold general so 1 square in any direction except for backwards diagonal
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public Set<String> getMoves(int startY, int startX) {
        Set<String> m = new HashSet<>();
        if (!getIsPromoted()) {
            // check owner
            int multiplier = -1;
            if (getOwner().equals(Player.upper)) {
                multiplier = 1;
            }
            int[][] addOns = {{1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};;

            for (int[] addOn : addOns) {
                int newPosY = startY + (multiplier * addOn[0]);
                int newPosX = startX + addOn[1];
                m.add("(" + newPosY + ", " + newPosX + ")");
            }
        } else {
            // promoted
            // check owner
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
        }
        return m;
    }
}
