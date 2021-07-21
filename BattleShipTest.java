package org.cis120.battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class BattleShipTest {

    @Test
    public void testSetUpCoords() {
        Board test = new Board(2);
        test.setUpCoords();
        assertEquals("A1", test.getBoard()[0][0].getPos());
        assertEquals("A2", test.getBoard()[0][1].getPos());
        assertEquals("B1", test.getBoard()[1][0].getPos());
        assertEquals("B2", test.getBoard()[1][1].getPos());
    }

    @Test
    public void testAddShip() {
        Board test = new Board(3);
        test.setUpCoords();
        Player p1 = new Player("p1");
        Ship ship1 = new Ship(1, p1, 2, 'v', "A1");
        test.addShip(ship1);
        assertEquals(ship1, test.getBoard()[0][0].getShipCopy());
        assertEquals(ship1, test.getBoard()[1][0].getShipCopy());
        assertTrue(test.getBoard()[2][0].getShipsCopy().isEmpty());
        assertTrue(test.getBoard()[0][1].getShipsCopy().isEmpty());
        assertTrue(test.getBoard()[1][1].getShipsCopy().isEmpty());
        assertTrue(test.getBoard()[1][2].getShipsCopy().isEmpty());
    }

    @Test
    public void testFire() {
        Board test = new Board(5);
        test.setUpCoords();
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Ship ship1 = new Ship(1, p1, 2, 'v', "A1"); // A1-B1
        test.addShip(ship1);
        Ship ship2 = new Ship(2, p1, 3, 'v', "A3"); // A3-C3
        test.addShip(ship2);
        Ship ship3 = new Ship(3, p2, 2, 'h', "A4"); // A4-A5
        test.addShip(ship3);
        Ship ship4 = new Ship(3, p2, 4, 'h', "D2"); // D2-D5
        test.addShip(ship4);
        p1.fire("A4", test);
        p1.fire("A5", test);

        assertTrue(test.getCoordCopy(0, 4).getShipCopy().getSunkCoords().contains("A4"));
        assertTrue(test.getCoordCopy(0, 4).getShipCopy().getSunkCoords().contains("A5"));
        assertTrue(test.getCoordCopy(0, 4).getShipCopy().isSunk());
    }

    @Test
    public void testGameOver() {
        Board test = new Board(5);
        test.setUpCoords();
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Ship ship1 = new Ship(1, p1, 2, 'v', "A1"); // A1-B1
        test.addShip(ship1);
        Ship ship2 = new Ship(2, p2, 2, 'v', "A3"); // A3-B3
        test.addShip(ship2);
        p1.fire("A3", test);
        p1.fire("B3", test);
        assertTrue(p2.hasLost(test));
        assertFalse(p1.hasLost(test));
    }

    @Test
    public void testSetOrientationIllegalArgumentException() {
        Player p1 = new Player("p1");
        Ship s = new Ship(1, p1, 3);
        assertThrows(IllegalArgumentException.class, () -> {
            s.setOrientation('d');
        });
    }

    @Test
    public void testConstructorInvalidOrientation() {
        Player p1 = new Player("p1");
        assertThrows(IllegalArgumentException.class, () -> {
            new Ship(1, p1, 3, 'a', "B2");
        });
    }

    @Test
    public void testSetOrientationAlreadySet() {
        Player p1 = new Player("p1");
        Ship s = new Ship(1, p1, 3, 'v', "A1");
        assertThrows(IllegalStateException.class, () -> {
            s.setOrientation('h');
        });
    }

    @Test
    public void testFileLineIteratorFileNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FileLineIterator("Invalid file name");
        });
    }

    @Test
    public void testFileLineIterator() {
        FileLineIterator f = new FileLineIterator("files/test");
        assertTrue(f.hasNext());
        assertEquals(4, Integer.parseInt(f.next()));
        assertTrue(f.hasNext());
        assertEquals(3, Integer.parseInt(f.next()));
        assertTrue(f.hasNext());
        assertEquals(4, Integer.parseInt(f.next()));
        assertTrue(f.hasNext());
        assertEquals(2, Integer.parseInt(f.next()));
        assertTrue(f.hasNext());
        assertEquals(1, Integer.parseInt(f.next()));
        assertFalse(f.hasNext());
    }

    @Test
    public void testBattleShipConstructorEmptyFile() {
        assertThrows(IllegalStateException.class, () -> {
            new BattleShip("files/empty_file");
        });
    }

    @Test
    public void testBattleShipConstructorInvalidDim() {
        assertThrows(IllegalStateException.class, () -> {
            new BattleShip("files/invalid_dim");
        });
    }

    @Test
    public void testBattleShipConstructorInvalidShipLength() {
        assertThrows(IllegalStateException.class, () -> {
            new BattleShip("files/invalid_ship_length");
        });
    }

    @Test
    public void testBattleShipConstructorShipLengthNotAnInt() {
        assertThrows(IllegalStateException.class, () -> {
            new BattleShip("files/failing_file");
        });
    }

    @Test
    public void testBattleShipConstructorDimNotAnInt() {
        assertThrows(IllegalStateException.class, () -> {
            new BattleShip("files/failing_file2");
        });
    }

    @Test
    public void testNoShips() {
        BattleShip b = new BattleShip("files/no_ships");
        assertEquals(0, b.getGamePiecesSize());
        b.addGamePiecesToAllShips();
        assertEquals(0, b.getAllShipsCopy().size());
    }

    @Test
    public void testMiniGame() {
        BattleShip b = new BattleShip("files/mini_game");
        assertEquals(4, b.getBoardSize());
        assertEquals(2, b.getGamePiecesSize());
        b.addGamePiecesToAllShips();
        LinkedList<Ship> shipsCopy = b.getAllShipsCopy();
        for (int i = 0; i < shipsCopy.size(); i++) {
            b.replaceShip(i, shipsCopy.get(i));
        }
        shipsCopy.get(0).setStartAndOrientation("A1", 'h', b.getBoardSize());
        shipsCopy.get(1).setStartAndOrientation("A2", 'v', b.getBoardSize());
        shipsCopy.get(2).setStartAndOrientation("B2", 'v', b.getBoardSize());
        shipsCopy.get(3).setStartAndOrientation("A1", 'v', b.getBoardSize());

        Player p1 = b.getP1();
        Player p2 = b.getP2();

        Board newBoard = b.getBoardCopy();
        b.replaceBoard(newBoard);

        p1.fire("B2", newBoard);
        p2.fire("B1", newBoard);
        p1.fire("A2", newBoard);
        p2.fire("A1", newBoard);
        p1.fire("A1", newBoard);

        assertEquals(4, b.getAllShipsCopy().size());
        assertTrue(p2.hasLost(newBoard));
    }

    @Test
    public void testIsSunk() {
        Player p1 = new Player("p1");
        Ship s1 = new Ship(1, p1, 2, 'v', "A2");
        s1.addSunkCoord(new Coord('B', 2));
        s1.addSunkCoord(new Coord('A', 2));
        assertTrue(s1.isSunk());
    }

}
