package org.cis120.battleship;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

@SuppressWarnings("serial")
public class MainScreen extends JPanel {

    private BattleShip model;
    private JLabel status;
    private JLabel winnerMessage;
    private int numGamePieces;
    private int gridSize;
    private static LinkedList<Integer> shipSizes;
    private static int width = 400;
    private static int height = 200;

    // 0 = passing view, 1 = player 1 view, 2 = player 2 view, 3 = game over
    private int mode;

    public MainScreen(String filename, JLabel statusBar1, JLabel statusBar2) {
        super();

        PlayerBoard pb = new PlayerBoard();

        OpponentBoard ob = new OpponentBoard();

        this.add(pb);
        this.add(ob);

        setFocusable(true);

        // properties of model/grid
        model = new BattleShip(filename);
        gridSize = model.getBoardSize();
        numGamePieces = model.getGamePiecesSize();
        shipSizes = model.getShipSizes();
        // responsible for updating these JLabels (instantiated in RunBattleShip.java)
        status = statusBar1;
        winnerMessage = statusBar2;

        // initialize mode to 1 (p1 turn)
        mode = 1;

        // listen for user typing 'f' key
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // if game is over, don't respond to any keyboard input
                if (mode == 3) {
                    return;
                }
                // player presses 'f' key when they're ready to fire
                if (c == 'f') {
                    // in passing mode
                    if (mode == 0) {
                        if (model.getP1Turn()) {
                            mode = 1;
                            status.setText(
                                    "Player 1, time to make a move. Hit 'f' when you're"
                                            + " ready to fire."
                            );
                        } else {
                            mode = 2;
                            status.setText(
                                    "Player 2, time to make a move. Hit 'f' when you're"
                                            + " ready to fire."
                            );
                        }
                    } else {
                        // triggers a input dialog box to pop up and ask for a coordinate
                        String pos = JOptionPane.showInputDialog("Guess a coordinate.");
                        try {
                            // if player 1 is up, call play() with p1
                            if (model.getP1Turn()) {
                                model.play(model.getP1(), pos);
                                mode = 0;
                                status.setText(
                                        "Passing the computer... type 'f' when you're ready"
                                                + " to see the board."
                                );
                            } else {
                                // if player 2 is up, call play() with p2
                                model.play(model.getP2(), pos);
                                mode = 0;
                                status.setText(
                                        "Passing the computer... type 'f' when you're ready"
                                                + " to see the board."
                                );
                            }
                        } catch (IllegalArgumentException exception) {
                            // if user input is invalid, alert the user
                            model.addToGameReport("Invalid coordinate");
                            System.out.println(
                                    "Invalid coordinate. Please type a capital letter "
                                            + "immediately followed by a number >= # of columns"
                            );
                            JOptionPane.showMessageDialog(
                                    null, "Invalid coordinate. Please type "
                                            + "a capital letter immediately followed by a number"
                                            + " >=  # of columns.",
                                    "alert", JOptionPane.ERROR_MESSAGE
                            );
                            // keep asking for a coordinate until the guess is valid
                            boolean failed = true;
                            while (failed) {
                                pos = JOptionPane.showInputDialog("Guess a coordinate.");
                                try {
                                    if (model.getP1Turn()) {
                                        model.play(model.getP1(), pos);
                                        mode = 0;
                                        status.setText(
                                                "Passing the computer... type 'f' when "
                                                        + "you're ready to see the board."
                                        );
                                    } else {
                                        model.play(model.getP2(), pos);
                                        mode = 0;
                                        status.setText(
                                                "Passing the computer... type 'f' when "
                                                        + "you're ready to see the board."
                                        );
                                    }
                                    failed = false;
                                } catch (IllegalArgumentException x) {
                                    System.out.println("Invalid coordinate\n");
                                    JOptionPane.showMessageDialog(
                                            null, "Invalid coordinate.",
                                            "alert", JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        }

                    }
                    // checks if the player has won with the most recent guess
                    if (model.isGameOver()) {
                        // set mode to 3 (game over)
                        mode = 3;
                        status.setText("<<<<<<<< GAME OVER >>>>>>>>");
                        winnerMessage.setText(model.whoIsWinner().getName() + " WINS!!!");
                        model.addToGameReport(model.whoIsWinner().getName() + " won.");
                        model.gameReportToFile(model.getGameReport(), false);
                    }

                }
                repaint(); // repaints the game board
            }
        });
    }

    // getters
    public int getNumGamePieces() {
        return this.numGamePieces;
    }

    public int getGridSize() {
        return this.gridSize;
    }

    // calls addGamePiecesToAllShips() on this.model
    public void addGamePieces() {
        model.addGamePiecesToAllShips();
    }

    // calls addShip() with each ship added to board on this.model
    public void addShipsToBoard() {
        Board replace = model.getBoardCopy();
        model.replaceBoard(replace);
        for (Ship s : model.getAllShipsCopy()) {
            replace.addShip(s);
        }
    }

    // takes UserInput and sets the start and orientation of the appropriate ship
    public void addUserInput(LinkedList<UserInput> input) {
        for (int i = 0; i < input.size(); i++) {
            String pos = input.get(i).getPos();
            char orientation = input.get(i).getOrientation();
            Ship newShip = model.getAllShipsCopy().get(i);
            model.replaceShip(i, newShip);
            newShip.setStartAndOrientation(pos, orientation, gridSize);
            // System.out.println(model.getAllShipsNotCopy().get(i).getStart());
        }
    }

