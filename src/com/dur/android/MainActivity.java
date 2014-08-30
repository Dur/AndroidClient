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
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dur.android.WebSocket;

public class MainActivity extends Activity {
	
	private String HOST_URL = "192.168.1.10:8080/ServerSide";
	private String WS = "ws://";
	private String HTTP = "http://";
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

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
			HttpGet httpGet = new HttpGet(HTTP + HOST_URL + "/http/rest/hello.json");
			httpGet.setHeader(new BasicHeader("Accept", "application/json"));
			String text = null;
			try {
				connectWebSocket();
				Log.i("Connecting to: ", httpGet.getURI().getPath());
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				text = new String(EntityUtils.toString(entity));
				Log.i("Normal hhtp", text);

			} catch (Exception e) {
				Log.e("HTTP Connection", e.getMessage());
			}
			return text;
		}

		protected void onPostExecute(String result) {

		}
	} // end CallAPI
	
	private void connectWebSocket() {
	    WebSocket client;
		try {
			Log.i("WebSocket", "Trying to connect to server");
			client = new WebSocket(new URI(WS + HOST_URL + "/websocket/websocket"), new Draft_17());
			client.connect();
			while(! client.getConnection().isOpen()){
				Thread.sleep(1000);
			}
			client.send("jedziemy");
		} catch (URISyntaxException e1) {
			Log.i("Websocket", e1.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
