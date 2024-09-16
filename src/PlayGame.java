package src;

import java.util.ArrayList;
import java.util.List;

import src.Utils.InitialPosition;

/*
 * Description: initializes a Shogi board and players and controls the flow of the game and movement of the pieces.
 */

public class PlayGame {

    private int numMoves;
    // gameState = -1 = player is in check
    // gameState = 0 = game is not over
    // gameState = 1 = game is over from checkmate
    // gameState = 2 = game is over from illegal move
    // gameState = 3 = game is over from too many moves
    private int gameState;      
    private Board board;
    private Player upper;
    private Player lower;
    private Player currPlayer;

    public PlayGame() {
        // initialize game in interactive mode (default: default piece placement + lower player goes first)
        numMoves = 0;
        gameState = 0;
        board = new Board();

        upper = new Player(Player.upper);
        lower = new Player(Player.lower);
        currPlayer = lower;

        switchCurrPlayer();
        ifInFront(1, 4);
        switchCurrPlayer();
        ifInFront(3, 0);

        // initialize each player's myPieces
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < board.BOARD_SIZE; col++) {
                if (board.isOccupied(row, col)) {
                    Piece p = board.getPiece(row, col);
                    upper.addMyPieces(p);
                }
            }
        }
        for (int row = board.BOARD_SIZE - 2; row < board.BOARD_SIZE; row++) {
            for (int col = 0; col < board.BOARD_SIZE; col++) {
                if (board.isOccupied(row, col)) {
                    Piece p = board.getPiece(row, col);
                    lower.addMyPieces(p);
                }
            }
        }
    }

    public PlayGame(List<InitialPosition> initialPieces, List<String> upperCaptures, List<String> lowerCaptures) {
        // initialize game in file mode
        numMoves = 0;
        gameState = 0;

        // initialize players and board
        upper = new Player(Player.upper);
        lower = new Player(Player.lower);
        board = new Board(initialPieces, upper, lower);

        // initialize each player's capturedPieces
        if (upperCaptures.size() > 1) {
            for (String capture : upperCaptures) {
                // captured pieces have {0, 0} location
                Piece p = board.makePiece(capture, new int[]{0, 0});
                upper.addCapturedPieces(p);
            }
        }
        if (lowerCaptures.size() > 1) {
            for (String capture : lowerCaptures) {
                // captured pieces have {0, 0} location
                Piece p = board.makePiece(capture, new int[]{0, 0});
                lower.addCapturedPieces(p);
            }
        }

        currPlayer = lower;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int num) {
        this.gameState = num;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void addNumMoves() {
        this.numMoves += 1;
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public void switchCurrPlayer() {
        if (currPlayer.getType().equals(Player.lower)) {
            currPlayer = upper;
        } else {
            currPlayer = lower;
        }
    }

    /**
     * Given user's move, perform move/drop/promotion if legal and end game if illegal.
     * 
     * @param move            String like "move a1 a2" or "drop n e2"
     * @return
     */
    public void interpretMove(String move) {
        String[] moveArray = move.split(" ");
        
        if (moveArray[0].equals("move")) {

            int[] moveFrom = board.convertPositionToInt(moveArray[1]);
            int[] moveTo = board.convertPositionToInt(moveArray[2]);

            // check if move is legal
            if (ifLegalMove(moveFrom[0], moveFrom[1], moveTo[0], moveTo[1], true)) {
                Piece p = board.getPiece(moveFrom[0], moveFrom[1]);

                // check for promotions first
                if ((moveArray.length == 4 && moveArray[3].equals("promote")) || 
                        (p.getName().equalsIgnoreCase("p")) && moveTo[0] == currPlayer.getPromotionRow()) {
                    // promote piece if legal
                    if (ifLegalPromote(moveFrom[0], moveTo[0], moveTo[1])) {
                        if (!p.promote(moveTo[0], moveTo[1])) {
                            // unsuccessfull promotion = illegal move
                            setGameState(2);
                            return;
                        }
                    } else {
                        // illegal move
                        setGameState(2);
                        return;
                    }
                }
                // capture piece if neccassary then move piece
                capturePiece(moveTo[0], moveTo[1]);
                movePiece(p, moveFrom[0], moveFrom[1], moveTo[0], moveTo[1]);
            } else {
                // illegal move
                setGameState(2);
            }
            
        } else if (moveArray[0].equals("drop")) {
            String piece = moveArray[1];
            int[] dropTo = board.convertPositionToInt(moveArray[2]);

            // check if drop is legal
            if (ifLegalDrop(piece, dropTo[0], dropTo[1])) {
                // drop piece
                Piece p = currPlayer.getACapturedPiece(piece);
                dropPiece(p, dropTo[0], dropTo[1]);
            } else {
                // illegal move
                setGameState(2);
            }
        } else {
            // illegal move
            setGameState(2);
        }
    }

    /**
     * Checks if a move is legal based on the starting and ending coordinates and the piece's movements
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting y coordinate
     * @param endY              int, piece's ending y coordinate
     * @param endX              int, piece's ending x coordinate
     * @param isMoving          boolean, true = actually moving piece, false = simulating piece move
     * @return                  boolean, true = move is legal, false = move is illegal
     */
    public boolean ifLegalMove(int startY, int startX, int endY, int endX, boolean isMoving) {
        // check coordinates are in bounds
        if (startY < 0 || startX < 0 || endY < 0 || endX < 0 || startY >= board.BOARD_SIZE || 
                startX >= board.BOARD_SIZE || endY >= board.BOARD_SIZE || endX >= board.BOARD_SIZE) {
            return false;
        }

        // check there is a piece at (startX, startY) on board
        if (!board.isOccupied(startY, startX)) {
            return false;
        }

        // cannot move to same position
        if (startY == endY && startX == endX) {
            return false;
        }
        
        Piece p = board.getPiece(startY, startX);
        // cannot move other player's piece
        if (!p.getOwner().equals(currPlayer.getType())) {
            return false;
        }
        
        // check not to capture own piece
        if (board.isOccupied(endY, endX)) {
            Piece p2 = board.getPiece(endY, endX);
            if (p2.getOwner().equals(currPlayer.getType())) {
                return false;
            }
        }

        if (p.getName().equalsIgnoreCase("d")) {
            // does king's new position put itself in check?
            switchCurrPlayer();
            for(int row = 0; row < board.BOARD_SIZE; row++) {
                for (int col = 0; col < board.BOARD_SIZE; col++) {
                    if (board.isOccupied(row, col)) {
                        // temporarily remove king
                        board.setPiece(null, startY, startX);
                        Piece op = board.getPiece(row, col);
                        if (currPlayer.getMyPieces().contains(op) && op.canReach(row, col, endY, endX, board)) {
                            // king put itself in check
                            switchCurrPlayer();
                            board.setPiece(p, startY, startX); 
                            return false;
                        }
                        // put king back
                        board.setPiece(p, startY, startX); 
                    }
                    
                }
            }
            switchCurrPlayer();
        }

        // check if move is legal for piece
        if (isMoving) {
            return p.ifLegal(endY, endX);
        } 
        return true;
    }

    public void ifInFront(int startY, int startX) {
        int behindY = startY - 1;
        if (currPlayer.getType().equals(Player.lower)) {
            behindY = startY + 1;
        }

        if (board.isOccupied(behindY, startX)) {
            Piece p = board.getPiece(startY, startX);
            Piece behindP = board.getPiece(behindY, startX);
            if (behindP.getOwner().equals(currPlayer.getType())) {
                p.setMovesBehind(behindP.getMoves(startY, startX));
            }
        }

    }

    /**
     * Checks if a promotion is legal based on if the starting/ending y coordinate is in the promotion zone
     * 
     * @param startY            int, piece's starting y coordinate
     * @param endY              int, piece's ending y coordinate
     * @param endX              int, piece's ending x coordinate
     * @return                  boolean, true = promotion is legal, false = move is illegal
     */
    public boolean ifLegalPromote(int startY, int endY, int endX) {
        // check startY/endY is in promotion row + coordinates are in bounds
        if ((startY != currPlayer.getPromotionRow() && endY != currPlayer.getPromotionRow()) || endX < 0 || endX >= board.BOARD_SIZE) {
            return false;
        }

        // can try to promote piece
        return true;
    }

    /**
     * Checks if a drop is legal based on starting (y, x) and piece
     * 
     * @param piece             String like n or g, String representation of piece to be dropped
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  boolean, true = drop is legal, false = drop is illegal
     */
    public boolean ifLegalDrop(String piece, int startY, int startX) {
        // check coordinates are in bounds
        if (startY < 0 || startX < 0 || startY >= board.BOARD_SIZE || startX >= board.BOARD_SIZE) {
            return false;
        }

        // check there is not a piece at (startX, startY) on board
        if (board.isOccupied(startY, startX)) {
            return false;
        }

        // check if piece p is in player's captured pieces list
        if (currPlayer.getACapturedPiece(piece) == null) {
            return false;
        }
        
        if (piece.equalsIgnoreCase("p")) {
            // check for specific drop rules for pawns

            // cannot drop pawn in promotion zone
            if (startY == currPlayer.getPromotionRow()) {
                return false;
            }

            // cannot drop pawn where immediate checkmate is possible (king is in front of pawn)
            int upY = startY;
            if (currPlayer.getType().equals(Player.lower)) {
                upY--;
            } else {
                upY++;
            }
            if (upY >= 0 && upY < board.BOARD_SIZE) {
                if (board.isOccupied(upY, startX)) {
                    Piece p2 = board.getPiece(upY, startX);
                    if (!(currPlayer.getType().equals(p2.getOwner())) && p2.getName().equalsIgnoreCase("d")) {
                        return false;
                    }
                }
            }

            // cannot drop pawn in same col as another unpromoted pawn
            for (int row = 0; row < board.BOARD_SIZE; row++) {
                if (board.isOccupied(row, startX)) {
                    Piece p2 = board.getPiece(row, startX);
                    if (currPlayer.getType().equals(p2.getOwner()) && p2.getName().equalsIgnoreCase("p")) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * moves piece p from start(y, x) to end(y, x)
     * 
     * @param p                 Piece, piece that currPlayer moved
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @param endY              int, piece's ending y coordinate
     * @param endX              int, piece's ending x coordinate
     * @return                  void
     */
    public void movePiece(Piece p, int startY, int startX, int endY, int endX) {
        // move piece
        board.setPiece(null, startY, startX);
        board.setPiece(p, endY, endX); 
        ifInFront(endY, endX);
        
        if (p.getName().equalsIgnoreCase("d")) {
            // update king's curr position
            King k = (King) p;
            k.setCurrPos(endY, endX);
        } else {
            // check if piece puts opponent's king in check
            ifCheck(p, endY, endX);
        }
    }

    
    /**
     * captures piece at end(y, x) if necessary
     * 
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @param endY              int, piece's ending y coordinate
     * @param endX              int, piece's ending x coordinate
     * @return                  void
     */
    public void capturePiece(int endY, int endX) {
        // capture piece if necessary
        if (board.isOccupied(endY, endX)) {
            Piece capturedP = board.getPiece(endY, endX);
            // capture piece
            capturedP.capture();
            currPlayer.addCapturedPieces(capturedP);
            
            // remove piece from opponent's myPieces
            switchCurrPlayer();
            currPlayer.removeMyPieces(capturedP);
            switchCurrPlayer();
        }
    }

    /**
     * drops piece p at start(y, x)
     * 
     * @param p                 Piece, piece that currPlayer dropped
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public void dropPiece(Piece p, int startY, int startX) {
        // drop piece
        board.setPiece(p, startY, startX);
        currPlayer.removeCapturedPieces(p);
        currPlayer.addMyPieces(p);
        
        // initialize piece's moves
        p.setMoves(p.getMoves(startY, startX));
        ifInFront(startY, startX);

        if (p.getName().equalsIgnoreCase("d")) {
            // update king's curr position
            King k = (King) p;
            k.setCurrPos(startY, startX);
        } else {
            // does piece put opponent's king in check
            ifCheck(p, startY, startX);
        }
    }
    
    /**
     * checks if piece p puts the opponent king's in check
     * 
     * @param p                 Piece, piece that currPlayer moved
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public void ifCheck(Piece p, int startX, int startY) {
        switchCurrPlayer();
        int[] kingXY = currPlayer.getKingPos();

        if (p.canReach(startX, startY, kingXY[0], kingXY[1], board)) {
            // piece p can reach king + opponent is in check
            setGameState(-1);
            findAllMoves(p, startX, startY);
        }

        switchCurrPlayer();
    }
    
    /**
     * finds all possible moves to get currPlayer's king out of check
     * 
     * @param op                Piece, opponent's piece that puts king in check
     * @param startY            int, piece's starting y coordinate
     * @param startX            int, piece's starting x coordinate
     * @return                  void
     */
    public void findAllMoves(Piece op, int startX, int startY) {
        // stores all squares between op and king (if any)
        List<int[]> betweenSquares = new ArrayList<>();
        
        // get all moves for king
        King k = null;
        for (Piece p : currPlayer.getMyPieces()) {
            if (p.getName().equalsIgnoreCase("d")) {
                k = (King) p;
                break;
            }
        }
        int[] kingXY = k.getCurrPos();
        int endX = kingXY[0];
        int endY = kingXY[1];
        for (String move : k.moves) {
            // process king's move
            int row = move.charAt(1) - '0';
            int col = 1;
            if (move.charAt(4) == '-') {
                col *= -1;
            }
            col *= move.charAt(move.length() - 2) - '0';

            // add move if legal
            if (ifLegalMove(endX, endY, row, col, false)) {
                String finalMove = "move " + board.convertPositionToString(new int[] {endX, endY})
                        + " " + board.convertPositionToString(new int[] {row, col});
                currPlayer.addToAllMoves(finalMove);
            }
        }
        
        // find between squares
        if (op.getName().equalsIgnoreCase("g") || op.getName().equalsIgnoreCase("g+")) {
            // op is a bishop
            int directionX = -1;
            int directionY = -1;
            if (startX < endX) {
                // moving upwards
                directionX = 1;
            }
            if (startY < endY) {
                // moving left
                directionY = 1;
            }
            int[] addOn = new int[] {directionX, directionY};
            for (int multiplier = 1; multiplier < board.BOARD_SIZE; multiplier++) {
                // bishop moves diagonally
                int newPosX = startX + (multiplier * addOn[0]);
                int newPosY = startY + (multiplier * addOn[1]);
                if (newPosX < 0 || newPosY < 0 || newPosX >= board.BOARD_SIZE || newPosY >= board.BOARD_SIZE) {
                    // any further in this direction is out of bounds
                    break;
                }
                if (newPosX == endX && newPosY == endY) {
                    break;
                }
                if (!board.isOccupied(newPosX, newPosY)) {
                    // found betwween square
                    betweenSquares.add(new int[] {newPosX, newPosY});
                }
            }
        } else if (op.getName().equalsIgnoreCase("n") || op.getName().equalsIgnoreCase("n+")) {
            // op is a rook
            int rookX = startX;
            int rookY = startY;
            if (rookX == endX) {
                // moving right
                if (endY < rookY) {
                    int temp = rookY;
                    rookY = endY;
                    endY = temp;
                }

                // check for obstructions on path between startPos and endPos
                for (int col = rookY + 1; col < endY; col++) {
                    if (!board.isOccupied(rookX, col)) {
                        // found between square
                        betweenSquares.add(new int[] {endX, col});
                    }
                }
            } else if (rookY == endY) {
                // moving down
                if (endX < rookX) {
                    int temp = rookX;
                    rookX = endX;
                    endX = temp;
                }

                // check for obstructions on path between startPos and endPos
                for (int row = rookX + 1; row < endX; row++) {
                    if (!board.isOccupied(row, rookY)) {
                        // found between square
                        betweenSquares.add(new int[] {row, endY});
                    }
                }
            } 
        }
        // count op's current location as between square for now
        int[] startXY = new int[] {startX, startY};
        betweenSquares.add(startXY);

        for (int[] endXY : betweenSquares) {
            // get all moves for currPlayer's other pieces
            for (int row = 0; row < board.BOARD_SIZE; row++) {
                for (int col = 0; col < board.BOARD_SIZE; col++) {
                    if (board.isOccupied(row, col)) {
                        // check if piece can block/capture to avoid checkmate
                        Piece p = board.getPiece(row, col);
                        if (currPlayer.getMyPieces().contains(p) && !p.getName().equalsIgnoreCase("d")
                                && p.canReach(row, col, endXY[0], endXY[1], board)) {
                            // piece can reach square so add move as option
                            String move = "move " + board.convertPositionToString(new int[]{row, col})
                                    + " " + board.convertPositionToString(new int[]{endXY[0], endXY[1]});
                            currPlayer.addToAllMoves(move);

                            // add promotion as option if possible
                            if (row == currPlayer.getPromotionRow() && !p.getName().equalsIgnoreCase("s")) {
                                String promotion = move + " promote";
                                currPlayer.addToAllMoves(promotion);
                            }
                        }
                    }
                }
            }
        }
        betweenSquares.remove(startXY);
        
        for (int[] endXY : betweenSquares) {
            // add drop as option if possible
            for (Piece p : currPlayer.getCapturedPieces()) {
                String move = "drop " + p.getName().toLowerCase() + " " + board.convertPositionToString(new int[] {endXY[0], endXY[1]});
                currPlayer.addToAllMoves(move);
            }
        }
    }

    /**
     * toString of current game state, includes the current board and list of captured pieces of both players
     * 
     * @return                  void
     */
    public String gameStateToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(board.toString() + "\n");
        sb.append("Captures " + Player.upper + ":" + upper.capturedPiecesToString() + "\n");
        sb.append("Captures " + Player.lower + ":" + lower.capturedPiecesToString() + "\n");
        return sb.toString();
    }
}
