import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

class Board extends JPanel implements ActionListener {
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int DELAY = 140;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int currentLevel = 1;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    private final int[][] level3Obstacles = {
            {50, 50}, {100, 100}, {150, 150}, {200, 200}
    };

    private Runnable exitToDashboard;

    public Board() {
        this.exitToDashboard = exitToDashboard;
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        loadImages();
        initGame();

        addKeyListener(new TAdapter());
    }

    private void loadImages() {
        ball = new ImageIcon(getClass().getResource("/resources/dot.png")).getImage();
        apple = new ImageIcon(getClass().getResource("/resources/apple.png")).getImage();
        head = new ImageIcon(getClass().getResource("/resources/head.png")).getImage();
    }

    private void initGame() {
        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * DOT_SIZE;
            y[z] = 50;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw the apple
            g.drawImage(apple, apple_x, apple_y, this);

            // Draw the snake
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            // Draw obstacles for Level 3
            if (currentLevel == 3) {
                g.setColor(Color.red);
                for (int[] obstacle : level3Obstacles) {
                    g.fillRect(obstacle[0], obstacle[1], DOT_SIZE, DOT_SIZE);
                }
            }

            if (currentLevel == 4) {
                g.setColor(Color.red);
                for (int[] obstacle : level3Obstacles) {
                    g.fillRect(obstacle[0], obstacle[1], 10, 40);
                }
            }

            if(currentLevel==5){
                g.setColor(Color.blue);
                g.fillRect(150, 150, 100, 100);
            }

            // Draw scoreboard
            scoreboard(g);

            // Draw border if level is passed
            if (dots >= 10) { // Example threshold for passing a level
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.yellow);

                int borderThickness = 2; // Change this value for thicker or thinner borders
                g2d.setStroke(new BasicStroke(borderThickness));

                g2d.drawRect(
                        borderThickness / 2,
                        borderThickness / 2,
                        B_WIDTH - borderThickness,
                        B_HEIGHT - borderThickness
                );
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

        // Stop the timer and delay the return to the dashboard
        timer.stop();

        Timer delayTimer = new Timer(2000, e -> exitToDashboard.run());
        delayTimer.setRepeats(false);
        delayTimer.start();
    }


    private void scoreboard(Graphics g) {
        String levelMsg = "Level -> " + currentLevel; // Level message
        String scoreMsg = "Score: " + (dots - 3); // Score message (dots beyond initial length)
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);

        // Draw level message centered at the top
        int levelX = (B_WIDTH - metr.stringWidth(levelMsg)) /15;
        int levelY = metr.getHeight();
        g.drawString(levelMsg, levelX, levelY);

        // Draw score message to the top-right corner
        int scoreX = B_WIDTH - metr.stringWidth(scoreMsg) - 10; // Right-align with padding
        int scoreY = metr.getHeight();
        g.drawString(scoreMsg, scoreX, scoreY);
    }



    private void checkApple() {
        if (x[0] == apple_x && y[0] == apple_y) {
            dots++;
            locateApple();
            if (dots == 10) currentLevel = 2; // Move to Level 2
            if (dots == 25) currentLevel = 3; // Move to Level 3
            if (dots == 37) currentLevel = 4; // Move to Level 4
            if (dots == 50) currentLevel = 5; // Move to Level 5
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[z - 1];
            y[z] = y[z - 1];
        }

        if (leftDirection) x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection) y[0] -= DOT_SIZE;
        if (downDirection) y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (currentLevel == 1) {
            if (x[0] >= B_WIDTH) x[0] = 0;
            if (x[0] < 0) x[0] = B_WIDTH - DOT_SIZE;
            if (y[0] >= B_HEIGHT) y[0] = 0;
            if (y[0] < 0) y[0] = B_HEIGHT - DOT_SIZE;
        } else if (currentLevel >= 2) {
            if (x[0] >= B_WIDTH || x[0] < 0 || y[0] >= B_HEIGHT || y[0] < 0) {
                inGame = false;
            }
        }

        if (currentLevel == 3) {
            for (int[] obstacle : level3Obstacles) {
                if (x[0] == obstacle[0] && y[0] == obstacle[1]) {
                    inGame = false;
                }
            }
        }
        if(currentLevel==4){
            for (int[] obstacle : level3Obstacles) {
                if (x[0] == obstacle[0] && y[0] == obstacle[1]) {
                    inGame = false;
                }
            }
        }

        if(currentLevel==5){
            if(x[0]==150 && y[0]==150){
                inGame=false;
            }
        }

        if (!inGame) timer.stop();
    }

    private void locateApple() {
        Set<Point> snakeBody = new HashSet<>();
        for (int i = 0; i < dots; i++) {
            snakeBody.add(new Point(x[i], y[i]));
        }

        do {
            apple_x = (int) (Math.random() * (B_WIDTH / DOT_SIZE)) * DOT_SIZE;
            apple_y = (int) (Math.random() * (B_HEIGHT / DOT_SIZE)) * DOT_SIZE;
        } while (snakeBody.contains(new Point(apple_x, apple_y)));
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

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}