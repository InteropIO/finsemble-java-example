package io.interop.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.fdc3.versions.DesktopAgent1_2;
import com.chartiq.finsemble.fdc3.versions.DesktopAgent2_0;
import com.finsemble.fdc3.Context;
import com.finsemble.fdc3.FDC3;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;

public class FreestandingFinsembleFDC3Client extends AbstractFreestandingFDC3Client<DesktopAgent1_2, DesktopAgent2_0> {

    // Enum for the connection modes
    private enum MODES {
        autodetect,
        legacy,
        iocd
    }


    // The Finsemble instance
    private Finsemble finsemble;


    /**
     * The main entry to the program; no command line arguments are used.
     *
     * @param args command line arguments (ignored)
     */
    public static void main(final String[] args) {
        new FreestandingFinsembleFDC3Client().executeTests();
    }

    /**
     * Connects to the server.
     *
     * @throws Exception when something goes wrong
     */
    protected DesktopAgentFactory<DesktopAgent1_2, DesktopAgent2_0> connect() throws Exception {
        // Build the connection mode panel
        //
        // The radio buttons and group
        final JRadioButton optionAutodetect = new JRadioButton("Auto-Detect", true); // Initially selected
        optionAutodetect.setActionCommand(MODES.autodetect.name());
        //
        final JRadioButton optionLegacy = new JRadioButton("Legacy Finsemble");
        optionLegacy.setActionCommand(MODES.legacy.name());
        //
        final JRadioButton optionIOCD = new JRadioButton("IOCD");
        optionIOCD.setActionCommand(MODES.iocd.name());
        //
        final ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(optionAutodetect);
        modeButtonGroup.add(optionLegacy);
        modeButtonGroup.add(optionIOCD);
        //
        //
        // The panel
        final JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.add(optionAutodetect);
        optionPanel.add(optionLegacy);
        optionPanel.add(optionIOCD);


        int result = JOptionPane.showConfirmDialog(
                null,                    // Parent component (null for centered on screen)
                optionPanel,                   // The custom panel containing the radio buttons
                "Radio Button Input",    // Dialog title
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (JOptionPane.OK_OPTION == result) {
            final String[] args;
            switch (MODES.valueOf(modeButtonGroup.getSelection().getActionCommand())) {
                case autodetect -> args = new String[]{};
                case legacy -> args = new String[]{"hostType=finsemble"};
                case iocd -> args = new String[]{"hostType=iocd"};
                default -> {
                    JOptionPane.showMessageDialog(null, "Invalid selection, using auto-detect", "Error", JOptionPane.ERROR_MESSAGE);
                    args = new String[]{};
                }
            }

            finsemble = new Finsemble(Arrays.asList(args), this);
            finsemble.setAppName(getApplicationName());
            finsemble.connect();

            return createDesktopAgentFactory(finsemble);
        } else {
            System.exit(0);
            return null;
        }

    }

    protected DesktopAgentFactory<DesktopAgent1_2, DesktopAgent2_0> createDesktopAgentFactory(final Finsemble finsemble) {
        return new DesktopAgentFactory<>() {
            @Override
            public DesktopAgent1_2 getDesktopAgent_1_2() throws Exception {
                // Get the implementation instance
                output("Creating DesktopAgent...");
                final DesktopAgent1_2 desktopAgent = FDC3.createDesktopAgent1_2Instance(finsemble);
                output("DesktopAgent created");


                // Output metadata about the implementation
                com.chartiq.finsemble.fdc3.meta.ImplementationMetadata implementationMetadata = desktopAgent.getInfo().get();
                outputWithAdditionalIndent("FDC3 Version:          " + implementationMetadata.getFdc3Version(), 1);
                outputWithAdditionalIndent("FDC3 Provider:         " + implementationMetadata.getProvider(), 1);
                outputWithAdditionalIndent("FDC3 Provider Version: " + implementationMetadata.getProviderVersion(), 1);


                return desktopAgent;
            }

            @Override
            public DesktopAgent2_0 getDesktopAgent_2_0() throws Exception {
                // Get the implementation instance
                output("Creating DesktopAgent...");
                final DesktopAgent2_0 desktopAgent = FDC3.createDesktopAgent2_0Instance(finsemble);
                output("DesktopAgent created");


                // Output metadata about the implementation
                com.finsemble.fdc3.ImplementationMetadata implementationMetadata = desktopAgent.getInfo().toCompletableFuture().get();
                outputWithAdditionalIndent("FDC3 Version:          " + implementationMetadata.getFdc3Version(), 1);
                outputWithAdditionalIndent("FDC3 Provider:         " + implementationMetadata.getProvider(), 1);
                outputWithAdditionalIndent("FDC3 Provider Version: " + implementationMetadata.getProviderVersion(), 1);
                //
                outputWithAdditionalIndent("AppMetadata:", 1);
                outputWithAdditionalIndent("AppId:       " + implementationMetadata.getAppMetadata().getAppId(), 2);
                outputWithAdditionalIndent("Title:       " + implementationMetadata.getAppMetadata().getTitle(), 2);
                outputWithAdditionalIndent("Name:        " + implementationMetadata.getAppMetadata().getName(), 2);
                outputWithAdditionalIndent("InstanceId:  " + implementationMetadata.getAppMetadata().getInstanceId(), 2);
                outputWithAdditionalIndent("Version:     " + implementationMetadata.getAppMetadata().getVersion(), 2);
                outputWithAdditionalIndent("Description: " + implementationMetadata.getAppMetadata().getDescription(), 2);
                outputWithAdditionalIndent("ResultType:  " + implementationMetadata.getAppMetadata().getResultType(), 2);
                outputWithAdditionalIndent("ToolTip:     " + implementationMetadata.getAppMetadata().getToolTip(), 2);
                //
                outputWithAdditionalIndent("ImplementationMetadata:", 1);
                outputWithAdditionalIndent("isOriginatingAppMetadata:    " + implementationMetadata.getOptionalFeatures().isOriginatingAppMetadata(), 2);
                outputWithAdditionalIndent("isUserChannelMembershipAPIs: " + implementationMetadata.getOptionalFeatures().isUserChannelMembershipAPIs(), 2);


                return desktopAgent;
            }
        };
    }

    @Override
    protected void testDesktopAgent_1_2(final DesktopAgent1_2 desktopAgent) {
        // Join "Channel 1"
        final String channelId = "Channel 1";
        output("Joining channel=" + channelId);
        desktopAgent.joinChannel(channelId);//.get();
        // There is a problem with the FDC3 1.2 api whereby Finsemble does not notify the DesktopAgent when the channel
        // has been joined so we insert a short wait to be sure that the channel has been joined; a more robust
        // solution would be to poll until the current channels contains the channelId
        waitFor(2000);
        //
        output("Joined channel=" + channelId);


        // Broadcast a context
        final Context context = new Context(
                new HashMap<>() {{
                    put("ticker", getApplicationName() + System.currentTimeMillis());
                }},
                "fdc3.instrument",
                "fdc3.instrument"
        );
        output("Broadcasting context=" + context + "...");
        desktopAgent.broadcast(context);
        output("Broadcast complete");
    }

    @Override
    protected void testDesktopAgent_2_0(final DesktopAgent2_0 desktopAgent) {
        // Join "Channel 1"
        final String channelId = "Channel 1";
        output("Joining channel=" + channelId);
        desktopAgent.joinUserChannel(channelId).toCompletableFuture().join();
        output("Joined channel=" + channelId);


        // Broadcast a context
        final Context context = new Context(
                new HashMap<>() {{
                    put("ticker", getApplicationName() + System.currentTimeMillis());
                }},
                "fdc3.instrument",
                "fdc3.instrument"
        );
        output("Broadcasting context=" + context + "...");
        desktopAgent.broadcast(context).toCompletableFuture().join();
        output("Broadcast complete");
    }

    /**
     * Pauses for a set time.
     *
     * @param milliseconds the number of milliseconds to pause for
     */
    private void waitFor(final int milliseconds) {
        try{Thread.sleep(milliseconds);} catch (final Exception exception){
            // There is nothing to do here
        }
    }

}