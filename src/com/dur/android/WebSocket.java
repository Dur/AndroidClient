package com.dur.android;

import java.net.URI;

import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.client.WebSocketClient;

import android.util.Log;

public class WebSocket extends WebSocketClient {

	 public WebSocket(URI serverUri, Draft draft) {
	        super(serverUri, draft);
	    }

	    public WebSocket(URI serverURI) {
	        super(serverURI);
	    }

	    @Override
	    public void onOpen(ServerHandshake handshakedata) {
	    	Log.i("Websocket", "Statring websocket");
	    	send("Hello My friend");
	    }

	    @Override
	    public void onClose(int code, String reason, boolean remote) {
	    	Log.i("Websocket", "Closed");
	    }

	    @Override
	    public void onMessage(String message) {
	    	Log.i("Websocket", "Receivrd: " + message);
	    }

	    @Override
	    public void onError(Exception ex) {
	    	Log.i("Websocket", ex.getMessage());
	    }
}
