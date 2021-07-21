package org.cis120.battleship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInput {

    private String pos;
    private char orientation;

    public UserInput(String pos, char orientation) {
        this.pos = pos;
        this.orientation = orientation;

    }

    // constructor (none of the fields are initialized)
    public UserInput() {
    }

    // setters
    public void setPos(String pos, int maxSize) {
        Pattern p = Pattern.compile("([A-Z])(\\d+)");
        Matcher m = p.matcher(pos);
        int row = 0;
        int col = 0;
        if (m.find()) {
            char c = m.group(1).charAt(0);
            row = (int) (c - 65);
            col = Integer.parseInt(m.group(2)) - 1;
            if (row < 0 || row >= maxSize || col >= maxSize || col < 0) {
                throw new IllegalArgumentException();
            }
            this.pos = pos;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setOrientation(char c) {
        if (c == 'v') {
            this.orientation = c;
        } else if (c == 'h') {
            this.orientation = c;
        } else {
            throw new IllegalArgumentException("Orientation argument is invalid");
        }
    }

    // getters
    public String getPos() {
        return this.pos;
    }

    public char getOrientation() {
        return this.orientation;
    }

}
