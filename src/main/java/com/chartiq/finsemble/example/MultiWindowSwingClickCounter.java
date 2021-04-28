package com.chartiq.finsemble.example;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

public class MultiWindowSwingClickCounter {

    public static void main(String[] args) {

        String appId = "multiWindowSwingClickCounter";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                SwingClickCounter.main(message.split("§"));
                return null;
            });
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {
            SwingClickCounter.main(args);
        } else {
            JUnique.sendMessage(appId, String.join("§", args));
        }
    }
}
