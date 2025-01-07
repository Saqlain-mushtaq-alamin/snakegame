import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DashboardPage dashboardPage = new DashboardPage(frame);
            frame.add(dashboardPage);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class DashboardPage extends JPanel {
    private JFrame parentFrame;


    public DashboardPage(JFrame frame) {
        this.parentFrame = frame;
        setBackground(Color.darkGray);


        // Load the background image
        add(new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/background.jpg")))));



        // Set preferred size for the panel
        setPreferredSize(new Dimension(300, 300));

        JButton startButton = new JButton("Start the Game");
        startButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            Board gameBoard = new Board();
            parentFrame.add(gameBoard);
            parentFrame.revalidate();
            parentFrame.repaint();
            gameBoard.requestFocusInWindow();
        });

        // Use GridBagLayout for centering the button
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(startButton, gbc);
    }


}



