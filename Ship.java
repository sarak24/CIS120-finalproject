package org.cis120.battleship;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ship {

    // orientation is either horizontal or vertical
    public enum Orientation {
        Horizontal,
        Vertical
    }

    private int id;
    private Player owner;
    final private int length;
    private Orientation orientation;
    private String start;
    private LinkedList<String> sunkCoords;

    // constructor (arguments will come from user input and input file)
    public Ship(int id, Player owner, int length, char orientation, String start) {
        this.id = id;
        this.owner = owner;
        this.length = length;
        this.start = start;
        if (orientation == 'v') {
            this.orientation = Orientation.Vertical;
        } else if (orientation == 'h') {
            this.orientation = Orientation.Horizontal;
        } else {
            throw new IllegalArgumentException("Orientation argument is invalid");
        }
        this.sunkCoords = new LinkedList<String>();
    }

    // overloaded constructor- player can set orientation and start pos later
    public Ship(int id, Player p, int length) {
        this.id = id;
        this.owner = p;
        this.length = length;
        this.sunkCoords = new LinkedList<String>();
    }

    // returns true if ship is completely sunk
    public boolean isSunk() {
        boolean notFound = false;
        for (String s : shipLocation()) {
            if (!sunkCoords.contains(s)) {

                notFound = true;
            }
        }
        return !notFound;
    }

    // returns a list of all positions a ship occupies
    public LinkedList<String> shipLocation() {
        String start = this.start;
        Orientation o = this.orientation;
        LinkedList<String> listOfCoords = new LinkedList<String>();
        Pattern p = Pattern.compile("([A-Z])(.*)");
        Matcher m = p.matcher(start);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2));
        } else {
            throw new IllegalArgumentException("Invalid start coordinate");
        }
        int l = this.length;
        // depending on orientation, adds ship to all Coord below/right of start Coord
        switch (o) {
            // increments the rows if ship is vertical
            case Vertical:
                for (int i = 0; i < l; i++) {
                    String coord = "";
                    coord = String.valueOf((char) (row + 65)) + col;
                    listOfCoords.add(coord);
                    row++;
                }
                break;
            // increments the columns if ship horizontal
            case Horizontal:
                for (int i = 0; i < l; i++) {
                    String coord = "";
                    coord = String.valueOf((char) (row + 65)) + col;
                    listOfCoords.add(coord);
                    col++;
                }
                break;
            default:
                break;
        }
        return listOfCoords;
    }

    // adds a coordinate to the list of sunkCoord
    public void addSunkCoord(Coord c) {
        this.sunkCoords.add(c.getPos());
        /*
         * for (int i = 0; i < this.sunkCoords.size(); i++) {
         * System.out.println(this.sunkCoords.get(i));
         * }
         */

    }

    // getters and setters
    public int getId() {
        return this.id;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public String getStart() {
        return this.start;
    }

    public int getLength() {
        return this.length;
    }

    public Player getOwner() {
        return this.owner;
    }

    public LinkedList<String> getSunkCoords() {
        LinkedList<String> coordPos = new LinkedList<String>();
        for (String s : this.sunkCoords) {
            coordPos.add(s);
        }
        return coordPos;
    }

    // setters
    public void setOrientation(char c) {
        if (this.orientation != null) {
            throw new IllegalStateException("Cannot set orientation once it's set");
        } else {
            if (c == 'v') {
                this.orientation = Orientation.Vertical;
            } else if (c == 'h') {
                this.orientation = Orientation.Horizontal;
            } else {
                throw new IllegalArgumentException("Orientation argument is invalid");
            }
        }
    }

    public void setStart(String s, int maxSize) {
        if (this.start != null) {
            throw new IllegalStateException("Cannot set start once it's set");
        }
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(s);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
            if (this.orientation.equals(Orientation.Vertical)) {
                if (row + this.length > maxSize) {
                    throw new IllegalArgumentException("Ship is too long to fit here.");
                }
            } else {
                if (col + this.length > maxSize) {
                    throw new IllegalArgumentException("Ship is too wide to fit here.");
                }
            }

            this.start = s;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setStartAndOrientation(String s, char c, int maxSize) {
        this.setOrientation(c);
        this.setStart(s, maxSize);
    }

    // makes a copy of a ship (encapsulated)
    public static Ship copyShip(Ship s) {
        char c = ' ';
        if (s.orientation == null || s.start == null) {

            LinkedList<String> sunk = new LinkedList<String>();
            for (String str : s.sunkCoords) {
                sunk.add(str);
            }
            Ship newShip = new Ship(s.id, s.owner, s.length);
            newShip.sunkCoords = sunk;
            return newShip;
        } else {

            switch (s.orientation) {
                case Horizontal:
                    c = 'h';
                    break;
                case Vertical:
                    c = 'v';
                    break;
                default:
                    break;
            }
            LinkedList<String> sunk = new LinkedList<String>();
            for (String str : s.sunkCoords) {
                sunk.add(str);
            }
            Ship newShip = new Ship(s.id, s.owner, s.length, c, s.start);
            newShip.sunkCoords = sunk;
            return newShip;
        }

    }

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
        Ship that = (Ship) o;
        if (id != that.id) {
            return false;
        }
        if (!owner.getName().equals(that.owner.getName())) {
            return false;
        }
        if (length != that.length) {
            return false;
        }
        if (orientation != that.orientation) {
            return false;
        }
        if (!start.equals(that.start)) {
            return false;
        }
        return true;
    }

}
