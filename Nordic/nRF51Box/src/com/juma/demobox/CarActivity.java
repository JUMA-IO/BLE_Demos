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

package com.juma.demobox;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class CarActivity extends Activity implements SensorEventListener {


	private boolean mConnected = false,sendData = false,stopScan = true,startScan = true;
	private int u = 0,d = 0,l = 0,r = 0, motion = 0,mChange = 0;
	private byte type=0x01;
	private Handler handler;
	private Message msg;
	private JumaDevice myDevice = null;
	private Button btUp,btDown,btLeft,btRight;
	private Spinner spChoose;
	private ImageView imageBt;
	private leftThread ti;
	private List<String> listName = new ArrayList<String>();  
	private List<JumaDevice> listDevice = new ArrayList<JumaDevice>();  
	private List<UUID> listUuid = new ArrayList<UUID>(); 
	private ArrayAdapter<String>apName;    
	private SensorManager mSensorMgr = null;    
	Sensor mSensor = null; 
	private ScanHelper scanner;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CONTEXT_MENU);  
        setTitle("");
		setContentView(R.layout.activity_car);
		initView();
		initDevice();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				switch(msg.what)  
				{  
				case 0:  
					Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
					break; 
				case 3:  
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
					break; 
				case 1:
					stopScan = true;
					Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
					listUuid.clear();
					listName.clear();
					listDevice.clear();
					apName.clear();
					apName.add("Choose Device");
					if(startScan){
						scanner.startScan(null);
					}

					break; 
				case 2:
					apName.clear();
					apName.add("Choose Device");
					apName.addAll(listName);
					break;
				}  

			}
		};
		ti=new leftThread();
		ti.start();
		ButtonOn();
	}

	private void initView(){
		spChoose = (Spinner)findViewById(R.id.car_sp);
		imageBt = (ImageView)findViewById(R.id.imagebt);
		btUp = (Button) findViewById(R.id.bt2);
		btDown = (Button) findViewById(R.id.bt4);
		btLeft = (Button) findViewById(R.id.bt1);
		btRight = (Button) findViewById(R.id.bt3); 
		apName = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);  
		apName.add("Choose Device");
		spChoose.setAdapter(apName);
		mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 

	}
	private JumaDeviceCallback callback = new JumaDeviceCallback(){
		@Override
		public void onConnectionStateChange(int status, int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(status, newState);
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
				mConnected = true;
				sendData = true;
				msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			}else{
				mConnected = false;
				sendData = false;
				msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}
		@Override
		public void onSend(int status) {
			// TODO Auto-generated method stub
			super.onSend(status);
			sendData = true;
		}
		
	};
	private void initDevice(){
		scanner = new ScanHelper(getApplicationContext(), new ScanCallback() {
			
			@Override
			public void onScanStateChange(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDiscover(JumaDevice device, int arg1) {
				// TODO Auto-generated method stub
				if(stopScan ){
					if(!listUuid.contains(device.getUuid())){
						listUuid.add(device.getUuid());
						listName.add(device.getName());
						listDevice.add(device);
						msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
				}
				}
				
			}
		});

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

	private class leftThread extends Thread{


		public void run() {      

			while(true){ 
				if(sendData && (motion != mChange) ){

					mChange = motion;

					switch(motion)  
					{ 
					case 0:  
						myDevice.send(type,hexToByte("0000"));
						break; 
					case 1:  
						myDevice.send(type,hexToByte("0101"));
						break; 
					case 2:
						myDevice.send(type,hexToByte("0202"));
						break; 
					case 4:
						myDevice.send(type,hexToByte("0201"));
						break;
					case 8:
						myDevice.send(type,hexToByte("0102"));
						break; 
					case 5:
						myDevice.send(type,hexToByte("0001"));
						break; 
					case 9:
						myDevice.send(type,hexToByte("0100"));
						break;
					case 6:
						myDevice.send(type,hexToByte("0002"));
						break;
					case 10:
						myDevice.send(type,hexToByte("0200"));
						break;
					}
					sendData = false;

				}
			}

		}   
	}
	@Override
	protected void onStart() {
		super.onStart();
		startScan = true;
		if(scanner.isEnabled()){
			scanner.startScan(null);
		}else{
			Toast.makeText(this, "Bluetooth disable", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		startScan = false;
		scanner.stopScan();
		if(mConnected){
			myDevice.disconnect();
		}
		finish();
	}
	  @Override    
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
	        if (resultCode == RESULT_OK) {    
	            Uri uri = data.getData();    
	            ContentResolver cr = this.getContentResolver();    
	            try {    
	                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
	                ImageView imageView = (ImageView) findViewById(R.id.imagebt);   
	                imageView.setImageBitmap(bitmap);    
	            } catch (FileNotFoundException e) {       
	            }    
	        }    
	        super.onActivityResult(requestCode, resultCode, data);    
	    }    
	
	@SuppressLint("ClickableViewAccessibility")
	private void ButtonOn(){
		spChoose.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(myDevice != null && myDevice.isConnected()){
					myDevice.send((byte)0x01, DemoboxActivity.hexToByte("00"));
					myDevice.disconnect();
				}else if(!apName.getItem(position).equals("Choose Device")){
				myDevice = listDevice.get(position-1);
				myDevice.connect(callback);
				}else{
					myDevice = null;
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}});
		imageBt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 Intent intent = new Intent();       
                 intent.setType("image/*");       
                 intent.setAction(Intent.ACTION_GET_CONTENT);    
                 startActivityForResult(intent, 1);    
				
			}
			
		});
		btUp.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(motion != 2){
					int ea = event.getAction();
					switch (ea) {
					case MotionEvent.ACTION_DOWN:
						u = 1;
						motion = u+d+l+r;
						break;
					case MotionEvent.ACTION_UP: 
						u = 0;
						motion = u+d+l+r;
						break;
					}}
				return false;
			}
		});

		btDown.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(motion != 1){
					int ea = event.getAction();
					switch (ea) { 
					case MotionEvent.ACTION_DOWN: 
						d = 2;
						motion = u+d+l+r;
						break;                   
					case MotionEvent.ACTION_UP:  
						d = 0;
						motion = u+d+l+r;
						break;
					}}
				return false;
			}
		});

		btLeft.setOnTouchListener(new OnTouchListener() {


			@Override
			public boolean onTouch(View v, MotionEvent event) { 

				if(motion != 8){
					int ea = event.getAction(); 
					switch (ea) {     
					case MotionEvent.ACTION_DOWN:  
						l = 4;
						motion = u+d+l+r;
						break;    
					case MotionEvent.ACTION_UP: 
						l = 0;
						motion = u+d+l+r;
						break;  
					}}
				return false;
			}
		});

		btRight.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(motion != 4){
					int ea = event.getAction(); 
					switch (ea) {    
					case MotionEvent.ACTION_DOWN:   
						r = 8;
						motion = u+d+l+r;
						break; 
					case MotionEvent.ACTION_UP:  
						r = 0;
						motion = u+d+l+r;
						break;
					}}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.car, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.start_GSensor) {

			mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
			return true;
		}
		if (id == R.id.stop_GSensor) {
			mSensorMgr.unregisterListener(this, mSensor);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(u != 0||d != 0||l != 0||r != 0){
			mSensorMgr.unregisterListener(this, mSensor);
			Toast.makeText(getApplicationContext(), "已关闭重力感应", Toast.LENGTH_SHORT).show();
		}

		float mGX = event.values[SensorManager.DATA_X];
		float  mGY= event.values[SensorManager.DATA_Y];
		if(Math.abs(mGX) <5 && Math.abs(mGY) <5){
			motion = 0;
		}else if(Math.abs(mGX) > Math.abs(mGY)){
			motion = (mGX > 0 ? 2:1);
		}else if(Math.abs(mGX) < Math.abs(mGY)){
			motion = (mGY > 0 ? 9:5);
		}


	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
