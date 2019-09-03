package com.chartiq.finsemble.example;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handler to write log messages to the message area of the form.
 */
class MessageHandler extends Handler {
    private JavaExample javaExample;

    /**
     * Sets the java example the handler should append messages to.
     * @param javaExample The Java example instance
     */
    void setJavaExample(JavaExample javaExample) {
        this.javaExample = javaExample;
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
        if (javaExample == null) {
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

        javaExample.appendMessage(message);
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
