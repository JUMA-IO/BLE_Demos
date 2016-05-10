package com.example.echo;

import java.io.UnsupportedEncodingException;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private ScanHelper scanHelper;
	private JumaDevice device = null;
	private String sendMessage = null;
	private ListView listView;
	private EditText editText;
	private Button sendbt,clearbt;
	private ArrayAdapter<String> adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView)findViewById(R.id.listView1);
		editText = (EditText) findViewById(R.id.editText1);
		sendbt = (Button) findViewById(R.id.button1);
		clearbt = (Button) findViewById(R.id.button2);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		sendbt.setEnabled(false);
		clearbt.setEnabled(false);
		sendbt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendMessage = editText.getText().toString();
				if(sendMessage != null)
					try {
						device.send((byte)0x00,sendMessage.getBytes("UTF8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		clearbt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				adapter.clear();
			}
		});
		
		scanHelper = new ScanHelper(this, new ScanCallback() {
			
			@Override
			public void onScanStateChange(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDiscover(JumaDevice jumaDevice, int arg1) {
				// TODO Auto-generated method stub
				device = jumaDevice;
				device.connect(new JumaDeviceCallback() {
					@Override
					public void onConnectionStateChange(int status, int newState) {
						// TODO Auto-generated method stub
						super.onConnectionStateChange(status, newState);
						if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									sendbt.setEnabled(true);
									clearbt.setEnabled(true);
								}
							});
						}
					}
					@Override
					public void onSend(int status) {
						// TODO Auto-generated method stub
						super.onSend(status);
						if(status == JumaDevice.SUCCESS){
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									adapter.add("send message : "+sendMessage);
									listView.smoothScrollByOffset(adapter.getCount() - 1);

								}
							});
						}
					}
					@Override
					public void onReceive(byte type, final byte[] message) {
						// TODO Auto-generated method stub
						super.onReceive(type, message);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								try {
									adapter.add("Receiver message : "+new String(message,"UTF8"));
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								listView.smoothScrollByOffset(adapter.getCount() - 1);

							}
						});
					}
				});
			}
		});
		if(scanHelper.isEnabled())
			scanHelper.startScan("CAF_ECHO");
	}
	@Override
	protected void onStop(){
		super.onStop();
		if(device != null && device.isConnected())
			device.disconnect();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
