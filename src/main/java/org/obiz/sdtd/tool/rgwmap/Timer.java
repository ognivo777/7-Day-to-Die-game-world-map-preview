package org.obiz.sdtd.tool.rgwmap;

import java.util.HashMap;
import java.util.Map;

public class Timer {
    private static Map<String, Long> timers;

    static {
        timers = new HashMap<>();
    }

    public static synchronized void startTimer(String name) {
        if(timers.containsKey(name)) {
            System.err.println("Timer already started: " + name);
            return;
        }
        timers.put(name, System.currentTimeMillis());
    }

    public static synchronized Long stopTimer(String name) {
        Long now = System.currentTimeMillis();
        if(timers.containsKey(name)) {
            Long startTime = timers.remove(name);
            System.out.println("Timer '" + name + "' finished: " + (now-startTime)/1000f);
            return now-startTime;
        } else {
            System.err.println("No timer found for name: " + name);
            return -1L;
        }
    }

}