    // returns true if given the arguments, the start position is valid
    public static void posIsValid(String str, char letter, int index, int gridSize) {
        if (letter != 'h' && letter != 'v') {
            throw new IllegalArgumentException();
        }
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(str);
        int row = 0;
        int col = 0;
        int length = shipSizes.get(index) - 1;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 64);
            col = Integer.parseInt(m.group(2));
        } else {
            throw new IllegalArgumentException();
        }
        if (letter == 'h') {
            if (col + length > gridSize) {
                throw new IllegalArgumentException();
            }
        }
        if (letter == 'v') {
            if (row + length > gridSize) {
                throw new IllegalArgumentException();
            }
        }
    }

    // inner class for left grid
    class PlayerBoard extends JPanel {

        public PlayerBoard() {
            super();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            width = gridSize * 50;
            height = gridSize * 50;

            // draw grid
            g.setColor(Color.BLACK);
            for (int i = 0; i < gridSize; i++) {
                g.drawLine(i * 50, 0, i * 50, height);
                g.drawLine(0, i * 50, width, i * 50);
            }
            g.drawLine(width, 0, width, height);
            g.drawLine(0, height, width, height);

            // show player the placement of their ships
            if (mode == 0) {
                paintPassingScreen(g);
            } else if (mode == 1) {
                paintP1View(g);
            } else if (mode == 2) {
                paintP2View(g);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
            }

        }

        // colors the squares where the current player owns ships
        private void markPlayerShips(Player p, Graphics g) {
            for (int i = 0; i < model.getBoardSize(); i++) {
                for (int j = 0; j < model.getBoardSize(); j++) {
                    if (model.getPlayerShips(p, i, j)) {
                        Coord curr = model.getBoardCopy().getBoard()[i][j];
                        if (p.equals(model.getP1())) {
                            if (model.getP2().getGuessedCorrect().contains(curr.getPos())) {
                                // blue if hit by opponent
                                g.setColor(Color.BLUE);
                            } else {
                                // black if not hit by opponent
                                g.setColor(Color.BLACK);
                            }
                        } else {
                            if (model.getP1().getGuessedCorrect().contains(curr.getPos())) {
                                // blue if hit by opponent
                                g.setColor(Color.BLUE);
                            } else {
                                // black if not hit by opponent
                                g.setColor(Color.BLACK);
                            }
                        }

                        g.fillRect(j * 50, i * 50, 50, 50);
                    }
                }
            }
        }

        public void paintP1View(Graphics g) {
            markPlayerShips(model.getP1(), g);
        }

        public void paintPassingScreen(Graphics g) {
            g.fillRect(0, 0, width, height);
        }

        public void paintP2View(Graphics g) {
            markPlayerShips(model.getP2(), g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

    }

    // inner class for right grid
    class OpponentBoard extends JPanel {

        public OpponentBoard() {
            super();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            width = gridSize * 50;
            height = gridSize * 50;

            // draw grid
            g.setColor(Color.BLACK);
            for (int i = 0; i < gridSize; i++) {
                g.drawLine(i * 50, 0, i * 50, height);
                g.drawLine(0, i * 50, width, i * 50);
            }
            g.drawLine(width, 0, width, height);
            g.drawLine(0, height, width, height);

            // show player the placement of their ships
            if (mode == 0) {
                paintPassingScreen(g);
            } else if (mode == 1) {
                paintP1View(g);
            } else if (mode == 2) {
                paintP2View(g);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
            }

        }

        // colors the squares where player has hit opponent
        private void markOpponentShips(Player p, Graphics g) {
            LinkedList<String> l = p.getGuessedCorrect();
            for (String str : l) {
                int[] rowAndCol = Coord.regex(str);
                int row = rowAndCol[0];
                int col = rowAndCol[1];
                Coord[][] coordArray = model.getBoardCopy().getBoard();
                LinkedList<Ship> listOfShipsAtCoord = coordArray[row][col].getShipsCopy();
                for (Ship s : listOfShipsAtCoord) {
                    if (!s.getOwner().getName().equals(p.getName())) {
                        if (s.isSunk()) {
                            // red if completely sunk
                            g.setColor(Color.RED);
                        } else {
                            // yellow if partially sunk
                            g.setColor(Color.YELLOW);
                        }
                        g.fillRect(col * 50, row * 50, 50, 50);
                    }
                }

            }
        }

        // color any guessedWrong squares gray
        public void colorGuessedSquares(Player p, Graphics g) {
            for (String s : p.getGuessedWrong()) {
                int[] rowAndCol = Coord.regex(s);
                int row = rowAndCol[0];
                int col = rowAndCol[1];
                g.setColor(Color.GRAY);
                g.fillRect(col * 50, row * 50, 50, 50);

            }
        }

        public void paintP1View(Graphics g) {
            markOpponentShips(model.getP1(), g);
            colorGuessedSquares(model.getP1(), g);
        }

        public void paintPassingScreen(Graphics g) {
            g.fillRect(0, 0, width, height);
        }

        public void paintP2View(Graphics g) {
            markOpponentShips(model.getP2(), g);
            colorGuessedSquares(model.getP2(), g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * width, 2 * height);
    }

}
