package org.cis120.battleship;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {

    final private String name;
    private LinkedList<String> guessedCorrect;
    private LinkedList<String> guessedWrong;

    // constructor
    public Player(String name) {
        this.name = name;
        this.guessedCorrect = new LinkedList<String>();
        this.guessedWrong = new LinkedList<String>();
    }

    // getters
    public String getName() {
        return this.name;
    }

    // encapsulated
    public LinkedList<String> getGuessedCorrect() {
        LinkedList<String> returnList = new LinkedList<String>();
        for (String str : this.guessedCorrect) {
            returnList.add(str);
        }
        return returnList;
    }

    // encapsulated
    public LinkedList<String> getGuessedWrong() {
        LinkedList<String> returnList = new LinkedList<String>();
        for (String str : this.guessedWrong) {
            returnList.add(str);
        }
        return returnList;
    }

    // a player can make a ship and that ship's owner will be that player
    public Ship makeShip(int id, int length, char orientation, String start) {
        return new Ship(id, this, length, orientation, start);
    }

    public Ship makeShip(int id, int length) {
        return new Ship(id, this, length);
    }

    // fires a shot at the coordinate that the player guesses
    public boolean fire(String guess, Board board) {
        // do nothing if the game is over
        if (this.hasLost(board)) {
            return false;
        }
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(guess);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
        } else {
            throw new IllegalArgumentException("Invalid guess");
        }
        if (row >= board.getBoard().length || col >= board.getBoard().length) {
            throw new IllegalArgumentException("Out of range");
        }
        Coord guessOnBoard = board.getCoordCopy(row, col);
        // replace old Coord with guessOnBoard to preserve encapsulation
        board.setBoard(row, col, guessOnBoard);

        if (guessOnBoard.noShips()) {
            System.out.println(this.name + ": unlucky guess");
            this.guessedWrong.add(guessOnBoard.getPos());
            return false;
        } else {
            boolean success = false;
            LinkedList<Ship> coordShips = guessOnBoard.getShipsCopy();
            guessOnBoard.replaceShips(coordShips);
            guessOnBoard.updateShips(board);
            for (int i = 0; i < coordShips.size(); i++) {
                Ship s = coordShips.get(i);
                if (!s.getOwner().getName().equals(this.name)) {
                    System.out.println(this.name + ": hit opponent at " + guess);
                    // if unsuccessful, add coordinate to player's guessedWrong list
                    this.guessedCorrect.add(guessOnBoard.getPos());
                    // add hit coordinate to ship's sunkCoords list
                    s.addSunkCoord(guessOnBoard);
                    // System.out.println(s.getSunkCoords().getLast());
                    guessOnBoard.replace(i, s);
                    // System.out.println(guessOnBoard.getShipsCopy().size());
                    success = true;
                }
            }

            if (!success) {
                // if unsuccessful, add coordinate to player's guessedWrong list
                this.guessedWrong.add(guessOnBoard.getPos());
                System.out.println(this.name + ": unlucky guess");
                return false;
            }
            return false;
        }

    }

    // determines if the player has lost by checking all of the player's ships'
    // sunkCoord
    public boolean hasLost(Board b) {
        boolean hasLost = true;
        int size = b.getBoard().length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Coord curr = b.getCoordCopy(i, j);
                for (Ship s : curr.getShipsCopy()) {
                    if (s.getOwner().getName().equals(this.name)) {
                        // if any ship is not sunk, the player has not lost
                        if (!s.isSunk()) {
                            hasLost = false;
                        }
                    }
                }
            }
        }

        return hasLost;
    }

    // two players are equal if they share the same name (name is final)
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(getClass() == o.getClass())) {
            return false;
        }
        Player that = (Player) o;
        if (!name.equals(that.name)) {
            return false;
        }
        return true;
    }

}
