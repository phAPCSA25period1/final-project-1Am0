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

    public Board(int w, int h) {
        width = w;
        height = h;
        // Standard mine density
        numMines = (int) (width * height * 0.206);
        board = new Square[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = new Square();
            }
        }
    }

    public void updateSquares() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Square square = board[i][j];
                if (square.getIsMine()) {
                    continue;
                }

                int minesAround = 0;
                for (int a = -1; a < 2; a++) {
                    for (int b = -1; b < 2; b++) {
                        if (a == 0 && b == 0) {
                            continue;
                        }
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

    public void Click(int x, int y) {
        if (gameEnded) {
            return;
        }

        if (!gameStarted) {
            startGame(x, y);
        }

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        Square hitSquare = board[x][y];
        if (!hitSquare.getHidden()) {
            // Do nothing, this square has already been unearthed
            return;
        }

        // If flagged, remove the flag first
        if (hitSquare.isFlagged()) {
            hitSquare.unflag();
        }

        if (hitSquare.getIsMine()) {
            hitSquare.unhide();
            endGame(false);
        } else if (hitSquare.getValue() == 0) {
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
            hitSquare.unhide();
            checkForWin();
        }
    }

    public void Flag(int x, int y) {
        if (gameEnded) {
            return;
        }

        if (!gameStarted) {
            startGame(x, y);
        }

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        Square hitSquare = board[x][y];
        if (!hitSquare.getHidden()) {
            // Do nothing, this square has already been unearthed
            return;
        } else if (hitSquare.isFlagged()) {
            hitSquare.unflag();
        } else if (getFlagsRemaining() > 0) {
            hitSquare.flag();
        }
    }

    public void printBoard() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void startGame(int x, int y) {
        gameStarted = true;

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

        int totalSquares = width * height;
        int maxSafeSquares = Math.max(1, Math.min(totalSquares - numMines, Math.max(4, totalSquares / 8)));
        int minSafeSquares = Math.max(1, Math.min(maxSafeSquares, Math.max(9, totalSquares / 40)));
        int targetSafeSquares = minSafeSquares + (int) (Math.random() * (maxSafeSquares - minSafeSquares + 1));

        int carvedSafeSquares = countSafeSquares(safeZone);
        carvedSafeSquares = carveSafeArea(x, y, safeZone, targetSafeSquares, carvedSafeSquares);

        int notPlaced = numMines;

        while (notPlaced > 0) {
            int randX = (int) (Math.random() * (width));
            int randY = (int) (Math.random() * (height));

            boolean inSafeStartArea = safeZone[randX][randY];

            if (!inSafeStartArea && !board[randX][randY].getIsMine() && board[randX][randY].getValue() == -1) {
                board[randX][randY].setMine();
                notPlaced--;
            }
        }

        updateSquares();
    }

    private int carveSafeArea(int x, int y, boolean[][] safeZone, int targetSafeSquares, int carvedSafeSquares) {
        if (carvedSafeSquares >= targetSafeSquares) {
            return carvedSafeSquares;
        }

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return carvedSafeSquares;
        }

        if (!safeZone[x][y]) {
            safeZone[x][y] = true;
            carvedSafeSquares++;
        }

        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

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

    private int countSafeSquares(boolean[][] safeZone) {
        int safeSquares = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (safeZone[i][j]) {
                    safeSquares++;
                }
            }
        }

        return safeSquares;
    }

    public int getNumMines() {
        return numMines;
    }

    public int getFlagsPlaced() {
        int flagsPlaced = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].isFlagged()) {
                    flagsPlaced++;
                }
            }
        }

        return flagsPlaced;
    }

    public int getFlagsRemaining() {
        return numMines - getFlagsPlaced();
    }

    public boolean isStarted() {
        return gameStarted;
    }

    public void endGame(boolean won) {
        gameWon = won;
        gameEnded = true;
    }

    public void checkForWin() {
        if (gameEnded) {
            return;
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!board[i][j].getIsMine() && board[i][j].getHidden()) {
                    return;
                }
            }
        }

        endGame(true);
    }

    public boolean isEnded() {
        return gameEnded;
    }

    public boolean isWon() {
        return gameWon;
    }
}
