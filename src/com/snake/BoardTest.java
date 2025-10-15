package com.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("java.awt.headless", "true");
        board = new Board();

        Field tf = Board.class.getDeclaredField("timer");
        tf.setAccessible(true);
        Timer t = (Timer) tf.get(board);
        if (t != null) t.stop();
    }

    // helpers
    private int getInt(String name) throws Exception {
        Field f = Board.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.getInt(board);
    }
    private void setInt(String name, int v) throws Exception {
        Field f = Board.class.getDeclaredField(name);
        f.setAccessible(true);
        f.setInt(board, v);
    }
    private int[] getArray(String name) throws Exception {
        Field f = Board.class.getDeclaredField(name);
        f.setAccessible(true);
        return (int[]) f.get(board);
    }
    private void setBool(String name, boolean v) throws Exception {
        Field f = Board.class.getDeclaredField(name);
        f.setAccessible(true);
        f.setBoolean(board, v);
    }
    private boolean getBool(String name) throws Exception {
        Field f = Board.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.getBoolean(board);
    }
    private void setDirection(Board.Direction d) throws Exception {
        Field f = Board.class.getDeclaredField("currentDirection");
        f.setAccessible(true);
        f.set(board, d);
    }
    private Board.Direction getDirection() throws Exception {
        Field f = Board.class.getDeclaredField("currentDirection");
        f.setAccessible(true);
        return (Board.Direction) f.get(board);
    }
    private void tick() { board.actionPerformed(new ActionEvent(this, 0, "")); }

    @Test
    void tc1_eatAppleIncrementsAndRelocates() throws Exception {
        int[] x = getArray("x");
        int[] y = getArray("y");

        setInt("dots", 3);
        x[0] = 50; y[0] = 50;

        setDirection(Board.Direction.RIGHT);

        Field ax = Board.class.getDeclaredField("appleX");
        Field ay = Board.class.getDeclaredField("appleY");
        ax.setAccessible(true); ay.setAccessible(true);
        ax.setInt(board, 60); ay.setInt(board, 50);

        tick();

        assertEquals(4, getInt("dots"));
        int newAX = board.getAppleX();
        int newAY = board.getAppleY();
        assertTrue(newAX >= 0 && newAX <= 290 && newAX % 10 == 0);
        assertTrue(newAY >= 0 && newAY <= 290 && newAY % 10 == 0);
        assertTrue(getBool("inGame"));
    }

    @Test
    void tc2_wallCollisionEndsGame() throws Exception {
        setDirection(Board.Direction.RIGHT);
        int bw = getInt("B_WIDTH");
        int[] x = getArray("x");
        int[] y = getArray("y");
        setInt("dots", 3);

        x[0] = bw - 10; y[0] = 0;

        tick();

        assertFalse(getBool("inGame"));
    }

    @Test
    void tc3_selfCollisionEndsGame() throws Exception {
        int[] x = getArray("x");
        int[] y = getArray("y");
        setInt("dots", 5);

        x[0]=40; y[0]=50;
        x[1]=50; y[1]=50;
        x[2]=60; y[2]=50;
        x[3]=60; y[3]=40;
        x[4]=30; y[4]=50;

        setDirection(Board.Direction.LEFT);

        tick();

        assertFalse(getBool("inGame"));
    }

    @Test
    void tc4_oppositeKeyIgnored() throws Exception {
        setDirection(Board.Direction.RIGHT);
        var adapter = board.new TAdapter();
        adapter.keyPressed(new KeyEvent(board, 0, 0, 0, KeyEvent.VK_LEFT, ' '));
        assertEquals(Board.Direction.RIGHT, getDirection());
    }

    @Test
    void tc5_allowedKeyApplied() throws Exception {
        setDirection(Board.Direction.RIGHT);
        var adapter = board.new TAdapter();
        adapter.keyPressed(new KeyEvent(board, 0, 0, 0, KeyEvent.VK_UP, ' '));
        assertEquals(Board.Direction.UP, getDirection());
    }

    @Test
    void tc6_plainTickMoves() throws Exception {
        int[] x = getArray("x");
        int[] y = getArray("y");
        int oldX = x[0], oldY = y[0];
        setDirection(Board.Direction.RIGHT);

        tick();

        assertTrue(getBool("inGame"));
        assertEquals(oldX + 10, x[0]);
        assertEquals(oldY, y[0]);
    }

    @Test
    void tc7_noWorkWhenGameOver() throws Exception {
        int[] x = getArray("x");
        int[] y = getArray("y");
        int ox = x[0], oy = y[0];
        setBool("inGame", false);

        tick();

        assertEquals(ox, x[0]);
        assertEquals(oy, y[0]);
    }

    @Test
    void tc8_appleWithinBoundsAndOnGrid() throws Exception {
        var m = Board.class.getDeclaredMethod("locateApple");
        m.setAccessible(true);
        for (int i = 0; i < 200; i++) {
            m.invoke(board);
            int ax = board.getAppleX();
            int ay = board.getAppleY();
            assertTrue(ax >= 0 && ax <= 290);
            assertTrue(ay >= 0 && ay <= 290);
            assertEquals(0, ax % 10);
            assertEquals(0, ay % 10);
        }
    }
}