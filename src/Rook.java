package src;
import java.util.*;

public class Rook extends Piece {

    public Rook(String name, String owner, int currX, int currY) {
        super(name, owner, currX, currY);
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
    public Set<String> getMoves(int startX, int startY) {
        Set<String> m = new HashSet<>();
        // do not need to check owner, movement is the same regardless
        for (int addOn = 0; addOn < BOARD_SIZE; addOn++) {
            // rook moves up and down
            m.add("(" + addOn + ", " + startY + ")");
            // rook moves left and right
            m.add("(" + startX + ", " + addOn + ")");
        }

        if (getIsPromoted()) {
            // promoted rook can also move like king
            // do not need to check owner, movement is the same regardless
            int[][] addOnsKing = {{0, 1}, {0, -1}, {1, 0}, {1, 1}, {1, -1}, {-1, 0}, {-1, 1}, {-1, -1}};

            for (int[] addOnKing : addOnsKing) {
                // king can move 1 square in any direction
                m.add("(" + (startX + addOnKing[0]) + ", " + (startY + addOnKing[1]) + ")");
            }
        }
        return m;
    }

    /**
     * Check if rook can move from start(y, x) to end(y, x)
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @param endY              int, piece's starting y coordinate
     * @param endX              int, piece's starting x coordinate
     * @param board             Board, current board
     * @return                  void
     */
    public boolean canReach(int startY, int startX, int endY, int endX, Board board) {
        if (startY == endY) {
            // moving right
            if (endX < startX) {
                int temp = startX;
                startX = endX;
                endX = temp;
            }
            
            // check for obstructions on path between startPos and endPos
            for (int col = startX + 1; col < endX; col++) {
                if (board.isOccupied(startY, col)) {
                    return false;
                }
            }
            return true;
        } else if (startX == endX) {
            // moving down
            if (endY < startY) {
                int temp = startY;
                startY = endY;
                endY = temp;
            }

            // check for obstructions on path between start(y, x) and end(y, x)
            for (int row = startY + 1; row < endY; row++) {
                if (board.isOccupied(row, startX)) {
                    return false;
                }
            }
            return true;
        } else if (!getIsPromoted()) {
            // cannot reach
            return false;
        }
        
        if (getIsPromoted()) {
            // check end location is reachable for piece
            String endXY = "(" + endY + ", " + endX + ")";
            return moves.contains(endXY);
        }
        return true;
    }
}
