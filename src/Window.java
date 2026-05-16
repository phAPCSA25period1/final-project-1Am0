import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Manages the GUI for Minesweeper using Java Swing.
 *
 * Handles:
 * - Game window creation and rendering
 * - Button grid layout and responsiveness
 * - Mouse input (left/right click)
 * - Visual updates for game state
 * - Status bar (timer, flags remaining)
 * - Game-over animations and dialogs
 * - Difficulty selection
 */

// Custom layout manager that maintains aspect ratio
class AspectRatioLayout implements java.awt.LayoutManager {
    private double aspectRatio;

    public AspectRatioLayout(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    public void addLayoutComponent(String name, java.awt.Component comp) {
    }

    @Override
    public void removeLayoutComponent(java.awt.Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(java.awt.Container parent) {
        return new Dimension(800, (int) (800 / aspectRatio));
    }

    @Override
    public Dimension minimumLayoutSize(java.awt.Container parent) {
        return new Dimension(200, (int) (200 / aspectRatio));
    }

    @Override
    public void layoutContainer(java.awt.Container parent) {
        if (parent.getComponentCount() == 0)
            return;

        int containerWidth = parent.getWidth();
        int containerHeight = parent.getHeight();
        double currentAspect = (double) containerWidth / containerHeight;

        int compWidth = containerWidth;
        int compHeight = containerHeight;

        if (currentAspect > aspectRatio) {
            // Too wide, constrain by height
            compWidth = (int) (containerHeight * aspectRatio);
        } else {
            // Too tall, constrain by width
            compHeight = (int) (containerWidth / aspectRatio);
        }

        int x = (containerWidth - compWidth) / 2;
        int y = (containerHeight - compHeight) / 2;

        java.awt.Component comp = parent.getComponent(0);
        comp.setBounds(x, y, compWidth, compHeight);
    }
}

// Custom panel that maintains aspect ratio
class AspectRatioPanel extends JPanel {
    public AspectRatioPanel(double aspectRatio) {
        setLayout(new AspectRatioLayout(aspectRatio));
    }
}

public class Window {
    public static JButton[][] buttons;
    public static Board b;
    static int width;
    static int height;
    static int buttonSize;
    static int fontSize;
    static JFrame gameFrame;
    static JPanel statusBar;
    static JLabel timeLabel;
    static JLabel flagsLabel;
    static Timer statusTimer;
    static Timer gameOverTimer;
    static int elapsedSeconds;
    static int currentDifficulty;
    static boolean gameOverAnimationRunning;

    public static void main(String[] args) {
        // Create difficulty selection screen
        JFrame difficultyFrame = new JFrame("Minesweeper - Select Difficulty");
        difficultyFrame.setSize(400, 200);
        difficultyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel difficultyPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficultyFrame.dispose();
                startGame(0);
            }
        });

        mediumButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficultyFrame.dispose();
                startGame(1);
            }
        });

        hardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficultyFrame.dispose();
                startGame(2);
            }
        });

        difficultyPanel.add(easyButton);
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(hardButton);

        difficultyFrame.add(difficultyPanel);
        difficultyFrame.setVisible(true);
    }

    public static void startGame(int difficulty) {
        currentDifficulty = difficulty;
        elapsedSeconds = 0;
        gameOverAnimationRunning = false;

        if (gameOverTimer != null) {
            gameOverTimer.stop();
            gameOverTimer = null;
        }

        if (difficulty == 0) {
            width = 12;
            height = 8;
        } else if (difficulty == 1) {
            width = 18;
            height = 12;
        } else {
            width = 27;
            height = 18;
        }

        gameFrame = new JFrame("My First Window");
        gameFrame.setLayout(new BorderLayout());
        createStatusBar();

        // Get screen dimensions and calculate available space
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxScreenWidth = (int) (screenSize.width * 0.95); // Leave 5% margin
        int maxScreenHeight = (int) (screenSize.height * 0.85); // Leave 15% margin for taskbar and window decorations

        // Account for window decorations (approximately 30-40 pixels for title bar, 2-4
        // pixels for borders)
        maxScreenHeight -= 70;
        maxScreenWidth -= 10;

        // Calculate button size to fit on screen while keeping squares square
        int buttonSizeByWidth = maxScreenWidth / width;
        int buttonSizeByHeight = maxScreenHeight / height;
        buttonSize = Math.min(buttonSizeByWidth, buttonSizeByHeight);

        // Calculate frame size based on button size
        int frameWidth = buttonSize * width;
        int frameHeight = buttonSize * height;

        // Calculate font size proportional to button size - larger multiplier for
        // bigger text
        fontSize = Math.max(12, (int) (buttonSize * 0.45));

        int statusBarHeight = Math.max(70, (int) (fontSize * 2.5));
        gameFrame.setSize(frameWidth, frameHeight + statusBarHeight);

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(true);

        b = new Board(width, height);
        updateStatusLabels();

        buttons = new JButton[width][height];

        // Create inner panel with GridLayout
        final JPanel gridPanel = new JPanel(new GridLayout(height, width, 0, 0));

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                final int finalI = i;
                final int finalJ = j;

                JButton button = new JButton("3");

                button.setMargin(new java.awt.Insets(0, 0, 0, 0));

                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.decode("#3d4424"), 1));
                button.setFocusPainted(false);

                button.setFocusable(false); // Stops the blue "selection" highlight
                button.setRolloverEnabled(false); // Hover is handled manually for a custom effect
                button.setContentAreaFilled(false); // Disables the default "button" shading/animation
                button.setOpaque(true); // IMPORTANT: Makes sure your background colors still show up

                button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));

                buttons[finalI][finalJ] = button;

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            b.Click(finalI, finalJ);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            b.Flag(finalI, finalJ);
                        }
                        startTimerIfNeeded();
                        b.printBoard();
                        updateVisuals();
                        updateStatusLabels();
                        if (b.isEnded() && !gameOverAnimationRunning) {
                            startGameOverAnimation();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!gameOverAnimationRunning) {
                            applyButtonAppearance(finalI, finalJ, true);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (!gameOverAnimationRunning) {
                            applyButtonAppearance(finalI, finalJ, false);
                        }
                    }
                });

                gridPanel.add(button);
            }
        }

        // Create aspect ratio panel to wrap the grid
        double aspectRatio = (double) width / height;
        AspectRatioPanel panel = new AspectRatioPanel(aspectRatio);
        panel.add(gridPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(statusBar, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        gameFrame.add(mainPanel, BorderLayout.CENTER);

        // Add component listener to handle window resize
        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeButtons(gridPanel);
            }
        });

        updateVisuals();

        gameFrame.setVisible(true);

        System.out.println("started");
    }

    public static void resizeButtons(JPanel gridPanel) {
        // Get the grid panel's actual size
        int panelWidth = gridPanel.getWidth();
        int panelHeight = gridPanel.getHeight();

        if (panelWidth <= 0 || panelHeight <= 0)
            return;

        // Calculate button size from the actual rendered size
        int newButtonSize = Math.min(panelWidth / width, panelHeight / height);

        if (newButtonSize > 0) {
            buttonSize = newButtonSize;
            fontSize = Math.max(12, (int) (buttonSize * 0.45));

            // Update all buttons with new font size
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    JButton button = buttons[i][j];
                    button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));
                }
            }

            // Revalidate and repaint the panel
            gridPanel.revalidate();
            gridPanel.repaint();
        }
    }

    public static void updateVisuals() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                applyButtonAppearance(i, j, false);
            }
        }
    }

    public static void applyButtonAppearance(int x, int y, boolean hovered) {
        JButton button = buttons[x][y];
        Square square = b.board[x][y];

        boolean hidden = square.getHidden();
        boolean flagged = square.isFlagged();
        boolean mine = square.getIsMine();
        int value = square.getValue();

        boolean won = b != null && b.isEnded() && b.isWon();
        boolean lost = b != null && b.isEnded() && !b.isWon();

        String displayText = "";
        Color backgroundColor;
        Color foregroundColor = Color.BLACK;

        if (gameOverAnimationRunning && lost && mine) {
            displayText = "✹";
            backgroundColor = hovered ? Color.decode("#ff6b6b") : Color.decode("#d64545");
            foregroundColor = Color.WHITE;
        } else if (gameOverAnimationRunning && won && !mine && !hidden) {
            displayText = value > 0 ? String.valueOf(value) : "";
            backgroundColor = hovered ? Color.decode("#f6ddb0") : Color.decode("#ead391");
            foregroundColor = getNumberColor(value);
        } else if (hidden) {
            Color baseHidden = ((x + y) % 2 == 0) ? Color.decode("#67cf5a") : Color.decode("#58be4a");
            if (flagged) {
                displayText = "⚑";
                backgroundColor = hovered ? baseHidden.brighter() : baseHidden;
                foregroundColor = Color.decode("#c62828");
            } else {
                displayText = "";
                backgroundColor = hovered ? baseHidden.brighter() : baseHidden;
                foregroundColor = Color.WHITE;
            }
        } else if (mine) {
            displayText = "✹";
            backgroundColor = Color.decode("#8d2f2f");
            foregroundColor = Color.WHITE;
        } else {
            displayText = value > 0 ? String.valueOf(value) : "";
            backgroundColor = hovered ? Color.decode("#f2e1b9") : Color.decode("#ddd0a4");
            foregroundColor = getNumberColor(value);
        }

        button.setText(displayText);
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setBorder(BorderFactory.createLineBorder(backgroundColor.darker(), 1));
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, Math.max(8, fontSize)));
    }

    public static Color getNumberColor(int value) {
        switch (value) {
            case 1:
                return Color.decode("#1e5eff");
            case 2:
                return Color.decode("#1b8f3a");
            case 3:
                return Color.decode("#c62828");
            case 4:
                return Color.decode("#0d47a1");
            case 5:
                return Color.decode("#6a1b9a");
            case 6:
                return Color.decode("#00838f");
            case 7:
                return Color.decode("#263238");
            case 8:
                return Color.decode("#455a64");
            default:
                return Color.BLACK;
        }
    }

    public static void startGameOverAnimation() {
        if (gameOverAnimationRunning) {
            return;
        }

        gameOverAnimationRunning = true;
        stopStatusTimer();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buttons[i][j].setEnabled(false);
            }
        }

        final boolean won = b.isWon();
        final int[] frame = { 0 };

        updateGameOverFrame(won, true);

        gameOverTimer = new Timer(140, e -> {
            frame[0]++;
            updateGameOverFrame(won, frame[0] % 2 == 0);

            if (frame[0] >= 6) {
                ((Timer) e.getSource()).stop();
                gameOverAnimationRunning = false;
                handleGameEnd();
            }
        });

        gameOverTimer.start();
    }

    public static void updateGameOverFrame(boolean won, boolean highlightPhase) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                JButton button = buttons[i][j];
                Square square = b.board[i][j];

                boolean hidden = square.getHidden();
                boolean mine = square.getIsMine();
                int value = square.getValue();

                String displayText = button.getText();
                Color backgroundColor = button.getBackground();
                Color foregroundColor = button.getForeground();

                if (won && !mine) {
                    displayText = value > 0 ? String.valueOf(value) : "";
                    backgroundColor = highlightPhase ? Color.decode("#f0d58c") : Color.decode("#ddd0a4");
                    foregroundColor = getNumberColor(value);
                } else if (!won && mine) {
                    displayText = "✹";
                    backgroundColor = highlightPhase ? Color.decode("#ff7676") : Color.decode("#c84141");
                    foregroundColor = Color.WHITE;
                } else if (hidden && !square.isFlagged()) {
                    Color baseHidden = ((i + j) % 2 == 0) ? Color.decode("#67cf5a") : Color.decode("#58be4a");
                    displayText = "";
                    backgroundColor = highlightPhase ? baseHidden.brighter() : baseHidden;
                    foregroundColor = Color.WHITE;
                }

                if (square.isFlagged() && hidden) {
                    displayText = "⚑";
                    Color baseHidden = ((i + j) % 2 == 0) ? Color.decode("#67cf5a") : Color.decode("#58be4a");
                    backgroundColor = highlightPhase ? baseHidden.brighter() : baseHidden;
                    foregroundColor = Color.decode("#c62828");
                }

                button.setText(displayText);
                button.setBackground(backgroundColor);
                button.setForeground(foregroundColor);
                button.setBorder(BorderFactory.createLineBorder(backgroundColor.darker(), 1));
            }
        }
    }

    public static void handleGameEnd() {
        boolean won = b.isWon();
        String result = won ? "You Won!" : "You Lost!";
        String message = won
                ? buildGameOverMessage("You cleared all safe squares!\n\nWhat would you like to do?")
                : buildGameOverMessage("You Hit a Mine! Game Over!\n\nWhat would you like to do?");

        // Show dialog with options
        String[] options = { "Play Again", "New Game", "Exit" };
        int choice = JOptionPane.showOptionDialog(
                gameFrame,
                message,
                result,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            // Play Again - restart the same difficulty
            gameFrame.dispose();
            startGame(currentDifficulty);
        } else if (choice == 1) {
            // New Game - go back to difficulty selection
            gameFrame.dispose();
            main(new String[] {});
        } else {
            // Exit or closed dialog
            gameFrame.dispose();
        }
    }

    public static void createStatusBar() {
        if (statusTimer != null) {
            statusTimer.stop();
        }

        statusBar = new JPanel(new GridLayout(1, 2, 12, 0));
        int statusBarHeight = Math.max(60, (int) (fontSize * 3.5));
        statusBar.setPreferredSize(new Dimension(0, statusBarHeight));
        statusBar.setBackground(Color.decode("#d8e4c5"));
        statusBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#3d4424")));

        timeLabel = new JLabel("Time: 00:00");
        flagsLabel = new JLabel("Flags Left: 0");

        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        flagsLabel.setHorizontalAlignment(JLabel.CENTER);
        int statusFontSize = Math.max(14, (int) (fontSize * 1.2));
        timeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, statusFontSize));
        flagsLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, statusFontSize));

        statusBar.add(timeLabel);
        statusBar.add(flagsLabel);

        statusTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            updateStatusLabels();
        });

        updateStatusLabels();
    }

    public static void startTimerIfNeeded() {
        if (b != null && b.isStarted() && statusTimer != null && !statusTimer.isRunning()) {
            statusTimer.start();
        }
    }

    public static void stopStatusTimer() {
        if (statusTimer != null) {
            statusTimer.stop();
        }
        updateStatusLabels();
    }

    public static void updateStatusLabels() {
        if (timeLabel != null) {
            timeLabel.setText("Time: " + formatTime(elapsedSeconds));
        }
        if (flagsLabel != null && b != null) {
            flagsLabel.setText("Flags Left: " + b.getFlagsRemaining());
        }
    }

    public static String buildGameOverMessage(String baseMessage) {
        return baseMessage + "\nTime Elapsed: " + formatTime(elapsedSeconds) + "\nFlags Left: " + b.getFlagsRemaining();
    }

    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
