public class Board {
    public Square[][] board;
    private int width;
    private int height;
    private int numMines;
    private boolean gameStarted = false;
    private boolean gameEnded = false;

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

    public void updateSquares()
    {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Square square = board[i][j];
                if (square.getIsMine())
                {
                    continue;
                }

                int minesAround = 0;
                for (int a = -1; a < 2; a++)
                {
                    for (int b = -1; b < 2; b++)
                    {
                        if (a == 0 && b == 0)
                        {
                            continue;
                        }
                        if (i + a >= 0 && i + a < width && j + b >= 0 && j + b < height && board[i + a][j + b].getIsMine())
                        {
                            minesAround++;
                        }
                    }
                }
                square.setValue(minesAround);
            }
        }
    }

    public void Click(int x, int y) {
        if (!gameStarted)
        {
            startGame(x, y);
        }

        if (x < 0 || x >= width || y < 0 || y >= height)
        {
            return;
        }

        Square hitSquare = board[x][y];
        if (!hitSquare.getHidden()) {
            // Do nothing, this square has already been unearthed
            return;
        } else if (hitSquare.getIsMine()) {
            hitSquare.unhide();
            endGame();
        } else if (hitSquare.getValue() == 0) {
            hitSquare.unhide();
            // TODO: Unhide all mines around
            for (int a = -1; a < 2; a++){
                for (int b = -1; b < 2; b++){
                    if (a == 0 && b == 0){
                        continue;
                    }
                    Click(x + a, y + b);
                }
            }
        } else {
            hitSquare.unhide();
            // TODO: Unearth this square, it should be safe and continue logic
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

        // Make safe initial squares
        for (int a = -1; a < 2; a++)
        {
            for (int b = -1; b < 2; b++)
            {
                if (x + a >= 0 && x + a < width && y + b >= 0 && y + b < height)
                {
                    board[x + a][y + b].setValue(0);
                }
            }
        }

        int notPlaced = numMines;

        while (notPlaced > 0)
        {
            int randX = (int)(Math.random() * (width));
            int randY = (int)(Math.random() * (height));

            if (!board[randX][randY].getIsMine() && board[randX][randY].getValue() == -1)
            {
                board[randX][randY].setMine();
                notPlaced--;
            }
        }

        updateSquares();
    }

    void endGame()
    {
        //For now just print you lost
        System.out.println("you have lost");
        gameEnded = true;
    }

    public boolean isEnded(){
        return gameEnded;
    }
}
