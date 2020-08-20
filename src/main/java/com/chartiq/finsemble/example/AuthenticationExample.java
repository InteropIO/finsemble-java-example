/***********************************************************************************************************************
 Copyright 2018-2020 by ChartIQ, Inc.
 Licensed under the ChartIQ, Inc. Developer License Agreement https://www.chartiq.com/developer-license-agreement
 **********************************************************************************************************************/
package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import  java.util.List;

public class AuthenticationExample extends JFrame implements ActionListener {
    private final Container contentPane;
    private final JLabel userLabel;
    private final JLabel passwordLabel;
    private final JLabel messageLabel;
    private final JTextField userNameTextField;
    private final JPasswordField passwordTextField;
    private final JButton submitButton;
    private final Finsemble fsbl;

    AuthenticationExample(List<String> args) throws Exception {
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

        fsbl = new Finsemble(args, this);
        fsbl.connect(new MessageHandler());
    }

    public static void main(String[] args) throws Exception {
        final List<String> argList = new ArrayList<>(Arrays.asList(args));

        new AuthenticationExample(argList);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final String userName = userNameTextField.getText();
        final String password = passwordTextField.getText();
        final JSONObject credentials = new JSONObject(){{
           put("user", userName);
           put("password", password);
        }};

        fsbl.getClients().getAuthenticationClient().publishAuthorization(userName, credentials);

        fsbl.getClients().getAuthenticationClient().getCurrentCredentials((err, res) -> {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
    }
}
