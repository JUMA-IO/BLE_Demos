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
public static final String ACTION_DEVICE_DISCOVERED = "com.juma.demo.ACTION_DEVICE_DISCOVERED";

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
     	
     }
 @Override
    protected void onResume() {
        super.onResume();
     	Buttonclick();
     }
 @Override
    protected void onPause() {
        super.onPause();
     	
     }
 @Override
    protected void onStop() {
        super.onStop();
      
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
			device.stopScan();
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
							rescan=0;
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
				boolean isChecked) 
		{
			// TODO Auto-generated method stub
			if(deviceUuid!=null)
			{
				if(rescan==0)
				{
					rescan=1;
					if(isChecked)
					{
						senddate=1;
						device.connect(deviceUuid);
						//device.send(type, hexToByte("01"));
					}
					else
					{
						senddate=2;
						device.connect(deviceUuid);
						//device.send(type, hexToByte("00"));
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Too fast", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "No choice", Toast.LENGTH_LONG).show();
			}
		}
	});
	
	
}
private void initDevice(){
device=new JumaDevice(){

	@Override
	public void onConnect(UUID uuid, int status) {
		// TODO Auto-generated method stub
		if (status == JumaDevice.STATE_SUCCESS) {
			switch(senddate){
			case 1:
				device.send(type, hexToByte("01"));
				break;
			case 2:
				device.send(type, hexToByte("00"));
				break;					
			}
		}else if (status == JumaDevice.STATE_ERROR ) {
			System.out.println("connect is error");
			device.connect(deviceUuid);
		}
	
	}

	@Override
	public void onDisconnect(UUID uuid, int status) {
		System.out.println("onDisconnet state : "+status);
		if(status==JumaDevice.STATE_SUCCESS){
			rescan=0;
		}
		else if(status==JumaDevice.STATE_ERROR){
			device.disconnect(deviceUuid);
		}
	}
		

	@Override
	public void onDiscover(String name, UUID uuid, int rssi) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
		intent.putExtra(MainActivity.NAME_STR, name);
		intent.putExtra(MainActivity.UUID_STR, uuid.toString());
		intent.putExtra(MainActivity.RSSI_STR, rssi);
		sendBroadcast(MainActivity.this, intent);
	}

	@Override
	public void onError(Exception e, int errorCode) {
		System.out.println(e.toString());
		if(errorCode==JumaDevice.ERROR_SCAN){
			device.scan(null);
		}
		
	}

	@Override
	public void onMessage(byte type, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSend(int status) {
		// TODO Auto-generated method stub
		System.out.println("onSend state : "+status);
		if(status!=JumaDevice.STATE_BUSY){
			
			device.disconnect(deviceUuid);
		}
		
	}

	@Override
	public void onStopScan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateFirmware(int status) {
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
	
	
}
};

private IntentFilter getIntentFilter(){
IntentFilter filter = new IntentFilter();
filter.addAction(MainActivity.ACTION_DEVICE_DISCOVERED);
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