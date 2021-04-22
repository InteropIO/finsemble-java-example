package com.chartiq.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.fdc3.*;
import com.chartiq.finsemble.fdc3.channel.ChannelClient;
import com.chartiq.finsemble.fdc3.context.Context;
import com.chartiq.finsemble.fdc3.meta.AppMetadata;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InteropServiceExample {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(InteropServiceExample.class.getName());

    /**
     * Arguments passed via the command line
     */
    private List<String> args;

    //region FXML controls
    @FXML
    private AnchorPane controlsPanel;

    @FXML
    private Circle connectionStatus;

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private Button getSystemChannelsButton;

    @FXML
    private Button getCurrentChannelButton;

    @FXML
    private TextField channelName;

    @FXML
    private Button joinChannelButton;

    @FXML
    private Button leaveChannelButton;

    @FXML
    private Label currentChannelLabel;

    @FXML
    private Button broadcastButton;

    @FXML
    private Button getChannelCurrentContextButton;

    @FXML
    private TextField applicationName;

    @FXML
    private Button applicationNameButton;

    @FXML
    private CheckBox openUseContext;

    @FXML
    private TextField contextType;

    @FXML
    private TextField contextName;

    @FXML
    private TextField contextId;

    @FXML
    private Button addContextListenerButton;

    @FXML
    private Button removeContextListenerButton;

    @FXML
    private TextField intent;

    @FXML
    private CheckBox intentUseContext;

    @FXML
    private Button findIntentButton;

    @FXML
    private Button raiseIntentButton;

    @FXML
    private Button addIntentListenerButton;

    @FXML
    private Button removeIntentListenerButton;

    @FXML
    private TextField channelApiChannelName;

    @FXML
    private Button getOrCreateChannelButton;

    @FXML
    private Button broadcastChannelApiButton;

    @FXML
    private Button channelContextListener;

    @FXML
    private Button getInfoButton;

    @FXML
    private AnchorPane loggerPanel;

    @FXML
    private Button clearMessagesButton;

    @FXML
    private TextArea messages;

    @FXML
    private Button exportMessagesButton;
    //endregion

    /**
     * The finsemble connection
     */
    private Finsemble fsbl;

    /**
     * The window object managed by Finsemble
     */
    private Window window;

    private FinsembleDesktopAgent finsembleDesktopAgent;

    private boolean windowReady = false;

    private Map<String, IListener> listenersMap = new LinkedHashMap<>();

    private ChannelClient getOrCreateChannel;

    /**
     * Initializes a new instance of the JavaExample class.
     */
    public InteropServiceExample() {
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

    @FXML
    void addContextListener(ActionEvent event) {
        String contextType = buildContext().getType();
        listenersMap.put(contextType, finsembleDesktopAgent.addContextListener(contextType, context -> {
            appendMessage("*** Context Listener triggered ***");
            appendMessage(String.format("Context Id: %s\nContext type: %s", context.getId(), context.getType()));
            appendMessage("*** --- ***");
        }));
    }

    @FXML
    void addIntentListener(ActionEvent event) {
        String intentString = intent.getText();
        listenersMap.put(intentString, finsembleDesktopAgent.addIntentListener(intentString, context -> {
            appendMessage("*** Intent Listener triggered ***");
            appendMessage(String.format("Context Id: %s\nContext type: %s", context.getId(), context.getType()));
            appendMessage("*** --- ***");
        }));
    }

    @FXML
    void broadcast(ActionEvent event) {
        finsembleDesktopAgent.broadcast(buildContext());
    }

    @FXML
    void broadcastChannelApi(ActionEvent event) {
        Context context = buildContext();
        getOrCreateChannel.broadcast(context);
    }


    @FXML
    void findIntent(ActionEvent event) {
        if (intentUseContext.isSelected()) {
            finsembleDesktopAgent.findIntent(intent.getText(), buildContext());
        } else {
            finsembleDesktopAgent.findIntent(intent.getText());
        }
    }


    @FXML
    void getChannelCurrentContext(ActionEvent event) {
        try {
            Context context = finsembleDesktopAgent.channelGetCurrentContext(buildContext().getType()).get();
            appendMessage("getChannelCurrentContext: " + new JSONObject(context).toString(2));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void getInfo(ActionEvent event) {
        appendMessage("*** fdc3.getInfo() - NOT YET IMPLEMENTED");
    }

    @FXML
    void getOrCreateChannel(ActionEvent event) throws ExecutionException, InterruptedException {
        getOrCreateChannel = (ChannelClient) finsembleDesktopAgent.getOrCreateChannel(channelApiChannelName.getText()).get();
        appendMessage(String.format("getOrCreateChannel - %s", getOrCreateChannel.getId()));
    }


    @FXML
    void raiseIntent(ActionEvent event) {
        if (intentUseContext.isSelected()) {
            finsembleDesktopAgent.raiseIntent(intent.getText(), buildContext());
        } else {
            finsembleDesktopAgent.raiseIntent(intent.getText());
        }
    }

    @FXML
    void removeContextListener(ActionEvent event) {
        String contextType = buildContext().getType();
        listenersMap.get(contextType).unsubscribe();
    }

    @FXML
    void removeIntentListener(ActionEvent event) {
        listenersMap.get(intent.getText()).unsubscribe();
    }

    @FXML
    void getCurrentChannel(ActionEvent event) {
        finsembleDesktopAgent.getCurrentChannel();
    }

    @FXML
    void getSystemChannels(ActionEvent event) {
        finsembleDesktopAgent.getSystemChannels();
    }

    @FXML
    void joinChannel(ActionEvent event) {
        finsembleDesktopAgent.joinChannel(channelName.getText());
    }

    @FXML
    void leaveChannel(ActionEvent event) {
        finsembleDesktopAgent.leaveCurrentChannel();
    }

    @FXML
    void openApplication(ActionEvent event) throws JsonProcessingException {
        AppMetadata targetApp = new AppMetadata();
        targetApp.setAppId(applicationName.getText().trim());
        if (openUseContext.isSelected()) {
            finsembleDesktopAgent.open(targetApp, buildContext());
        } else {
            finsembleDesktopAgent.open(targetApp);
        }
    }


    @FXML
    void clearMessages(ActionEvent event) {
        messages.clear();
    }


    @FXML
    void addChannelContextListener(ActionEvent event) {
        Context context = buildContext();
//        getOrCreateChannel.addContextListener(context1 -> {
//            appendMessage("WHAT IS THIS?" + context1);
//        });

        getOrCreateChannel.addContextListener(context.getType(), context1 -> {
            appendMessage("app channel context listener triggered: " + context1.toString());
        });
    }

    @FXML
    void exportMessages(ActionEvent event) {}

    private Context buildContext() {
        Context context = new Context();
        context.setType(contextType.getText());
        if (contextId.getText() != null && !contextId.getText().isEmpty()) {
            String[] idFields = contextId.getText().split(",");
            Map<String, Object> contextId = new HashMap<>();
            for (String field : idFields) {
                String[] f = field.split(":");
                contextId.put(f[0], f[1]);
            }
            context.setId(contextId);
        }
        context.setName(contextName.getText());

        return context;
    }



    /**
     * Connect to Finsemble.
     */
    void connect() {
        if (fsbl != null) {
            // Has already connected once, and reconnecting doesn't work.
            return;
        }

        messages.setEditable(false);

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting JavaExample: %s", String.join(", ", args)));

        List<String> windowSize = new LinkedList<>();
        windowSize.add("width=1100");
        windowSize.add("height=840");
        windowSize.add("componentType=JavaDesktopAgent");

        setArguments(Stream.concat(args.stream(), windowSize.stream())
                .collect(Collectors.toList()));
        fsbl = new Finsemble(args, window);

        try {
            fsbl.connect();
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
                public void onWindowSateReady(ConnectionEventGenerator from) {
                    windowReady = true;
                    appendMessage("Window window state: READY");
                    try {
                        appendMessage("Registering with InteropService...");
                        registerWithInteropService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            appendMessage("Window registered with Finsemble");

            //GetComponentState
            final JSONArray getComponentStateFields = new JSONArray() {{
                put("Finsemble_Linker");
                put("symbol");
            }};
            final JSONObject getComponentStateParam = new JSONObject() {{
                put("fields", getComponentStateFields);
            }};
            // fsbl.getClients().getWindowClient().getComponentState(getComponentStateParam, this::handleGetComponentStateCb);


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
     * Adds a message to the message box.
     *
     * @param s The message to add.
     */
    void appendMessage(String s) {
        if (messages != null) {
            messages.appendText(String.format("\n%s", s));
        }
    }

    private void registerWithInteropService() throws Exception {

        LOGGER.info("Finsemble Desktop Agent registration started...");
        finsembleDesktopAgent = new FinsembleDesktopAgent(fsbl, new DesktopAgentEvents() {

            @Override
            public void onRegistrationFinished() {
                LOGGER.info("Registered with the InteropService");
                appendMessage("Registered with the InteropService");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setConnectionStatus(finsembleDesktopAgent.isRegistered());
                    }
                });

            }

            @Override
            public void onUnregister() {
                LOGGER.info("Finsemble Desktop Agent unregistered");
                appendMessage("Finsemble Desktop Agent unregistered");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setConnectionStatus(finsembleDesktopAgent.isRegistered());
                    }
                });
            }

            @Override
            public void onMessageSent(String message) {
                appendMessage(message);
            }

            @Override
            public void onMessageReceived(String message) {
                appendMessage(message);
            }

            @Override
            public void onCurrentChannelChanged(ChannelClient channelClient) {
                appendMessage("Current channel: " + channelClient.getId());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentChannelLabel.setText(channelClient.getId());
                    }
                });

            }
        });
    }

    private void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            connectionStatus.setFill(Color.LIMEGREEN);
            connectionStatusLabel.setText("Connected");
        } else {
            connectionStatus.setFill(Color.RED);
            connectionStatusLabel.setText("Offline");
        }
    }

    public Finsemble getFinsembleBridge() {
        return fsbl;
    }
}
