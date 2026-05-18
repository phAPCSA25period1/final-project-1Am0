/**
 * Represents the Minesweeper game board.
 *
 * Manages:
 * - Grid of squares with mines and their adjacent mine counts
 * - Game state (started, ended, won/lost)
 * - Click and flag logic
 * - Mine placement and safe first click guarantee
 */
public class Board {
    public Square[][] board;
    private int width;
    private int height;
    private int numMines;
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private boolean gameWon = false;

    /**
     * Constructs a Board with specified dimensions.
     * Initializes the board with empty squares and calculates mine count
     * based on standard Minesweeper mine density (20.6%).
     *
     * @param w the width of the board (number of columns)
     * @param h the height of the board (number of rows)
     */
    public Board(int w, int h) {
        width = w;
        height = h;
        // Standard mine density for Minesweeper is approximately 20.6%
        numMines = (int) (width * height * 0.206);
        board = new Square[width][height];

        // Initialize all squares on the board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = new Square();
            }
        }
    }

    /**
     * Updates all non-mine squares with the count of adjacent mines.
     * For each non-mine square, counts how many of its 8 neighbors contain mines.
     * This count is displayed to the player when the square is revealed.
     */
    public void updateSquares() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Square square = board[i][j];
                // Skip mine squares; their value is not used in gameplay
                if (square.getIsMine()) {
                    continue;
                }

                // Count mines in all 8 adjacent squares
                int minesAround = 0;
                for (int a = -1; a < 2; a++) {
                    for (int b = -1; b < 2; b++) {
                        // Skip the center square (the current square)
                        if (a == 0 && b == 0) {
                            continue;
                        }
                        // Check bounds and count if neighbor is a mine
                        if (i + a >= 0 && i + a < width && j + b >= 0 && j + b < height
                                && board[i + a][j + b].getIsMine()) {
                            minesAround++;
                        }
                    }
                }
                square.setValue(minesAround);
            }
        }
    }

    /**
     * Handles left-mouse click on a square at the given coordinates.
     * On first click, initializes the game board.
     * Reveals the clicked square and potentially its neighbors (flood-fill
     * algorithm).
     * Ends the game if a mine is clicked.
     *
     * @param x the column coordinate of the clicked square
     * @param y the row coordinate of the clicked square
     */
    public void Click(int x, int y) {
        // Ignore clicks if the game has already ended
        if (gameEnded) {
            return;
        }

        // Start the game on the first click, ensuring this location is safe
        if (!gameStarted) {
            startGame(x, y);
        }

        // Validate coordinates are within board bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        Square hitSquare = board[x][y];
        // Ignore clicks on already-revealed squares
        if (!hitSquare.getHidden()) {
            return;
        }

        // Remove flag if the square was flagged
        if (hitSquare.isFlagged()) {
            hitSquare.unflag();
        }

        // Handle different square states
        if (hitSquare.getIsMine()) {
            // Player hit a mine - reveal it and end the game in loss
            hitSquare.unhide();
            endGame(false);
        } else if (hitSquare.getValue() == 0) {
            // Square has no adjacent mines - use flood-fill to reveal neighbors
            hitSquare.unhide();
            for (int a = -1; a < 2; a++) {
                for (int b = -1; b < 2; b++) {
                    if (a == 0 && b == 0) {
                        continue;
                    }
                    Click(x + a, y + b);
                }
            }
            checkForWin();
        } else {
            // Square has adjacent mines but is not a mine
            hitSquare.unhide();
            checkForWin();
        }
    }

    /**
     * Handles right-mouse click to toggle a flag on a square.
     * On first right-click, initializes the game board.
     * Players use flags to mark suspected mines.
     * Flag count is limited to the total number of mines on the board.
     *
     * @param x the column coordinate of the square to flag/unflag
     * @param y the row coordinate of the square to flag/unflag
     */
    public void Flag(int x, int y) {
        // Ignore flag attempts if the game has ended
        if (gameEnded) {
            return;
        }

        // Start the game on the first flag action
        if (!gameStarted) {
            startGame(x, y);
        }

        // Validate coordinates are within board bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        Square hitSquare = board[x][y];
        // Only allow flagging hidden squares
        if (!hitSquare.getHidden()) {
            return;
        } else if (hitSquare.isFlagged()) {
            // Remove existing flag
            hitSquare.unflag();
        } else if (getFlagsRemaining() > 0) {
            // Place a new flag if player has flags remaining
            hitSquare.flag();
        }
    }

    /**
     * Prints the board state to the console for debugging purposes.
     * Uses the Square toString() method to display:
     * '#' = hidden square
     * 'F' = flagged hidden square
     * 'X' = revealed mine
     * '0'-'8' = revealed square with adjacent mine count
     */
    public void printBoard() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Initializes the game board after the first click.
     * Guarantees a safe zone around the clicked square.
     * Places mines randomly while avoiding the safe zone.
     * Calculates adjacent mine counts for all non-mine squares.
     *
     * @param x the column coordinate of the first click
     * @param y the row coordinate of the first click
     */
    public void startGame(int x, int y) {
        gameStarted = true;

        // Create a safe zone around the first click to guarantee playability
        boolean[][] safeZone = new boolean[width][height];
        for (int a = -1; a < 2; a++) {
            for (int b = -1; b < 2; b++) {
                int safeX = x + a;
                int safeY = y + b;

                if (safeX >= 0 && safeX < width && safeY >= 0 && safeY < height) {
                    safeZone[safeX][safeY] = true;
                }
            }
        }

        // Calculate target safe area size to create opening paths
        int totalSquares = width * height;
        int maxSafeSquares = Math.max(1, Math.min(totalSquares - numMines, Math.max(4, totalSquares / 8)));
        int minSafeSquares = Math.max(1, Math.min(maxSafeSquares, Math.max(9, totalSquares / 40)));
        int targetSafeSquares = minSafeSquares + (int) (Math.random() * (maxSafeSquares - minSafeSquares + 1));

        // Expand safe zone using recursive carving algorithm
        int carvedSafeSquares = countSafeSquares(safeZone);
        carvedSafeSquares = carveSafeArea(x, y, safeZone, targetSafeSquares, carvedSafeSquares);

        // Place mines randomly outside the safe zone
        int notPlaced = numMines;
        while (notPlaced > 0) {
            int randX = (int) (Math.random() * (width));
            int randY = (int) (Math.random() * (height));

            boolean inSafeStartArea = safeZone[randX][randY];

            // Place mine if location is safe, unoccupied, and uninitialized
            if (!inSafeStartArea && !board[randX][randY].getIsMine() && board[randX][randY].getValue() == -1) {
                board[randX][randY].setMine();
                notPlaced--;
            }
        }

        // Calculate adjacent mine counts for all squares
        updateSquares();
    }

    /**
     * Recursively expands the safe zone from the clicked square.
     * Uses depth-first search with random direction shuffling to create
     * varied, natural-looking safe areas without mines.
     *
     * @param x                 the current x coordinate
     * @param y                 the current y coordinate
     * @param safeZone          the 2D array tracking which squares are in the safe
     *                          zone
     * @param targetSafeSquares the target number of safe squares to carve
     * @param carvedSafeSquares the current count of safe squares
     * @return the updated count of carved safe squares
     */
    private int carveSafeArea(int x, int y, boolean[][] safeZone, int targetSafeSquares, int carvedSafeSquares) {
        // Stop recursion if target is reached
        if (carvedSafeSquares >= targetSafeSquares) {
            return carvedSafeSquares;
        }

        // Stop recursion if coordinates are out of bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return carvedSafeSquares;
        }

        // Mark this square as safe if not already marked
        if (!safeZone[x][y]) {
            safeZone[x][y] = true;
            carvedSafeSquares++;
        }

        // Define all 8 adjacent directions (excluding center)
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        // Shuffle directions using Fisher-Yates algorithm for randomness
        for (int i = directions.length - 1; i > 0; i--) {
            int swapIndex = (int) (Math.random() * (i + 1));
            int[] temp = directions[i];
            directions[i] = directions[swapIndex];
            directions[swapIndex] = temp;
        }

        for (int[] direction : directions) {
            carvedSafeSquares = carveSafeArea(x + direction[0], y + direction[1], safeZone, targetSafeSquares,
                    carvedSafeSquares);

            if (carvedSafeSquares >= targetSafeSquares) {
                return carvedSafeSquares;
            }
        }

        return carvedSafeSquares;
    }

    /**
     * Counts the number of squares currently marked as safe.
     *
     * @param safeZone the 2D boolean array tracking safe squares
     * @return the total count of safe squares
     */
    private int countSafeSquares(boolean[][] safeZone) {
        int safeSquares = 0;

        // Iterate through entire board and count marked safe squares
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (safeZone[i][j]) {
                    safeSquares++;
                }
            }
        }

        return safeSquares;
    }

    /**
     * Returns the total number of mines on this board.
     *
     * @return the number of mines
     */
    public int getNumMines() {
        return numMines;
    }

    /**
     * Counts the number of flags currently placed on the board.
     *
     * @return the number of flagged squares
     */
    public int getFlagsPlaced() {
        int flagsPlaced = 0;

        // Count all flagged squares on the board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].isFlagged()) {
                    flagsPlaced++;
                }
            }
        }

        return flagsPlaced;
    }

    /**
     * Calculates how many more flags the player can place.
     * This is the total number of mines minus flags already placed.
     *
     * @return the number of flags remaining to place
     */
    public int getFlagsRemaining() {
        return numMines - getFlagsPlaced();
    }

    /**
     * Checks if the game has been started.
     *
     * @return true if the game has started, false otherwise
     */
    public boolean isStarted() {
        return gameStarted;
    }

    /**
     * Ends the game and records whether the player won or lost.
     *
     * @param won true if the player won, false if they lost
     */
    public void endGame(boolean won) {
        gameWon = won;
        gameEnded = true;
    }

    /**
     * Checks if the player has won by revealing all non-mine squares.
     * If the player has revealed all safe squares, ends the game in victory.
     */
    public void checkForWin() {
        // Don't check for win if game has already ended
        if (gameEnded) {
            return;
        }

        // Check if any non-mine squares remain hidden
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // If we find a non-mine that's still hidden, the game continues
                if (!board[i][j].getIsMine() && board[i][j].getHidden()) {
                    return;
                }
            }
        }

        // All safe squares have been revealed - player wins!
        endGame(true);
    }

    /**
     * Checks if the game has ended (either win or loss).
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean isEnded() {
        return gameEnded;
    }

    /**
     * Checks if the player has won the game.
     *
     * @return true if the player won, false otherwise
     */
    public boolean isWon() {
        return gameWon;
    }
}
