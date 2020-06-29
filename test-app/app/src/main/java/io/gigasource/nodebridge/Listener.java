package io.gigasource.nodebridge;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.gigasource.webview3.content_shell_apk.ContentShellWebView;

import java.util.ArrayList;

import io.gigasource.MainActivity;

public class Listener {
    private static CallFromNS call = null;
    private static boolean nodeIsReady = false;
    private static boolean hasSendQueuedMsg = false;
    private static String SYSTEM_CHANNEL = "_SYSTEM_";
    private static String EVENT_CHANNEL = "_EVENTS_";
    protected static int TO_NODE = 1;
    protected static int TO_NATIVESCRIPT = 2;
    private static ContentShellWebView webview;
    private static ImageView logo;
    private static MainActivity activity;

    private static class BridgeMessage {
        int dest;
        String message;
        String channel;
        BridgeMessage(int dest, String channel, String message) {
            this.dest = dest;
            this.channel = channel;
            this.message = message;
        }
    }

    public static void setWebviewAndLogo(ContentShellWebView webview, ImageView logo) {
        Listener.webview = webview;
        Listener.logo = logo;
    }

    private static ArrayList<BridgeMessage> waitingMsg = new ArrayList<>();

    public static native void sendMessageToNodeChannel(String channelName, String msg);

    public static void sendToNode(String channelName, String msg) {
        if (nodeIsReady) {
            sendMessageToNodeChannel(channelName, msg);
        } else {
            Listener.waitingMsg.add(new Listener.BridgeMessage(Listener.TO_NODE, channelName, msg));
        }
    }

    public static void setActivity(MainActivity activity) {
        Listener.activity = activity;
    }

    public static void setCall(CallFromNS call) {
        Listener.call = call;
        sendSystemChannelMessage("Nativescript Ready");
    }

    public static void sendToNS(String msg) {
        call.callback(msg);
    }

    // This message receive message from node
    public static void receiveMessageFromNode(String channelName, String msg) {
        if (!channelName.equals(Listener.SYSTEM_CHANNEL)) {
            if (call != null) {
                call.callback(msg);
            } else {
                Listener.waitingMsg.add(new Listener.BridgeMessage(Listener.TO_NATIVESCRIPT, channelName, msg));
            }
        } else {
            sendSystemChannelMessage(msg);
        }
    }

    private static void triggerReady() {
        for (BridgeMessage bridgeMessage : waitingMsg) {
            if (bridgeMessage.dest == TO_NODE) {
                sendMessageToNodeChannel(bridgeMessage.channel, bridgeMessage.message);
            } else {
                sendToNS(bridgeMessage.message);
            }
        }
    }

    // To resolve system channel
    private static void sendSystemChannelMessage(String msg) {
        switch (msg) {
            case "Node Ready":
                nodeIsReady = true;
                if (call != null) {
                    triggerReady();
                }
                break;
            case "Nativescript Ready":
                if (nodeIsReady) {
                    triggerReady();
                }
                break;
            case "Start Node":
                Listener.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Listener.activity.mainWebView.setVisibility(View.VISIBLE);
                        Listener.activity.mainWebView.loadUrl("http://localhost:8888");
                        Listener.activity.logo.setVisibility(View.INVISIBLE);
                    }
                });
                break;
        }
    }
}
