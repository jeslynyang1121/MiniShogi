package src;

import java.util.List;

import src.Utils.InitialPosition;

/**
 * Class to represent Box Shogi board
 */
public class Board {

    Piece[][] board;
    final int BOARD_SIZE = 5;
    

    public Board() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];

    	// set default pieces for Upper player
        setPiece(new Rook("N", Player.upper, 0, 0), 0, 0);
        setPiece(new Bishop("G", Player.upper, 0, 1), 0, 1);
        setPiece(new SilverGeneral("R", Player.upper, 0, 2), 0, 2);
        setPiece(new GoldGeneral("S", Player.upper, 0, 3), 0, 3);
        setPiece(new King("D", Player.upper, 0, 4), 0, 4);
        setPiece(new Pawn("P", Player.upper, 1, 4), 1, 4);

        // set default pieces for Lower player
        setPiece(new Rook("n", Player.lower, 4, 4), 4, 4);
        setPiece(new GoldGeneral("s", Player.lower, 3, 1), 4, 1);
        setPiece(new SilverGeneral("r", Player.lower, 2, 0), 4, 2);
        setPiece(new Bishop("g", Player.lower, 4, 3), 4, 3);
        setPiece(new King("d", Player.lower, 4, 0), 4, 0);
        setPiece(new Pawn("p", Player.lower, 3, 0), 3, 0);

        
        
    }

    public Board(List<InitialPosition> initialPieces, Player upper, Player lower) {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];

        // set up board according to file
        for (InitialPosition ip : initialPieces) {
            // add piece to board
            int[] coordinates = convertPositionToInt(ip.position);
            Piece p = makePiece(ip.piece, coordinates);
            setPiece(p, coordinates[0], coordinates[1]);
            
            // add piece to player's myPieces
            if (p.getOwner().equals(Player.upper)) {
                upper.addMyPieces(p);
            } else {
                lower.addMyPieces(p);
            }
        }
    }

    public boolean isOccupied(int row, int col) {
        return board[row][col] != null;
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(Piece p, int row, int col) {
        board[row][col] = p;
    }

    /**
     * convert a string piece to its corresponding piece
     * 
     * @param setup             String, String representation of piece to be made
     * @param currPos           int[], piece's starting (y, x) coordinates
     * @return                  Piece
     */
    public Piece makePiece(String setup, int[] currPos) {

        Piece p;
        String piece = setup.substring(0, 1);
        char pieceAsChar = setup.charAt(0);
        if (pieceAsChar == '+') {
            // piece starts as promoted
            pieceAsChar = setup.charAt(1);
            piece = setup.substring(1);
        }
        // get owner of piece
        String owner = Player.lower;
        if (pieceAsChar >= 'A' && pieceAsChar <= 'Z') {
            owner = Player.upper;
        }
        pieceAsChar = Character.toLowerCase(pieceAsChar);

        // make new piece based on pieceAsChar
        switch (pieceAsChar) {
            case 'd':
                // king
                p = new King(piece, owner, currPos[0], currPos[1]);
                break;
            case 'n':
                // rook
                p = new Rook(piece, owner, currPos[0], currPos[1]);
                break;
            case 'g':
                // bishop
                p = new Bishop(piece, owner, currPos[0], currPos[1]);
                break;
            case 's':
                // gold general
                p = new GoldGeneral(piece, owner, currPos[0], currPos[1]);
                break;
            case 'r':
                // silver general
                p = new SilverGeneral(piece, owner, currPos[0], currPos[1]);
                break;
            case 'p':
                // pawn
                p = new Pawn(piece, owner, currPos[0], currPos[1]);
                break;
            default:
                // illegal piece made
                p = null;
        }

        if (p != null && setup.length() == 2) {
            // promote piece p
            char ifPromote = setup.charAt(0);
            if (ifPromote == '+') {
                if (!p.promote(currPos[0], currPos[1])) {
                    // illegal piece made
                    p = null;
                } 
            } else {
                // illegal piece made
                p = null;
            }
        }

        return p;

    }

    /**
     * Converts user input version of move to (y, x) coordinates
     * For example, converts a5 to {0, 0}
     * 
     * @param pos               String, user input version of move
     * @return                  int[], (y, x) coordinates version 
     */
    public int[] convertPositionToInt(String pos) {
        int[] convertedPos = new int[2];
        convertedPos[0] = 5 - (pos.charAt(1) - '0');
        convertedPos[1] = pos.charAt(0) - 'a';
        return convertedPos;
    }

    /**
     * Converts (y, x) coordinates to user input version of move
     * For example, converts {0, 0} to a5
     * 
     * @param pos               int[], (y, x) coordinates  
     * @return                  String, user input version of move
     */
    public String convertPositionToString(int[] pos) {
        String convertedPos = "";
        convertedPos += (char)(pos[1] + 'a');
        convertedPos += 5 - pos[0];
        return convertedPos;
    }
    
    /**
     * Makes a string representation of current board
     * 
     * @return                  String, current board state
     */
    public String toString() {
        String[][] pieces = new String[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece curr = (Piece) board[col][row];
                pieces[col][row] = this.isOccupied(col, row) ? board[col][row].toString() : "";
            }
        }
        return stringifyBoard(pieces);
    }

    public String stringifyBoard(String[][] board) {
        String str = "";

        for (int row = 0; row < board.length; row++) {

            str += Integer.toString(5 - row) + " |";
            for (int col = 0; col < board[row].length; col++) {
                str += stringifySquare(board[row][col]);
            }
            str += System.getProperty("line.separator");
        }

        str += "    a  b  c  d  e" + System.getProperty("line.separator");

        return str;
    }

    private String stringifySquare(String sq) {
        switch (sq.length()) {
            case 0:
                return "__|";
            case 1:
                return " " + sq + "|";
            case 2:
                return sq + "|";
        }

        throw new IllegalArgumentException("Board must be an array of strings like \"\", \"P\", or \"+P\"" + " not " + sq);
    }
}

