package com.juma.demobox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.juma.demobox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class TemperatureAty extends Activity {

	private Spinner sp;
	private TableRow tb;
	private boolean back = false;
	private boolean isTouch = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trh);
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		tb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onStop();
			}
		});
		lvTemperature = (ListView) findViewById(R.id.lvTemperature);
		listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
		lvTemperature.setAdapter(listAdapter);

		lvTemperature.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == 0)
					isTouch = true;
				else if(event.getAction() ==1)
					isTouch = false;
				return false;
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
				myDevice.disconnect();
			}else {
			if(scanner.isScanning()){
				scanner.stopScan();
			}
			finish();
			}
	     }
	private ScanHelper scanner;
	private JumaDevice myDevice;
	private List<JumaDevice> deviceList = new ArrayList<JumaDevice>();
	private List<UUID> uuidList = new ArrayList<UUID>();
	private List<String> NameList = new ArrayList<String>();
	private ArrayAdapter<String> apName;
	private ListView lvTemperature = null;
	private ArrayAdapter<String> listAdapter = null;
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
					}});
				
			}else if (newState == JumaDevice.STATE_DISCONNECTED ){
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
						if(!back){
						scanner.startScan(null);
						}else{
							finish();
						}
					}});
			}
		}
		private SimpleDateFormat sdf = null;
		private Calendar cal = null;
		private StringBuffer buffer = new StringBuffer();
		
		@Override
		public void onReceive(byte type, final byte[] message) {
			if(Math.abs(type) == 0){
				if(buffer != null)
					buffer.append(byteToHex(message));
			}else if(Math.abs(type) == 1){
				runOnUiThread(new Runnable() {

					@SuppressLint("SimpleDateFormat")
					@Override
					public void run() {
						if(buffer  != null){
							String temperatures = buffer.toString();
							buffer = null;
							sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							cal =Calendar.getInstance();
							cal.add(Calendar.SECOND, -5 * (temperatures.length()/2));
							int length = temperatures.length()/2;
							for (int i = 0; i < length; i++) {
								cal.add(Calendar.SECOND, 5);
								StringBuffer sb = new StringBuffer();
								sb.append(sdf.format(cal.getTime()));
								sb.append("\t\t\t\t\t\t\t\t\t\t\t\t");
								sb.append(Integer.parseInt(byteToHex(message).substring(0, 2), 16));
								
								listAdapter.add(sb.toString());
								
								if(temperatures.length() > 2)
									temperatures = temperatures.substring(2, temperatures.length());
								if(!isTouch)
									lvTemperature.smoothScrollByOffset(listAdapter.getCount() - 1);
							}
						}
						
						StringBuffer b = new StringBuffer();
						b.append(getCurrentData(getApplicationContext()));
						b.append("\t\t\t\t\t\t\t\t\t\t\t\t");
						b.append(Integer.parseInt(byteToHex(message), 16));
						listAdapter.add(b.toString());
						if(!isTouch)
							lvTemperature.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

		}
	
	};
	@SuppressLint("SimpleDateFormat")
	private static String getCurrentData(Context context){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");    
		return sdf.format(new java.util.Date());
	}

	@SuppressLint("DefaultLocale")
	public String byteToHex(byte[] b) {  
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
