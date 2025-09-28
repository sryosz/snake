package com.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int appleX;
    private int appleY;

    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }


    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGame(g);
    }

    private void drawGame(Graphics g) {

        if (inGame) {

            g.drawImage(apple, getAppleX(), getAppleY(), this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }


    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {

        if ((x[0] == getAppleX()) && (y[0] == getAppleY())) {

            dots++;
            locateApple();
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[z - 1];
            y[z] = y[z - 1];
        }

        switch (currentDirection) {
            case LEFT -> x[0] -= DOT_SIZE;
            case RIGHT -> x[0] += DOT_SIZE;
            case UP -> y[0] -= DOT_SIZE;
            case DOWN -> y[0] += DOT_SIZE;
        }
    }


    private void checkCollision() {
        checkSelfCollision();
        checkWallCollision();

        if (!inGame) {
            timer.stop();
        }
    }

    private void checkSelfCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }
    }

    private void checkWallCollision() {
        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }
    }


    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        appleX = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        appleY = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private Direction currentDirection = Direction.RIGHT;

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (currentDirection != Direction.RIGHT) currentDirection = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (currentDirection != Direction.LEFT) currentDirection = Direction.RIGHT;
                    break;
                case KeyEvent.VK_UP:
                    if (currentDirection != Direction.DOWN) currentDirection = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    if (currentDirection != Direction.UP) currentDirection = Direction.DOWN;
                    break;
            }
        }
    }

}
