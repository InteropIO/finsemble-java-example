/***********************************************************************************************************************
 Copyright 2018-2020 by ChartIQ, Inc.
 Licensed under the ChartIQ, Inc. Developer License Agreement https://www.chartiq.com/developer-license-agreement
 **********************************************************************************************************************/
package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    @FXML
    private CheckBox dockingCheckbox;
    //endregion


    private final String DRAG_START_CHANNEL = "DragAndDropClient.dragStart";
    private final String DRAG_END_CHANNEL = "DragAndDropClient.dragEnd";

    private HBox fxScrim;

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
     *
     * @param args The arguments
     */
    void setArguments(List<String> args) {
        this.args = args;
    }

    /**
     * Sets the window used by Finsemble for registration
     *
     * @param window The window
     */
    void setWindow(Window window) {
        this.window = window;
    }

    /**
     * Connect to Finsemble.
     */
    void connect() {
        if (fsbl != null) {
            // Has already connected once, and reconnecting doesn't work.
            return;
        }

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting JavaExample: %s", String.join(", ", args)));

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

            appendMessage("Window registered with Finsemble");

            // populate component combo box
            populateComboBox();

            // Handle Docking
            fsbl.getClients().getRouterClient().subscribe("Finsemble.WorkspaceService.groupUpdate", this::handleDockingGroupUpdate);

            // Subscribe to linker channel
            fsbl.getClients().getLinkerClient().subscribe("symbol", this::handleSymbol);

            setFormEnable(true);

            // Sample to enable drag and drop feature
            setDraggable();
            setDropable();

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

    private void handleDockingGroupUpdate(JSONObject err, JSONObject res) {
        if (err != null) {
            fsbl.getClients().getLogger().error(err.toString());
        } else {
            final JSONObject groupData = res.getJSONObject("data").getJSONObject("groupData");
            final String currentWindowName = fsbl.getClients().getWindowClient().getWindowIdentifier().getString("windowName");

            JSONObject thisWindowGroups = new JSONObject();
            thisWindowGroups.put("dockingGroup", "");
            thisWindowGroups.put("snappingGroup", "");
            thisWindowGroups.put("topRight", false);

            for (String windowGroupId : groupData.keySet()) {
                JSONObject windowGroup = groupData.getJSONObject(windowGroupId);
                JSONArray windowNames = windowGroup.getJSONArray("windowNames");

                boolean windowInGroup = false;
                for (int i = 0; i < windowNames.length(); i++) {
                    String windowName = windowNames.getString(i);
                    if (windowName.equals(currentWindowName)) {
                        windowInGroup = true;
                    }
                }

                if (windowInGroup) {
                    if (windowGroup.getBoolean("isMovable")) {
                        thisWindowGroups.put("dockingGroup", windowGroupId);
                        if (windowGroup.getString("topRightWindow").equals(currentWindowName)) {
                            thisWindowGroups.put("topRight", true);
                        }
                    } else {
                        thisWindowGroups.put("snappingGroup", windowGroupId);
                    }
                }
            }

            if (!thisWindowGroups.getString("dockingGroup").equals("")) {
                // docked
                dockingCheckbox.setSelected(true);
            } else if (!thisWindowGroups.get("snappingGroup").equals("")) {
                // Snapped
                dockingCheckbox.setDisable(false);
            } else {
                // unsnapped/undocked
                dockingCheckbox.setSelected(false);
                dockingCheckbox.setDisable(true);
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

        // TODO: Make callbacks optional where possible
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
            Platform.runLater(() -> symbolLabel.setText(symbol));
        }
    }

    @FXML
    private void toggleLinker(ActionEvent e) {
        final Button btn = (Button) e.getSource();
        final String channel = btn.getId().replace("Button", "");
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
                    final String[] launchableByUserComponents = componentNames
                            .stream()
                            .filter(componentName -> {
                                final JSONObject component = data.getJSONObject(componentName);
                                return component.has("foreign") &&
                                        component.getJSONObject("foreign").has("components") &&
                                        component.getJSONObject("foreign").getJSONObject("components").has("App Launcher") &&
                                        component.getJSONObject("foreign").getJSONObject("components").getJSONObject("App Launcher").has("launchableByUser") &&
                                        component.getJSONObject("foreign").getJSONObject("components").getJSONObject("App Launcher").getBoolean("launchableByUser");
                            })
                            .sorted()
                            .toArray(String[]::new);

                    Platform.runLater(() -> {
                        // Add them to the component combo
                        componentComboBox.setItems(FXCollections.observableArrayList(launchableByUserComponents));
                        if (componentComboBox.getItems().size() > 0) {
                            // If there are components, select the first
                            componentComboBox.getSelectionModel().select(0);
                        } else {
                            // If there aren't components, disable spawn buttons
                            launchComponentButton.setDisable(true);
                            LOGGER.info(("No components to spawn, disabling Launch Component button"));
                        }
                    });
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
        linkerPanel.setDisable(!enabled);
        dockingCheckbox.setDisable(!enabled);
    }

    private void setDraggable() {
        //Add emitters
        Map<String, Function> emitters = new HashMap<>();
        emitters.put("symbol", this::emitterCallback);
        fsbl.getClients().getDragAndDropClient().setEmitters(emitters);

        //Set fx uielement to be draggable
        symbolTextField.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                // Example to use dragStart
                // Either use emitter to get drag data / put data into json object yourself
                JSONObject data = new JSONObject();

                // Get drag data from emitter
                for (Map.Entry emitter : emitters.entrySet()) {
                    JSONObject tempObj = new JSONObject();
                    String key = emitter.getKey().toString();
                    Function func = (Function) emitter.getValue();
                    data.put(key, tempObj.put(key, func.apply(null)));
                }

//                // Get drag data from textbox yourself
//                data.put("symbol",symbolTextField.getText());

                // Send the drag data to dragboard
                fsbl.getClients().getDragAndDropClient().dragStart(data);
                Dragboard db = symbolTextField.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(new JSONObject() {{
                    put("FSBL", true);
                    put("containsData", true);
                    put("window", fsbl.getClients().getWindowClient().getWindowIdentifier().getString("windowName"));
                    put("data", data);
                }}.toString());
                db.setContent(content);
                if (symbolTextField.getOnDragDone() == null) {
                    symbolTextField.setOnDragDone(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            fsbl.getClients().getRouterClient().transmit(DRAG_END_CHANNEL, new JSONObject());
                            event.consume();
                        }
                    });
                }
                event.consume();
            }
        });
    }

    private Object emitterCallback(Object o) {
        return symbolTextField.getText();
    }

    private void setDropable() {
        //Must be added to enable drop feature
        fsbl.getClients().getDragAndDropClient().setAddWindowHighlight(this::addWindowHighlight);
        fsbl.getClients().getDragAndDropClient().setRemoveWindowHighlight(this::removeWindowHighlight);

        //Set Receivers
        Map<String, CallbackListener> receivers = new HashMap<>();
        receivers.put("symbol", this::symbolReceiverCallback);
        fsbl.getClients().getDragAndDropClient().addReceivers(receivers);
    }

    private void addWindowHighlight(JSONObject err, JSONObject res) {
        Platform.runLater(() -> {
            fxScrim = new HBox();
            fxScrim.setId("scrim");
            fxScrim.setMinWidth(window.getWidth());
            fxScrim.setMinHeight(window.getHeight());

            boolean canReceiveData = res.getBoolean("canReceiveData");
            if (canReceiveData) {
                fxScrim.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 99, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
                fxScrim.setOnDragOver((DragEvent dragEvent) -> {
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                });
                fxScrim.setOnDragDropped((DragEvent dragEvent) -> {
                    Dragboard db = dragEvent.getDragboard();
                    final String dragDataStr = db.getString();
                    final JSONObject dragDataJson = new JSONObject(dragDataStr);
                    fsbl.getClients().getDragAndDropClient().drop(dragDataJson);
                });
            } else
                fxScrim.setBackground(new Background(new BackgroundFill(Color.rgb(99, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

            ((Pane) window.getScene().getRoot()).getChildren().add(fxScrim);
        });
    }


    private void removeWindowHighlight(JSONObject err, JSONObject res) {
        Platform.runLater(() -> {
            ((Pane) window.getScene().getRoot()).getChildren().remove(fxScrim);
        });
    }

    private void symbolReceiverCallback(JSONObject err, JSONObject res) {
        appendMessage("Received Symbol Drop data: " + res.toString());
        String tempSymbol = "";
        final Object symbolObj = res.getJSONObject("data").get("symbol");
        if (symbolObj instanceof JSONObject)
            tempSymbol = res.getJSONObject("data").getJSONObject("symbol").getString("symbol");
        else if (symbolObj instanceof String)
            tempSymbol = res.getJSONObject("data").getString("symbol");

        final String symbol = tempSymbol;

        Platform.runLater(() -> symbolLabel.setText(symbol));
    }


    /**
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    void appendMessage(String s) {
        if (messages != null) {
            messages.appendText(String.format("\n%s", s));
        }
    }

    @FXML
    protected void initialize() {
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
    }

    public void toggleDock(ActionEvent actionEvent) {
        final CheckBox checkbox = (CheckBox) actionEvent.getSource();
        String currentWindowName = fsbl.getClients().getWindowClient().getWindowIdentifier().getString("windowName");
        if (checkbox.isSelected()) {
            fsbl.getClients().getRouterClient().transmit("DockingService.formGroup", new JSONObject() {{
                put("windowName", currentWindowName);
            }});
        } else {
            fsbl.getClients().getRouterClient().query("DockingService.leaveGroup", new JSONObject() {{
                put("name", currentWindowName);
            }}, new JSONObject(), this::handeLeaveGroupcb);
            checkbox.setDisable(true);
        }
    }

    private void handeLeaveGroupcb(JSONObject err, JSONObject res) {
    }
}
