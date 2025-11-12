package io.interop.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A self-contained application which can test different FDC3 implementations.
 *
 */
public abstract class AbstractFreestandingFDC3Client<DesktopAgent_1_2, DesktopAgent_2_0> extends JFrame {

    static {
        // Make a call to Finsemble to configure the built-in logging
        Finsemble.configureLogging();
    }


    /**
     * Constructor, this prepares the UI.
     */
    public AbstractFreestandingFDC3Client() {
        configureUI();
    }


    /**
     * Execute the tests for the FDC3 implementation.
     */
    public void executeTests() {
        // Connect to the server
        final AtomicReference<DesktopAgentFactory<DesktopAgent_1_2, DesktopAgent_2_0>> desktopAgentFactoryAtomicReference = new AtomicReference<>();

        executeTest(MSG_CONNECTING, () -> desktopAgentFactoryAtomicReference.set(connect()), MSG_CONNECTED);

        // Test the FDC3 1.2 implementation
        executeTest(MSG_STARTING_DESKTOPAGENT_1_2_TEST, () -> testDesktopAgent_1_2(desktopAgentFactoryAtomicReference.get().getDesktopAgent_1_2()), MSG_ENDING_DESKTOPAGENT_1_2_TEST);

        // Test the FDC3 2.0 implementation
        executeTest(MSG_STARTING_DESKTOPAGENT_2_0_TEST, () -> testDesktopAgent_2_0(desktopAgentFactoryAtomicReference.get().getDesktopAgent_2_0()), MSG_ENDING_DESKTOPAGENT_2_0_TEST);
    }


    //<editor-fold desc="Testing and helper methods to be implemented">
    /**
     * Connects to the server.
     *
     * @throws Exception when something goes wrong
     */
    protected abstract DesktopAgentFactory<DesktopAgent_1_2, DesktopAgent_2_0> connect() throws Exception;


    /**
     * Run the FDC3 1.2 test.
     */
    protected void testDesktopAgent_1_2(final DesktopAgent_1_2 desktopAgent) {
        throw new UnsupportedOperationException("This implementation does not provide support for FDC3 1.2");
    }


    /**
     * Runs the FDC3 2.0 test.
     */
    protected void testDesktopAgent_2_0(final DesktopAgent_2_0 desktopAgent) {
        throw new UnsupportedOperationException("This implementation does not provide support for FDC3 2.0");
    }
    //</editor-fold>


    //<editor-fold desc="Helper methods">
    /**
     * Gets the application name for output.
     *
     * @return the application name for output
     */
    protected String getApplicationName() {
        return getClass().getSimpleName();
    }
    //</editor-fold>


    //<editor-fold desc="Helper test methods">
    /**
     * Executes one test, in a safe manner, with reasonable output formatting.
     *
     * @param beforeMessage the message to display before the test
     * @param function the function which implements the test
     * @param afterMessage the message to display after the test
     */
    protected void executeTest(final String beforeMessage, final ThrowingFunction function, final String afterMessage) {
        // Output a header row
        outputHeaderRow();

        // Output the "before message"
        outputAndUpdateHeader(beforeMessage);
        // Increase the indent
        indentCount++;
        try {
            // Run the tests
            function.apply();
        } catch (final Exception exception) {
            // Display the exception
            output(exception);
        } finally {
            // Decrease the indent
            indentCount--;
            // Output the "after message"
            outputAndUpdateHeader(afterMessage);
        }
    }
    //</editor-fold>


    //<editor-fold desc="Output methods">
    /**
     * Output to the UI console and update the header.
     *
     * @param message the message to use
     */
    protected void outputAndUpdateHeader(final String message) {
        headerLabel.setText(MSG_HEADER_PREFIX + " - " + message);
        output(message);
    }

    /**
     * Outputs a header row to the UI console and the console
     */
    protected void outputHeaderRow() {
        messageTextArea.append(MSG_HEADER + "\n");
        System.out.println(MSG_HEADER);
    }

    /**
     * Outputs a message to the UI console, the console and the logger. Indents are honors both in the UI console as
     * well as the console.
     *
     * @param message the message to output
     */
    protected void output(final String message) {
        final String timestampedMessage = "[" + new Date() + "]: " + decorateWithIndent(message);
        messageTextArea.append(timestampedMessage + "\n");
        LOGGER.info(message);
        System.out.println(timestampedMessage);
    }

    /**
     * Outputs a message to the UI console, the console and the logger. Indents are honors both in the UI console as
     * well as the console. Additional indents are used, as specified.
     *
     * @param message the message to output
     * @param additionalIndent the additional indent count (beyond the current indent count)
     */
    protected void outputWithAdditionalIndent(final String message, final int additionalIndent) {
        final String timestampedMessage = "[" + new Date() + "]: " + decorateWithIndent(message, indentCount + additionalIndent);
        messageTextArea.append(timestampedMessage + "\n");
        LOGGER.info(message);
        System.out.println(timestampedMessage);
    }


    /**
     * Outputs an exception to the console and also updates the header label.
     *
     * @param exception the thrown exception
     */
    protected void output(final Exception exception) {
        exception.printStackTrace();
        outputAndUpdateHeader("Error: " + exception.getLocalizedMessage());
    }
    //</editor-fold>


    //<editor-fold desc="Indent methods">
    // The indent count
    private int indentCount = 0;
    // The indent string
    private final String INDENT_STRING = "\t";

    /**
     * Adds the correct amount of indent, based on the indentCount.
     *
     * @param message the message to indent
     * @return the indented message
     */
    private String decorateWithIndent(final String message) {
        return decorateWithIndent(message, indentCount);
    }

    /**
     * Adds the correct amount of indent, based on the indentCount.
     *
     * @param message the message to indent
     * @param numberOfIndents the number of indents to add
     * @return the indented message
     */
    private String decorateWithIndent(final String message, int numberOfIndents) {
        return INDENT_STRING.repeat(Math.max(0, numberOfIndents)) + message;
    }
    //</editor-fold>


    //<editor-fold desc="UI elements">
    /**
     * Configures the UI.
     */
    protected void configureUI() {
        final String applicationName = getApplicationName();
        setTitle(applicationName);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerLabel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(messageTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // The header label
    private final JLabel headerLabel = new JLabel(MSG_HEADER_PREFIX);
    // The UI console
    private final JTextArea messageTextArea = new JTextArea(40, 120) {{
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setEditable(false);
        // Uncomment to make the text area appear as if it cannot be edited
        // setEnabled(false);
    }};
    //</editor-fold>


    //<editor-fold desc="Static members">
    // The logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFreestandingFDC3Client.class);

    // Strings for messages
    private static final String MSG_HEADER_PREFIX = "Connection Test";
    private static final String MSG_CONNECTING = "Connecting...";
    private static final String MSG_CONNECTED = "Connected!";
    private final static String MSG_STARTING_DESKTOPAGENT_1_2_TEST = "Starting DesktopAgent 1 test";
    private final static String MSG_ENDING_DESKTOPAGENT_1_2_TEST = "DesktopAgent 1 test complete";
    private final static String MSG_STARTING_DESKTOPAGENT_2_0_TEST = "Starting DesktopAgent 2 test";
    private final static String MSG_ENDING_DESKTOPAGENT_2_0_TEST = "DesktopAgent 2 test complete";
    private final static String MSG_HEADER = "================================================================================================================================================================";
    //</editor-fold>


//    /**
//     * A functional interface declaring a function which may throw.
//     */
//    @FunctionalInterface
//    interface ThrowingFunction extends io.interop.finsemble.example.fdc3.ThrowingFunction {
//        void apply() throws Exception;
//    }

}

