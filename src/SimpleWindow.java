import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class SimpleWindow {
    public static JButton[][] buttons;
    public static Board b;

    public static void main(String[] args) {
        JFrame frame = new JFrame("My First Window");

        frame.setSize(600, 600);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        b = new Board(25, 25);

        buttons = new JButton[25][25];

        JPanel panel = new JPanel(new GridLayout(25, 25));

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                final int finalI = i;
                final int finalJ = j;

                JButton button = new JButton("3");

                button.setMargin(new java.awt.Insets(0, 0, 0, 0));

                button.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

                button.setBounds(i * 24, j * 24, 24, 24);

                buttons[finalI][finalJ] = button;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        b.Click(finalI, finalJ);
                        b.printBoard();
                        updateVisuals();
                    }
                });

                panel.add(button);
            }
        }

        frame.add(panel);

        updateVisuals();

        frame.setVisible(true);

        System.out.println("started");
    }

    public static void updateVisuals()
    {
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                final int finalI = i;
                final int finalJ = j;

                JButton button = buttons[i][j];

                String newText = b.board[i][j].toString();

                if (newText.equals("#"))
                {
                    button.setText("");
                    button.setBackground(Color.LIGHT_GRAY);
                }
                else
                {
                    button.setBackground(Color.WHITE);
                    button.setText(newText);
                }

                if (newText.equals("X"))
                {
                    button.setBackground(Color.RED);
                }
            }
        }
    }
}
