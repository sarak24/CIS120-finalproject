package org.cis120.battleship;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleShip {

    private Board board;
    private Player p1;
    private Player p2;
    private boolean p1Turn;

    // for reading in data from file: key = id, value = length
    private TreeMap<Integer, Integer> gamePieces;
    // list of all ships on the board
    private LinkedList<Ship> allShips;
    // list of ship sizes -> index should align with game pieces
    private LinkedList<Integer> shipSizes;
    // list containing the play-by-play descriptions (String) to be written to
    // output file
    private LinkedList<String> gameReport;
    private static int round = 2;

    private static final String PATH_TO_GAME_SETUP = "files/gameSetup";
    private static final String PATH_TO_GAME_REPORT = "files/game_report";

    // constructor (includes reading in input file)
    public BattleShip(String file) {
        FileLineIterator f = new FileLineIterator(file);
        // all lists start out empty
        this.gamePieces = new TreeMap<Integer, Integer>();
        this.allShips = new LinkedList<Ship>();
        this.gameReport = new LinkedList<String>();
        this.shipSizes = new LinkedList<Integer>();
        // player 1 starts
        this.p1Turn = true;
        int dim = 0;
        if (f.hasNext()) {
            try {
                dim = Integer.parseInt(f.next());
            } catch (NumberFormatException e) {
                // if dimension given is not an integer
                System.out.println("First line should be an intger between 1 - 10");
                throw new IllegalStateException("Invalid dimension");
            }

        } else {
            // if file is empty
            System.out.println("File doesn't have any lines to read.");
            f.hasNext();
            throw new IllegalStateException("Empty file");
        }
        // throw exception if dim is not between 1 and 10 inclusive
        if (dim < 1 || dim > 10) {
            System.out.println("An intger between 1 - 10 was not provided");
            f.hasNext();
            throw new IllegalStateException("Invalid dimension");
        }

        // unique id for Ship constructor
        int uniqueID = 0;
        // any more lines in the file should be specifying a new ship's length
        while (f.hasNext()) {
            int length = 0;
            try {
                String next = f.next();
                length = Integer.parseInt(next);
                if (length > 0 && length <= 4) {
                    // add each new ship to list of game pieces
                    gamePieces.put(uniqueID, length);
                    shipSizes.add(length);
                } else {
                    // if ship length is not between 1 and 4 inclusive
                    System.out.println("Ships must be between 1 and 4 inclusive in length");
                    f.hasNext();
                    throw new IllegalStateException("Invalid length");
                }

            } catch (NumberFormatException e) {
                // if ship length is not an integer
                System.out.println("An integer between 1 and 4 inclusive was not provided");
                f.hasNext();
                throw new IllegalStateException("Invalid length");
            }

            uniqueID++;
        }
        // instantiate a board and set up the coordinates
        this.board = new Board(dim);
        board.setUpCoords();
        // Player p1 and Player p2
        this.p1 = new Player("p1");
        this.p2 = new Player("p2");
    }

    // returns true if the given position (String) is on the board
    public static boolean posIsValid(String str, int maxHeight, int maxWidth) {
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(str);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
            if (row >= maxHeight || col >= maxWidth) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    // returns true if the given orientation (char) is 'h' or 'v'
    public static boolean orientationIsValid(char c, int maxHeight, int maxWidth) {
        return c == 'v' || c == 'h';
    }

    // for each game piece, creates two new Ship objects, one for each player
    public void addGamePiecesToAllShips() {
        for (Map.Entry<Integer, Integer> kv : gamePieces.entrySet()) {
            int id = kv.getKey();
            int length = kv.getValue();
            Ship s1 = p1.makeShip(id, length);
            Ship s2 = p2.makeShip(id, length);
            // add Ships to this.allShips
            allShips.add(s1);
            allShips.add(s2);

        }
    }

    // returns true if p1 or p2 has lost
    public boolean isGameOver() {
        return p1.hasLost(board) || p2.hasLost(board);
    }

    // returns the Player who has won (throws exception if no player has won yet)
    public Player whoIsWinner() {
        if (!this.isGameOver()) {
            throw new IllegalStateException("Game isn't over");
        }
        if (p1.hasLost(this.board)) {
            return p2;
        } else {
            return p1;
        }
    }

    // method for adding a line to the game report which will be saved at the end
    public void addToGameReport(String str) {
        gameReport.add(str);
    }

    // for testing: print gameReport to console
    public void printGameReport() {
        System.out.println("Game report: ");
        for (String str : this.gameReport) {
            System.out.println(str);
        }
    }

    // called after the game has been won --> writes all strings in gameReport to
    // output file
    public void gameReportToFile(List<String> stringsToWrite, boolean append) {
        File file = Paths.get(PATH_TO_GAME_REPORT).toFile();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            for (String str : gameReport) {
                bw.write(str);
                bw.write("\n");
            }
        } catch (IOException e) {
            // hide any IOExceptions thrown as a result of creating a buffered writer
            System.out.println("IO Exception caught");
            return;
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    // hide any IOException thrown as a result of closing the buffered writer
                    System.out.println("IO Exception caught");
                }
            }
        }
    }

    // for testing: prints a narration of the game to the console
    public void printGameState() {
        LinkedList<Ship> p1Ships = new LinkedList<Ship>();
        LinkedList<Ship> p2Ships = new LinkedList<Ship>();
        if (isGameOver()) {
            System.out.println("<<<<<<<< GAME OVER >>>>>>>>");
            System.out.println(
                    "      " +
                            whoIsWinner().getName().toUpperCase() + " IS THE WINNER."
            );
        }
        for (Ship s : allShips) {
            if (s.getOwner().getName().equals(p1.getName())) {
                p1Ships.add(s);
            } else {
                p2Ships.add(s);
            }
        }
        System.out.println("P1 ships are hit at coordinates...");
        for (Ship s : p1Ships) {
            boolean next = false;
            System.out.print("Ship" + s.getId() + ":");
            for (int i = 0; i < board.getBoard().length; i++) {
                for (int j = 0; j < board.getBoard().length; j++) {
                    LinkedList<Ship> ships = board.getCoordCopy(i, j).getShipsCopy();
                    if (ships.contains(s)) {
                        int index = ships.indexOf(s);
                        for (String str : ships.get(index).getSunkCoords()) {
                            System.out.print(str + " ");
                        }
                        System.out.println("");
                        next = true;
                    }
                    if (next) {
                        break;
                    }
                }
                if (next) {
                    break;
                }
            }
        }
        System.out.println("P2 ships are hit at coordinates...");
        for (Ship s : p2Ships) {
            boolean next = false;
            System.out.print("Ship" + s.getId() + ":");
            for (int i = 0; i < board.getBoard().length; i++) {
                for (int j = 0; j < board.getBoard().length; j++) {
                    LinkedList<Ship> ships = board.getCoordCopy(i, j).getShipsCopy();
                    if (ships.contains(s)) {
                        int index = ships.indexOf(s);
                        for (String str : ships.get(index).getSunkCoords()) {
                            System.out.print(str + " ");
                        }
                        System.out.println("");
                        next = true;
                    }
                    if (next) {
                        break;
                    }
                }
                if (next) {
                    break;
                }
            }
        }
        System.out.println("");
        System.out.println("************");
        System.out.println("");
    }

    // returns true if player owns any ships in the Coord
    public boolean getPlayerShips(Player p, int row, int col) {
        Coord cell = this.board.getBoard()[row][col];
        if (!cell.noShips()) {
            for (Ship s : cell.getShipsCopy()) {
                Player owner = s.getOwner();
                // compare p's name to the name of the owner of Ship s
                if (owner.getName().equals(p.getName())) {
                    // if match, return true
                    return true;
                }
            }
        }
        return false;
    }

    // method runs every time a player takes a turn
    public void play(Player p, String pos) {
        if (isGameOver()) {
            System.out.println("Game Over");
            return;
        }
        addToGameReport("Turn " + round / 2 + ":");
        if (p1Turn) {
            if (p1.fire(pos, board)) {
                addToGameReport("P1 hit opponent at " + pos + "\n");
            } else {
                addToGameReport("P1 unlucky guess at " + pos + "\n");
            }
            p1Turn = !p1Turn;
        } else {
            if (p2.fire(pos, board)) {
                addToGameReport("P2 hit opponent at " + pos + "\n");
            } else {
                addToGameReport("P2 unlucky guess at " + pos + "\n");
            }
            p1Turn = !p1Turn;
        }
        round++;
    }

    // getters and setters
    public LinkedList<Ship> getAllShipsCopy() {
        LinkedList<Ship> returnList = new LinkedList<Ship>();
        for (Ship s : this.allShips) {
            returnList.add(Ship.copyShip(s));
        }
        return returnList;
    }

    public LinkedList<Integer> getShipSizes() {
        LinkedList<Integer> returnList = new LinkedList<Integer>();
        for (int i : this.shipSizes) {
            returnList.add(i);
        }
        return returnList;
    }

    /*
     * public LinkedList<Ship> getAllShipsNotCopy() {
     * return this.allShips;
     * }
     */

    public int getGamePiecesSize() {
        return gamePieces.size();
    }

    public int getBoardSize() {
        return board.getBoard().length;
    }

    public Player getP1() {
        return this.p1;
    }

    public String getP1Name() {
        return this.p1.getName();
    }

    public Player getP2() {
        return this.p2;
    }

    public String getP2Name() {
        return this.p2.getName();
    }

    public boolean getP1Turn() {
        return this.p1Turn;
    }

    /*
     * public Board getBoard() {
     * return this.board;
     * }
     */

    // encapsulated
    public Board getBoardCopy() {
        return board.copyBoard();
    }

    // replace board with board b
    public void replaceBoard(Board b) {
        this.board = b;
    }

    // replaces ship at index i
    public void replaceShip(int index, Ship s) {
        allShips.remove(index);
        allShips.add(index, s);
    }

    public LinkedList<String> getGameReport() {
        LinkedList<String> returnList = new LinkedList<String>();
        for (String s : gameReport) {
            returnList.add(s);
        }
        return returnList;
    }

    // simulate game play using model
    public static void main(String[] args) {
        BattleShip b = new BattleShip(PATH_TO_GAME_SETUP);
        b.addGamePiecesToAllShips();
        /*
         * for (Map.Entry<Integer, Integer> kv : b.gamePieces.entrySet()) {
         * int id = kv.getKey();
         * int length = kv.getValue();
         * Ship s1 = b.p1.makeShip(id, length);
         * Ship s2 = b.p2.makeShip(id, length);
         * 
         * b.allShips.add(s1);
         * b.allShips.add(s2);
         * 
         * }
         */

        // each player has three ships: <1,2>, <2,2>, <3,3>
        b.allShips.get(0).setStartAndOrientation("A1", 'v', b.getBoardSize()); // player1 ship1
        b.allShips.get(1).setStartAndOrientation("A2", 'h', b.getBoardSize()); // player2 ship1
        b.allShips.get(2).setStartAndOrientation("E1", 'h', b.getBoardSize()); // player1 ship2
        b.allShips.get(3).setStartAndOrientation("C4", 'v', b.getBoardSize()); // player2 ship2
        b.allShips.get(4).setStartAndOrientation("C3", 'h', b.getBoardSize()); // player1 ship3
        b.allShips.get(5).setStartAndOrientation("C2", 'v', b.getBoardSize()); // player2 ship3

        /*
         * Ship ship1 = new Ship(1, b.p1, 2, 'v', "A1"); // A1-B1
         * b.board.addShip(ship1);
         * Ship ship2 = new Ship(2, b.p1, 3, 'v', "A3"); // A3-C3
         * b.board.addShip(ship2);
         * Ship ship3 = new Ship(3, b.p2, 2, 'h', "A4"); // A4-A5
         * b.board.addShip(ship3);
         * Ship ship4 = new Ship(3, b.p2, 4, 'h', "D2"); // D2-D5
         * b.board.addShip(ship4);
         * Ship ship5 = new Ship(3, b.p2, 5, 'h', "E2"); // too long
         * b.board.addShip(ship5);
         * b.p1.fire("A4", b.board);
         * b.p1.fire("B3", b.board);
         * b.p2.fire("B2", b.board);
         * b.p2.fire("D3", b.board);
         * 
         * Ship ship1 = new Ship(1, b.p1, 2, 'v', "A1"); // A1-B1
         * b.board.addShip(ship1);
         * Ship ship2 = new Ship(2, b.p2, 2, 'v', "A3"); // A3-C3
         * b.board.addShip(ship2);
         */
        for (Ship s : b.allShips) {
            b.board.addShip(s);
        }

        // b.p1.fire("A3", b.board);
        b.play(b.p1, "A3");
        b.printGameState();

        // b.p2.fire("D4", b.board);
        b.play(b.p2, "D4");
        b.printGameState();

        // b.p1.fire("A2", b.board);
        b.play(b.p1, "A2");
        b.printGameState();

        // b.p2.fire("E1", b.board);
        b.play(b.p2, "E1");
        b.printGameState();

        // b.p1.fire("B5", b.board);
        b.play(b.p1, "B5");
        b.printGameState();

        // b.p2.fire("E2", b.board);
        b.play(b.p2, "E2");
        b.printGameState();

        // b.p1.fire("C4", b.board);
        b.play(b.p1, "C4");
        b.printGameState();

        // b.p2.fire("E3", b.board);
        b.play(b.p2, "E3");
        b.printGameState();

        // b.p1.fire("D4", b.board);
        b.play(b.p1, "D4");
        b.printGameState();

        // b.p2.fire("D1", b.board);
        b.play(b.p2, "D1");
        b.printGameState();

        // b.p1.fire("E2", b.board);
        b.play(b.p1, "E2");
        b.printGameState();

        // b.p2.fire("A4", b.board);
        b.play(b.p2, "A4");
        b.printGameState();

        // b.p1.fire("E3", b.board);
        b.play(b.p1, "E3");
        b.printGameState();

        // b.p2.fire("C3", b.board);
        b.play(b.p2, "C3");
        b.printGameState();

        // b.p1.fire("D2", b.board);
        b.play(b.p1, "D2");
        b.printGameState();

        // b.p2.fire("D3", b.board);
        b.play(b.p2, "D3");
        b.printGameState();

        // b.p1.fire("D1", b.board);
        b.play(b.p1, "D1");
        b.printGameState();

        // b.p2.fire("C4", b.board);
        b.play(b.p2, "C4");
        b.printGameState();

        // b.p1.fire("C2", b.board);
        b.play(b.p1, "C2");
        b.printGameState();
        
    }

}
