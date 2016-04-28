package com.juma.demobox;

import java.util.ArrayList;
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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class BarometerActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
	private Button bt;
	private boolean back = false;
	private String FirstName = "Choose Device";
	private String sendData = "01";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_barometer);
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		bt = (Button)findViewById(R.id.button1);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myDevice != null && !myDevice.isConnected()){
					myDevice.connect(callback);
				}
				
			}
		});
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
			public void onSend(int status) {
				// TODO Auto-generated method stub
				super.onSend(status);
				if(status == JumaDevice.SUCCESS){
					FirstName = myDevice.getName();
					sendData = (sendData.equals("00")?"01":"00");
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							bt.setBackgroundResource(sendData.equals("01")?R.drawable.fan2:R.drawable.fan1);
							Toast.makeText(getApplicationContext(), sendData.equals("01")?"Stop":"Start", Toast.LENGTH_SHORT).show();
						}});
				}
				myDevice.disconnect();
			}
			@Override
			public void onConnectionStateChange(int status,
					int newState) {
				// TODO Auto-generated method stub
				super.onConnectionStateChange(status, newState);
				if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
					myDevice.send((byte)0x01, DemoboxActivity.hexToByte(sendData));
				
					
				}else{
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							bt.setBackgroundResource(sendData.equals("01")?R.drawable.fan2:R.drawable.fan1);
							uuidList.clear();
							deviceList.clear();
							NameList.clear();
							apName.clear();
							apName.add(FirstName);
							if(!back){
							scanner.startScan(null);
							}else{
								finish();
							}
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
							  apName.add(FirstName);
							  apName.addAll(NameList);
						}
						
					});
				}
			}
		};
		
		@SuppressLint("HandlerLeak")
		public void setSpinner(){
			apName = new ArrayAdapter<String>(this,R.layout.message);  
			apName.add(FirstName);
			sp.setAdapter(apName);	
			scanner = new ScanHelper(this, scancallback);
			sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(myDevice != null && myDevice.isConnected()){
						myDevice.disconnect();
					}else if(!apName.getItem(position).equals(FirstName)){
					myDevice = deviceList.get(position-1);
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
