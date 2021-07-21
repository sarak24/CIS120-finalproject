package org.cis120.battleship;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class SideLettering extends JPanel {

    private int numRow;

    public SideLettering(int gridSize) {

        this.numRow = gridSize;

    }

    // draws letters to the left of the grid that line up with the rows
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < this.numRow; i++) {
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf((char) (i + 65)), 10, i * 50 + 35);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 50 * this.numRow);
    }

}
