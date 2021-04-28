package com.chartiq.finsemble.example;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.util.Arrays;
import java.util.logging.Logger;

public class MultiWindowJavaSwingExample {

    private static final Logger LOGGER = Logger.getLogger(MultiWindowJavaSwingExample.class.getName());

    public static void main(String[] args) {

        String appId = "multiWindowJavaSwingExample";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                new JavaSwingExample(Arrays.asList(message.split("§")));
                return null;
            });
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {
            new JavaSwingExample(Arrays.asList(args));
        } else {
            JUnique.sendMessage(appId, String.join("§", args));
        }
    }
}
