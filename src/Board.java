public class Board {
    private Square[][] board;
    private int width;
    private int height;
    private int numMines;

    public Board(int size) {
        width = size;
        height = size;
        // Standard mine density
        numMines = (int) (width * height * 0.206);
        System.out.println(numMines);
        board = new Square[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = new Square();
                // For now just 15% chance that it will be a mine
                if (Math.random() < 0.15) {
                    board[i][j].setMine();
                }
            }
        }

        updateSquares();
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
        Square hitSquare = board[x][y];
        if (!hitSquare.getHidden()) {
            // Do nothing, this square has already been unearthed
            return;
        } else if (hitSquare.getIsMine()) {
            // TODO: End the game, the player has clicked on a mine
        } else {
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
}
