package io.interop.finsemble.example.fdc3;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.fdc3.versions.DesktopAgent1_2;
import com.chartiq.finsemble.fdc3.versions.DesktopAgent2_0;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.chartiq.finsemble.util.io.IOUtil;
import com.finsemble.fdc3.Channel;
import com.finsemble.fdc3.Context;
import com.finsemble.fdc3.FDC3;
import com.finsemble.fdc3.Listener;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FreestandingFinsembleFDC3Client extends AbstractFreestandingFDC3Client<DesktopAgent1_2, DesktopAgent2_0> {

    /**
     * Constructor, this prepares the UI.
     *
     * @param args the command line arguments
     */
    public FreestandingFinsembleFDC3Client(final String[] args) {
        super(args);
    }

    /**
     * The main entry to the program; no command line arguments are used.
     *
     * @param args command line arguments (ignored)
     */
    public static void main(final String[] args) {
        new FreestandingFinsembleFDC3Client(args).executeTests();
    }


    // The Finsemble instance
    private Finsemble finsemble;


    /**
     * Connects to the server.
     *
     * @throws Exception when something goes wrong
     */
    protected DesktopAgentFactory<DesktopAgent1_2, DesktopAgent2_0> connect() throws Exception {
        // Allow the user to select the host type
        final Finsemble.HostType hostType = getHostType();
        if (null != hostType) {
            final List<String> args = getArgs();
            final String hostTypeName = "hostType";

            // Set the "hostType" "command line argument" to coincide with the user's selection
            switch (hostType) {
                case finsemble -> IOUtil.addOrUpdateCommandLineArguments(hostTypeName, Finsemble.HostType.finsemble.name(), args);
                case iocd -> IOUtil.addOrUpdateCommandLineArguments(hostTypeName, Finsemble.HostType.iocd.name(), args);
                default -> {
                    JOptionPane.showMessageDialog(null, "Invalid selection, using auto-detect", "Error", JOptionPane.ERROR_MESSAGE);
                    IOUtil.addOrUpdateCommandLineArguments(hostTypeName, Finsemble.HostType.autodetect.name(), args);
                }
            }

            finsemble = new Finsemble(args, this);
            finsemble.setAppName(getApplicationName());

            finsemble.addShutdownHook(new CallbackListener() {
                @Override public void callback(JSONObject err, JSONObject res) {
                    final String reasonString = null == res ? "unknown" : res.toString();
                    output("Finsemble is shutting down: " + reasonString);
                }
                // Not necessary, but this makes the logs look a bit cleaner
                @Override public String toString() {
                    return "CallbackListener 1";
                }
            });

            finsemble.connect();

            return createDesktopAgentFactory(finsemble);
        } else {
            output("No host type selected; terminating");
            System.exit(0);
            return null;
        }

    }

    /**
     * Gets the host type from the user by displaying a selector.
     *
     * @return the desired host type
     */
    private Finsemble.HostType getHostType() {
        // Build the connection mode panel
        //
        // The radio buttons and group
        final JRadioButton optionAutodetect = new JRadioButton("Auto-Detect", true); // Initially selected
        optionAutodetect.setActionCommand(Finsemble.HostType.autodetect.name());
        //
        final JRadioButton optionLegacy = new JRadioButton("Legacy Finsemble");
        optionLegacy.setActionCommand(Finsemble.HostType.finsemble.name());
        //
        final JRadioButton optionIOCD = new JRadioButton("IOCD");
        optionIOCD.setActionCommand(Finsemble.HostType.iocd.name());
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


        // Get the user selection
        int result = JOptionPane.showConfirmDialog(null, optionPanel,"Radio Button Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (JOptionPane.OK_OPTION == result) {
            // Return the user selection as a Finsemble.HostType value
            return Finsemble.HostType.valueOf(modeButtonGroup.getSelection().getActionCommand());
        } else {
            // Return null (no user selection)
            return null;
        }
    }

    /**
     * Creates a factory to produce DesktopAgent instances.
     *
     * @param finsemble the Finsemble connection
     * @return a factor which produces DesktopAgent instances
     */
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
        desktopAgent.addContextListener("fdc3.instrument", context1 -> output("fdc31.2.onOnContext1:" + context1));
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

        desktopAgent.joinChannel("Channel 2");
        desktopAgent.addContextListener("fdc3.instrument", context1 -> output("fdc31.2.onOnContext2:" + context1));
    }

    @Override
    protected void testDesktopAgent_2_0(final DesktopAgent2_0 desktopAgent) {
        final String contextType = "fdc3.instrument";

        final String[] channelsToUse = Arrays.copyOf(desktopAgent.getSystemChannels().toCompletableFuture().join().stream().map(Channel::getId).sorted().toArray(String[]::new), 3);

        final JButton[] joinChannelButtons = createJoinChannelButtons(desktopAgent, channelsToUse);
        final JButton[] removeListenerButtons = createRemoveListenerButtons(channelsToUse);

        final JButton broadcastButton = new JButton("Broadcast Context");
        broadcastButton.addActionListener(e -> handleBroadcastContext(desktopAgent));

        final JButton broadcastCurrentChannelButton = new JButton("Broadcast Current Channel");
        broadcastCurrentChannelButton.addActionListener(e -> handleBroadcastCurrentChannel(desktopAgent));


        final JButton addContextListenerButton = new JButton("Add Context Listener");
        addContextListenerButton.addActionListener(e -> handleAddContextListener(desktopAgent, contextType));

        final JButton addChannelContextListenerButton = new JButton("Add Channel Context Listener");
        addChannelContextListenerButton.addActionListener(e -> handleAddChannelContextListener(desktopAgent, contextType));

        final JButton showListenersButton = new JButton("Show All Listeners");
        showListenersButton.addActionListener(e -> handleShowAllListeners());


        final JButton removeAllChannelContextListenersButton = new JButton("Remove All Channel Context Listeners");
        removeAllChannelContextListenersButton.addActionListener(e -> handleRemoveAllChannelContextListeners());

        final JButton removeAllContextListenersButton = new JButton("Remove All Context Listeners");
        removeAllContextListenersButton.addActionListener(e -> handleRemoveAllContextListeners());


        final JButton showCurrentChannelButton = new JButton("Show Current Channel");
        showCurrentChannelButton.addActionListener(e -> handleShowCurrentChannel(desktopAgent));

        final JButton showCurrentChannelsButton = new JButton("Show Current Channels");
        showCurrentChannelsButton.addActionListener(e -> handleShowCurrentChannels(desktopAgent));

        final JButton leaveCurrentChannelButton = new JButton("Leave Current (All) Channel");
        leaveCurrentChannelButton.addActionListener(e -> handleLeaveAllChannels(desktopAgent));

        addFunctionButtons("Join/Listeners", Stream.concat(Arrays.stream(joinChannelButtons), Arrays.stream(removeListenerButtons)).toArray(JButton[]::new));
        addFunctionButtons("Broadcast", broadcastButton, broadcastCurrentChannelButton);
        addFunctionButtons("Listeners", addContextListenerButton, addChannelContextListenerButton, showListenersButton, removeAllChannelContextListenersButton, removeAllContextListenersButton);
        addFunctionButtons("Channels", showCurrentChannelButton, showCurrentChannelsButton, leaveCurrentChannelButton);
    }

    //<editor-fold desc="Button handlers">
    /**
     * Handles the UI action when a channel should be joined.
     *
     * @param desktopAgent the DesktopAgent instance
     * @param channelId the channel to join
     */
    private void handleJoinChannel(final DesktopAgent2_0 desktopAgent, final String channelId) {
        output("Joining channel=" + channelId);
        desktopAgent.joinUserChannel(channelId).toCompletableFuture().join();
        output("Joined channel=" + channelId);
    }

    /**
     * Handles the UI action when context broadcast is requested.
     *
     * @param desktopAgent the DesktopAgent instance
     */
    private void handleBroadcastContext(final DesktopAgent2_0 desktopAgent) {
        final Channel currentChannel = desktopAgent.getCurrentChannel().toCompletableFuture().join();
        if (null != currentChannel) {
            final Context context = createContext(currentChannel.getId(), "broadcast");
            output("Broadcasting " + context);
            desktopAgent.broadcast(context).toCompletableFuture().join();
            output("Broadcasted");
        } else {
            output("Cannot broadcast because no channel is currently joined");
        }
    }

    /**
     * Handles the UI action when a broadcast on the current channel is requested.
     *
     * @param desktopAgent the DesktopAgent instance
     */
    private void handleBroadcastCurrentChannel(final DesktopAgent2_0 desktopAgent) {
        final Channel currentChannel = desktopAgent.getCurrentChannel().toCompletableFuture().join();
        if (null != currentChannel) {
            final Context context = createContext(currentChannel.getId(), "broadcastCurrentChannel");
            output("Broadcasting " + context);
            desktopAgent.getCurrentChannel().toCompletableFuture().join().broadcast(context).toCompletableFuture().join();
            output("Broadcasted");
        } else {
            output("Cannot broadcast because no channel is currently joined");
        }
    }

    /**
     * Handles the UI action when the current channel should be displayed.
     *
     * @param desktopAgent the DesktopAgent instance
     */
    private void handleShowCurrentChannel(final DesktopAgent2_0 desktopAgent) {
        final Channel currentChannel = desktopAgent.getCurrentChannel().toCompletableFuture().join();
        output("Current Channel: " + (null == currentChannel ? "NONE" : currentChannel.getId()));
    }

    /**
     * Handles the UI action when all the current channels should be displayed.
     *
     * @param desktopAgent the DesktopAgent instance
     */
    private void handleShowCurrentChannels(final DesktopAgent2_0 desktopAgent) {
        final List<Channel> currentChannels = desktopAgent.getCurrentChannels().toCompletableFuture().join();
        final String channelsString = 0 == currentChannels.size() ? "NONE" : getChannelNamesList(currentChannels);
        output("Current Channels: " + channelsString);
    }

    /**
     * Handles the UI action when a context listener is added.
     *
     * @param desktopAgent the DesktopAgent instance
     * @param contextType the context type
     */
    private void handleAddContextListener(final DesktopAgent2_0 desktopAgent, final String contextType) {
        output("Adding context listener, contextType=" + contextType);
        final Listener listener = desktopAgent.addContextListener("fdc3.instrument", (context, contextMetadata) -> output("context listener (" + contextType + "): context=" + context + ", contextMetadata=" + contextMetadata)).toCompletableFuture().join();
        output("Added context listener, contextType=" + contextType);
        addToContextListenerMap(contextType, listener);
    }

    /**
     * Handles the UI action when a channel adds a context listener.
     *
     * @param desktopAgent the DesktopAgent instance
     * @param contextType the context type
     */
    private void handleAddChannelContextListener(final DesktopAgent2_0 desktopAgent, final String contextType) {
        final Channel currentChannel = desktopAgent.getCurrentChannel().toCompletableFuture().join();
        if (null == currentChannel) {
            output("Cannot add a listener because no channel is joined");
        } else {
            final String channelId = currentChannel.getId();
            output("Adding channel context listener, channel=" + channelId + ", contextType=" + contextType);
            final Listener listener = currentChannel.addContextListener("fdc3.instrument", (context, contextMetadata) -> output(channelId + " channel context listener (" + contextType + "): context=" + context + ", contextMetadata=" + contextMetadata)).toCompletableFuture().join();
            output("Added channel context listener, channel=" + channelId + ", contextType=" + contextType);
            addToChannelListenerMap(channelId, listener);
        }
    }

    /**
     * Handles the UI action when all listeners are shown.
     */
    private void handleShowAllListeners() {
        showAllChannelListeners();
        showAllContextListeners();
    }

    /**
     * Handles the UI action when a channel removes a context listener
     */
    private void handleRemoveAllChannelContextListeners() {
        final int totalListenerCount = channelListenerMap.values().stream().mapToInt(List::size).sum();
        if (0 == totalListenerCount) {
            output("There are no channel context listeners to remove");
        } else {
            output("Removing (" + totalListenerCount + ") channel context listeners:");
            channelListenerMap.forEach((channel, value) -> {
                if (0 != value.size()) {
                    removeListenersForChannel(channel);
                }
            });
            output("Removed (" + totalListenerCount + ") channel context listeners:");
        }
    }

    /**
     * Handles the UI action when all context listeners are removed.
     */
    private void handleRemoveAllContextListeners() {
        final int totalListenerCount = contextListenerMap.values().stream().mapToInt(List::size).sum();
        if (0 == totalListenerCount) {
            output("There are no context listeners to remove");
        } else {
            output("Removing (" + totalListenerCount + ") context listeners:");
            contextListenerMap.forEach((channel, value) -> {
                if (0 != value.size()) {
                    removeListenersForContext(channel);
                }
            });
            output("Removed (" + totalListenerCount + ") context listeners:");
        }
    }

    /**
     * Handles the UI action when listeners are removed for a channel.
     *
     * @param channelId the channel to remove the listeners from
     */
    private void handleRemoveListenersForChannel(final String channelId) {
        removeListenersForChannel(channelId);
    }

    /**
     * Handles the UI action when all channels should be left.
     *
     * @param desktopAgent the DesktopAgent instance
     */
    private void handleLeaveAllChannels(final DesktopAgent2_0 desktopAgent) {
        final List<Channel> currentChannels = desktopAgent.getCurrentChannels().toCompletableFuture().join();
        if (0 == currentChannels.size()) {
            output("Cannot leave channels, there are no channels joined");
        } else {
            output("Leaving " + currentChannels.size() + " channels (" + getChannelNamesList(currentChannels) + ")");
            desktopAgent.leaveCurrentChannel();
            output("Left " + currentChannels.size() + " channels");
        }
    }

    //<editor-fold desc="Listener map helpers">
    private Map<String, List<Listener>> channelListenerMap = new HashMap<>();
    private Map<String, List<Listener>> contextListenerMap = new HashMap<>();

    /**
     * Adds a channel listener to the channel/listener map.
     *
     * @param channel the channel to add the listener to
     * @param listener the listener to add
     */
    private void addToChannelListenerMap(final String channel, final Listener listener) {
        if (!channelListenerMap.containsKey(channel)) {
            channelListenerMap.put(channel, new ArrayList<>());
        }
        if (null != listener) {
            channelListenerMap.get(channel).add(listener);
        }
    }

    /**
     * Adds a context listener to the context/listener map.
     *
     * @param contextType the context to add the listener to
     * @param listener the listener
     */
    private void addToContextListenerMap(final String contextType, final Listener listener) {
        if (!contextListenerMap.containsKey(contextType)) {
            contextListenerMap.put(contextType, new ArrayList<>());
        }
        if (null != listener) {
            contextListenerMap.get(contextType).add(listener);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Misc helpers">
    /**
     * Creates a Context instance.
     *
     * @param channelId the channel ID to add to
     * @param action the action of the context
     * @return a Context instance for the channel
     */
    private Context createContext(final String channelId, final String action) {
        return new Context(
                new HashMap<>() {{
                    put("ticker", "2x.Java." + channelId + "." + action + "." + System.currentTimeMillis());
                }},
                "fdc3.instrument",
                "fdc3.instrument"
        );
    }

    /**
     * Shows all the channel listeners.
     */
    private void showAllChannelListeners() {
        final AtomicInteger totalListenerCountAtomic = new AtomicInteger();
        final List<String> channelsListenerCount = new ArrayList<>();

        channelListenerMap.forEach((channelId, value) -> {
            final int count = value.size();
            if (0 != count) {
                totalListenerCountAtomic.addAndGet(count);
                channelsListenerCount.add(channelId + ": " + count);
            }
        });
        if (0 == totalListenerCountAtomic.get()) {
            output("There are no channel context listeners");
        } else {
            output("Channel Context Listeners (" + totalListenerCountAtomic.get() + "):");
            channelsListenerCount.forEach(s -> outputWithAdditionalIndent(s, 1));
        }
    }

    /**
     * Shows all context listeners.
     */
    private void showAllContextListeners() {
        final AtomicInteger totalListenerCountAtomic = new AtomicInteger();
        final List<String> channelsListenerCount = new ArrayList<>();

        contextListenerMap.forEach((channelId, value) -> {
            final int count = value.size();
            if (0 != count) {
                totalListenerCountAtomic.addAndGet(count);
                channelsListenerCount.add(channelId + ": " + count);
            }
        });
        if (0 == totalListenerCountAtomic.get()) {
            output("There are no context listeners");
        } else {
            output("Context Listeners (" + totalListenerCountAtomic.get() + "):");
            channelsListenerCount.forEach(s -> outputWithAdditionalIndent(s, 1));
        }
    }

    /**
     * Removes all listeners for a specified channel.
     *
     * @param channelId the channel to remove listeners from
     */
    private void removeListenersForChannel(final String channelId) {
        addToChannelListenerMap(channelId, null);

        final List<Listener> listeners = channelListenerMap.get(channelId);

        final int channelListenerCount = listeners.size();
        if (0 == channelListenerCount) {
            output("No channel context listeners to remove for channel: " + channelId);
        } else {
            output("Removing (" + channelListenerCount + ") channel context listeners for channel: " + channelId);
            listeners.forEach(Listener::unsubscribe);
            listeners.clear();
            output("Removed (" + channelListenerCount + ") channel context listeners for channel: " + channelId);
        }
    }

    /**
     * Removes all listeners for a specified context.
     *
     * @param context the context to remove all listeners from
     */
    private void removeListenersForContext(final String context) {
        addToContextListenerMap(context, null);

        final List<Listener> listeners = contextListenerMap.get(context);

        final int channelListenerCount = listeners.size();
        if (0 == channelListenerCount) {
            output("No context listeners to remove for context: " + context);
        } else {
            output("Removing (" + channelListenerCount + ") context listeners for context: " + context);
            listeners.forEach(Listener::unsubscribe);
            listeners.clear();
            output("Removed (" + channelListenerCount + ") context listeners for context: " + context);
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Create channel/listener buttons">

    /**
     * Creates the "Join Channel" buttons.
     *
     * @param desktopAgent the DesktopAgent instance to use
     * @param channelIds the channel ID's to create buttons for
     * @return the "Join Channel" buttons
     */
    private JButton[] createJoinChannelButtons(final DesktopAgent2_0 desktopAgent, final String[] channelIds) {
        return Arrays.stream(channelIds).map(channelId -> createJoinChannelButton(desktopAgent, channelId)).toList().toArray(new JButton[]{});
    }

    /**
     * Creates a "Join Channel" button, which will join the channel the button is associated with when clicked.
     *
     * @param desktopAgent the DesktopAgent to use
     * @param channelId the channel to create a button for
     * @return a "Join Channel" button
     */
    private JButton createJoinChannelButton(final DesktopAgent2_0 desktopAgent, final String channelId) {
        final JButton joinChannelButton = new JButton("Join " + channelId);
        joinChannelButton.addActionListener(e -> handleJoinChannel(desktopAgent, channelId));
        return joinChannelButton;
    }

    /**
     * Creates the "Remove Listeners" button for each of the specified channels.
     *
     * @param channelIds the channels to create the buttons for
     * @return the "Remove Listeners" buttons for each specified channel
     */
    private JButton[] createRemoveListenerButtons(final String[] channelIds) {
        return Arrays.stream(channelIds).map(this::createRemoveListenerButton).toList().toArray(new JButton[]{});
    }

    /**
     * Creates a "Remove Listeners" button for a specified channel.
     *
     * @param channelId the channel to remove the listeners from
     * @return a button which will remove listeners when clicked
     */
    private JButton createRemoveListenerButton(final String channelId) {
        final JButton removeListenersChannelButton = new JButton("Remove Listeners " + channelId);
        removeListenersChannelButton.addActionListener(e -> handleRemoveListenersForChannel(channelId));
        return removeListenersChannelButton;
    }
    //</editor-fold>

    //<editor-fold desc="Misc helpers">
    /**
     * Gets the names from a list of Channels.
     *
     * @param channels the list of Channels to get the names for
     * @return a list of names of the Channel instances in the list
     */
    private String getChannelNamesList(final List<Channel> channels) {
        final String[] channelNames = channels.stream().map(Channel::getId).toList().toArray(new String[]{});
        return String.join(", ", channelNames);
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
    //</editor-fold>

}