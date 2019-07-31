package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

public class JavaExample implements WindowListener {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaExample.class.getName());

    private final List<String> launchArgs;

    private JPanel mainPanel;
    private JButton sendSymbolButton;
    private JTextField symbolTextField;
    private JComboBox componentComboBox;
    private JButton launchComponentButton;
    private JCheckBox dockCheckBox;
    private JButton messagesButton;
    private JTextArea messages;
    private JButton linkerButton;
    private JPanel group1Panel;
    private JPanel group2Panel;
    private JPanel group3Panel;
    private JPanel group4Panel;
    private JPanel group5Panel;
    private JPanel group6Panel;
    private JPanel linkerPanel;

    private Finsemble fsbl;

    /**
     * Initializes a new instance of the JavaExample class.
     *
     * @param args The arguments passed to the Java application from the command line
     */
    private JavaExample(List<String> args) {
        appendMessage(String.format(
                "Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        appendMessage("Initiating Finsemble connection");

        launchArgs = args;

        setFormEnable(false);

        // Add messages button handler
        messagesButton.addActionListener((e) -> this.toggleMessages());
        toggleMessages();

        // TODO: Hiding linker panel until linker is figured out
        linkerPanel.setVisible(false);

        // Hide linker pips initially to be shown later
        group1Panel.setVisible(false);
        group2Panel.setVisible(false);
        group3Panel.setVisible(false);
        group4Panel.setVisible(false);
        group5Panel.setVisible(false);
        group6Panel.setVisible(false);

        // TODO: Show when docking is supported
        dockCheckBox.setVisible(false);

        initFinsemble();
    }

    private void initFinsemble() {
        // Get frame for registration with Finsemble
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

        // TODO: populate this with a way to test the API
        fsbl = new Finsemble(launchArgs, frame);
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
                    appendErrorMessage(String.format("Error from Finsemble:\n%s", e.getMessage()));
                }
            });
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
                appendErrorMessage(String.format("Error closing Finsemble connection:\n%s", e1.getMessage()));
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
                appendErrorMessage("Error publishing symbol");
            } else {
                LOGGER.info("Symbol published");
                appendMessage("Symbol published");
            }
        });
    }

    private void initForm() {
        // Add click handlers

        // Send symbol
        symbolTextField.addActionListener(e -> sendSymbol());

        // populate component combo box
        fsbl.getClients().getLauncherClient().getComponentList((err, res) -> {
            if (err != null) {
                LOGGER.log(Level.SEVERE, "Error getting component list", err);
                appendErrorMessage(String.format(":\n%s", err));
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
                            }).toArray(String[]::new);

                    // Add them to the component combo
                    componentComboBox.setModel(new DefaultComboBoxModel<>(nonSystemComponents));

                    if (componentComboBox.getItemCount() > 0) {
                        // If there are components, select the first
                        componentComboBox.setSelectedIndex(0);
                    } else {
                        // If there aren't components, spawn buttons
                        launchComponentButton.setEnabled(false);
                        appendMessage("No components to spawn, disabling Launch Component button");
                    }
                }
            }
        });

        // Add launch component button handler
        launchComponentButton.addActionListener(e -> launchComponent());

        // dock checkbox
        // TODO: Figure this out at some point

        // Linker button
        linkerButton.addActionListener((e) -> showLinker());
    }

    private void showLinker() {
        // TODO: Show linker

//        var channelsToSend = new JArray { };
//        if (channels != null)
//        {
//            foreach (var item in channels)
//            {
//                var channelinfo = allChannels.Where(jt => jt["name"].ToString() == item.ToString())?.First();
//                if (channelinfo != null) channelsToSend.Add(channelinfo);
//            }
//        }
//        JObject data = new JObject
//        {
//                ["channels"] = channelsToSend,
//                ["windowIdentifier"] = windowClient.windowIdentifier
//        };
//
//        routerClient.Query("Finsemble.LinkerWindow.SetActiveChannels", data, new JObject { }, delegate (object sender, FinsembleEventArgs e)
//        {
//            Application.Current.Dispatcher.Invoke((Action)delegate //main thread
//            {
//
//                var wi = new JObject
//                {
//                        ["componentType"] = "linkerWindow"
//                };
//                var parameters = new JObject
//                {
//                        ["position"] = "relative",
//                        ["left"] = left,
//                        ["top"] = top,
//                        ["spawnIfNotFound"] = false
//                };
//                launcherClient.ShowWindow(wi, parameters, (EventHandler<FinsembleEventArgs>)delegate (object s2, FinsembleEventArgs e2) { });
//
//            });
//        });
    }

    /**
     * Launches the currently selected component from the component combo box.
     */
    private void launchComponent() {
        final String componentName = componentComboBox.getSelectedItem() != null ? componentComboBox.getSelectedItem().toString() : null;

        if (componentName == null) {
            LOGGER.warning("No selected component");
            appendMessage(("WARNING: No selected component"));
            return;
        }

        fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject(), (err, res) -> {
            if (err != null) {
                LOGGER.log(Level.SEVERE, String.format("Error spawning \"%s\"", componentName), err);
                appendErrorMessage(String.format("Error spawning\n%s", err));
            } else {
                LOGGER.info(String.format("\"%s\" spawned", componentName));
                appendMessage(String.format("\"%s\" spawned", componentName));
            }
        });
    }


    private void setFormEnable(boolean enabled) {
        symbolTextField.setEnabled(enabled);
        componentComboBox.setEnabled(enabled);
        launchComponentButton.setEnabled(enabled);
        dockCheckBox.setEnabled(enabled);
        linkerButton.setEnabled(enabled);
    }

    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    private void appendMessage(String s) {
        try {
            Document doc = messages.getDocument();
            doc.insertString(0, String.format("%s\n", s), null);
        } catch (BadLocationException exc) {
            LOGGER.severe(exc.getMessage());
        }
    }

    /**
     * Adds an error message to the message box.
     *
     * @param s The message to add.
     */
    private void appendErrorMessage(String s) {
        try {
            Document doc = messages.getDocument();
            doc.insertString(0, String.format("ERROR: %s\n", s), null);
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
        frame.setContentPane(new JavaExample(args).mainPanel);
        frame.pack();
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    //region Description

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
        componentComboBox = new JComboBox();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(componentComboBox, gbc);
        launchComponentButton = new JButton();
        launchComponentButton.setEnabled(false);
        launchComponentButton.setText("Launch Component");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(launchComponentButton, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 48, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(2);
        label1.setHorizontalTextPosition(11);
        label1.setInheritsPopupMenu(false);
        label1.setText("AAPL");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        mainPanel.add(label1, gbc);
        dockCheckBox = new JCheckBox();
        dockCheckBox.setEnabled(true);
        dockCheckBox.setText("Dock");
        dockCheckBox.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(dockCheckBox, gbc);
        symbolTextField = new JTextField();
        symbolTextField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(symbolTextField, gbc);
        sendSymbolButton = new JButton();
        sendSymbolButton.setEnabled(false);
        sendSymbolButton.setText("Send Symbol");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(sendSymbolButton, gbc);
        messagesButton = new JButton();
        messagesButton.setEnabled(true);
        messagesButton.setFocusable(false);
        messagesButton.setText("Messages");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(messagesButton, gbc);
        messages = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(messages, gbc);
        linkerPanel = new JPanel();
        linkerPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(linkerPanel, gbc);
        group1Panel = new JPanel();
        group1Panel.setLayout(new GridBagLayout());
        group1Panel.setBackground(new Color(-7896643));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group1Panel, gbc);
        group2Panel = new JPanel();
        group2Panel.setLayout(new GridBagLayout());
        group2Panel.setBackground(new Color(-8139));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group2Panel, gbc);
        group3Panel = new JPanel();
        group3Panel.setLayout(new GridBagLayout());
        group3Panel.setBackground(new Color(-7743485));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group3Panel, gbc);
        group4Panel = new JPanel();
        group4Panel.setLayout(new GridBagLayout());
        group4Panel.setBackground(new Color(-105886));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group4Panel, gbc);
        group5Panel = new JPanel();
        group5Panel.setLayout(new GridBagLayout());
        group5Panel.setBackground(new Color(-13783809));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group5Panel, gbc);
        group6Panel = new JPanel();
        group6Panel.setLayout(new GridBagLayout());
        group6Panel.setBackground(new Color(-24064));
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        linkerPanel.add(group6Panel, gbc);
        linkerButton = new JButton();
        linkerButton.setLabel("Linker");
        linkerButton.setText("Linker");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        linkerPanel.add(linkerButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

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
}
