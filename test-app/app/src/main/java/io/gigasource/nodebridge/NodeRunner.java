package io.gigasource.nodebridge;

public class NodeRunner {
    public static boolean startedNodeAlready = false;

    public static void startNode(String[] arguments) {
        startedNodeAlready = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startNodeWithArguments(arguments);
            }
        }).start();
    }

    public static native Integer startNodeWithArguments(String[] arguments);
}
