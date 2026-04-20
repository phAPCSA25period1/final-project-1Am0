import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board b = new Board(25);

        Scanner scanner = new Scanner(System.in);

        b.printBoard();

        String in = "";

        while (!in.equals("quit"))
        {
            in = scanner.nextLine();

            String[] parts = in.split(" ");
            if (parts.length != 2)
            {
                continue;
            }

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            b.Click(x, y);

            b.printBoard();
        }

        scanner.close();
    }
}
