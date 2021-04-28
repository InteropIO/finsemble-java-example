package com.chartiq.finsemble.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingClickCounter {

    private int count;
    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SwingClickCounter window = new SwingClickCounter();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public SwingClickCounter() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 226, 188);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Click Counter");

        JLabel lblNumber = new JLabel("Number of Mouse Clicks = 0");
        lblNumber.setFont(new Font("Sitka Text", Font.BOLD | Font.ITALIC,      13));
        lblNumber.setBounds(10, 11, 190, 28);
        frame.getContentPane().add(lblNumber);

        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                count++;
                lblNumber.setText("Number of Mouse Clicks = "+ count);
            }
        });
        panel.setBounds(0, 35, 210, 114);
        frame.getContentPane().add(panel);
    }
}
