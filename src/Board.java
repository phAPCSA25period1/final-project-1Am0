public class Board {
    private Square[][] board;
    private int width;
    private int height;
    private int numMines = 25;

    public Board(int size)
    {
        width = size;
        height = size;
        board = new Square[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                board[i][j] = new Square();
            }
        }

        
    }

    public void Click(int x, int y)
    {
        Square hitSquare = board[x][y];
        if (!hitSquare.getHidden())
        {
            // Do nothing, this square has already been unearthed
            return;
        }
        else if (hitSquare.getIsMine())
        {
            // TODO: End the game, the player has clicked on a mine
        }
        else
        {
            // TODO: Unearth this square, it should be safe and continue logic
        }
    }

    public void printBoard()
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
