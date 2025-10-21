package io.interop.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.example.util.ExampleKeys;
import com.finsemble.fdc3.*;
import com.finsemble.fdc3.finsemble.FinsembleAppIdentifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import static io.interop.finsemble.example.fdc3.IgnoreException.*;

/**
 * An example to raise intents within an FDC3 2.0 Desktop Agent.
 */
public class JavaFDC3Example extends JFrame {

    // The resource name for the logging config
    private final static String RESOURCE_NAME_LOGGING_CONFIG = "logging.properties";

    // Configure the logger using "logging.properties"
    static {
        ignoreException(() -> LogManager.getLogManager().readConfiguration(JavaFDC3Example.class.getClassLoader().getResourceAsStream(RESOURCE_NAME_LOGGING_CONFIG)));
    }

    // The Finsemble connection
    private Finsemble finsemble;

    // The Desktop Agent
    private DesktopAgent desktopAgent;

    private final List<VoidZeroParamFunction> onConnectedHandlers = new ArrayList<>();
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
        // NOTE: The UI must be visible before Finsemble is connected
        setupUI();

        // Connect to Finsemble
        connectFinsemble();

        // Execute the onConnected handlers
        executeOnConnectedHandlers();
    }


    /**
     * Configures Finsemble. The command line arguments are passed in, the app name is set and a Finsemble
     * ConnectionListener is added.
     *
     * @param args command line arguments
     */
    private void configureFinsemble(final String[] args) {
        finsemble = new Finsemble(Arrays.stream(args).sequential().collect(Collectors.toList()), this);
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
        // Use the built-in RSA private key; this will only be used if dynamic auth cannot be used (i.e. standalone mode)
        desktopAgent = FDC3.createDesktopAgentInstance(finsemble, ExampleKeys.getPrivateRSAKey());
    }

    /**
     * Invoked to disconnect from Finsemble.
     **/
    private void disconnectFinsemble() {
        // Do our best to close
        try {
            finsemble.close();
        } catch (final Exception e) {
            System.err.println("could not disconnect: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Raises a ViewChart intent with the specified ticker.
     *
     * @param ticker the ticker to raise a ViewChart intent for
     */
    private void raiseIntent(final String ticker) {
        // Raise the intent and block until the intent has been raised
        ignoreException(() -> desktopAgent.raiseIntent(
                "ViewChart",
                createInstrumentContextWithTicker(ticker),
                (AppIdentifier) null
        ).toCompletableFuture().get());
    }

    /**
     * Opens the ChartIQ app with the specified ticker.
     *
     * @param ticker the ticker to open the ChartIQ app to
     */
    private void open(final String ticker) {
        ignoreException(() -> desktopAgent.open(
                new FinsembleAppIdentifier("ChartIQ Example App"),
                createInstrumentContextWithTicker(ticker)
        ).toCompletableFuture().get());
    }

    /**
     * Broadcasts a fdc3.instrument on the specified channel.
     *
     * @param userChannel the channel to bradcast on
     * @param ticker the instrument ticker to broadcast
     */
    private void broadcast(final String userChannel, final String ticker) {
        ignoreException(() -> {
            desktopAgent.joinUserChannel(userChannel).toCompletableFuture().get();
            desktopAgent.broadcast(
                    createInstrumentContextWithTicker(ticker)
            ).toCompletableFuture().get();
        });
    }

    /**
     * Gets a list of user channels from the DesktopAgent.
     *
     * @return an array of user channels from the DesktopAgent
     */
    private String[] getUserChannels() {
        return IgnoreException.ignoreException(() ->
                        desktopAgent.getUserChannels()
                                .toCompletableFuture().get()
                                .stream().map(Channel::getId)
                                .toList()
                                .toArray(new String[]{})
                , new String[]{});
    }

    /**
     * Creates a FDC3 "instrument" context with the specified ticker.
     *
     * @param ticker the ticker for the instrument
     * @return a FDC3 "instrument" context with the specified ticker
     */
    private Context createInstrumentContextWithTicker(final String ticker) {
        return new Context("fdc3.instrument") {{
            getId().put("ticker", ticker);
        }};
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
                ignoreException(JavaFDC3Example.this::disconnectFinsemble);
                super.windowClosing(windowEvent);
            }
        });


        // Create and configure the UI components
        //
        // The tabbed pane which will hold all tabs
        final JTabbedPane tabbedPane = new JTabbedPane();

        // Create the tabs
        //
        // The "raiseIntent" tab
        // The text field for the ticker name
        final JTextField tickerTextField = new JTextField("TSLA");
        // The button to raise the intent
        final JButton raiseIntentButton = new JButton("Raise Chart Intent");
        // Set the button to be disabled until Finsemble is connected
        raiseIntentButton.setEnabled(false);
        onConnectedHandlers.add(() -> raiseIntentButton.setEnabled(true));
        // The tab panel
        final JPanel raiseIntentPanel = new JPanel(new BorderLayout());
        // Add an action for when the intent button is clicked.
        raiseIntentButton.addActionListener(event -> raiseIntent(tickerTextField.getText().trim()));
        // Add the tabbed panel
        raiseIntentPanel.setLayout(new BorderLayout());
        raiseIntentPanel.add(tickerTextField, BorderLayout.NORTH);
        raiseIntentPanel.add(raiseIntentButton, BorderLayout.SOUTH);
        // Add the "raise intent" panel to the tabbed panel
        tabbedPane.addTab("RaiseIntent", raiseIntentPanel);

        // The "open" tab
        // The text field for the ticker name
        final JTextField openTickerTextField = new JTextField("MSFT");
        // The button to raise the intent
        final JButton openButton = new JButton("Open");
        // Set the button to be disabled until Finsemble is connected
        openButton.setEnabled(false);
        onConnectedHandlers.add(() -> openButton.setEnabled(true));
        // The tab panel
        final JPanel openPanel = new JPanel(new BorderLayout());
        // Add an action for when the intent button is clicked.
        openButton.addActionListener(event -> open(openTickerTextField.getText().trim()));
        // Add the tabbed panel
        openPanel.setLayout(new BorderLayout());
        openPanel.add(openTickerTextField, BorderLayout.NORTH);
        openPanel.add(openButton, BorderLayout.SOUTH);
        // Add the "open" panel to the tabbed panel
        tabbedPane.add("Open", openPanel);

        // The "broadcast" tab
        // The panel
        final JPanel broadcastPanel = new JPanel(new GridBagLayout());
        // The dropdown box for the user channels
        final JComboBox<String> broadcastChannelComboBox = new JComboBox<>();
        // The text field for the ticker name
        final JTextField broadcastTickerTextField = new JTextField("MSFT");
        // The user channels are only available AFTER Finsemble has connected; use an onConnected handler
        onConnectedHandlers.add(() -> {
            for (final String userChannel:getUserChannels()) {
                broadcastChannelComboBox.addItem(userChannel);
            }
        });
        // The button to raise the intent
        final JButton broadcastButton = new JButton("Broadcast");
        // Set the button to be disabled until Finsemble is connected
        broadcastButton.setEnabled(false);
        onConnectedHandlers.add(() -> broadcastButton.setEnabled(true));
        // Add an action for when the intent button is clicked.
        broadcastButton.addActionListener(event -> {
            if (null != broadcastChannelComboBox.getSelectedItem()) {
                broadcast(
                        broadcastChannelComboBox.getSelectedItem().toString(),
                        broadcastTickerTextField.getText().trim()
                );
            }
        });
        // GridBagConstraints
        final GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.gridy = 0;
        // Combobox
        cs.gridx = 0;
        cs.anchor = GridBagConstraints.WEST;
        cs.gridwidth = GridBagConstraints.RELATIVE;
        cs.weightx = 0;
        broadcastPanel.add(broadcastChannelComboBox, cs);
        // Ticker text field
        cs.gridx = 1;
        cs.gridwidth = GridBagConstraints.REMAINDER;
        cs.weightx = 100;
        broadcastPanel.add(broadcastTickerTextField, cs);
        // Button
        cs.gridx = 0;
        cs.gridy = 1;
        broadcastPanel.add(broadcastButton, cs);
        // Add the "broadcast" panel to the tabbed panel
        tabbedPane.add("Broadcast", broadcastPanel);


        // Layout and add the components
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        // Pack the components
        pack();
        // Set the window size
        setSize(600, 125);
        // Center the window
        setLocationRelativeTo(null);
        // Display the window
        setVisible(true);
    }

    /**
     * Executes any onConnected handlers for Finsemble.
     */
    private void executeOnConnectedHandlers() {
        // Safely handle each handler
        onConnectedHandlers.forEach(IgnoreException::ignoreException);
    }


    /**
     * The main point of entry.
     *
     * @param args command line arguments
     * @throws Exception when something goes wrong
     */
    public static void main(final String[] args) throws Exception {
        // Set the look and feel (it is OK to throw an exception here)
        ignoreException(() -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));

        // Open a new UI instance
        new JavaFDC3Example(args);
    }

}

