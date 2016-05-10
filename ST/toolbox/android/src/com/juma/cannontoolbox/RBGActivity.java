package com.juma.cannontoolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.cannontoolbox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.widget.ColorPicker;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class RBGActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
	private ColorPicker myView;
	private boolean tosend = true;
	private boolean back = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_rbg);
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		myView = (ColorPicker) findViewById(R.id.colorPicker);
		tb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onStop();
			}
		});
				
		setSpinner();
	}
	private ScanHelper scanner;
	private JumaDevice myDevice,cDevice;
	private boolean redata = true;
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
				myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte((myDevice.getName().equals("RGB_Light")?"00":"01")));
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Connected Boss", Toast.LENGTH_SHORT).show();
					}});
				
			}else{
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
						if(!back){
						scanner.startScan(null);
						}else{
							finish();
						}
					}});
			}
		}
		private int ax = 0,ay = 0,az = 0;
		String x ,y ,z; 
		@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(redata){
				redata = false;
					ax = 0;
					ax |= message[6];
					ax <<= 8;
					ax |= (message[7]&0x00FF);
					ay = 0;
					ay |= message[8];
					ay <<= 8;
					ay |= (message[9]&0x00FF);
					az = 0;
					az |= message[10];
					az <<= 8;
					az |= (message[11]&0x00FF);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							ax = (ax*255/2)/1020+255/2;
							ay = (ay*255/2)/1020+255/2;
							az = (az*255/2)/1020+255/2;
							myView.setColor(ax,ay,az);
							Log.e("","aaaaaa"+tosend);
							if(cDevice != null && tosend && cDevice.isConnected()){
								tosend = false;
								x = ((ax < 16) ? ("0"+((ax<0) ? "0":Integer.toHexString(ax))) : ((ax<255) ? Integer.toHexString(ax):"FF"));
								y = ((ay < 16) ? ("0"+((ay<0) ? "0":Integer.toHexString(ay))) : ((ay<255) ? Integer.toHexString(ay):"FF"));
								z = ((az < 16) ? ("0"+((az<0) ? "0":Integer.toHexString(az))) : ((az<255) ? Integer.toHexString(az):"FF"));
								cDevice.send((byte)0x00, CannonToolboxActivity.hexToByte("01"+x+y+z));
							}
							redata = true;
						}
					});
					
				
			}
			
		}
	
	};
	private JumaDeviceCallback callback1 = new JumaDeviceCallback() {
		@Override
		public void onSend(int status) {
			// TODO Auto-generated method stub
			super.onSend(status);
			if(status == JumaDevice.SUCCESS){
				tosend = true;
			}
		}
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
						Toast.makeText(getApplicationContext(), "Connect Staff", Toast.LENGTH_SHORT).show();
					}});
				
			}else if (newState == JumaDevice.STATE_DISCONNECTED){
				if(back){
					finish();
				}
				cDevice = null;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Disconnected Staff", Toast.LENGTH_SHORT).show();
					}});
				
			}
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
			if(!uuidList.contains(device.getUuid())){
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
	
	public void setSpinner(){
		apName = new ArrayAdapter<String>(this,R.layout.message);  
		apName.add("Choose Device");
		sp.setAdapter(apName);
		scanner = new ScanHelper(this, scancallback);
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(myDevice != null && cDevice != null){
					myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("00"));
					myDevice.disconnect();
				}else if(!apName.getItem(position).equals("Choose Device")){
					if(myDevice == null){
				        myDevice = deviceList.get(position-1);
				        myDevice.connect(callback);
					}else if(cDevice == null && myDevice.getUuid() != deviceList.get(position-1).getUuid()){
						cDevice = deviceList.get(position-1);
						cDevice.connect(callback1);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}});
		scanner.startScan(null);
	}
	 @Override
	    protected void onStop() {
	        super.onStop();
	        back = true;
			// TODO Auto-generated method stub
			if(myDevice != null && myDevice.isConnected()){
				myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("00"));
				myDevice.disconnect();
			}else{
			if(scanner.isScanning()){
				scanner.stopScan();
			}
			finish();
			}
	     }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cube_activitty, menu);
		return true;
	}

}