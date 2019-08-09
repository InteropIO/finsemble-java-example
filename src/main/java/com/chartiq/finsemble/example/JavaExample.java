package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;

public class JavaExample extends Application implements WindowListener {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaExample.class.getName());

    @FXML
    private Button sendSymbolButton;
    @FXML
    private TextField symbolTextField;
    @FXML
    private ComboBox<String> componentComboBox;
    @FXML
    private Button launchComponentButton;
    @FXML
    private CheckBox dockCheckBox;
    @FXML
    private Button messagesButton;
    @FXML
    private TextArea messages;
    @FXML
    private Panel linkerPanel;
    @FXML
    private Button group1Button;
    @FXML
    private Button group2Button;
    @FXML
    private Button group3Button;
    @FXML
    private Button group4Button;
    @FXML
    private Button group5Button;
    @FXML
    private Button group6Button;
    @FXML
    private Label symbolLabel;

    /**
     * The list of arguments passed in by Finsemble
     */
    private List<String> launchArgs;

    /**
     * The finsemble connection
     */
    private Finsemble fsbl;

    /**
     * The window object managed by Finsemble
     */
    private Window window;

    /**
     * Initializes a new instance of the JavaExample class.
     */
    public JavaExample() {
    }

    private void initFinsemble() {
        // TODO: populate this with a way to test the API
        fsbl = new Finsemble(launchArgs, window);
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
            });
            fsbl.register();
            appendMessage("Window registered with Finsemble");

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
    @FXML
    private void toggleMessages() {
        final boolean isVisible = messages.isVisible();

        messages.setVisible(!isVisible);

        if (messages.isVisible()) {
            messagesButton.setText("Hide messages");
        } else {
            messagesButton.setText("Show messages");
        }
    }

    @FXML
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

    @FXML
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
                    componentComboBox.setItems(FXCollections.observableArrayList(nonSystemComponents));
                    if (componentComboBox.getItems().size() > 0) {
                        // If there are components, select the first
                        componentComboBox.getSelectionModel().select(0);
                    } else {
                        // If there aren't components, disable spawn buttons
                        launchComponentButton.setDisable(true);
                        LOGGER.info(("No components to spawn, disabling Launch Component button"));
                    }
                }
            }
        });
    }


    /**
     * Launches the currently selected component from the component combo box.
     */
    @FXML
    private void launchComponent() {
        final String componentName = componentComboBox.getValue() != null ? componentComboBox.getValue() : null;

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
        sendSymbolButton.setDisable(!enabled);
        componentComboBox.setDisable(!enabled);
        launchComponentButton.setDisable(!enabled);
        dockCheckBox.setDisable(!enabled);
        linkerPanel.setEnabled(enabled);
    }

    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    private void appendMessage(String s) {
        if (messages != null) {
            messages.append(String.format("\n%s", s));
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

        launch(args);

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

    //region JavaFX Application Implementation
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) {
        final URL resource = JavaExample.class.getResource("JavaExample.fxml");

        // TODO: Get arguments from Application
        final List<String> args = getParameters().getRaw();
        LOGGER.info(String.format(
                "Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        launchArgs = args;

        try {
            final Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            this.window = scene.getWindow();
            primaryStage.setTitle("JavaExample");
            primaryStage.setScene(new Scene(root, 265, 400));
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in start", e);
        }

    }

    @FXML
    public void initialize() {
        LOGGER.addHandler(new MessageHandler());

        LOGGER.info("Initiating Finsemble connection");

        setFormEnable(false);

        symbolTextField.setText("MSFT");

        toggleMessages();

        group1Button.setText("");
        group2Button.setText("");
        group3Button.setText("");
        group4Button.setText("");
        group5Button.setText("");
        group6Button.setText("");

        // TODO: Show when docking is supported
        dockCheckBox.setVisible(false);

        // populate component combo box
        populateComboBox();

        // dock checkbox
        // TODO: Figure this out at some point

        fsbl.getClients().getLinkerClient().subscribe("symbol", this::handleSymbol);
    }
    //endregion

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
