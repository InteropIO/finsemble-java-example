package com.chartiq.finsemble.example.iocd;

import com.chartiq.finsemble.Finsemble;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * An example application demonstrating:
 *   - RouterClient.addResponder / RouterClient.query
 *   - RouterClient.addListener / RouterClient.transmit
 *
 * This application can be run in two modes:
 *   - "send" mode, whereby the UI permits transmit and query
 *   - "listen" mode, whereby the UI sets up a query responder and listener
 *
 * If any of the command line arguments is "mode=listen", then the application will open in "listen" mode. If no command
 * line argument is "mode=listen" then the application will open in "send" mode.
 *
 * This demonstration requires two instances to be running concurrently - one in "listen" mode and one in "send" mode.
 *
 * In "listen" mode, the "Echo Responder" tab shows the contents of incoming queries on the "echo" channel and the
 * "Listener" tab shows incoming messages transmitted on the "listener" channel.
 *
 * In "send" mode, the "Echo Query" tab allows for messages to be sent. In this case, the message will be sent and
 * is visible in the "listen" mode instance on the "Echo Responder" tab. As well, the response from the query will
 * be visible in the "send" node instance. The "Transmit" tab will allow for messages to be transmitted and the "listen"
 * mode instance will realize these messages in the "Listener" tab. Note that RouterClient.transmit() does NOT have
 * a call back and consequently the "send" mode instance will have no confirmation in the text area.
 *
 * NOTE: In order to run against IOCD, the pom.xml dependency for Finsemble must be changed to an IOCD dep. This looks
 *       something like:
 *
 *       <dependency>
 *           <groupId>com.chartiq.finsemble</groupId>
 *           <artifactId>finsemble</artifactId>
 *           <!-- <version>8.14.3</version> -->
 *           <version>10.0.0-beta-41</version>
 *       </dependency>
 */
public class IOCDExample extends JFrame {

    /**
     * Main entry point; creates an instance of the IOCDExample frame.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new IOCDExample(args);
    }


    // The date formatter
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    //
    // Constants for channels
    private final static String RESPONDER_CHANNEL_ECHO = "echo";
    private final static String LISTENER_CHANNEL_LISTENER = "listener";
    // Constants for response parsing
    private final static String KEY_PAYLOAD = "payload";
    private final static String KEY_DATA = "data";
    //
    // Constants for text area sizes
    private final static int DIMENSION_TEXTAREA_COLS = 80;
    private final static int DIMENSION_TEXTAREA_ROWS = 10;
    // Constants for UI messages
    private final static String MESSAGE_CONNECTING = "Connecting...";
    private final static String MESSAGE_CONNECTED = "Connected";


    // This promise will resolve to Finsemble upon connection
    private CompletableFuture<Finsemble> finsembleConnectionPromise;


    /**
     * Constructor, taking the command line arguments.
     *
     * @param args the command line arguments (passed to the Finsemble constructor)
     */
    public IOCDExample(final String[] args) {
        // Start connecting async (no need to wait)
        connectAsync(args);

        // Set the look and feel
        trySetLookAndFeel();

        // Release resources on close
        setupCloseListener();

        // Build the UI
        setupUI(args);

        // Show the UI
        showUI();
    }

    /**
     * Starts the connection in a new thread.
     *
     * @param args the command line args to send to the Finsemble constructor
     * @return a CompletionStage which resolves to a connected Finsemble instance
     */
    private CompletionStage<Finsemble> connectAsync(final String[] args) {
        // Only one connection is allowed; return any existing promises if they exist
        if (null != finsembleConnectionPromise) {
            return finsembleConnectionPromise;
        }

        // Instantiate the promise
        finsembleConnectionPromise = new CompletableFuture<>();

        // Connect in a new thread
        new Thread(() -> {
            try {
                // Instantiate the Finsemble instance
                final Finsemble finsemble = new Finsemble(args);
                // Connect
                finsemble.connect();
                // Resolve the promise
                finsembleConnectionPromise.complete(finsemble);
            } catch (final Exception exception) {
                // Reject the promise
                finsembleConnectionPromise.completeExceptionally(exception);
            }
        }).start();

        // Return the promise
        return finsembleConnectionPromise;
    }

