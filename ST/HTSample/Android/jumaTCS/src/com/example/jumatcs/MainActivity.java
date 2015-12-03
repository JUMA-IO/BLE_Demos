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
package com.example.jumatcs;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView td,wd,tt,trh,ws,ss;
	private EditText edT,edH;
	private double Tlimit = 28.0,HRlimit = 50.0;
	private Button btStart;
	private ScanHelper searcher;
	private JumaDevice tDevice = null,wDevice = null;
	private String tName = "BlueNRG_IOT_1",wName = "BlueNRG_IOT_2";
	private Handler handler = new Handler();
	private boolean sStop = false,wStop = true, cStop = false;
	private String sendData = null;
	private AlertDialog.Builder builder;
	private BluetoothAdapter mBluetoothAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan);
		builder = new Builder(this);
	    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		initView();
		initScan();
	}
	private void initView(){
		td = (TextView)findViewById(R.id.td);
		wd = (TextView)findViewById(R.id.wd);
		tt = (TextView)findViewById(R.id.tt);
		trh = (TextView)findViewById(R.id.trh);
		ws = (TextView)findViewById(R.id.ws);
		ss = (TextView)findViewById(R.id.ss);
		edT = (EditText)findViewById(R.id.edT);
		edH = (EditText)findViewById(R.id.edH);
		btStart = (Button)findViewById(R.id.bt_start);
		btStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!mBluetoothAdapter.isEnabled()) {
					builder.setTitle(" Please turn on Bluetooth ");
					builder.setPositiveButton("OK",null);
					builder.create().show();
				}else if(btStart.getText().equals("Start") && wStop ){
					Tlimit = Double.valueOf(edT.getText().toString());
					HRlimit = Double.valueOf(edH.getText().toString());
					wStop = false;
					cStop = false;
					searcher.startScan(null);
				}else if(btStart.getText().equals("Stop") && !wStop ){
					wStop = true;
					if(searcher.isScanning()){
						sStop = true;
						searcher.stopScan();
					}else{
					cStop = true;
					}
				}
			}
		});
		
	}
	private double getValue = 0,TValue = 0,HRValue = 0;
	private byte stype = 0x00;
	 @SuppressLint("UseValueOf")
		private static final byte[] hexToByte(String hex)throws IllegalArgumentException {
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
	private synchronized  void getValue(byte[] arg1,boolean e){
		    getValue = (double)Integer.parseInt(byteToHex(arg1),16)/100;
			if(e){
				TValue = getValue;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tt.setText(TValue+"¡æ");
						tt.setTextColor(TValue-Tlimit < 0?Color.BLACK:Color.RED);
					}
				});
				}else{
					HRValue = getValue;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							trh.setText(HRValue+"%RH");
							trh.setTextColor(HRValue-HRlimit < 0?Color.BLACK:Color.RED);
						}
					});
				}
				sendData = ((TValue-Tlimit)<0 && (HRValue-HRlimit)<0)?"00":"01";
				wDevice.send(stype, hexToByte(sendData));
			
 } 
	private JumaDeviceCallback tcallback = new JumaDeviceCallback() {
	@Override
	public void onConnectionStateChange(int status, int newState) {
		// TODO Auto-generated method stub
		super.onConnectionStateChange(status, newState);
		if(tDevice != null && wDevice != null){
		if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
			
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					td.setText("Connected");
					wDevice.connect(scallback);
				}
			}
			);
			//SDF();
		}else if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.ERROR){
			if(tDevice.isConnected()){
				tDevice.disconnect();
			}else{
				init();
			}
			
		}
		else if(newState == JumaDevice.STATE_DISCONNECTED && status == JumaDevice.SUCCESS){
			if(wDevice.isConnected()){
				wDevice.disconnect();
			}else{
				init();
			}
		}
		else if(newState == JumaDevice.STATE_DISCONNECTED && status == JumaDevice.ERROR){
			if(tDevice.isConnected()){
				tDevice.disconnect();
			}else if(wDevice.isConnected()){
				wDevice.disconnect();
			}else{
				cStop = true;
			}
		}
		}	
	}
	@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(wDevice.isConnected()){
				if(cStop){
					tDevice.disconnect();
				}else {
				if(type == (byte)0x00){
					getValue(message,true);
				}else if(type == (byte)0x01){
					getValue(message,false);
				}
			}
		}
		}
	};
	 @Override
	    protected void onStop() {
	        super.onStop();
	        if(btStart.getText().equals("Stop") && !wStop){
				wStop = true;
				if(searcher.isScanning()){
					sStop = true;
					searcher.stopScan();
				}else{
				cStop = true;
				}
		}
	     }
	private JumaDeviceCallback scallback = new JumaDeviceCallback() {
	
		@Override
		public void onConnectionStateChange(int status, int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(status, newState);
			if(tDevice != null && wDevice != null){
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
				if(cStop){
					wDevice.disconnect();
				}else{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ss.setText("Working");
						wd.setText("Connected");
					}
				}
				);
				}
			}else if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.ERROR){
				if(wDevice.isConnected()){
					wDevice.disconnect();
				}else if(tDevice.isConnected()){
					tDevice.disconnect();
				}else{
					init();
				}
				
			}
			else if(newState == JumaDevice.STATE_DISCONNECTED && status == JumaDevice.SUCCESS){
				if(tDevice.isConnected()){
					tDevice.disconnect();
				}else{
					init();
				}
			}
			else if(newState == JumaDevice.STATE_DISCONNECTED && status == JumaDevice.ERROR){
				if(wDevice.isConnected()){
					wDevice.disconnect();
				}else if(tDevice.isConnected()){
					tDevice.disconnect();
				}else{
					init();
				}
			}
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
						// TODO Auto-generated method stub
						ws.setText(sendData.equals("00")?"OFF":"ON");
					}
				
				}
				);
				}
		}
		};
		private void init(){
			
			tDevice = null;
			wDevice = null;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						td.setText("");
						wd.setText("");
						tt.setText("");
						trh.setText("");
						ss.setText("");
						ws.setText("");
						edT.setEnabled(true);
						edH.setEnabled(true);
						wStop = true;
						btStart.setText("Start");
						
					}
				
				}
				);
		}
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sStop = true;
			searcher.stopScan();
			builder.setTitle(" Did not find any devices ");
			builder.setPositiveButton("OK",null);
			builder.create().show();
		}
	};
	private void initScan(){
		searcher = new ScanHelper(getApplicationContext(), new ScanCallback() {
			
			@Override
			public void onScanStateChange(int status) {
				// TODO Auto-generated method stub
				if(status == ScanHelper.STATE_START_SCAN){
					handler.postDelayed(runnable, 10000);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							btStart.setText("Stop");
							ws.setText("OFF");
							edT.setEnabled(false);
							edH.setEnabled(false);
							ss.setText("Scanning");
						}
					}
					);
				}else{
					handler.removeCallbacks(runnable);
					if(sStop){
						sStop = false;
						tDevice = null;
						wDevice = null;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								edT.setEnabled(true);
								edH.setEnabled(true);
								ss.setText("");
								ws.setText("");
								td.setText("");
								wd.setText("");
								wStop = true;
								btStart.setText("Start");
							}
						}
						);
					}else{
						if(tDevice != null && wDevice != null){
						tDevice.connect(tcallback);
						}
					}
				}
				
			}
			
			@Override
			public void onDiscover(JumaDevice device, int arg1) {
				// TODO Auto-generated method stub
				if(device.getName().equals(tName)){
					tDevice = device;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(tDevice != null){
							td.setText(tDevice.getName());
							}
						}
					}
					);
				}else if(device.getName().equals(wName)){
					wDevice = device;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(wDevice != null){
							wd.setText(wDevice.getName());
							}
						}
					}
					);
				}
				if(tDevice != null && wDevice != null){
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ss.setText("Connecting");
							searcher.stopScan();
						}
					}
					);
				}
				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
