package com.chartiq.finsemble.example;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.chartiq.finsemble.Finsemble;

public class JavaExample {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaExample.class.getName());

    private JButton connectButton;
    private JPanel mainPanel;
    private JEditorPane messages;

    public JavaExample(List<String> args) {
        appendMessage(String.format("Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        connectButton.addActionListener((e) -> {
            connectButton.setEnabled(false);

            JavaExample.this.appendMessage("Initiating Finsemble connection");

            // Get frame for registration with Finsemble
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(JavaExample.this.getMainPanel());

            // TODO: populate this with a way to test the API
            Finsemble fsbl = new Finsemble(args, frame);
            try {
                fsbl.connect();
                JavaExample.this.appendMessage("Connected to Finsemble");

                fsbl.register();
                JavaExample.this.appendMessage("Window registered with Finsemble");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error initializing Finsemble connection", ex);
                JavaExample.this.appendMessage("Error initializing Finsemble connection: " + ex.getMessage());
                connectButton.setEnabled(true);
                try {
                    fsbl.close();
                } catch (IOException e1) {
                    LOGGER.log(Level.SEVERE, "Error closing Finsemble connection", e1);
                    JavaExample.this.appendMessage((e1.getMessage()));
                }
            }
        });
    }

    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    private void appendMessage(String s) {
        LOGGER.info(String.format("Message to UI", s));
        try {
            Document doc = messages.getDocument();
            doc.insertString(0, String.format("%s\n", s), null);
        } catch (BadLocationException exc) {
            LOGGER.severe(exc.getMessage());
        }
    }

    /**
     * Gets the main panel of the application.
     *
     * @return The main panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * The main function of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        final List<String> argList = new ArrayList<>(Arrays.asList(args));

        initLogging(argList);

        // Parse message into ArrayList of strings so arguments can be added.
        final List<String> otherArgs = new ArrayList<>(Arrays.asList(args));

        otherArgs.add("Not first");
        launchForm(otherArgs);

        argList.add("First");
        launchForm(argList);

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting JavaExample: %s", Arrays.toString(args)));
    }

    private static void initLogging(List<String> args) {
        if (System.getProperty("java.util.logging.config.file") != null) {
            // Config file property has been set, no further initialization needed.
            return;
        }

        // Check whether logging file has been specified
        final List<String> properties = args
                .stream()
                .filter(arg -> arg.startsWith("-Djava.util.logging.config.file"))
                .collect(Collectors.toList());

        if ((properties.size() == 0) || !properties.get(0).contains("=")) {
            // No logging properties specified
            return;
        }

        // Get filename from parameter
        final String loggingPropertiesPath = properties.get(0).split("=")[1];
        try {
            final InputStream inputStream = new FileInputStream(loggingPropertiesPath);
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load default logging.properties file", e);
        }
    }

    /**
     * Launches the example form based on the command line arguments.
     *
     * @param args The command line arguments.
     */
    private static void launchForm(List<String> args) {
        final JFrame frame = new JFrame("JavaExample");
        frame.setContentPane(new JavaExample(args).getMainPanel());
        frame.pack();
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        connectButton = new JButton();
        connectButton.setText("Connect");
        mainPanel.add(connectButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        messages = new JEditorPane();
        mainPanel.add(messages, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
