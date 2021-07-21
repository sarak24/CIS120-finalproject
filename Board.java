package org.cis120.battleship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {

    private Coord[][] gameBoard;

    // constructor (needs a dimension as an argument)
    public Board(int dim) {
        this.gameBoard = new Coord[dim][dim];
    }

    public Board(Coord[][] coordArray) {
        this.gameBoard = coordArray;
    }

    // gives all Coord in grid a pos (String of letter-number pair)
    public void setUpCoords() {
        for (int i = 0; i < gameBoard.length; i++) {
            char row = (char) (65 + i);
            for (int j = 0; j < gameBoard.length; j++) {
                gameBoard[i][j] = new Coord(row, j + 1);
            }
        }
    }

    // returns a copy of the board (encapsulated)
    public Coord[][] getBoard() {
        int dim = this.gameBoard.length;
        Coord[][] copy = new Coord[dim][];
        for (int i = 0; i < dim; i++) {
            copy[i] = this.gameBoard[i].clone();
        }
        return copy;
    }

    /*
     * public Coord[][] getBoardNotCopy() {
     * return this.gameBoard;
     * }
     */

    // encapsulated: sets element of gameBoard to new Coord
    public void setBoard(int row, int col, Coord c) {
        gameBoard[row][col] = c;
    }

    // encapsulated
    public Coord getCoordCopy(int row, int col) {
        Coord c = gameBoard[row][col];
        Coord replace = Coord.copyCoord(c);
        return replace;
    }

    // encapsulated
    public void setShip(int row, int col, int index, Ship s) {
        gameBoard[row][col].replace(index, s);
    }

    // encapsulated
    public Board copyBoard() {
        return new Board(this.getBoard());
    }

    // adds ship to board using start and orientation fields of Ship argument
    public void addShip(Ship s) {
        Ship.Orientation o = s.getOrientation();
        String start = s.getStart();
        // regex expression is letter-number pair
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(start);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
        } else {
            // throw exception if coordinate is invalid
            throw new IllegalArgumentException("Invalid start coordinate");
        }
        int l = s.getLength();
        // depending on orientation, adds ship to all Coord below/right of start Coord
        switch (o) {
            case Vertical:
                // ship cannot exceed (bottom) boundaries of gameboard
                if (l + row > gameBoard.length) {
                    System.out.println("Ship is too long to fit here");
                    break;
                }
                for (int i = row; i < l + row; i++) {
                    this.gameBoard[i][col].addShip(s);

                }
                break;
            case Horizontal:
                // ship cannot exceed (right) boundaries of gameboard
                if (l + col > gameBoard.length) {
                    System.out.println("Ship is too long to fit here");
                    break;
                }
                for (int i = col; i < l + col; i++) {
                    this.gameBoard[row][i].addShip(s);
                }
                break;
            default:
                break;
        }

    }

}
