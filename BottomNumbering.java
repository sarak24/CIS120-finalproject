package org.cis120.battleship;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class BottomNumbering extends JPanel {

    private int numCol;

    public BottomNumbering(int gridSize) {
        this.numCol = gridSize;
    }

    // draws numbers beneath the grid that line up with the columns
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < this.numCol; i++) {
            g.setColor(Color.BLACK);
            g.drawString("" + (i + 1), i * 50 + 40, 10);
        }
        for (int i = 0; i < this.numCol; i++) {
            g.setColor(Color.BLACK);
            g.drawString("" + (i + 1), (this.numCol * 50) + (i * 50 + 50), 10);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50 * this.numCol, 10);
    }

}
