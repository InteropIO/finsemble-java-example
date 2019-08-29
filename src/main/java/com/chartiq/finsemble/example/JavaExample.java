package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;

public class JavaExample {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(JavaExample.class.getName());

    /**
     * Arguments passed via the command line
     */
    private List<String> args;

    //region FXML controls
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
    private AnchorPane linkerPanel;
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
    //endregion

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

    /**
     * Sets the arguments passed to Finsemble.
     * @param args The arguments
     */
    public void setArguments(List<String> args) {
        this.args = args;
    }

    /**
     * Sets the window used by Finsemble for registration
     * @param window The window
     */
    public void setWindow(Window window) {
        this.window = window;
    }

    /**
     * Connect to Finsemble.
     */
    public void connect() {
        if (fsbl != null) {
            // Has already connected once, and reconnecting doesn't work.
            return;
        }

        // TODO: When we get to this point window is still null, so the window isn't registered with finsemble.
        fsbl = new Finsemble(args, window);
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

            // populate component combo box
            populateComboBox();

            // dock checkbox
            // TODO: Figure this out at some point

            fsbl.getClients().getLinkerClient().subscribe("symbol", this::handleSymbol);

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
        final String componentName = componentComboBox.getValue();

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
        symbolTextField.setDisable(!enabled);
        sendSymbolButton.setDisable(!enabled);
        componentComboBox.setDisable(!enabled);
        launchComponentButton.setDisable(!enabled);
        dockCheckBox.setDisable(!enabled);
        linkerPanel.setDisable(!enabled);
    }

    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    private void appendMessage(String s) {
        if (messages != null) {
            messages.appendText(String.format("\n%s", s));
        }
    }

    @FXML
    protected void initialize() {
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
    }

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
                sw.append("\n");

                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);

                stackTrace = sw.toString();
            }

            final String message = String.format(
                    "%s: %s.%s %s%s",
                    record.getLevel(),
                    record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".")),
                    record.getSourceMethodName(),
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
