package io.gigasource.nodebridge;

import android.util.Log;

public class Listener {
    private static CallFromNS call = null;

    public static native void sendMessageToNodeChannel(String channelName, String msg);

    public static void sendToNode(String channelName, String message) {
        sendMessageToNodeChannel(channelName, message);
    }

    public static void setCall(CallFromNS call) {
        Listener.call = call;
    }

    public static void sendToNS(String msg) {
        call.callback(msg);
    }

    public static void sendMessageToApplication(String channelName, String msg) {
        if (call != null) {
            call.callback(msg);
        }
    }
}
