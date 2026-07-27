package com.cybermed.cdoc_patient.signalr;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cybermed.cdoc_patient.common.CDoctor2Application;

import java.util.Arrays;
import java.util.List;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;


public class ChatManager {

    private HubConnection hubConnection;
    private HubProxy chatHub;
    boolean isReconnecting;

    public ChatManager() {
        initConnection();
    }

    private void initConnection() {
        String osPlayerId = CDoctor2Application.getLoginInfo().getOneSignalUserId();


        if ( osPlayerId == null || osPlayerId.isEmpty()) return;

        Logger logger = new Logger() {
            @Override
            public void log(String message, LogLevel level) {

               // Log.d("Log Chat Message : ", message);
            }
        };
        QueryStringObj qso = new QueryStringObj("patient", CDoctor2Application.getLoginInfo().getAccount(),
                CDoctor2Application.getLoginInfo().getUserInfo().getService_code(),
                CDoctor2Application.getLoginInfo().getOneSignalUserId());

        hubConnection = new HubConnection("https://login.cybermedehr.com/signalr", qso.toString(), true, logger);
        chatHub = hubConnection.createHubProxy("signalr_hub");



        chatHub.on("on_chatmsg_received", ( toUser, msgType, msgStr,time) -> {
            Log.d("ChatManager",   toUser + ", " + msgType+", "+ msgStr+", "+ time);
        },  String.class, String.class, String.class, String.class);

        // Handle chat error
        chatHub.on("on_chatmsg_error", args -> {
            /*String errorMsg = args.length() > 0 ? (String) args.get(0) : "Unknown error";*/
            Log.e("ChatManager", "❌ Chat error: " + args.toString());
        }, String.class);

        hubConnection.reconnected(() -> Log.d("SignalR", "Reconnected"));
        hubConnection.reconnecting(() -> Log.d("SignalR", "Reconnecting..."));
        hubConnection.connectionSlow(() -> Log.d("SignalR", "Connection slow"));

        // Subscribe to the connected event
        hubConnection.connected(new Runnable() {

            @Override
            public void run() {
                Log.d("ChatManager", "CONNECTED");
            }
        });
        // Connection lifecycle handlers
        hubConnection.closed(new Runnable() {

            @Override
            public void run() {
                System.out.println("DISCONNECTED");
            }
        });

        hubConnection.error(throwable -> {
            Log.e("ChatManager", "❌ Connection error: " + throwable.getMessage());
            reconnectingSignalR();
        });

        hubConnection.start().done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                System.out.println("DONE CONNECTING");
                Log.d("SignalR", "✅ Connection started");
            }
        });

    }



    public void sendChatMessage(  String fromUser,String toUser, String msgType, String msgStr) {
        Log.d("ChatManager", "Sending from: " + fromUser + ", to: " + toUser + ", type: " + msgType + ", msg: " + msgStr);
        List<Object> args = Arrays.asList(fromUser, toUser, msgType, msgStr);

        chatHub.invoke("Send_chatMsg", fromUser, toUser, msgType, msgStr).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("ChatManager", "❌ Failed to send message: " + throwable.getMessage());
            }
        }).done(new microsoft.aspnet.signalr.client.Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                Log.d("ChatManager", "✅ Message sent successfully");
            }
        });
    }


    private void reconnectingSignalR() {
        Log.d("signalr", "reconnecting");
        if (hubConnection.getState() == ConnectionState.Disconnected) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hubConnection.start();
                }
            }, 1000);

        }
    }
}

