package io.interop.finsemble.example.fdc3;

/**
 * Functional way to handle exceptions which can be ignored (but logged).
 */
class IgnoreException {

    /**
     * Private constructor - this class cannot be instantiated.
     */
    private IgnoreException() {}


    /**
     * Executes a function and catches any exceptions which are thrown.
     *
     * @param func the function to execute
     */
    public static void ignoreException(final VoidZeroParamFunction func) {
        ignoreException((ignore) -> func.execute(), null);
    }

    /**
     * Executes a function and catches any exceptions which are thrown.
     *
     * @param func the function to execute
     * @param input the input to the function
     * @param <T> the input type
     */
    public static <T>void ignoreException(final VoidOneParamFunction<T> func, final T input) {
        ignoreException(() -> {
            func.execute(input);
            return null;
        }, null);
    }

    /**
     * Executes a function and catches any exceptions which are thrown.
     *
     * @param func the function to execute
     * @param defaultValue the default value to return if the function throws
     * @param <R> the return type
     * @return the function result, or the default value if the function throws
     */
    public static <R>R ignoreException(final ZeroParamFunction<R> func, final R defaultValue) {
        return ignoreException((OneParamFunction<Void, R>) v -> func.execute(), null, defaultValue);
    }

    /**
     * Executes a function and catches any exceptions which are thrown.
     *
     * @param func the function to execute
     * @param input the input to the function
     * @param defaultValue the default value to return if the function throws
     * @return the function result, or the default value if the function throws
     * @param <T> the input type
     * @param <R> the return type
     */
    public static <T,R> R ignoreException(final OneParamFunction<T, R> func, final T input, final R defaultValue) {
        try {
            return func.execute(input);
        } catch (final Exception exception) {
            System.err.println("Could not execute function, returning default value");
            exception.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * A functional interface for a function with zero parameters and no return type.
     */
    @FunctionalInterface
    public interface VoidZeroParamFunction {
        void execute() throws Exception;
    }

    /**
     * A functional interface for a function with zero parameters and a return type.
     *
     * @param <R> the return type
     */
    @FunctionalInterface
    public interface ZeroParamFunction<R> {
        R execute() throws Exception;
    }

    /**
     * A functional interface for a function with one parameter and no return type.
     *
     * @param <I> the function parameter type
     */
    @FunctionalInterface
    public interface VoidOneParamFunction<I> {
        void execute(I i) throws Exception;
    }

    /**
     * A functional interface for a function with one parameter and a return type.
     *
     * @param <I> the function parameter type
     * @param <R> the return type
     */
    @FunctionalInterface
    public interface OneParamFunction<I, R> {
        R execute(I i) throws Exception;
    }

}
