package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

public class JavaExample2 implements ConnectionListener {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaExample2.class.getName());

    private JPanel mainPanel;
    private JButton sendSymbolButton;
    private JTextField symbolTextField;
    private JComboBox componentComboBox;
    private JButton launchComponentButton;
    private JCheckBox dockCheckBox;
    private JCheckBox group1CheckBox;
    private JCheckBox group2CheckBox;
    private JCheckBox group3CheckBox;
    private JCheckBox group4CheckBox;
    private JCheckBox group5CheckBox;
    private JCheckBox group6CheckBox;
    private JButton messagesButton;
    private JTextArea messages;

    /**
     * Initializes a new instance of the JavaExample2 class.
     *
     * @param args
     */
    public JavaExample2(List<String> args) {
        appendMessage(String.format("Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        appendMessage("Initiating Finsemble connection");

        // Get frame for registration with Finsemble
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

        // TODO: populate this with a way to test the API
        Finsemble fsbl = new Finsemble(args, frame);
        try {
            fsbl.connect();
            appendMessage("Connected to Finsemble");

            fsbl.register();
            appendMessage("Window registered with Finsemble");

            initForm();
            setFormEnable(true);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initializing Finsemble connection", ex);
            appendMessage("Error initializing Finsemble connection: " + ex.getMessage());
            try {
                fsbl.close();
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, "Error closing Finsemble connection", e1);
                JavaExample2.this.appendMessage((e1.getMessage()));
            }
        }
    }

    private void toggleMessages() {
    }

    private void initForm() {
        // Add click handlers

        // Send symbol

        // component combobox

        // launch component button

        // dock checkbox

        // group check boxes

        // messages button
        messagesButton.addActionListener((e) -> this.toggleMessages());
    }



    private void setFormEnable(boolean enabled) {
        symbolTextField.setEnabled(enabled);
        componentComboBox.setEnabled(enabled);
        launchComponentButton.setEnabled(enabled);
        dockCheckBox.setEnabled(enabled);
        group1CheckBox.setEnabled(enabled);
        group2CheckBox.setEnabled(enabled);
        group3CheckBox.setEnabled(enabled);
        group4CheckBox.setEnabled(enabled);
        group5CheckBox.setEnabled(enabled);
        group6CheckBox.setEnabled(enabled);
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


    @Override
    public void disconnected(ConnectionEventGenerator from) {
        setFormEnable(false);
    }

    @Override
    public void error(ConnectionEventGenerator from, Exception e) {
        LOGGER.log(Level.SEVERE, "Error from Finsemble connection", e);
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
        frame.setContentPane(new JavaExample2(args).mainPanel);
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
        mainPanel.setLayout(new GridBagLayout());
        sendSymbolButton = new JButton();
        sendSymbolButton.setText("Connect");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(sendSymbolButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
