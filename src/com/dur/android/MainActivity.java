package com.dur.android;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	WebSocketClient mWebSocketClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		editText.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void checkboxActionPerformed(View view) {
		CheckBox box = (CheckBox) findViewById(R.id.check_Box);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		if (box.isChecked()) {
			editText.setEnabled(true);
			editText.setText("Kutas Kozla");
		}
		else {
			editText.setEnabled(false);
			CallAPI api = new CallAPI();
			String[] args = new String[2];
			api.execute(args);
		}
	}

	private class CallAPI extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("http://192.168.1.11:8080/ServerSide/rest/hello.json");
			httpGet.setHeader(new BasicHeader("Accept", "application/json"));
			String text = null;
			try {
				
				connectWebSocket();

				HttpResponse response = httpClient.execute(httpGet, localContext);

				HttpEntity entity = response.getEntity();

				text = new String(EntityUtils.toString(entity));

			} catch (Exception e) {
				return e.getLocalizedMessage();

			}
			return text;
		}

		protected void onPostExecute(String result) {

		}
	} // end CallAPI
	
	private void connectWebSocket() {
		URI uri;
		try {
			uri = new URI("ws://websockethost:8080");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		mWebSocketClient = new WebSocketClient(uri) {
			
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				Log.i("Websocket", "Opened");
				mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
			}

			@Override
			public void onMessage(String s) {
				final String message = s;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						TextView textView = (TextView) findViewById(R.id.messages);
//						textView.setText(textView.getText() + "\n" + message);
					}
				});
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.e("closed", "closed");
			}

			@Override
			public void onError(Exception e) {
				Log.e("Unable to connect", "Unable to connect");
			}
		};
		mWebSocketClient.connect();
	}

}
