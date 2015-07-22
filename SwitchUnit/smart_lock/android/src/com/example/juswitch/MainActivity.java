/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Author CNM, JUMA Inc.
 *
*/
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
	
	public static final String ACTION_DEVICE_DISCOVERED = "com.juma.demo.ACTION_DEVICE_DISCOVERED";
    public static final String NAME_STR = "name";
    public static final String UUID_STR = "uuid";
    public static final String RSSI_STR = "rssi";

    private Button btChoose;
    private Button swSwitch;
    private JumaDevice device = null;
    private byte type = 0x01;
    private UUID deviceUuid = null;
    private List<HashMap<String, Object>> deviceInfo = null;
    private CustomListViewAdapter lvDevcieAdapter = null;
    int senddate = 0,rescan = 0;
    
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
        
     	buttonClick();
     	
    }
    @Override
    protected void onStop() {
        super.onStop();
        
     	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
     	
    }
    
    private void initView(){
	 
	btChoose = (Button)findViewById(R.id.bt1);
	swSwitch = (Button)findViewById(R.id.switch_switch1);  
	
    }
    
    public  void buttonClick(){
	 
    	btChoose.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    		public void onClick(View v) {
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
    							btChoose.setText(""+name);
    							rescan = 0;
    						}
    						
    					});
    					
    				}
    				
    			});
    			
    			scanDialog.show();
    			
    		}
    		
    	});
    	
    	swSwitch.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(deviceUuid != null){
    				if(rescan == 0){
    					rescan = 1;
    					senddate = 1;
    					device.connect(deviceUuid);
    				}else{
    					
    					Toast.makeText(getApplicationContext(), "Too fast", Toast.LENGTH_LONG).show();
    				}
    			}else{
    				
    				Toast.makeText(getApplicationContext(), "No choice", Toast.LENGTH_LONG).show();
    				
    			}
				
			}
    	});
    	
    }
    
    private void initDevice(){
    	
    	device = new JumaDevice(){
    		
    		@Override
    		public void onConnect(UUID uuid, int status) {
    			
    			if (status == JumaDevice.STATE_SUCCESS) {
    				
    				device.send(type, hexToByte("01"));
    			
    			}else if (status == JumaDevice.STATE_ERROR ) {
    				
    				device.connect(deviceUuid);
    			}
    			
    		}

	@Override
	public void onDisconnect(UUID uuid, int status) {
		
		if(status == JumaDevice.STATE_SUCCESS){
			rescan=0;
		}
		else if(status == JumaDevice.STATE_ERROR){
			device.disconnect(deviceUuid);
		}
	}
		

	@Override
	public void onDiscover(String name, UUID uuid, int rssi) {
		
		Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
		intent.putExtra(MainActivity.NAME_STR, name);
		intent.putExtra(MainActivity.UUID_STR, uuid.toString());
		intent.putExtra(MainActivity.RSSI_STR, rssi);
		sendBroadcast(MainActivity.this, intent);
	}

	@Override
	public void onError(Exception e, int errorCode) {
		
		if(errorCode == JumaDevice.ERROR_SCAN){
			device.scan(null);
		}
		
	}

	@Override
	public void onMessage(byte type, byte[] message) {
		
	}

	@Override
	public void onSend(int status) {
		
		if(status != JumaDevice.STATE_BUSY){
			if(senddate == 1){
				device.send(type, hexToByte("00"));
				senddate = 0;
			}else{
			device.disconnect(deviceUuid);
			}
		}
		
	}

	@Override
	public void onStopScan() {
		
	}

	@Override
	public void onUpdateFirmware(int status) {
		
	}
	
};

device.init(getApplicationContext());

}
private void addDeviceInfo(String name, String uuid, int rssi){
	
	if((deviceInfo != null) && (lvDevcieAdapter != null)){
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
	
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
	
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	
	int id = item.getItemId();
	if (id == R.id.action_settings) {
		return true;
	}
	
	return super.onOptionsItemSelected(item);
}
}