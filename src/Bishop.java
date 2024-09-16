package src;

import java.util.HashSet;
import java.util.Set;

public class Bishop extends Piece {

    public Bishop(String name, String owner, int currY, int currX) {
        super(name, owner, currY, currX);
    }

    /**
     * Generate all possible moves for bishop based on start(y, x)
     * Bishop can any number of squares diagonally
     * Promoted bishop can also move like a king so 1 square in any direction 
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public Set<String> getMoves(int startY, int startX) {
        // do not need to check owner, movement is the same regardless
        Set<String> m = new HashSet<>();
        
        int[][] addOns = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
        for (int[] addOn : addOns) {
            for (int multiplier = 1; multiplier < BOARD_SIZE; multiplier++) {
                int newPosX = startY + (multiplier * addOn[0]);
                int newPosY = startX + (multiplier * addOn[1]);
                if (newPosX < 0 || newPosY < 0 || newPosX >= BOARD_SIZE || newPosY >= BOARD_SIZE) {
                    // any further in this direction is out of bounds
                    break;
                }
                m.add("(" + newPosX + ", " + newPosY + ")");
            }
        }

        if (getIsPromoted()) {
            // promoted 
            // do not need to check owner, movement is the same regardless
            int[][] addOnsKing = {{0, 1}, {0, -1}, {1, 0}, {1, 1}, {1, -1}, {-1, 0}, {-1, 1}, {-1, -1}};

            for (int[] addOnKing : addOnsKing) {
                m.add("(" + (startY + addOnKing[0]) + ", " + (startX + addOnKing[1]) + ")");
            }
        }

        return m;
    }

    /**
     * Check if bishop can move from start(y, x) to end(y, x)
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @param endY              int, piece's starting y coordinate
     * @param endX              int, piece's starting x coordinate
     * @param board             Board, current board
     * @return                  void
     */
    public boolean canReach(int startY, int startX, int endY, int endX, Board board) {
        int directionY = -1;
        int directionX = -1;
        if (startY < endY) {
            // moving upwards
            directionY = 1;
        }
        if (startX < endX) {
            // moving left
            directionX = 1;
        }
        int[] addOn = new int[] {directionY, directionX};
        for (int multiplier = 1; multiplier < BOARD_SIZE; multiplier++) {
            // bishop moves diagonally
            int newPosY = startY + (multiplier * addOn[0]);
            int newPosX = startX + (multiplier * addOn[1]);
            if (newPosY < 0 || newPosX < 0 || newPosY >= BOARD_SIZE || newPosX >= BOARD_SIZE) {
                // any further in this direction is out of bounds
                break;
            }
            if (newPosY == endY && newPosX == endX) {
                return true;
            }
            if (board.isOccupied(newPosY, newPosX)) {
                return false;
            }
        }
        if (getIsPromoted()) {
            // check end(y, x) is reachable for piece
            String endXY = "(" + endY + ", " + endX + ")";
            return moves.contains(endXY);
        } else {
            return false;
        }
    }
}
