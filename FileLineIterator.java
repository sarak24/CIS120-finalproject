package org.cis120.battleship;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileLineIterator implements Iterator<String> {

    private Reader r;
    private String line;

    public FileLineIterator(String filePath) {
        try {
            this.r = new BufferedReader(new FileReader(filePath));
            advance();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean hasNext() {
        if (this.line == null) {
            try {
                r.close();
            } catch (IOException e) {
                System.out.println("IO error from closing reader");
            }
            return false;
        }
        return true;
    }

    @Override
    public String next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        String prevLine = this.line;
        advance();
        return prevLine;

    }

    private void advance() {
        String newLine = null;
        try {
            newLine = ((BufferedReader) r).readLine();
        } catch (IOException e) {
            System.out.println(
                    "IO exception caught. Throw NoSuchElement Exception "
                            + "on next call"
            );
            this.line = null;
        }
        this.line = newLine;
    }

}
