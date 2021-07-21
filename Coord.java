package org.cis120.battleship;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coord {

    private char row;
    private int col;
    private String pos;

    // a coordinate may contain more than one ship
    private LinkedList<Ship> ships;

    // constructor
    public Coord(char row, int col) {
        this.row = row;
        this.col = col;
        this.pos = String.valueOf(row) + col;
        // empty unless addShip() is called
        this.ships = new LinkedList<Ship>();
    }

    // getters and setters
    public char getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public String getPos() {
        return this.pos;
    }

    public void addShip(Ship s) {
        this.ships.add(s);
    }

    /*
     * public LinkedList<Ship> getShips() {
     * return this.ships;
     * }
     */

    // returns the first ship in the list (encapsulated)
    public Ship getShipCopy() {
        return Ship.copyShip(ships.get(0));
    }

    // encapsulated method for getting Coord ships field
    public LinkedList<Ship> getShipsCopy() {
        LinkedList<Ship> copy = new LinkedList<Ship>();
        for (Ship s : this.ships) {
            copy.add(Ship.copyShip(s));
        }
        return copy;
    }

    // makes a copy of a Coord
    public static Coord copyCoord(Coord c) {
        Coord newCoord = new Coord(c.getRow(), c.getCol());
        // System.out.println(newCoord.getPos());

        LinkedList<Ship> newShips = new LinkedList<Ship>();
        for (Ship s : c.ships) {
            Ship copyShip = Ship.copyShip(s);
            newShips.add(copyShip);
        }
        newCoord.ships = newShips;

        return newCoord;
    }

    // updates the board so references to the same ship point to the same object
    public void updateShips(Board b) {
        for (Ship s : this.ships) {
            for (int k = 0; k < b.getBoard().length; k++) {
                for (int j = 0; j < b.getBoard().length; j++) {
                    Coord c = b.getCoordCopy(k, j);
                    LinkedList<Ship> shipsCopy = c.getShipsCopy();
                    if (shipsCopy.contains(s)) {
                        b.setBoard(k, j, c);
                        // c.replaceShips(shipsCopy);
                        // System.out.println("here");
                        int index = shipsCopy.indexOf(s);
                        b.setShip(k, j, index, s);
                        // shipsCopy.remove(index);
                        // shipsCopy.add(index, s);
                    }
                }
            }
        }
    }

    // returns int array with first element = row and second element = column
    public static int[] regex(String exp) {
        int[] rowCol = new int[2];
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(exp);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
            rowCol[0] = row;
            rowCol[1] = col;
            return rowCol;
        } else {
            throw new IllegalArgumentException();
        }
    }

    // returns true if list of ships is empty
    public boolean noShips() {
        return this.ships.isEmpty();
    }

    // replace ship at index in this.ships with s
    public void replace(int index, Ship s) {
        ships.remove(index);
        ships.add(index, s);
    }

    // replace this.ships with another LinkedList<Ship>
    public void replaceShips(LinkedList<Ship> replacement) {
        ships = replacement;
    }

}
