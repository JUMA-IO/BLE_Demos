package com.juma.demobox;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Speedometer extends Activity {
	private TextView tvMil,tvSpeed,tvBattery,tvTime,tvTemp,tvW;
	private Button btReset,btBack;
	private Spinner spDevice,spD;
	private BluetoothAdapter mBluetoothAdapter;
	private ScanHelper scanner;
	private JumaDevice myDevice = null;
	private boolean clearnumber = false,redata = true;
	private int n = 0,m = 0,t = 1;
	private double d = 0;
	private List<JumaDevice> deviceList = new ArrayList<JumaDevice>();
	private List<UUID> uuidList = new ArrayList<UUID>();
	private List<String> NameList = new ArrayList<String>();
	private ArrayAdapter<String> apName,apWD;
	private double[] wd = {406,451,559,584,622};
	private static final String[] WD = {"¦Õ406mm","¦Õ451mm","¦Õ559mm","¦Õ584mm","¦Õ622mm"};
	DecimalFormat    df;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_speedometer);
		initView(); 
		df   = new DecimalFormat("######0.0");   
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
		initDevice();
		onBtClick();
		if(mBluetoothAdapter.isEnabled()){
		scanner.startScan(null);
		}else{
			Toast.makeText(getApplicationContext(), "Please open the bluetooth", Toast.LENGTH_SHORT).show();
		}

	}
	private Handler handler = new Handler( );
	String hh = "00",mm = "00",ss = "00";
	int h = 0,M = 0,s = 0;
	private Runnable runnable = new Runnable( ) {
		public void run ( ) {
			t++;
			h = t/3600;
			M = t/60;
			s = t%60;
			hh = (h>9?""+h:"0"+h);
			mm = (M>9?""+M:"0"+M);
			ss = (s>9?""+s:"0"+s);
			if(M>40||h==1){
				tvTime.setText("YOU should rest:"+hh+":"+mm+":"+ss);
			}else if( h==2 ){
				tvTime.setText("Strike");
			}else{
			tvTime.setText(hh+":"+mm+":"+ss);
			}
			handler.postDelayed(runnable,1000); 
		}
	};
	private JumaDeviceCallback  callback = new JumaDeviceCallback() {
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
						btReset.setText("Stop");
						tvTime.setText("00:00:01");
						handler.postDelayed(runnable,1000); 
					}
					
				});
				
			}else {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						 handler.removeCallbacks(runnable);
						if(clearnumber){
							clearnumber = false;
							n = 0;
							m = 0;
							t = 1;
							deviceList.clear();
							uuidList.clear();
							NameList.clear();
							apName.clear();
							apName.add("Choose Device");
							apName.addAll(NameList);
							spDevice.setAdapter(apName);
							tvBattery.setText("¡ª¡ª¡ª¡ª¡ª");
							tvBattery.setTextColor(Color.BLACK);
//							tvTemp.setText("0¡æ");
						}
						btReset.setText("Start");
						if(!back){
						scanner.startScan(null);
						}else{
							finish();
						}
					}
					
				});
				
			}
		}
		private short bx = 0,by = 0,bz = 0,x = 0,y = 0,bT = 0,bV = 0;
		@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(redata){
				redata = false;
			bx = 0;
			bx |= message[0];
			bx <<= 8;
			bx |= (message[1]&0x00FF);
			by = 0;
			by |= message[2];
			by <<= 8;
			by |= (message[3]&0x00FF);
			bz = 0;
			bz |= message[4];
			bz <<= 8;
			bz |= (message[5]&0x00FF);
			bT = 0;
			bT |= message[7];
			bV = 0;
			bV |= message[6];
			if(bx > 0 && x < 0 || bx < 0 && x > 0 ){
				n++;
			}
			if(by > 0 && y < 0 || by < 0 && y > 0 ){
				m++;
			}
			x = bx;
			y = by;
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvMil.setText(""+(int)((n<m?n/2:m/2)*d));
					tvSpeed.setText(df.format((((double)((n<m?n/2:m/2)*d)/t)*3.6))+"");
					tvTemp.setText(bT+"¡æ");
					tvBattery.setTextColor(Color.GREEN);
					switch(bV){
					case 1:
						tvBattery.setTextColor(Color.RED);
						tvBattery.setText("¡ð¡ð¡ð¡ð¡ñ");
						break;
					case 2:
						tvBattery.setText("¡ð¡ð¡ð¡ñ¡ñ");
						break;
					case 3:
						tvBattery.setText("¡ð¡ð¡ñ¡ñ¡ñ");
						break;
					case 4:
						tvBattery.setText("¡ð¡ñ¡ñ¡ñ¡ñ");
						break;
					case 5:
						tvBattery.setText("¡ñ¡ñ¡ñ¡ñ¡ñ");
						break;
					}
					redata = true;
				}
				
			});
			}
			
		}
	};
	private void onBtClick(){
		btReset.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
					if(btReset.getText().equals("Start")){
					if(myDevice != null){
						tvW.setTextColor(Color.BLACK);
						btReset.setText("Waiting");
						d = wd[spD.getSelectedItemPosition()]/1000*3.14;
						scanner.stopScan();
					}else{
						tvW.setTextColor(Color.RED);
						Toast.makeText(getApplicationContext(), "Please choose the device", Toast.LENGTH_SHORT).show();
					}
					}else if(btReset.getText().equals("Stop")){
						clearnumber = true;
						myDevice.disconnect();
					}
					
				
			}
		});
		btBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onStop();
			}
		});
		 spDevice.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(myDevice != null && myDevice.isConnected()){
						myDevice.disconnect();
					}
					if(!apName.getItem(position).equals("Choose Device")){
					myDevice = deviceList.get(position-1);
					}else{
						myDevice = null;
						}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}});
	}
	private void initView(){
		tvMil = (TextView)findViewById(R.id.tvmil);
		tvW = (TextView)findViewById(R.id.tvw);
		tvSpeed = (TextView)findViewById(R.id.tvspeed);
		tvBattery = (TextView)findViewById(R.id.tvbattery);
		tvTemp = (TextView)findViewById(R.id.tvtemp);
		tvTime = (TextView)findViewById(R.id.tvtime);
		btReset = (Button)findViewById(R.id.btreset);
		btBack = (Button)findViewById(R.id.btback);
		spDevice = (Spinner)findViewById(R.id.spdevice);
		spD = (Spinner)findViewById(R.id.spd);
		apWD = new ArrayAdapter<String>(this, R.layout.message, WD);
		spD.setAdapter(apWD);
		apName = new ArrayAdapter<String>(this,R.layout.message);  
		apName.add("Choose Device");
		spDevice.setAdapter(apName);
	}
	private boolean back = false;
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
	private void initDevice(){
		scanner = new ScanHelper(getApplicationContext(), new ScanCallback() {
			
			@Override
			public void onScanStateChange(int status) {
				// TODO Auto-generated method stub
				if(status == ScanHelper.STATE_STOP_SCAN){
					if(myDevice != null)
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
							spDevice.setAdapter(apName);
							
						}
						
					});
				}
			}
		});
	}


}