//<editor-fold defaultstate="collapsed" desc="apps.json App Definition">
// {
//     "appId": "JavaFDC3Example",
//     "name": "JavaFDC3Example",
//     "type": "native",
// 	   "details": {
//        "path": "/path/to/javaw",
//        "arguments": "-jar $javaExampleJarRoot/JavaFDC3Example.jar"
// 	   },
// 	   "interop": {
//         "intents": {}
//     },
//     "hostManifests": {
//         "Finsemble": {
//             "window": {
//                 "windowType": "native",
//                 "addToWorkspace": false
//             },
//             "foreign": {
//                 "components": {
//                     "App Launcher": {
//                         "launchableByUser": true
//                     }
//                 }
//             },
//             "signatureKey": {
//                 "alg": "RS256",
//                 "e": "AQAB",
//                 "ext": true,
//                 "key_ops": [
//                     "verify"
//                 ],
//                 "kty": "RSA",
//                 "n": "x7HC2OMop_3mQVOSE8FdHTIf_aLJDtbz8vSwEnaMBjo-Sl2__FlRcxetooceTop8DnuCmYQjH3YKRPIdkC5yIkHsHj6gLMIQc5BJ5jGJBIaHR83ayzBc2tvX7JgNmee6MWjKWVXLRk-R6Dp0yWVr97oAkqzqQiUPTt45MtCfGnlePTj5XT1otLQ478zgpzBxtk1VaYOhzGEXIrWKG2jKO-CydbsJA5Az4NejqFSpexWk7U6Fy8cjN0B5s_tFlg_GlmHE89_N2SauBmCvcX_Pn4s38ZsxvR9Q2i_I4I4eocZ5ujAq9b0qTnrORHxwLPMV6YANqpz8C6Bnt46o2CerzQ"
//             }
//         }
//     }
// },
//</editor-fold>
