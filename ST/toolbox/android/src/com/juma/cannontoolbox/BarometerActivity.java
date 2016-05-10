package com.juma.cannontoolbox;

import java.text.DecimalFormat;
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
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BarometerActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
	private TextView tp,tc,tm;
	DecimalFormat df;
	private boolean back = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_barometer);
		df   = new DecimalFormat("######0.00");  
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		tp = (TextView)findViewById(R.id.tp);
		tc = (TextView)findViewById(R.id.tc);
		tm = (TextView)findViewById(R.id.tm);
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
					myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("02"));
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
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
			private int x = 0,y = 0;
			double t = 0,p = 0,h = 0;
			@Override
			public void onReceive(byte type, byte[] message) {
				// TODO Auto-generated method stub
				super.onReceive(type, message);
				if(redata){
					redata = false;
						x = 0;
						x |= message[0];
						x <<= 8;
						x |= (message[1]&0x00FF);
						x <<= 8;
						x |= (message[2]&0x0000FF);
						y = 0;
						y |= message[3];
						y <<= 8;
						y |= (message[4]&0x00FF);
						t = (double)y/100;
						p = (double)x/100;
						h = 18400*(1+t/273)*Math.log10(1027/p);
						
						
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								tp.setText(p+"hPa");
								tc.setText(t+"¡æ");
								tm.setText(df.format(h)+"m");
								redata = true;
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
