package com.chartiq.finsemble.example.fdc3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class InteropServiceMessageHandler extends Handler {

    private InteropServiceExample interopServiceExample;

    /**
     * Sets the java example the handler should append messages to.
     * @param interopServiceExample The Java example instance
     */
    void setJavaExample(InteropServiceExample interopServiceExample) {
        this.interopServiceExample = interopServiceExample;
    }


    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * The <tt>Handler</tt>  is responsible for formatting the message, when and
     * if necessary.  The formatting should include localization.
     *
     * @param record description of the log event. A null record is
     *               silently ignored and is not published
     */
    @Override
    public void publish(LogRecord record) {
        if (interopServiceExample == null) {
            return;
        }

        final Throwable throwable = record.getThrown();

        String stackTrace = "";
        if (throwable != null) {
            final StringWriter sw = new StringWriter();
            sw.append("\n");

            final PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            stackTrace = sw.toString();
        }

        final String message = String.format(
                "%s: %s.%s %s%s",
                record.getLevel(),
                record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".")),
                record.getSourceMethodName(),
                record.getMessage(),
                stackTrace);

        interopServiceExample.appendMessage(message);
    }

    /**
     * Flush any buffered output.
     */
    @Override
    public void flush() {

    }

    /**
     * Close the <tt>Handler</tt> and free all associated resources.
     * <p>
     * The close method will perform a <tt>flush</tt> and then close the
     * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
     * should no longer be used.  Method calls may either be silently
     * ignored or may throw runtime exceptions.
     *
     * @throws SecurityException if a security manager exists and if
     *                           the caller does not have <tt>LoggingPermission("control")</tt>.
     */
    @Override
    public void close() throws SecurityException {

    }
}
