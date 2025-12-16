package io.interop.finsemble.example.fdc3;

/**
 * A way to simplify getting the FDC3 implementations so that the Sandbox
 * can easily describe behavior.
 *
 * @param <DesktopAgent_1_2> The FDC3 1.2 implementation
 * @param <DesktopAgent_2_0> The FDC3 2.0 implementation
 */
public class DesktopAgentFactory<DesktopAgent_1_2, DesktopAgent_2_0> {

    /**
     * Gets an instance of an FDC3 1.2 implementation.
     *
     * @return the FDC3 1.2 implementation
     * @throws Exception when something goes wrong
     */
    public DesktopAgent_1_2 getDesktopAgent_1_2() throws Exception {
        throw new UnsupportedOperationException("This implementation does not provide support for FDC3 1.2");
    }

    /**
     * Gets an instance of an FDC3 2.0 implementation.
     *
     * @return the FDC3 2.0 implementation
     * @throws Exception when something goes wrong
     */
    public DesktopAgent_2_0 getDesktopAgent_2_0() throws Exception {
        throw new UnsupportedOperationException("This implementation does not provide support for FDC3 2.0");
    }

}
