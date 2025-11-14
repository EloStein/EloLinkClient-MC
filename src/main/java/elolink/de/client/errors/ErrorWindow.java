package elolink.de.client.errors;

import javax.swing.*;
import java.awt.*;

public class ErrorWindow {


    private JFrame frame;
    private static Errors code;
    private JPanel mainPanel;
    private JLabel errorLabel;
    private JLabel noteLabel;
    private JButton backToMenu;

    private static ErrorWindow instance;

    public void runWindow(Errors code,String note){

        ErrorWindow.code = code;

        SwingUtilities.invokeLater(() -> {

            ErrorWindow window = new ErrorWindow();
            window.initWindow(code, note);

        });
    }

    public ErrorWindow() {
        instance = this;
    }

    public static ErrorWindow getInstanceWithCode(Errors code) {
        if (code == ErrorWindow.code){
            return instance;
        }
        return null;
    }

    public void initWindow(Errors code, String note) {
        frame = new JFrame("An Error Occurred");
        frame.setSize(400, 200);
        frame.setLocation(500, 100);
        frame.getContentPane().setBackground(Color.darkGray);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.darkGray);

        errorLabel = new JLabel("Error: " + code, SwingConstants.CENTER);
        errorLabel.setForeground(Color.WHITE);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 16));

        noteLabel = new JLabel("-> " + note, SwingConstants.CENTER);
        noteLabel.setForeground(Color.WHITE);
        noteLabel.setFont(new Font("Arial", Font.BOLD, 16));
        if (note == null){
            noteLabel.setText("");
        }

        backToMenu = new JButton("OKAY");
        backToMenu.setBackground(Color.GRAY);
        backToMenu.setForeground(Color.DARK_GRAY);

        mainPanel.add(errorLabel, BorderLayout.CENTER);
        mainPanel.add(noteLabel, BorderLayout.PAGE_START);
        mainPanel.add(backToMenu, BorderLayout.EAST);

        frame.add(mainPanel);

        backToMenu.addActionListener(e -> {
            frame.dispose();
            instance = null;
        });

        frame.setVisible(true);
    }
}


