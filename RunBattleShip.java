package org.cis120.battleship;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

public class RunBattleShip implements Runnable {

    // list of UserInputs
    private LinkedList<UserInput> userInput;

    public void run() {

        // collection of players' input (position, orientation)
        userInput = new LinkedList<UserInput>();

        final JFrame frame = new JFrame("Battle Ship");
        frame.setLocation(300, 300);

        // Tell player 1 to press f to make their first guess
        JLabel statusBarTop = new JLabel(
                "Ready to play? Hit the 'f' key to start.",
                (int) Component.CENTER_ALIGNMENT
        );
        // Will display the winner at the end
        JLabel statusBarBottom = new JLabel(" ", (int) Component.CENTER_ALIGNMENT);

        // Panel for main screen and row/col labeling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Panel where all components will go
        JPanel entirePanel = new JPanel();
        entirePanel.setLayout(new BorderLayout());

        // open dialog box with instructions and tips
        JOptionPane.showMessageDialog(
                null, "Instructions: \n"
                        + "1. When prompted, enter an existing file name with the prefix “files/” "
                        + "(ex. “files/gameSetup”).\n"
                        + "**The first line of the file should be the board dimension, an integer "
                        + "between 1 and 10 inclusive.\n"
                        + "Any subsequent lines should contain the length(s) of each ship, an "
                        + "integer between 1 and 4 inclusive.\n"
                        + "Each line represents one ship, so each line should contain just "
                        + "ONE integer.\n"
                        + "2. When prompted, enter the start coordinate and orientation for  "
                        + "each ship. This coordinate will \n"
                        + "be the upper, leftmost coordinate of the ship. Ships can be placed "
                        + "horizontally (‘h’) or vertically (‘v’). Use LOWERCASE.\n"
                        + "Coordinates are CAPITAL letter-number pairs (ex. A4, B1).\n"
                        + "**This program is case sensitive. Use LOWERCASE when entering  "
                        + "orientations and an UPPERCASE letter for the coordinate.\n"
                        + "3. Once the board is set up, give the computer to player 1. "
                        + "The player’s own board is displayed \n"
                        + "on the left, and the board on the right shows where the player  "
                        + "has guessed and where the \n"
                        + "player has successfully hit the opponent. Player 1, hit the ‘f’ key "
                        + "when you’ve decided on a \n"
                        + "coordinate and are ready to fire.\n"
                        + "**On the left BLUE squares indicate where your opponent has "
                        + "hit your ships.\n"
                        + "**On the right YELLOW squares indicate positions where you've hit your "
                        + "opponent's ships, but they are NOT completely sunk. \n"
                        + "**On the right Red squares indicate positions of ships that are "
                        + "COMPLETELY sunk.\n"
                        + "4. After player 1 has guessed, the screen will be blacked out "
                        + "so the computer can be passed \n"
                        + "without anyone peeking at their opponent’s board. Player 2, hit the "
                        + "‘f’ key when you’ve \n"
                        + "decided on a coordinate and are ready to fire.\n"
                        + "5. Continue playing and passing until one player has sunk all their "
                        + "opponent’s ships.\n"
                        + "TIPS: \n"
                        + "*Ships may overlap, but this is NOT recommended. \n"
                        + "*You may NOT position a ship with an orientation that causes it "
                        + "to exceed the boundaries of the board.\n"
                        + "*Use the color indicators to make strategic guesses!\n",
                "Instructions/tips", JOptionPane.INFORMATION_MESSAGE
        );

        // Open dialog box for user to input filename of game setup
        String fileName = JOptionPane.showInputDialog("Enter filename for game setup.");
        MainScreen board = null;
        try {
            board = new MainScreen(fileName, statusBarTop, statusBarBottom);
        } catch (IllegalArgumentException e) {
            System.out.println("File not found");
            // alert the user if file name is invalid
            JOptionPane.showMessageDialog(
                    null, "Invalid file name.", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalStateException e) {
            System.out.println("Problems reading file");
            // alert the user if file is not formatted correctly
            JOptionPane.showMessageDialog(
                    null, "File could not be processed.", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // if file input fails, give user 3 more tries before closing window
        int tryAgain = 0;
        while (board == null && tryAgain < 3) {
            fileName = JOptionPane.showInputDialog("Enter filename for game setup.");
            try {
                board = new MainScreen(fileName, statusBarTop, statusBarBottom);
            } catch (IllegalArgumentException e) {
                System.out.println("File not found");
                JOptionPane.showMessageDialog(
                        null, "Invalid file name", "alert",
                        JOptionPane.ERROR_MESSAGE
                );
                tryAgain++;
            } catch (IllegalStateException e) {
                System.out.println("Problems reading file");
                JOptionPane.showMessageDialog(
                        null, "File could not be processed", "alert",
                        JOptionPane.ERROR_MESSAGE
                );
                tryAgain++;
            }
        }

        // gives the user three more attempts to input a valid file name
        if (tryAgain >= 3) {
            System.out.println("Exceeded max number of attempts");
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }

        // player's board view
        board.addGamePieces();
        setShipPositions(board);
        board.addUserInput(userInput);
        board.addShipsToBoard();

        // Displays lettering on left side panel
        SideLettering rowLetters = new SideLettering(board.getGridSize());

        // Displays number of columns at bottom
        BottomNumbering colNumbers = new BottomNumbering(board.getGridSize());

        // add board and labels to main panel
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(colNumbers, BorderLayout.PAGE_END);
        mainPanel.add(rowLetters, BorderLayout.LINE_START);
        // add status bars
        entirePanel.add(statusBarTop, BorderLayout.PAGE_START);
        entirePanel.add(statusBarBottom, BorderLayout.PAGE_END);
        // add main panel to entire panel
        entirePanel.add(mainPanel, BorderLayout.CENTER);

        // add entire panel to frame
        frame.add(entirePanel);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // paint initial board (p1 view)
        board.repaint();
        rowLetters.repaint();

    }

    // method for collecting users' input to be used later in setting up board
    public void setShipPositions(MainScreen gb) {
        int maxDim = gb.getGridSize();
        for (int i = 0; i < gb.getNumGamePieces(); i++) {
            // add player1Input to list of user inputs
            boolean decided = false;
            while (!decided) {
                userInput.add(getP1Input(i, maxDim));
                int confirm = JOptionPane.showConfirmDialog(
                        null, "Are you sure?", "Confirm",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.NO_OPTION) {
                    userInput.removeLast();
                }
                if (confirm == JOptionPane.YES_OPTION) {
                    decided = true;
                }
            }

            // add player2Input to list of user inputs
            decided = false;
            while (!decided) {
                userInput.add(getP2Input(i, maxDim));
                int confirm = JOptionPane.showConfirmDialog(
                        null, "Are you sure?", "Confirm",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.NO_OPTION) {
                    userInput.removeLast();
                }
                if (confirm == JOptionPane.YES_OPTION) {
                    decided = true;
                }
            }

        }
    }

    private UserInput getP1Input(int i, int maxDim) {
        // player 1 start coordinate and orientation for ship i in gamePieces
        UserInput player1Input = new UserInput();
        String p1ShipPos = JOptionPane
                .showInputDialog("Player 1, Where do you want ship " + (i + 1) + "?");
        try {
            player1Input.setPos(p1ShipPos, maxDim);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    null, "Invalid position", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
            // ask for start coordinate until a valid position is given
            boolean failed = true;
            while (failed) {
                p1ShipPos = JOptionPane.showInputDialog("Type a coordinate for ship position.");
                try {
                    player1Input.setPos(p1ShipPos, maxDim);
                    failed = false;
                } catch (IllegalArgumentException x) {
                    System.out.println("Invalid coordinate");
                    JOptionPane.showMessageDialog(
                            null, "Invalid coordinate.",
                            "alert", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        String p1ShipOrientation = JOptionPane.showInputDialog(
                "Player 1, How do you want it positioned? Type 'v'"
                        + " for vertical and 'h' for horizontal"
        );
        char p1Letter = p1ShipOrientation.charAt(0);

        try {
            // checks to make sure ship fits on grid with this orientation
            MainScreen.posIsValid(p1ShipPos, p1Letter, i, maxDim);
            // System.out.println("pos is valid");
            player1Input.setOrientation(p1Letter);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    null, "Invalid orientation", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
            // ask for an orientation until a valid one is given
            boolean failed = true;
            while (failed) {
                p1ShipOrientation = JOptionPane.showInputDialog("Type 'h' or 'v'.");
                p1Letter = p1ShipOrientation.charAt(0);
                try {
                    MainScreen.posIsValid(p1ShipPos, p1Letter, i, maxDim);
                    player1Input.setOrientation(p1Letter);
                    failed = false;
                } catch (IllegalArgumentException x) {
                    System.out.println("Invalid orientation");
                    JOptionPane.showMessageDialog(
                            null, "Invalid orientation.",
                            "alert", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        return player1Input;
    }

    private UserInput getP2Input(int i, int maxDim) {
        // player 2 start coordinate and orientation for ship i in gamePieces
        UserInput player2Input = new UserInput();
        String p2ShipPos = JOptionPane
                .showInputDialog("Player 2, where do you want ship " + (i + 1) + "?");
        try {
            player2Input.setPos(p2ShipPos, maxDim);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    null, "Invalid position", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
            // ask for start coordinate until a valid position is given
            boolean failed = true;
            while (failed) {
                p2ShipPos = JOptionPane.showInputDialog("Type a coordinate for ship position.");
                try {
                    player2Input.setPos(p2ShipPos, maxDim);
                    failed = false;
                } catch (IllegalArgumentException x) {
                    System.out.println("Invalid coordinate");
                    JOptionPane.showMessageDialog(
                            null, "Invalid coordinate.",
                            "alert", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        String p2ShipOrientation = JOptionPane.showInputDialog(
                "Player 2, How do you want it positioned? Type 'v'"
                        + " for vertical and 'h' for horizontal"
        );
        char p2Letter = p2ShipOrientation.charAt(0);
        try {
            // checks to make sure ship fits on grid with this orientation
            MainScreen.posIsValid(p2ShipPos, p2Letter, i, maxDim);
            player2Input.setOrientation(p2Letter);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    null, "Invalid orientation", "alert",
                    JOptionPane.ERROR_MESSAGE
            );
            // ask for an orientation until a valid one is given
            boolean failed = true;
            while (failed) {
                p2ShipOrientation = JOptionPane.showInputDialog("Type 'h' or 'v'.");
                p2Letter = p2ShipOrientation.charAt(0);
                try {
                    MainScreen.posIsValid(p2ShipPos, p2Letter, i, maxDim);
                    player2Input.setOrientation(p2Letter);
                    failed = false;
                } catch (IllegalArgumentException x) {
                    System.out.println("Invalid orientation");
                    JOptionPane.showMessageDialog(
                            null, "Invalid orientation.",
                            "alert", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        return player2Input;
    }

}
