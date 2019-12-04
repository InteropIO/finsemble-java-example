package com.chartiq.finsemble.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticationExample extends JFrame implements ActionListener {
    private final Container contentPane;
    private final JLabel userLabel;
    private final JLabel passwordLabel;
    private final JLabel messageLabel;
    private final JTextField userNameTextField;
    private final JPasswordField passwordTextField;
    private final JButton submitButton;
    
    AuthenticationExample() {
        contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets= new Insets(10,10,0,10);

        // User Label
        userLabel = new JLabel();
        userLabel.setText("User Name :");
        userNameTextField = new JTextField();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(userLabel, constraints);

        // Password
        passwordLabel = new JLabel();
        passwordLabel.setText("Password :");
        passwordTextField = new JPasswordField();
        constraints.gridy = 1;
        contentPane.add(passwordLabel, constraints);

        // Message
        messageLabel = new JLabel();
        constraints.gridy = 2;
        contentPane.add(messageLabel, constraints);

        constraints.gridy = 0;
        constraints.gridx = 1;
        contentPane.add(userNameTextField, constraints);

        constraints.gridy = 1;
        contentPane.add(passwordTextField, constraints);

        // Submit
        submitButton = new JButton("SUBMIT");
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        contentPane.add(submitButton, constraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Adding the listeners to components..
        submitButton.addActionListener(this);
        setTitle("Please Login Here !");
        setSize(300, 150);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new AuthenticationExample();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String userName = userNameTextField.getText();
        String password = passwordTextField.getText();
        if (userName.trim().equals("admin") && password.trim().equals("admin")) {
            messageLabel.setText(" Hello " + userName
                    + "");
        } else {
            messageLabel.setText(" Invalid user.. ");
        }

    }
}
