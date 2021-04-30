package com.chartiq.finsemble.example;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.util.Arrays;


public class MultiWindowJavaSwingExample {

    // JUnique has a bug in which if you pass a non-unicode char, the recreation of the string from
    // a inputstream gets messed up - therefore we have chosen to use & has the delimiter
    // similar to what gets used in a query params URL
    private static final String DELIMITER = "&";

    public static void main(String[] args) {

        String appId = MultiWindowJavaSwingExample.class.getSimpleName();
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                runExampleApplication(message.split(DELIMITER));
                return null;
            });
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {
            runExampleApplication(args);
        } else {
            JUnique.sendMessage(appId, String.join(DELIMITER, args));
        }
    }

    private static void runExampleApplication(String[] args) {

        String window = Arrays.stream(args).filter(arg -> arg.startsWith("example=")).findFirst().get();
        String app = window.split("=")[1];
        switch (app) {
            case "JavaSwingExample":
                JavaSwingExample.main(args);
                break;

            case "AuthenticationExample":
                try {
                    AuthenticationExample.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
