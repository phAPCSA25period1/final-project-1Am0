import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SimpleWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My First Window");

        frame.setSize(600, 600);
        frame.setLayout(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board b = new Board(25, 25);

        for (int i = 0; i < 25; i++){
            for (int j = 0; j < 25; j++){
                final int finalI = i;
                final int finalJ = j;

                JButton button = new JButton("#");

                button.setBounds(i * 24, j * 24, 24, 24);

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        b.Click(finalI, finalJ);
                        b.printBoard();
                    }
                });

                frame.add(button);
            }
        }


        frame.setVisible(true);

        System.out.println("started");
    }
}
