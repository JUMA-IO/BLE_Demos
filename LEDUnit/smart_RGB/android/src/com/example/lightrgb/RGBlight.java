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
 * Author YZM, JUMA Inc.
 *
*/
package com.example.lightrgb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import juma.sdk.lite.JumaDevice;

import com.example.lightrgb.ColorPicker.OnColorSelectedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RGBlight extends Activity {
	  
	  private EditText editr,editg,editb;
	  private Button btSet,btOff;
	  private Spinner spChoose;
	  private JumaDevice device;
	  private byte type=0x02;
	  private UUID deviceUuid=null;
	  private ColorPicker myView;
	  private List<String> listName = new ArrayList<String>();  
	  private List<String> listUuid = new ArrayList<String>(); 
	  private ArrayAdapter<String> apUuid,apName;    
	  private BluetoothAdapter mBluetoothAdapter;
	  private boolean ensend = true,mConnected = false;
	  private Handler handler;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_rgblight);
		initView();
		initDevice();
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			 Toast.makeText(this, "NO BLE", Toast.LENGTH_SHORT).show();
			 return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
				Thread.currentThread();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 }
		 }
		 handler = new Handler() {
		        @Override
		        public void handleMessage(Message msg) 
		        {
		         super.handleMessage(msg);
		        
             	 spChoose.setAdapter(apName);
          
		        }
	        };
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();
	        if(mBluetoothAdapter.isEnabled()){
	        	device.scan(null);
	        }else{
	        	Toast.makeText(this, "Bluetooth disable", Toast.LENGTH_SHORT).show();
	        }
	     }
	 @Override
	    protected void onResume() {
	        super.onResume();
	     	buttonClick();
	     }
	 @Override
	    protected void onStop() {
	        super.onStop();
	        device.stopScan();
	        if(mConnected){
	        	device.disconnect(deviceUuid);
	        }
	     }
	
	private void initView(){
		 myView = (ColorPicker) findViewById(R.id.colorPicker);
		 editr = (EditText)findViewById(R.id.rgb_r);
		 editg = (EditText)findViewById(R.id.rgb_g);
		 editb = (EditText)findViewById(R.id.rgb_b);
		 spChoose = (Spinner)findViewById(R.id.rgb_choose);
		 btSet = (Button)findViewById(R.id.rgb_set);
		 btOff = (Button)findViewById(R.id.rgb_off);
		 
		 editr.setTextColor(Color.RED);
		 editg.setTextColor(Color.GREEN);
		 editb.setTextColor(Color.BLUE);
		 apName = new ArrayAdapter<String>(this,R.layout.message, listName);  
		 apUuid = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, listUuid);
		 apName.add("None");
		 apUuid.add("None");
		 spChoose.setAdapter(apName);
	}
	private void initDevice(){
		device = new JumaDevice(){

			@Override
			public void onConnect(UUID uuid, int status) {
				if (status == JumaDevice.STATE_SUCCESS ) {
					mConnected = true;
				}
			}

			@Override
			public void onDisconnect(UUID uuid, int status) {
				if(status == JumaDevice.STATE_ERROR){
					device.disconnect(deviceUuid);
				}else{
					mConnected = false;
				}
			}
				

			@Override
			public void onDiscover(String name, UUID uuid, int rssi) {
				listName.add(name);
				listUuid.add(uuid.toString());
				Message msg = new Message();
				handler.sendMessage(msg);
			}

			@Override
			public void onError(Exception e, int errorCode) {
				System.out.println(e.toString());
				if(errorCode == JumaDevice.ERROR_SCAN){
					device.scan(null);
				}
				
			}

			@Override
			public void onMessage(byte type, byte[] message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSend(int status) {
				
				new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							ensend = true;
						}
					}).start();
				
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
	private void buttonClick(){
		spChoose.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(!apName.getItem(position).equals("None")){
				deviceUuid = UUID.fromString(apUuid.getItem(position));
				device.connect(deviceUuid);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//spChoose.setAdapter(apName);
				Toast.makeText(getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();
			}});
		 myView.setOnColorSelectedListener(new OnColorSelectedListener(){

				@Override
				public void onColorSelected(float[] colorHSV) {
					if(mConnected == true){
				hsvtorgb(colorHSV);
					}else{
						Toast.makeText(getApplicationContext(), "disconnect", Toast.LENGTH_SHORT).show();
					}
				}
				});
		
		 
		 btOff.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mConnected){
					if(ensend){
						
					    device.send(type, hexToByte("00000000"));
					}else{
						Toast.makeText(getApplicationContext(), "Too fast", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_LONG).show();
				}
			}});
		 btSet.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				int edR,edG,edB;
				edR = Integer.parseInt(editr.getText().toString());
				edG = Integer.parseInt(editg.getText().toString());
				edB = Integer.parseInt(editb.getText().toString());
				myView.setColor(edR,edG,edB);
				if(mConnected){
					if(ensend){
						String r1,g1,b1;
						r1 = ((edR < 16) ? ("0"+Integer.toHexString(edR)) : Integer.toHexString(edR));
						g1 = ((edG < 16) ? ("0"+Integer.toHexString(edG)) : Integer.toHexString(edG));
						b1 = ((edB < 16) ? ("0"+Integer.toHexString(edB)) : Integer.toHexString(edB));
						ensend = false;
					    device.send(type, hexToByte("01"+r1+g1+b1));
					}else{
						Toast.makeText(getApplicationContext(), "Too fast", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_LONG).show();
				}
				
				
			}});
	}
	public void hsvtorgb(float[] colorHSV){
		float h,s,v,f;
		int r = 255,g = 255,B = 255,i,a,b,c;
		String r1,g1,b1;
		h = colorHSV[0];
		s = colorHSV[1];
		v = colorHSV[2]*255;
		if(s == 0){
			r = g = B = (int)v;
		}else{
			h /= 60;
			i = (int)(h);
			f = h-i;
			a = (int)(v*(1-s));
			b = (int)(v*(1-s*f));
			c = (int)(v*(1-s*(1-f)));
			switch(i){
			case 0:
				r = (int)v; g = c; B = a;
				break;
			case 1:
				r = b; g = (int)v; B = a; 
				break;
			case 2: 
				r = a; g =(int) v; B = c; 
				break;
			case 3: 
				r = a; g = b; B = (int)v;
				break;
			case 4: 
				r = c; g = a; B = (int)v;
				break;
			case 5: 
				r = (int)v; g = a; B = b;
				break;
			}
			
		}
		editr.setText(""+r);
		editg.setText(""+g);
		editb.setText(""+B);
		if(ensend){
			r1 = ((r < 16) ? ("0"+Integer.toHexString(r)) : Integer.toHexString(r));
			g1 = ((g < 16) ? ("0"+Integer.toHexString(g)) : Integer.toHexString(g));
			b1 = ((B < 16) ? ("0"+Integer.toHexString(B)) : Integer.toHexString(B));
			ensend = false;
		    device.send(type, hexToByte("01"+r1+g1+b1));
		}
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
		getMenuInflater().inflate(R.menu.rgblight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_disconnect) {
			device.disconnect(deviceUuid);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

