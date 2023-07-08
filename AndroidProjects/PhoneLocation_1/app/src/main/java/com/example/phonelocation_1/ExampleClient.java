package com.example.phonelocation_1;

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class ExampleClient extends WebSocketClient {

    private int ackId = 1;

    private boolean connectionEstablished = false;

    public ExampleClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ExampleClient(URI serverURI) {
        super(serverURI);
    }

    public ExampleClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // send("Hello, it is me. Mario :)");
        Log.i("Websocket2","opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
        String j12= String.format("{\"type\":\"joinGroup\",\"group\":\"Group1\",\"ackId\":%d}", ackId++);
        send(j12);
        connectionEstablished = true;
    }

    @Override
    public void onMessage(String message) {
        Log.i("Websocket2","received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        connectionEstablished = false;
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        Log.i("Websocket2",
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

//    public static void main(String[] args) throws URISyntaxException {
//        ExampleClient c = new ExampleClient(new URI(
//                "ws://localhost:8887")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
//        c.connect();
//    }

    public void sendText(String data) {
        if (connectionEstablished) {
            String j12 = String.format("{\"type\":\"sendToGroup\",\"group\":\"Group1\",\"dataType\":\"text\",\"data\":\"%s\",\"ackId\":%d}", data, ackId++);
            send(j12);
        }
    }

    public boolean isConnectionEstablished(){
        return connectionEstablished;
    }
}
