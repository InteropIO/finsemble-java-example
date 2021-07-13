/***********************************************************************************************************************
 Copyright 2018-2020 by ChartIQ, Inc.
 Licensed under the ChartIQ, Inc. Developer License Agreement https://www.chartiq.com/developer-license-agreement
 **********************************************************************************************************************/
package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;

public class JavaSwingExample extends JFrame implements WindowListener {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaSwingExample.class.getName());

    private final List<String> launchArgs;

    private Container contentPane;
    private JButton sendSymbolButton;
    private JTextField symbolTextField;
    private JLabel symbolLabel;
    private JComboBox componentComboBox;
    private JButton launchComponentButton;
    private JButton messagesButton;
    private JTextArea messages;
    private JButton group1Button;
    private JButton group2Button;
    private JButton group3Button;
    private JButton group4Button;
    private JButton group5Button;
    private JButton group6Button;

    private HashMap<String, JButton> linkerButtons = new HashMap<>();

    private Finsemble fsbl;

    /**
     * Initializes a new instance of the JavaSwingExample class.
     *
     * @param args The arguments passed to the Java application from the command line
     */
    JavaSwingExample(List<String> args) {
        LOGGER.addHandler(new MessageHandler(messages));

        LOGGER.info(String.format(
                "Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        LOGGER.info("Initiating Finsemble connection");

        launchArgs = args;

        createForm();

        setFormEnable(false);

        symbolTextField.setText("MSFT");

        // Add messages button handler
        messagesButton.addActionListener((e) -> this.toggleMessages());

        group1Button.setText("");
        group2Button.setText("");
        group3Button.setText("");
        group4Button.setText("");
        group5Button.setText("");
        group6Button.setText("");

        // TODO: Implement docking
    }

    private void createForm() {
        setTitle("Java Swing Example");
        setBounds(20, 20, 300, 375);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 0, 10);

        // region Linker buttons
        group1Button = new JButton();
        group1Button.setBackground(new Color(135, 129, 189));
        group1Button.setForeground(new Color(187, 187, 187));
        group1Button.setName("group1");
        constraints.insets = new Insets(10, 10, 0, 0);
        constraints.ipady = 40;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(group1Button, constraints);
        linkerButtons.put("group1", group1Button);

        group2Button = new JButton();
        group2Button.setBackground(new Color(255, 224, 53));
        group2Button.setForeground(new Color(187, 187, 187));
        group2Button.setName("group2");
        constraints.insets = new Insets(10, 0, 0, 0);
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        contentPane.add(group2Button, constraints);
        linkerButtons.put("group2", group2Button);

        group3Button = new JButton();
        group3Button.setBackground(new Color(137, 216, 3));
        group3Button.setForeground(new Color(187, 187, 187));
        group3Button.setName("group3");
        constraints.insets = new Insets(10, 0, 0, 0);
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        contentPane.add(group3Button, constraints);
        linkerButtons.put("group3", group3Button);

        group4Button = new JButton();
        group4Button.setBackground(new Color(254, 98, 98));
        group4Button.setForeground(new Color(187, 187, 187));
        group4Button.setName("group4");
        constraints.insets = new Insets(10, 0, 0, 0);
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 3;
        constraints.gridy = 0;
        contentPane.add(group4Button, constraints);
        linkerButtons.put("group4", group4Button);

        group5Button = new JButton();
        group5Button.setBackground(new Color(45, 172, 255));
        group5Button.setForeground(new Color(187, 187, 187));
        group5Button.setName("group5");
        constraints.insets = new Insets(10, 0, 0, 0);
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 4;
        constraints.gridy = 0;
        contentPane.add(group5Button, constraints);
        linkerButtons.put("group5", group5Button);

        group6Button = new JButton();
        group6Button.setBackground(new Color(255, 162, 0));
        group6Button.setForeground(new Color(187, 187, 187));
        group6Button.setName("group6");
        constraints.insets = new Insets(10, 0, 0, 10);
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 5;
        constraints.gridy = 0;
        contentPane.add(group6Button, constraints);
        linkerButtons.put("group6", group6Button);
        // endregion

        // region Symbol
        symbolTextField = new JTextField("MSFT");
        symbolTextField.setEditable(true);
        symbolTextField.setEnabled(false);
        constraints.insets = new Insets(10, 10, 0, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipady = 10;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 6;
        contentPane.add(symbolTextField, constraints);

        sendSymbolButton = new JButton("Send Symbol");
        sendSymbolButton.setEnabled(false);
        constraints.ipady = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 6;
        contentPane.add(sendSymbolButton, constraints);

        symbolLabel = new JLabel("AAPL");
        symbolLabel.setFont(symbolLabel.getFont().deriveFont(48.0f));
        symbolLabel.setHorizontalAlignment(JLabel.CENTER);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 6;
        contentPane.add(symbolLabel, constraints);
        // endregion

        // region Launch Component
        componentComboBox = new JComboBox();
        componentComboBox.setEnabled(false);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 6;
        contentPane.add(componentComboBox, constraints);

        launchComponentButton = new JButton("Launch Component");
        launchComponentButton.setEnabled(false);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 6;
        contentPane.add(launchComponentButton, constraints);
        // endregion

        // region Messages
        messagesButton = new JButton("Show messages");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 6;
        contentPane.add(messagesButton, constraints);

        messages = new JTextArea();
        messages.setVisible(false);
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 6;
        contentPane.add(messages, constraints);
        // endregion
    }

    private void initFinsemble() {
        // TODO: populate this with a way to test the API
        fsbl = new Finsemble(launchArgs, this);
        try {
            fsbl.connect();
            appendMessage("Connected to Finsemble");

            fsbl.addListener(new ConnectionListener() {
                @Override
                public void disconnected(ConnectionEventGenerator from) {
                    LOGGER.info("Finsemble connection closed");
                    appendMessage("Finsemble connection closed");
                }

                @Override
                public void error(ConnectionEventGenerator from, Exception e) {
                    LOGGER.log(Level.SEVERE, "Error from Finsemble", e);
                }

                @Override
                public void onWindowStateReady(ConnectionEventGenerator from) {
                    // NoOp
                }
            });

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
            }
        }
    }

    /**
     * Toggles the visibility of the messages panel.
     */
    private void toggleMessages() {
        final boolean isVisible = messages.isVisible();

        messages.setVisible(!isVisible);

        if (messages.isVisible()) {
            messagesButton.setText("Hide messages");
        } else {
            messagesButton.setText("Show messages");
        }
    }

    private void sendSymbol() {
        final String symbol = symbolTextField.getText();
        if (symbol == null || symbol.equals("")) {
            // Must have a symbol to send
            symbolTextField.setText("AAPL");
            return;
        }

        final JSONObject args = new JSONObject() {{
            put("dataType", "symbol");
            put("data", symbol);
        }};

        // TODO: Make callback optional
        fsbl.getClients().getLinkerClient().publish(args, (err, res) -> {
            if (err != null) {
                LOGGER.log(Level.SEVERE, "Error publishing symbol", err);
            } else {
                LOGGER.info("Symbol published");
            }
        });
    }

    private void initForm() {
        // Add click handlers

        // Send symbol
        sendSymbolButton.addActionListener(e -> sendSymbol());

        // populate component combo box
        populateComboBox();

        // Add launch component button handler
        launchComponentButton.addActionListener(e -> launchComponent());

        // dock checkbox
        // TODO: Figure this out at some point

        fsbl.getClients().getLinkerClient().subscribe("symbol", this::handleSymbol);

        // Linker
        group1Button.addActionListener(this::toggleLinker);
        group2Button.addActionListener(this::toggleLinker);
        group3Button.addActionListener(this::toggleLinker);
        group4Button.addActionListener(this::toggleLinker);
        group5Button.addActionListener(this::toggleLinker);
        group6Button.addActionListener(this::toggleLinker);

        //GetComponentState
        final JSONArray componentStateFields = new JSONArray() {{
            put("Finsemble_Linker");
            put("symbol");
        }};
        final JSONObject getComponentStateParam = new JSONObject() {{
            put("fields", componentStateFields);
        }};
        fsbl.getClients().getWindowClient().getComponentState(getComponentStateParam, this::handleGetComponentStateCb);
    }

    private void handleGetComponentStateCb(JSONObject err, JSONObject res) {
        if (err != null) {
            LOGGER.log(Level.SEVERE, "Error in handleGetComponentStateCb", err);
        } else {
            //Set subscribe linker channel
            if (res.has("Finsemble_Linker")) {
                final JSONArray channelToLink = res.getJSONArray("Finsemble_Linker");
                final JSONObject windowIdentifier = fsbl.getClients().getWindowClient().getWindowIdentifier();
                for (int i = 0; i < channelToLink.length(); i++) {
                    try {
                        String currentChannel = channelToLink.getString(i);
                        JButton lkrBtn = linkerButtons.get(currentChannel);
                        fsbl.getClients().getLinkerClient().linkToChannel(currentChannel, windowIdentifier, (error, response) -> {
                            if (error != null) {
                                LOGGER.log(Level.SEVERE, String.format("Error linking to channel: %s", currentChannel), err);
                            } else {
                                LOGGER.info((String.format("Linked to channel: %s", currentChannel)));
                            }
                        });
                        lkrBtn.setText("X");
                        lkrBtn.updateUI();
                    } catch (Exception ex) {
                        String errorMsg = String.format("Error linking channel %s to group", channelToLink);
                        LOGGER.log(Level.SEVERE, errorMsg, ex.getMessage());
                        fsbl.getClients().getLoggerClient().system().error(errorMsg + " cause: " + ex.getMessage());
                    }
                }
            }

            //Set symbol value
            if (res.has("symbol")) {
                final String symbol = res.getString("symbol");
                if (!symbol.equals("")) {
                    symbolLabel.setText(symbol);
                }
            }
        }
    }

    private void handleSymbol(JSONObject err, JSONObject res) {
        if (err != null) {
            LOGGER.severe(err.toString());
        } else {
            final String symbol = res.has("data") && res.getJSONObject("data").has("data") ?
                    res.getJSONObject("data").getString("data") :
                    "";
            symbolLabel.setText(symbol);
        }
    }

    private void toggleLinker(ActionEvent e) {
        final JButton btn = (JButton) e.getSource();
        final String channel = btn.getName();
        final boolean selected = btn.getText().equals("X");

        final JSONObject wi = fsbl.getClients().getWindowClient().getWindowIdentifier();

        if (selected) {
            // unlink
            btn.setText("");
            fsbl.getClients().getLinkerClient().unlinkFromChannel(channel, wi, (err, res) -> {
                if (err != null) {
                    LOGGER.log(Level.SEVERE, String.format("Error unlinking from channel: %s", channel), err);
                } else {
                    LOGGER.info(String.format("Unlinked from channel: %s", channel));
                }
            });
        } else {
            // link
            btn.setText("X");
            fsbl.getClients().getLinkerClient().linkToChannel(channel, wi, (err, res) -> {
                if (err != null) {
                    LOGGER.log(Level.SEVERE, String.format("Error linking to channel: %s", channel), err);
                } else {
                    LOGGER.info((String.format("Linked to channel: %s", channel)));
                }
            });
        }
    }

    private void populateComboBox() {
        fsbl.getClients().getLauncherClient().getComponentList((err, res) -> {
            if (err != null) {
                LOGGER.log(Level.SEVERE, "Error getting component list", err);
            } else {
                if (res.has("data")) {
                    // Get list of component names
                    final List<String> componentNames = new ArrayList<>();
                    final JSONObject data = res.getJSONObject("data");
                    data.keys().forEachRemaining(componentNames::add);

                    // Get non-system components
                    final String[] nonSystemComponents = componentNames
                            .stream()
                            .filter(componentName -> {
                                final JSONObject component = data.getJSONObject(componentName);
                                return !component.has("component") ||
                                        !component.getJSONObject("component").has("category") ||
                                        !component.getJSONObject("component").getString("category").equals("system");
                            })
                            .sorted()
                            .toArray(String[]::new);

                    // Add them to the component combo
                    componentComboBox.setModel(new DefaultComboBoxModel<>(nonSystemComponents));

                    if (componentComboBox.getItemCount() > 0) {
                        // If there are components, select the first
                        componentComboBox.setSelectedIndex(0);
                    } else {
                        // If there aren't components, spawn buttons
                        launchComponentButton.setEnabled(false);
                        LOGGER.info(("No components to spawn, disabling Launch Component button"));
                    }
                }
            }
        });
    }

    /**
     * Launches the currently selected component from the component combo box.
     */
    private void launchComponent() {
        final String componentName = componentComboBox.getSelectedItem() != null ? componentComboBox.getSelectedItem().toString() : null;

        if (componentName == null) {
            LOGGER.warning("No selected component");
            return;
        }

        fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject(), (err, res) -> {
            if (err != null) {
                LOGGER.log(Level.SEVERE, String.format("Error spawning \"%s\"", componentName), err);
            } else {
                LOGGER.info(String.format("\"%s\" spawned", componentName));
            }
        });
    }

    private void setFormEnable(boolean enabled) {
        symbolTextField.setEnabled(enabled);
        sendSymbolButton.setEnabled(enabled);
        componentComboBox.setEnabled(enabled);
        launchComponentButton.setEnabled(enabled);
        group1Button.setEnabled(enabled);
        group2Button.setEnabled(enabled);
        group3Button.setEnabled(enabled);
        group4Button.setEnabled(enabled);
        group5Button.setEnabled(enabled);
        group6Button.setEnabled(enabled);
    }

    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    private void appendMessage(String s) {
        if (messages == null) {
            return;
        }

        try {
            Document doc = messages.getDocument();
            doc.insertString(0, String.format("%s\n", s), null);
        } catch (BadLocationException exc) {
            LOGGER.severe(exc.getMessage());
        }
    }

    /**
     * The main function of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        final List<String> argList = new ArrayList<>(Arrays.asList(args));

        initLogging(argList);

        launchForm(argList);

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting JavaSwingExample: %s", Arrays.toString(args)));
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
        final JavaSwingExample example = new JavaSwingExample(args);
        example.addWindowListener(example);
        example.setVisible(true);
    }

    //region Description

    //region WindowListener implementation

    /**
     * Invoked the first time a window is made visible.
     *
     * @param e The window event
     */
    @Override
    public void windowOpened(WindowEvent e) {
        initFinsemble();
    }

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.
     *
     * @param e The window event
     */
    @Override
    public void windowClosing(WindowEvent e) {

    }

    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     *
     * @param e The window event
     */
    @Override
    public void windowClosed(WindowEvent e) {
        if (fsbl != null) {
            try {
                fsbl.close();
            } catch (Exception ex) {
                // Do nothing
                fsbl = null;
            }
        }
    }

    /**
     * Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window
     * is displayed as the icon specified in the window's
     * iconImage property.
     *
     * @param e The window event
     * @see Frame#setIconImage
     */
    @Override
    public void windowIconified(WindowEvent e) {

    }

    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     *
     * @param e The window event
     */
    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or
     * a Dialog can be the active Window. The native windowing system may
     * denote the active Window or its children with special decorations, such
     * as a highlighted title bar. The active Window is always either the
     * focused Window, or the first Frame or Dialog that is an owner of the
     * focused Window.
     *
     * @param e The window event
     */
    @Override
    public void windowActivated(WindowEvent e) {

    }

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a
     * Dialog can be the active Window. The native windowing system may denote
     * the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused
     * Window, or the first Frame or Dialog that is an owner of the focused
     * Window.
     *
     * @param e The window event
     */
    @Override
    public void windowDeactivated(WindowEvent e) {

    }
    //endregion

    /**
     * Handler to write log messages to the message area of the form.
     */
    private class MessageHandler extends Handler {
        private final JTextArea messages;

        MessageHandler(JTextArea messages) {
            this.messages = messages;
        }

        /**
         * Publish a <tt>LogRecord</tt>.
         * <p>
         * The logging request was made initially to a <tt>Logger</tt> object,
         * which initialized the <tt>LogRecord</tt> and forwarded it here.
         * <p>
         * The <tt>Handler</tt>  is responsible for formatting the message, when and
         * if necessary.  The formatting should include localization.
         *
         * @param record description of the log event. A null record is
         *               silently ignored and is not published
         */
        @Override
        public void publish(LogRecord record) {
            final Throwable throwable = record.getThrown();

            String stackTrace = "";
            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);

                sw.append("\n");
                stackTrace = sw.toString();
            }

            final String message = String.format(
                    "%s: %s %s%s",
                    record.getLevel(),
                    record.getLoggerName(),
                    record.getMessage(),
                    stackTrace);

            appendMessage(message);
        }

        /**
         * Flush any buffered output.
         */
        @Override
        public void flush() {

        }

        /**
         * Close the <tt>Handler</tt> and free all associated resources.
         * <p>
         * The close method will perform a <tt>flush</tt> and then close the
         * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
         * should no longer be used.  Method calls may either be silently
         * ignored or may throw runtime exceptions.
         *
         * @throws SecurityException if a security manager exists and if
         *                           the caller does not have <tt>LoggingPermission("control")</tt>.
         */
        @Override
        public void close() throws SecurityException {

        }
    }
}
