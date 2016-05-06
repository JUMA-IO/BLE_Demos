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
import java.util.UUID;

import com.example.juswitch.CustomDialog.Callback;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity {
	private Button btChoose;
	private Switch mySwitch;
	private ScanHelper scanHelper;
	private HashMap<UUID, JumaDevice> jumaDevices = null;
	private JumaDevice jumaDevice = null;
	private CustomDialog scanDialog;
	
	public static final String ACTION_DEVICE_DISCOVERED = "com.juma.helper.ACTION_DEVICE_DISCOVERED";

	public static final String NAME_STR = "name";
	public static final String UUID_STR = "uuid";
	public static final String RSSI_STR = "rssi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_main);
	    initView();
	    initDevice(); 
	    btChoose.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    		public void onClick(View v) {
    			if(jumaDevice != null && jumaDevice.isConnected())
    				jumaDevice.disconnect();
    			scanDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SCAN);
    			scanDialog.setScanCallback(new Callback() {
    				
    				@Override
    				public void onName(String name) {
    					jumaDevices.clear();
    					scanHelper.startScan(name);
    				}

    				@Override
    				public void onDevice(final UUID uuid, final String name) {

    					scanHelper.stopScan();

    					jumaDevice = jumaDevices.get(uuid);
    					runOnUiThread(new Runnable() {
    						
    						@Override
    						public void run() {
    							btChoose.setText(""+jumaDevice.getName());
    							jumaDevice.connect(new JumaDeviceCallback() {
								});
    						}
    						
    					});
    					
    				}
    				
    			});
    			scanDialog.setNegativeButton(new OnClickListener() {

    				@Override
    				public void onClick(View arg0) {

    					scanDialog.dismiss();

    					scanHelper.stopScan();

    				}
    			});
    			
    			scanDialog.show();
    			
    		}
    		
    	});
    	
    	mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		
    		@Override
    		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
    			if(jumaDevice != null && jumaDevice.isConnected()){
    			jumaDevice.send((byte)0x00, hexToByte(isChecked?"01":"00"));
    			}
    		}
    		
    	});
	
    }
    @Override
    protected void onStop() {
        super.onStop();
       if(jumaDevice != null && jumaDevice.isConnected())
    	   jumaDevice.disconnect();
    }
    
    private void initView(){
	 btChoose = (Button)findViewById(R.id.bt1);
	 mySwitch = (Switch)findViewById(R.id.switch_switch1);
	 jumaDevices = new HashMap<UUID, JumaDevice>();
    }
    
    private void initDevice(){
    	scanHelper = new ScanHelper(getApplicationContext(), new ScanCallback() {
			
			@Override
			public void onScanStateChange(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDiscover(JumaDevice device, int rssi) {
				// TODO Auto-generated method stub
				if(!jumaDevices.containsKey(device.getUuid())){
					jumaDevices.put(device.getUuid(), device);
				}
				Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
				intent.putExtra(MainActivity.NAME_STR, device.getName());
				intent.putExtra(MainActivity.UUID_STR, device.getUuid().toString());
				intent.putExtra(MainActivity.RSSI_STR, rssi);
				LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
				
			}
		});
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