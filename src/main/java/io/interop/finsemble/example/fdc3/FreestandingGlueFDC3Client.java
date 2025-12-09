package io.interop.finsemble.example.fdc3;

import com.tick42.glue.Glue;
import com.tick42.glue.internal.Tick42Glue;
import io.interop.api.core.DesktopAgent;
import io.interop.api.metadata.ImplementationMetadata;
import io.interop.api.types.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class uses the Freestanding FDC3 Test Client system to test using a Glue-specific FDC3 API.
 */
public class FreestandingGlueFDC3Client extends AbstractFreestandingFDC3Client<Void, DesktopAgent> {

    /**
     * The main entry to the program; no command line arguments are used.
     *
     * @param args command line arguments (ignored)
     */
    public static void main(final String[] args) throws Exception {
        new FreestandingGlueFDC3Client().executeTests();
    }


    /**
     * The reference to the Glue connection. This must be atomic because of the way in which it is created.
     */
    private final AtomicReference<Glue> glueBridgeReference = new AtomicReference<>();


    @Override
    protected final DesktopAgentFactory<Void, DesktopAgent> connect() {
        // Create the bridge
        final Glue glueBridge = Tick42Glue.builder().build();

        // Store the bridge reference
        glueBridgeReference.set(glueBridge);

        // Output some information about the library and environment
        output("Application Name:          " + getApplicationName());
        output("Glue Version:              " + glueBridge.version());
        output("Glue Environnment:         " + glueBridge.env());
        output("Glue Region:               " + glueBridge.region());
        output("Glue supports this window: " + glueBridge.windows().isWindowSupported(this));

        glueBridge.windows().register(glueBridge.windows().getWindowHandle(this));

        // Add a shutdown listener
        glueBridge.appManager().registerShuttingDownHandler(args -> {
            output("IOCD is shutting down");
            return CompletableFuture.completedFuture(false);
        });

        // Connect to the server
        glueBridge.appManager().ready().toCompletableFuture().join();

        return new DesktopAgentFactory<>() {

            @Override
            public DesktopAgent getDesktopAgent_2_0() throws Exception {
                output("creating DesktopAgent...");
                final DesktopAgent desktopAgent = new io.interop.fdc3.impl.core.DesktopAgentImpl(glueBridgeReference.get());
                output("DesktopAgent created");


                // Print some info about the DesktopAgent instance
                ImplementationMetadata implementationMetadata = desktopAgent.getInfo().toCompletableFuture().get();
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
                outputWithAdditionalIndent("ToolTip:     " + implementationMetadata.getAppMetadata().getTooltip(), 2);
                //
                outputWithAdditionalIndent("ImplementationMetadata:", 1);
                outputWithAdditionalIndent("isOriginatingAppMetadata:    " + implementationMetadata.getOptionalFeatures().isOriginatingAppMetadata(), 2);
                outputWithAdditionalIndent("isUserChannelMembershipAPIs: " + implementationMetadata.getOptionalFeatures().isUserChannelMembershipAPIs(), 2);


                // Return the instance
                return desktopAgent;
            }
        };
    }

    @Override
    protected void testDesktopAgent_2_0(DesktopAgent desktopAgent) {
        // Join "Channel 1"
        final String channelId = "Channel 1";
        output("Joining channel=" + channelId);
        desktopAgent.joinUserChannel("Channel 1").toCompletableFuture().join();
        //
        output("Joined channel=" + channelId);


        // Broadcast
        final Map<String, String> context = new HashMap<>() {{
            put("ticker", "TSLA");
        }};
        output("Broadcasting context=" + context + "...");
        desktopAgent.broadcast(new Context<>("fdc3.instrument", "fdc3.instrument", context)).toCompletableFuture().join();
        output("Broadcast complete");
    }

}

