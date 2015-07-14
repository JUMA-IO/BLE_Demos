package com.example.juswitch;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.example.juswitch.CustomDialog.ScanCallback;

import juma.sdk.lite.JumaDevice;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {
private Button bt1;
private Switch swswitch;
private JumaDevice device = null;
private byte type;
private UUID deviceUuid = null;
private List<HashMap<String, Object>> deviceInfo = null;
private CustomListViewAdapter lvDevcieAdapter = null;
int senddate=0,rescan=0;
public static final String ACTION_START_SCAN = "com.juma.demo.ACTION_START_SCAN";
public static final String ACTION_STOP_SCAN = "com.juma.demo.ACTION_STOP_SCAN";
public static final String ACTION_DEVICE_DISCOVERED = "com.juma.demo.ACTION_DEVICE_DISCOVERED";
public static final String ACTION_CONNECT = "com.juma.demo.ACTION_CONNECT";
public static final String ACTION_CONNECTED = "com.juma.demo.ACTION_CONNECTED";
public static final String ACTION_DISCONNECT = "com.juma.demo.ACTION_DISCONNECT";
public static final String ACTION_DISCONNECTED = "com.juma.demo.ACTION_DISCONNECTED";
public static final String ACTION_SEND_MESSAGE = "com.juma.demo.ACTION_SEND_MESSAGE";
public static final String ACTION_RECEIVER_MESSAGE = "com.juma.demo.ACTION_RECEIVER_MESSAGE";
public static final String ACTION_ERROR = "com.juma.demo.ACTION_ERROR";

public static final String NAME_STR = "name";

public static final String UUID_STR = "uuid";

public static final String RSSI_STR = "rssi";

public static final String MESSAGE_STR = "message";

public static final String ERROR_STR = "error";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		initDevice();  
		
	}
	 @Override
	    protected void onStart() {
	        super.onStart();
	     	LocalBroadcastManager.getInstance(this).registerReceiver(receiver, getIntentFilter());
	     	Buttonclick();
	     }
	 @Override
	    protected void onPause() {
	        super.onPause();
	     	
	     }
	 @Override
	    protected void onStop() {
	        super.onStop();
	        if(deviceUuid!=null){
	        device.disconnect(deviceUuid);}
	     	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

	     }
		@Override
		protected void onDestroy() {

			super.onDestroy();
         
		}
	private void initView(){
		type=0x01;
		bt1=(Button)findViewById(R.id.bt1);
		swswitch=(Switch)findViewById(R.id.switch_switch1);  
		  
	}
	public  void Buttonclick(){
		bt1.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				CustomDialog scanDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SCAN);
				scanDialog.setScanCallback(new ScanCallback() {

					@Override
					public void onName(String name) {

						device.scan(name);

					}

					@Override
					public void onDevice(final UUID uuid, final String name) {

						deviceUuid = uuid;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								//device.connect(deviceUuid);	
								bt1.setText(""+name);
							}
						});

					}
				
				});
			    scanDialog.show();
			}
			
		});
		swswitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					senddate=1;
					device.connect(deviceUuid);
					//device.send(type, hexToByte("01"));
				}
				else{
					senddate=2;
					device.connect(deviceUuid);
					//device.send(type, hexToByte("00"));
				}
			}
			
		});
		
		
	}
private void initDevice(){
	device=new JumaDevice(){

		@Override
		public void onConnect(UUID arg0, int arg1) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.ACTION_CONNECT);
			sendBroadcast(MainActivity.this, intent);
		}

		@Override
		public void onDisconnet(UUID arg0, int arg1) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.ACTION_DISCONNECT);
			sendBroadcast(MainActivity.this, intent);
		}

		@Override
		public void onDiscover(String arg0, UUID arg1, int arg2) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
			intent.putExtra(MainActivity.NAME_STR, arg0);
			intent.putExtra(MainActivity.UUID_STR, arg1.toString());
			intent.putExtra(MainActivity.RSSI_STR, arg2);
			sendBroadcast(MainActivity.this, intent);
		}

		@Override
		public void onError(Exception arg0, int arg1) {
			
		}

		@Override
		public void onMessage(byte arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSend(int arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.ACTION_DISCONNECTED);
			sendBroadcast(MainActivity.this, intent);
		}

		@Override
		public void onStopScan() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpdateFirmware(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
		
		
	device.init(getApplicationContext());
}
private void addDeviceInfo(String name, String uuid, int rssi){

	if(deviceInfo != null && lvDevcieAdapter != null){
		HashMap<String , Object> map = new HashMap<String, Object>();
		map.put(MainActivity.NAME_STR, name);
		map.put(MainActivity.UUID_STR, uuid);
		map.put(MainActivity.RSSI_STR, rssi);

		deviceInfo.add(map);

		lvDevcieAdapter.notifyDataSetChanged();

	}

}
private final BroadcastReceiver receiver = new BroadcastReceiver() {

	@Override
	public void onReceive(final Context context, final Intent intent) {

		String action = intent.getAction();

		if(action.equals(MainActivity.ACTION_DEVICE_DISCOVERED)){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					String uuid = intent.getStringExtra(MainActivity.UUID_STR);
					String name = intent.getStringExtra(MainActivity.NAME_STR);
					int rssi = intent.getIntExtra(MainActivity.RSSI_STR, 0);
					addDeviceInfo(name, uuid, rssi);
				}
			});
		}
		if(action.equals(MainActivity.ACTION_CONNECT)){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch(senddate){
					case 1:
						device.send(type, hexToByte("01"));
						break;
					case 2:
						device.send(type, hexToByte("00"));
						break;
						
					}
				}
			});
		}
		if(action.equals(MainActivity.ACTION_DISCONNECTED)){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					device.disconnect(deviceUuid);
				}
			});
		}
		if(action.equals(MainActivity.ACTION_DISCONNECT)){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
				}
			});
		}
		
	}
};

private IntentFilter getIntentFilter(){
	IntentFilter filter = new IntentFilter();
	filter.addAction(MainActivity.ACTION_START_SCAN);
	filter.addAction(MainActivity.ACTION_STOP_SCAN);
	filter.addAction(MainActivity.ACTION_DEVICE_DISCOVERED);
	filter.addAction(MainActivity.ACTION_CONNECT);
	filter.addAction(MainActivity.ACTION_CONNECTED);
	filter.addAction(MainActivity.ACTION_DISCONNECT);
	filter.addAction(MainActivity.ACTION_DISCONNECTED);
	filter.addAction(MainActivity.ACTION_SEND_MESSAGE);
	filter.addAction(MainActivity.ACTION_RECEIVER_MESSAGE);
	filter.addAction(MainActivity.ACTION_ERROR);
	return filter;
}
private void sendBroadcast(Context context, Intent intent){
	LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
}

@SuppressLint("UseValueOf")
public static final byte[] hexToByte(String hex)throws IllegalArgumentException {
	if (hex.length() % 2 != 0) {
		throw new IllegalArgumentException();
	}
	char[] arr = hex.toCharArray();
	byte[] b = new byte[hex.length() / 2];
	for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
		String swap = "" + arr[i++] + arr[i];
		int byteint = Integer.parseInt(swap, 16) & 0xFF;
		b[j] = new Integer(byteint).byteValue();
	}
	return b;
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
