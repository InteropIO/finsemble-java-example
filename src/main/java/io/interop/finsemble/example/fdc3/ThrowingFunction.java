package io.interop.finsemble.example.fdc3;

/**
 * A functional interface declaring a function which may throw.
 */
@FunctionalInterface
interface ThrowingFunction {
    void apply() throws Exception;
}
