import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your dimensions (ex: 30 25): ");

        int width = scanner.nextInt();
        int height = scanner.nextInt();

        Board b = new Board(width, height);

        b.printBoard();

        String in = "";

        while (!in.equals("quit")) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();

            b.Click(x, y);

            b.printBoard();

            if (b.isEnded()) {
                if (b.isWon()) {
                    System.out.println("you have won");
                } else {
                    System.out.println("you have lost");
                }
                break;
            }
        }

        scanner.close();
    }
}
