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

package com.example.mycar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.example.mycar.CustomDialog.ScanCallback;
import juma.sdk.lite.JumaDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

public static final String ACTION_DEVICE_DISCOVERED = "com.juma.demo.ACTION_DEVICE_DISCOVERED";
public static final String ACTION_CONNECTED = "com.juma.demo.ACTION_CONNECTED";
public static final String ACTION_DISCONNECTED = "com.juma.demo.ACTION_DISCONNECTED";
public static final String NAME_STR = "name";
public static final String UUID_STR = "uuid";
public static final String RSSI_STR = "rssi";

private JumaDevice device = null;
private List<HashMap<String, Object>> deviceInfo = null;
private Button rightAdd,leftAdd,leftCut,rightCut,chooseCar;
private UUID deviceUuid = null;
private String devicename=null;
private leftThread ti;
private Message msg;
private CustomListViewAdapter lvDevcieAdapter = null;
private boolean mConnected = false,ruonTouch=true,luonTouch=true,rdonTouch=true,ldonTouch=true,sendDate;
private SharedPreferences share;
private SharedPreferences.Editor edit;
private String du=null;
private byte type=0x01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		setContentView(R.layout.activity_main); 
		share=MainActivity.this.getSharedPreferences("UUID", Activity.MODE_PRIVATE);
		edit=share.edit(); 
		if(share.getString("duuid", "")!=""){
			
			du=share.getString("duuid", "");
	        devicename=share.getString("nmname", "");
	        
		}
		ti=new leftThread();
	 	ti.start();
		initView();
		initDevice();
	}
	
	 private void initView(){

			chooseCar = (Button) findViewById(R.id.bt5);
			leftAdd = (Button) findViewById(R.id.bt1);
			leftCut = (Button) findViewById(R.id.bt3);
			rightAdd= (Button) findViewById(R.id.bt2);
			rightCut = (Button) findViewById(R.id.bt4);  

		}
	 private void initDevice(){
			device = new JumaDevice() {

				@Override
				public void onConnect(UUID uuid, int status) {
					if(status==JumaDevice.STATE_SUCCESS){
						Intent intent = new Intent(MainActivity.ACTION_CONNECTED);
						sendBroadcast(MainActivity.this, intent);
					}
				}

				@Override
				public void onDisconnect(UUID uuid, int status) {
					Intent intent = new Intent(MainActivity.ACTION_DISCONNECTED);
					sendBroadcast(MainActivity.this, intent);
					
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
				public void onError(Exception e, int status) {
					
				}

				@Override
				public void onMessage(byte type, byte[] message) {
					
				}

				@Override
				public void onSend(int status) {
					if(status==JumaDevice.STATE_SUCCESS){
						sendDate=false;
					}
				}

				@Override
				public void onStopScan() {
					
				}

				@Override
				public void onUpdateFirmware(int arg0) {
					
				}
			};

			device.init(getApplicationContext());

		}

	 private void addDeviceInfo(String name, String uuid, int rssi){

			if(deviceInfo != null && lvDevcieAdapter != null){
				HashMap<String , Object> map = new HashMap<String, Object>();
				map.put(NAME_STR, name);
				map.put(UUID_STR, uuid);
				map.put(RSSI_STR, rssi);
				deviceInfo.add(map);
				lvDevcieAdapter.notifyDataSetChanged();

			}

		}
		private final BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {

				String action = intent.getAction();
				if(action.equals(ACTION_DEVICE_DISCOVERED)){
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
				if(action.equals(ACTION_CONNECTED)){
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mConnected=true;
                            chooseCar.setText(""+devicename);
                            
						}
					});
				}
				if(action.equals(ACTION_DISCONNECTED)){
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mConnected=false;
							chooseCar.setText("Car");
							Toast.makeText(getApplicationContext(), "已断开", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		};

		private IntentFilter getIntentFilter(){
			
			IntentFilter filter = new IntentFilter();
			filter.addAction(MainActivity.ACTION_DEVICE_DISCOVERED);
			filter.addAction(MainActivity.ACTION_CONNECTED);
			filter.addAction(MainActivity.ACTION_DISCONNECTED);
			return filter;
		}
		@SuppressLint("DefaultLocale")
		public static String byteToHex(byte[] b) {  
			
			StringBuffer hexString = new StringBuffer();  
			for (int i = 0; i < b.length; i++) {  
				String hex = Integer.toHexString(b[i] & 0xFF);  
				if (hex.length() == 1) {  
					hex = '0' + hex;  
				}  
				hexString.append(hex.toUpperCase());  
			}  
			return hexString.toString();  
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
		private void sendBroadcast(Context context, Intent intent){
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}
		 private class leftThread extends Thread{
			 public Handler mHandler;   
			  
		      public void run() {   
		          Looper.prepare();   
		  
		          mHandler = new Handler() {   
		              public void handleMessage(Message msg) {  
		            	  sendDate=true;
		            	  switch(msg.what)  
		                  {  
		                  case 1:  
		                      device.send(type,hexToByte("01"));
		                      break; 
		                  case 2:
		                	  device.send(type,hexToByte("02"));
		                	  break; 
		                  case 11:
		                	  device.send(type,hexToByte("11"));
		                	  break;
		                  case 3:
		                	  device.send(type,hexToByte("03"));
		                	  break; 
		                  case 4:
		                	  device.send(type,hexToByte("04"));
		                	  break; 
		                  case 14:
		                	  device.send(type,hexToByte("14"));
		                	  break;
		                  } 
		            	  while(sendDate){  
		            	  }
		              }   
		          };   
		          Looper.loop();   
		      }   
		 }
	 @Override
	    protected void onStart() {
	        super.onStart();
	        
	     	Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
	     LocalBroadcastManager.getInstance(this).registerReceiver(receiver, getIntentFilter());
	     
	     }
private void ButtonOn(){
	 chooseCar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(mConnected==false){
					if(du==null){
						CustomDialog scanDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SCAN);
						scanDialog.setScanCallback(new ScanCallback() {
							
							@Override
							public void onName(String name) {
								
								device.scan(name);
								
							}
							
							@Override
							public void onDevice(final UUID uuid, final String name) {
								
								deviceUuid = uuid;
								devicename=name;
								edit.putString("nmname", name);
								edit.putString("duuid", deviceUuid.toString());
								edit.commit(); 
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										
										device.connect(deviceUuid);	
										
									}	
								});
							}
						});
						
						scanDialog.show();
						
					}else{
						device.connect(UUID.fromString(du));
					}
				}else{
					Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_LONG).show();
				}
			}});
	 
     chooseCar.setOnLongClickListener(new OnLongClickListener(){
    	 
    	 @Override
    	 public boolean onLongClick(View v) {
    		 CustomDialog scanDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SCAN);
			scanDialog.setScanCallback(new ScanCallback() {
				
				@Override
				public void onName(String name) {
					
					device.scan(name);
					
				}
				
				@Override
				public void onDevice(final UUID uuid, final String name) {
					
					deviceUuid = uuid;
					devicename=name;
					edit.putString("nmname", name);
					edit.putString("duuid", deviceUuid.toString());
	                edit.commit(); 
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							device.connect(deviceUuid);	
						}
					});
				}
			});
			scanDialog.show();
			return false;
		}});
     
     leftAdd.setOnTouchListener(new OnTouchListener() {
    	 
    	 @Override
    	 public boolean onTouch(View v, MotionEvent event) {
    		 
    		 if(luonTouch==true){
    			 int ea = event.getAction();
    			 switch (ea) {
    			 case MotionEvent.ACTION_DOWN:
    				 ldonTouch=false;
    				 msg=new Message();
    				 msg.what=2;
    				 ti.mHandler.sendMessage(msg);
    				 break;
    			 case MotionEvent.ACTION_UP: 
    				 msg=new Message();
    				 msg.what=14;
    				 ti.mHandler.sendMessage(msg);
    				 ldonTouch=true;
    				 break;
    			 }}
     	        return false;
     	 }
    });
     
     rightAdd.setOnTouchListener(new OnTouchListener() {
    	 
    	 @Override
    	 public boolean onTouch(View v, MotionEvent event) {
    		 
    		 if(ruonTouch==true){
    			 int ea = event.getAction();
    			 switch (ea) { 
    			 case MotionEvent.ACTION_DOWN: 
    				 rdonTouch=false;
    				 msg=new Message();
    				 msg.what=1;
    				 ti.mHandler.sendMessage(msg);
    				 break;                   
                 case MotionEvent.ACTION_UP:  
        	         msg=new Message();
          	         msg.what=11;
          	         ti.mHandler.sendMessage(msg);
          	         rdonTouch=true;
          	         break;
          	     }}
    		 return false;
    	 }
    });
     
     leftCut.setOnTouchListener(new OnTouchListener() {
    	 
    	 @Override
		 public boolean onTouch(View v, MotionEvent event) { 
    		 
    		 if(ldonTouch==true){
    			 int ea = event.getAction(); 
    			 switch (ea) {     
    			 case MotionEvent.ACTION_DOWN:  
    				 luonTouch=false;
    				 msg=new Message();
    				 msg.what=4;
    				 ti.mHandler.sendMessage(msg);
    				 break;    
    			 case MotionEvent.ACTION_UP: 
    				 msg=new Message();
    				 msg.what=14;
    				 ti.mHandler.sendMessage(msg);
    				 luonTouch=true;
    				 break;  
    			}}
    		 return false;
    	}
    });
     
     rightCut.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(rdonTouch==true){
					int ea = event.getAction(); 
					switch (ea) {    
					case MotionEvent.ACTION_DOWN:   
						ruonTouch=false;
						msg=new Message();
						msg.what=3;
						ti.mHandler.sendMessage(msg);
						break; 
					case MotionEvent.ACTION_UP:  
						msg=new Message();
						msg.what=11;
						ti.mHandler.sendMessage(msg);
						ruonTouch=true;
						break;
					}}
				return false;
			}
	});
}


    @SuppressLint("ClickableViewAccessibility")
	@Override
	    protected void onResume() {
	        super.onResume();
	        ButtonOn();
	        
    }
	 @Override
		protected void onDestroy() {

			super.onDestroy();
			LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
			

		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_disconnect) {
			if(mConnected==true){
				if(du!=null){
					device.disconnect(UUID.fromString(du));
				}else{
					device.disconnect(deviceUuid);	
				}
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
