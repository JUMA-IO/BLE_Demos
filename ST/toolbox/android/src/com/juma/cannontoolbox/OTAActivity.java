package com.juma.cannontoolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.cannontoolbox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class OTAActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
	private EditText ed;
	private boolean otamode = false;
	private boolean back = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ota);
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		ed = (EditText)findViewById(R.id.editText1);
		tb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onStop();
			}
		});
		setSpinner();
	}

	 @Override
	    protected void onStop() {
	        super.onStop();
	        back = true;
			// TODO Auto-generated method stub
			if(myDevice != null && myDevice.isConnected()){
				myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("00"));
				myDevice.disconnect();
			}
			if(scanner.isScanning()){
				scanner.stopScan();
			}
			finish();
	     }

	 private ScanHelper scanner;
		private JumaDevice myDevice;
		private List<JumaDevice> deviceList = new ArrayList<JumaDevice>();
		private List<UUID> uuidList = new ArrayList<UUID>();
		private List<String> NameList = new ArrayList<String>();
		private ArrayAdapter<String> apName;
		private JumaDeviceCallback callback = new JumaDeviceCallback() {
			@Override
			public void onConnectionStateChange(int status,
					int newState) {
				// TODO Auto-generated method stub
				super.onConnectionStateChange(status, newState);
				if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
							ed.setEnabled(false);
						
						}});
					if(myDevice.setOtaMode()&&!otamode){
						otamode = true;
						//myDevice.disconnect();
					}else if(otamode){
						myDevice.updateFirmware(ed.getText().toString());
					}
				
					
				}else{
					if(!back){
						myDevice = null; 
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
								uuidList.clear();
								deviceList.clear();
								NameList.clear();
								apName.clear();
								apName.add("Choose Device");
								sp.setAdapter(apName);	
								scanner.startScan(null);
							}});
						}else{
							finish();
						}
					}
			}
			@Override
			public void onUpdateFirmware(int status) {
				// TODO Auto-generated method stub
				super.onUpdateFirmware(status);
				if(status == JumaDevice.SUCCESS){
					otamode = false;
				}
				myDevice.disconnect();
			}
			
		};
		private ScanCallback scancallback = new ScanCallback() {
			
			@Override
			public void onScanStateChange(int status) {
				// TODO Auto-generated method stub
				if(status == ScanHelper.STATE_STOP_SCAN && !back){
					myDevice.connect(callback);
				}
				
			}
			
			@Override
			public void onDiscover(JumaDevice device, int arg1) {
				// TODO Auto-generated method stub
				if(otamode){
					if(device.getName().equals("OTA Mode")){
						myDevice = device;
						myDevice.connect(callback);
				}
				}else if(!uuidList.contains(device.getUuid())){
					uuidList.add(device.getUuid());
					deviceList.add(device);
					NameList.add(device.getName());
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							  apName.clear();
							  apName.add("Choose Device");
							  apName.addAll(NameList);
							  sp.setAdapter(apName);
						}
						
					});
				}
			}
		};
		
		@SuppressLint("HandlerLeak")
		public void setSpinner(){
			apName = new ArrayAdapter<String>(this,R.layout.message);  
			apName.add("Choose Device");
			sp.setAdapter(apName);	
			scanner = new ScanHelper(this, scancallback);
			sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(myDevice != null && myDevice.isConnected()){
						myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("00"));
						myDevice.disconnect();
					}else if(!apName.getItem(position).equals("Choose Device")){
					myDevice = deviceList.get(position-1);
					myDevice.connect(callback);
					}else{
						myDevice = null;
						}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}});
			scanner.startScan(null);
		}
		
		


		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.magnetometer, menu);
			return true;
		}

	}
