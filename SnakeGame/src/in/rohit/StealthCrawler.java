package in.rohit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class StealthCrawler extends JPanel implements ActionListener {
    private final int UNIT_SIZE = 24;
    private final int SCREEN_WIDTH = 624;
    private final int SCREEN_HEIGHT = 624;
    private final int MAX_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final int SPEED = 120; 

    private final int[] xPos = new int[MAX_UNITS];
    private final int[] yPos = new int[MAX_UNITS];

    private int length = 5;
    private int foodX;
    private int foodY;
    private int score = 0;
    private boolean gameRunning = false;
    private boolean inputLock = false;
    private char movement = 'R';
    private Timer timer;
    private Random randomizer;

    public StealthCrawler() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(15, 15, 15)); // Darker Theme
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!inputLock) { 
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_A:
                            if (movement != 'R') movement = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_D:
                            if (movement != 'L') movement = 'R';
                            break;
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_W:
                            if (movement != 'D') movement = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            if (movement != 'U') movement = 'D';
                            break;
                    }
                    inputLock = true;
                }
            }
        });

        startGame();
    }

    private void startGame() {
        randomizer = new Random();
        spawnFood();
        gameRunning = true;
        timer = new Timer(SPEED, this);
        timer.start();
    }

    private void spawnFood() {
        foodX = randomizer.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = randomizer.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameRunning) {
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < length; i++) {
                g.setColor(i == 0 ? Color.CYAN : new Color(0, 150, 200));
                g.fillRect(xPos[i], yPos[i], UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 15, 20);
        } else {
            showGameOver(g);
        }
    }

    private void showGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Consolas", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2);
    }

    private void moveCrawler() {
        for (int i = length; i > 0; i--) {
            xPos[i] = xPos[i - 1];
            yPos[i] = yPos[i - 1];
        }

        switch (movement) {
            case 'U': yPos[0] -= UNIT_SIZE; break;
            case 'D': yPos[0] += UNIT_SIZE; break;
            case 'L': xPos[0] -= UNIT_SIZE; break;
            case 'R': xPos[0] += UNIT_SIZE; break;
        }

        inputLock = false;
    }

    private void checkCollisions() {
        for (int i = length; i > 0; i--) {
            if (xPos[0] == xPos[i] && yPos[0] == yPos[i]) gameRunning = false;
        }
        if (xPos[0] < 0 || xPos[0] >= SCREEN_WIDTH || yPos[0] < 0 || yPos[0] >= SCREEN_HEIGHT) gameRunning = false;
        if (!gameRunning) timer.stop();
    }

    private void checkFoodPickup() {
        if (xPos[0] == foodX && yPos[0] == foodY) {
            length++;
            score += 10;
            spawnFood();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            moveCrawler();
            checkFoodPickup();
            checkCollisions();
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stealth Crawler");
        StealthCrawler game = new StealthCrawler();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}