    /**
     * Attempts to set the look and feel to the system look and feel. Any exception will be logged and the look and feel
     * will remain as the JRE's default look and feel.
     */
    private void trySetLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception exception) {
            // Ignore but log
            System.err.println("WARNING: Could not set the system look and feel: " + exception.getLocalizedMessage());
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Setup a window close listener in order to release any resources
     */
    private void setupCloseListener() {

        // Add a window listener (for close events)
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(final WindowEvent windowEvent) {
                super.windowClosing(windowEvent);

                // Try to close the Finsemble connection
                finsembleConnectionPromise.thenAccept(finsemble -> {
                    try {finsemble.close();}catch(final Exception exception){/* No op */}
                });
            }
        });

        // Exit the application when this window closes
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Sets up the UI. The visible tabs will depend on the mode ("send" or "listen").
     *
     * @param args command line arguments; if "mode=listen" is present then "listen" mode is activated
     */
    private void setupUI(final String[] args) {
        // The modes
        final String MODE_SEND = "Send";
        final String MODE_LISTEN = "Listen";
        // Determine the mode
        final String mode = Arrays.stream(args).anyMatch(arg -> arg.toLowerCase(Locale.ROOT).equals("mode=" + MODE_LISTEN.toLowerCase(Locale.ROOT))) ? MODE_LISTEN : MODE_SEND;


        // Set the title (based on the mode)
        setTitle(mode + " [CONNECTING]");
        // Change the title once Finsemble connects
        finsembleConnectionPromise.thenRun(() -> setTitle(mode + " [CONNECTED]"));


        // The content pane for this window
        final JPanel contentPane = new JPanel();
        // A tabbed pane
        final JTabbedPane tabbedPane = new JTabbedPane();


        // Switch on mode
        switch (mode) {
            case MODE_SEND -> {
                tabbedPane.add("Echo Query", buildQueryPanel());
                tabbedPane.add("Transmit", buildTransmitPanel());
            }
            case MODE_LISTEN -> {
                tabbedPane.add("Echo Responder", buildResponderPanel());
                tabbedPane.add("Listener", buildListenerPanel());
            }
        }


        // Set the layout
        contentPane.setLayout(new BorderLayout());
        // Add the tabbed pane to the content pane
        contentPane.add(tabbedPane, BorderLayout.CENTER);


        // Set the content pane for this window
        this.setContentPane(contentPane);
    }

    /**
     * Shows the UI after packing and centering.
     */
    private void showUI() {
        this.pack();

        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    /**
     * Builds a query panel demonstrating RouterClient.query functionality. This includes a text field to enter
     * messages, a "send" button and a text area to display logging.
     *
     * @return a panel allowing for RouterClient.query calls to be invoked
     */
    private JPanel buildQueryPanel() {
        // The components for the panel
        //
        // The main panel
        final JPanel echoQueryPanel = new JPanel(new BorderLayout());
        //
        // The UI elements for the panel
        final JTextField echoQueryTextField = new JTextField(DIMENSION_TEXTAREA_COLS);
        final JButton echoQueryButton = new JButton("Send");
        final JTextArea echoQueryResponseTextArea = new JTextArea(DIMENSION_TEXTAREA_ROWS, DIMENSION_TEXTAREA_COLS);


        // Notify the UI that connection has started
        appendMessageToTextArea(echoQueryResponseTextArea, MESSAGE_CONNECTING);


        // Configure UI components
        echoQueryButton.setEnabled(false);
        echoQueryButton.addActionListener(e -> {
            final String queryMessage = echoQueryTextField.getText().trim();
            appendMessageToTextArea(echoQueryResponseTextArea, "Querying: " + queryMessage);
            finsembleConnectionPromise.join().getClients().getRouterClient().query(
                    RESPONDER_CHANNEL_ECHO,
                    new JSONObject() {{put(KEY_PAYLOAD, queryMessage);}},
                    null,
                    (err, res) -> appendMessageToTextArea(echoQueryResponseTextArea, "RECEIVED RESPONSE: " + getPayloadString(err, res))
            );
        });
        echoQueryResponseTextArea.setEditable(false);


        // After connection, update the UI
        finsembleConnectionPromise.thenRun(() -> {
            appendMessageToTextArea(echoQueryResponseTextArea, MESSAGE_CONNECTED);
            echoQueryButton.setEnabled(true);
        });


        // Build the panel
        //
        // The panel across the top, holds the text field and send button
        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(echoQueryTextField, BorderLayout.CENTER);
        topPanel.add(echoQueryButton, BorderLayout.EAST);
        echoQueryPanel.add(topPanel, BorderLayout.NORTH);
        //
        // The response panel
        echoQueryPanel.add(new JScrollPane(echoQueryResponseTextArea), BorderLayout.CENTER);


        // Return the panel
        return echoQueryPanel;
    }

    /**
     * Builds a panel used to showcase the RouterClient.transmit functionality. This includes a text field to enter
     * messages, a "send" button and a text area to display logging.
     *
     * @return a panel allowing for RouterClient.transmit calls to be invoked
     */
    private JPanel buildTransmitPanel() {
        // The components for the panel
        //
        // The main panel
        final JPanel transmitPanel = new JPanel(new BorderLayout());
        //
        // The UI elements for the panel
        final JTextField transmitTextField = new JTextField(DIMENSION_TEXTAREA_COLS);
        final JButton transmitButton = new JButton("Send");
        final JTextArea transmitResponseTextArea = new JTextArea(DIMENSION_TEXTAREA_ROWS, DIMENSION_TEXTAREA_COLS);


        // Notify the UI that connection has started
        appendMessageToTextArea(transmitResponseTextArea, MESSAGE_CONNECTING);


        // Configure UI components
        transmitButton.setEnabled(false);
        transmitButton.addActionListener(e -> {
            final String transmitMessage = transmitTextField.getText().trim();
            appendMessageToTextArea(transmitResponseTextArea, "Transmitting: " + transmitMessage);

            finsembleConnectionPromise.join().getClients().getRouterClient().transmit(
                    LISTENER_CHANNEL_LISTENER,
                    new JSONObject() {{put(KEY_PAYLOAD, transmitMessage);}});
        });
        transmitResponseTextArea.setEditable(false);


        // After connection, update the UI
        finsembleConnectionPromise.thenRun(() -> {
            transmitButton.setEnabled(true);
            appendMessageToTextArea(transmitResponseTextArea, MESSAGE_CONNECTED);
        });


        // Build the panel
        //
        // The panel across the top, holds the text field and send button
        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transmitTextField, BorderLayout.CENTER);
        topPanel.add(transmitButton, BorderLayout.EAST);
        transmitPanel.add(topPanel, BorderLayout.NORTH);
        //
        // The response panel
        transmitPanel.add(new JScrollPane(transmitResponseTextArea), BorderLayout.CENTER);


        // Return the panel
        return transmitPanel;
    }

    /**
     * Builds a panel used to demonstrate the RouterClient.addResponder functionality. This includes a text area to
     * display logging.
     *
     * @return a panel showing how to use the RouterClient.addResponder method
     */
    private JPanel buildResponderPanel() {
        // The components for the panel
        //
        // The main panel
        final JPanel echoResponderPanel = new JPanel(new BorderLayout());
        //
        // The UI elements for the panel
        final JTextArea echoResponderTextArea = new JTextArea(DIMENSION_TEXTAREA_ROWS, DIMENSION_TEXTAREA_COLS);


        // Configure UI components
        echoResponderTextArea.setEditable(false);


        // Build the panel
        echoResponderPanel.add(new JScrollPane(echoResponderTextArea), BorderLayout.CENTER);


        // Add the responder
        appendMessageToTextArea(echoResponderTextArea, MESSAGE_CONNECTING);
        //
        finsembleConnectionPromise.thenAccept(finsemble -> {
            appendMessageToTextArea(echoResponderTextArea, MESSAGE_CONNECTED);
            appendMessageToTextArea(echoResponderTextArea, "Adding query responder for '" + RESPONDER_CHANNEL_ECHO + "'");
            finsemble.getClients().getRouterClient().addResponder(
                    RESPONDER_CHANNEL_ECHO,
                    (err, queryMessageResponse) -> {
                        final String payloadString = getPayloadString(err, queryMessageResponse.getQueryMessage());

                        // Log the query
                        appendMessageToTextArea(echoResponderTextArea, "RECEIVED QUERY: " + payloadString);

                        // Echo back the response
                        queryMessageResponse.sendQueryResponse(null, queryMessageResponse.getQueryMessage());
                    });
            //
            appendMessageToTextArea(echoResponderTextArea, "Query responder added for '" + RESPONDER_CHANNEL_ECHO + "'");
        });


        // Return the panel
        return echoResponderPanel;
    }

    /**
     * Builds a panel to illustrate the RouterClient.addListener functionality. This includes a text area to display
     * logging.
     *
     * @return a panel showing how to use the RouterClient.addListener method
     */
    private JPanel buildListenerPanel() {
        // The components for the panel
        //
        // The main panel
        final JPanel listenPanel = new JPanel(new BorderLayout());
        //
        // The UI elements for the panel
        final JTextArea listenerTextArea = new JTextArea(DIMENSION_TEXTAREA_ROWS, DIMENSION_TEXTAREA_COLS);


        // Configure UI components
        listenerTextArea.setEditable(false);


        // Build the panel
        listenPanel.add(new JScrollPane(listenerTextArea), BorderLayout.CENTER);


        // Add the listener
        appendMessageToTextArea(listenerTextArea, MESSAGE_CONNECTING);
        //
        finsembleConnectionPromise.thenAccept(finsemble -> {
            appendMessageToTextArea(listenerTextArea, MESSAGE_CONNECTED);
            appendMessageToTextArea(listenerTextArea, "Adding listener for '" + LISTENER_CHANNEL_LISTENER + "'");
            finsemble.getClients().getRouterClient().addListener(
                    LISTENER_CHANNEL_LISTENER,
                    (err, res) -> appendMessageToTextArea(listenerTextArea, "RECEIVED TRANSMIT: " + getPayloadString(err, res))
            );
            //
            appendMessageToTextArea(listenerTextArea, "Listener added for '" + LISTENER_CHANNEL_LISTENER + "'");
        });


        // Return the panel
        return listenPanel;
    }

    /**
     * Appends messages to a text area using a uniform format.
     *
     * @param jTextArea the text area to append
     * @param message the message to append
     */
    private void appendMessageToTextArea(final JTextArea jTextArea, final String message) {
        final String messageToAppend = jTextArea.getText() + "\n" +
                "[" + DATE_FORMAT.format(new Date()) + "] " +
                message;
        jTextArea.setText(messageToAppend.trim());
    }

    /**
     * Gets the "payload" as a string from a JSONObject (usually a response).
     *
     * @param err the error object
     * @param res the response object
     * @return a string which contains the error (if present), the "payload" value as a string or "no data" otherwise
     */
    private String getPayloadString(final JSONObject err, final JSONObject res) {
        final String NO_DATA = "no data";
        if (null != err) {
            return "error: " + err.toString(2);
        } else if (null != res) {
            if (res.has(KEY_PAYLOAD)) {
                return res.get(KEY_PAYLOAD).toString();
            } else {
                // This code works with both the query responder getQueryMessage method as well as the response from
                // a callback (the paths may be different in these cases).
                return Optional.ofNullable(res.optQuery("/" + KEY_DATA + "/" + KEY_PAYLOAD))
                        .orElse(Optional.ofNullable(res.optQuery("/" + KEY_DATA + "/" + KEY_DATA + "/" + KEY_PAYLOAD))
                                .orElse(NO_DATA)).toString();
            }
        } else {
            return NO_DATA;
        }
    }

}
