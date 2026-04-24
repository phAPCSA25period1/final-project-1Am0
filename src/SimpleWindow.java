import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        JPanel panel = new JPanel(new GridLayout(25, 25, 0, 0));

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                final int finalI = i;
                final int finalJ = j;

                JButton button = new JButton("3");

                button.setMargin(new java.awt.Insets(0, 0, 0, 0));

                button.setBorderPainted(false);
                button.setFocusPainted(false);

                button.setFocusable(false);       // Stops the blue "selection" highlight
                button.setRolloverEnabled(false);  // Stops the button from lighting up when hovered
                button.setContentAreaFilled(false); // Disables the default "button" shading/animation
                button.setOpaque(true);            // IMPORTANT: Makes sure your background colors still show up

                button.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

                button.setBounds(i * 24, j * 24, 24, 24);

                buttons[finalI][finalJ] = button;

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
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
                JButton button = buttons[i][j];

                String newText = b.board[i][j].toString();

                if (newText.equals("#"))
                {
                    button.setText("");
                    if ((i + j) % 2 == 0)
                    {
                        button.setBackground(new Color(63, 224, 63));
                    }
                    else
                    {
                        button.setBackground(new Color(97, 237, 97));
                    }
                }
                else
                {
                    button.setText(newText);
                    if ((i + j) % 2 == 0)
                    {
                        button.setBackground(new Color(200, 217, 115));
                    }
                    else
                    {
                        button.setBackground(new Color(233, 252, 134));
                    }
                }

                if (newText.equals("X"))
                {
                    button.setBackground(Color.RED);
                }
            }
        }
    }
}
