package src;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        args = new String[]{"-i"};
        if (args.length == 1 && args[0].equals("-i")) {
            // play shogi in interactive move
            PlayGame g = new PlayGame();
            while (g.getGameState() <= 0) {
                // print current game state 
                System.out.print(g.gameStateToString());
                System.out.print(g.getCurrPlayer().getType() + ">");

                // get user's next move + move Piece
                Scanner scanner = new Scanner(System.in);
                String move = scanner.nextLine();

                // check player if player got out of check or not
                if (g.getGameState() == -1 ) {
                    if (g.getCurrPlayer().getAllMoves().contains(move)) {
                        // player got out of check
                        g.getCurrPlayer().getAllMoves().clear();
                        g.setGameState(0);
                    } else {
                        // player did not get out of check so illegal move
                        g.setGameState(2);
                    }
                }
                
                g.interpretMove(move);

                // numMoves goes up +1 and switch to other player's turn
                g.addNumMoves();
                g.switchCurrPlayer();
                
                // player is in check or checkmate
                if (g.getGameState() == -1) {
                    if (!g.getCurrPlayer().getAllMoves().isEmpty()) {
                        // player is in check so print all possible moves
                        System.out.println(g.getCurrPlayer().getType() + " player is in check!");
                        System.out.println("Available moves:");
                        Collections.sort(g.getCurrPlayer().getAllMoves());
                        for (String possibleMove : g.getCurrPlayer().getAllMoves()) {
                            System.out.println(possibleMove);
                        }
                    } else {
                        // player in checkmate
                        g.setGameState(1);
                    }
                }
                
                // game reached num of moves limit so it ends in a tie
                if (g.getGameState() == 0 && g.getNumMoves() == 400) {
                    g.setGameState(3);
                    break;
                }
            }
            if (g.getGameState() == 1) {
                g.switchCurrPlayer();
                System.out.println(g.getCurrPlayer().getType() + " player wins.  " + "Checkmate.");
            } else if (g.getGameState() == 2) {
                System.out.print(g.getCurrPlayer().getType() + " player wins. ");
                System.out.println("Illegal move.");
            } else if (g.getGameState() == 3) {
                System.out.println("Tie game.  Too many moves.");
            }
        } else if (args.length == 2 && args[0].equals("-f")) {
            try {
                // play game for file mode
                Utils.TestCase test = Utils.parseTestCase(args[1]);
                PlayGame g = new PlayGame(test.initialPieces, test.upperCaptures, test.lowerCaptures);
                String lastMove = "";
                for (String move : test.moves) {
                    lastMove = move;
                    
                    // check player if player got out of check or not
                    if (g.getGameState() == -1 ) {
                        if (g.getCurrPlayer().getAllMoves().contains(move)) {
                            // player got out of check
                            g.getCurrPlayer().getAllMoves().clear();
                            g.setGameState(0);
                        } else {
                            // player did not get out of check so illegal move
                            g.setGameState(2);
                        }
                    }
                    
                    // get user's next move + move Piece
                    g.interpretMove(move);

                    // numMoves goes up +1 and switch to other player's turn
                    g.addNumMoves();
                    g.switchCurrPlayer();

                    // player is in check or checkmate
                    if (g.getGameState() == -1) {
                        if (g.getCurrPlayer().getAllMoves().isEmpty()) {
                            // player in checkmate
                            g.setGameState(1);
                        }
                    }
                    
                    // game ties
                    if (g.getGameState() == 0 && g.getNumMoves() == 400) {
                        g.setGameState(3);
                    }

                    if (g.getGameState() > 0) {
                        break;
                    }
                }
                g.switchCurrPlayer();

                // print game's current state after last file move
                System.out.println(g.getCurrPlayer().getType() + " player action: " + lastMove);
                System.out.print(g.gameStateToString());
                System.out.println();

                g.switchCurrPlayer();
                if (g.getGameState() == -1) {
                    // player is in check so print all possible moves
                    System.out.println(g.getCurrPlayer().getType() + " player is in check!");
                    System.out.println("Available moves:");
                    Collections.sort(g.getCurrPlayer().getAllMoves());
                    for (String possibleMove : g.getCurrPlayer().getAllMoves()) {
                        System.out.println(possibleMove);
                    }

                    // game is not over yet so go into interactive mode
                    System.out.println(g.getCurrPlayer().getType() + ">");
                } else if (g.getGameState() == 0) {
                    // game is not over yet so go into interactive mode
                    System.out.println(g.getCurrPlayer().getType() + ">");
                } else if (g.getGameState() == 1) {
                    // game ended over checkmate
                    g.switchCurrPlayer();
                    System.out.println(g.getCurrPlayer().getType() + " player wins.  " + "Checkmate.");
                } else if (g.getGameState() == 2) {
                    // gamed ended over illegal move
                    System.out.print(g.getCurrPlayer().getType() + " player wins. ");
                    System.out.println(" Illegal move.");
                } else if (g.getGameState() == 3) {
                    // gamed ended in a tie
                    System.out.println("Tie game.  Too many moves.");
                }

                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else {
            System.out.println("Please specify input by -i or -f [file name]");
        }
    }


}
