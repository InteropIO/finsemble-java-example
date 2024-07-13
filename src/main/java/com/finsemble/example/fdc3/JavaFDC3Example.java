package com.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import com.finsemble.fdc3.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

/**
 * An example to raise intents within an FDC3 2.0 Desktop Agent.
 */
public class JavaFDC3Example extends JFrame {

    // The resource name for the logging config
    private final static String RESOURCE_NAME_LOGGING_CONFIG = "logging.properties";

    // Configure the logger using "logging.properties"
    static {
        try {
            LogManager.getLogManager().readConfiguration(JavaFDC3Example.class.getClassLoader().getResourceAsStream(RESOURCE_NAME_LOGGING_CONFIG));
        } catch (Exception exception) {
            System.err.println("Could not set the logging config from '" + RESOURCE_NAME_LOGGING_CONFIG + "'");
            exception.printStackTrace(System.err);
        }
    }

    // The text field for the ticker name
    private final JTextField tickerTextField = new JTextField("TSLA");
    // The button to raise the intent
    private final JButton raiseIntentButton = new JButton("Raise Chart Intent");

    // The Finsemble connection
    private Finsemble finsemble;

    // The Desktop Agent
    private DesktopAgent desktopAgent;

    // Connection listener (for logging)
    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void disconnected(ConnectionEventGenerator connectionEventGenerator) {
            System.err.println("disconnected");
            setVisible(false);
            dispose();
        }

        @Override
        public void error(ConnectionEventGenerator connectionEventGenerator, Exception e) {
            System.err.println("error");
            e.printStackTrace();
        }

        @Override
        public void onWindowStateReady(ConnectionEventGenerator connectionEventGenerator) {
            System.err.println("onWindowStateReady");
        }
    };


    /**
     * Constructor for the application class.
     *
     * @param args command line arguments
     * @throws Exception when something goes wrong
     */
    public JavaFDC3Example(final String[] args) throws Exception {
        // Configure Finsemble
        configureFinsemble(args);

        // Set up the UI
        setupUI();

        // Connect to Finsemble
        connectFinsemble();
    }

    /**
     * Configures Finsemble. The command line arguments are passed in, the app name is set and a Finsemble
     * ConnectionListener is added.
     *
     * @param args command line arguments
     */
    private void configureFinsemble(final String[] args) {
        finsemble = new Finsemble(Arrays.stream(args).sequential().collect(Collectors.toList()), this);
        finsemble.setAppName(getClass().getSimpleName());
        finsemble.addListener(connectionListener);
    }

    /**
     * Connects to Finsemble and instantiates a DesktopAgent with the Finsemble connection.
     *
     * @throws Exception when Finsemble cannot be connected to
     */
    private void connectFinsemble() throws Exception {
        // Connect Finsemble
        finsemble.connect();

        // This helper method will create an FDC3 2.0 instance; the FDC3 2.0 API is specified in
        // com.finsemble.fdc3.DesktopAgent (whereas the 1.2 spec is in com.chartiq.finsemble.fdc3.DesktopAgent).
        desktopAgent = FDC3.createDesktopAgentInstance(finsemble);
    }

    /**
     * Sets up the UI. Component are created, listeners added and the window is displayed in the center of the screen.
     */
    private void setupUI() {
        // Set the default close operation
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Add a window listener to disconnect from Finsemble when the window is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    disconnectFinsemble();
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
                super.windowClosing(windowEvent);
            }
        });

        // Add an action for when the intent button is clicked.
        raiseIntentButton.addActionListener(e -> {
            try {
                raiseIntent(tickerTextField.getText().trim());
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        });

        // Layout and add the components
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tickerTextField, BorderLayout.NORTH);
        getContentPane().add(raiseIntentButton, BorderLayout.SOUTH);
        // Pack the components
        pack();
        // Center the window
        setLocationRelativeTo(null);
        // Display the window
        setVisible(true);
    }

    /**
     * Invoked to disconnect from Finsemble.
     *
     * @throws Exception when Finsemble cannot be disconnected
     */
    private void disconnectFinsemble() throws Exception {
        finsemble.removeListener(connectionListener);
        finsemble.close();
    }

    /**
     * Raises a ViewChart intent with the specified ticker.
     *
     * @param ticker the ticker to raise a ViewChart intent for
     * @throws Exception when the DesktopAgent cannot raise an intent
     */
    private void raiseIntent(final String ticker) throws Exception {
        // Raise the intent and block until the intent has been raised
        desktopAgent.raiseIntent("ViewChart", new Context("fdc3.instrument") {{
            getId().put("ticker", ticker);
        }}, (AppIdentifier) null).toCompletableFuture().get();
    }


    /**
     * The main point of entry.
     *
     * @param args command line arguments
     * @throws Exception when something goes wrong
     */
    public static void main(String[] args) throws Exception {
        new JavaFDC3Example(args);
    }

}
