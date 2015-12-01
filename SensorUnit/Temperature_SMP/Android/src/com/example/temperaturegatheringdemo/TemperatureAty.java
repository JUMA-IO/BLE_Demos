package com.example.temperaturegatheringdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;

public class TemperatureAty extends Activity {

	private TextView tvName = null;
	private ListView lvTemperature = null;
	private ArrayAdapter<String> listAdapter = null;
	private JumaDevice jumaDevice = null;
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;
	private boolean isTouch = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature);
		powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);  
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

		jumaDevice = ScanAty.getDevice();


		tvName = (TextView) findViewById(R.id.TvDeviceName);
		tvName.setText(jumaDevice.getName());

		findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchPage();	
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

		if(!jumaDevice.connect(deviceCallback)){
			switchPage();
		}

	}

	private SimpleDateFormat sdf = null;
	private Calendar cal = null;
	private StringBuffer buffer = new StringBuffer();

	private JumaDeviceCallback deviceCallback = new JumaDeviceCallback() {

		public void onConnectionStateChange(int status, int newState) {

			if(status == JumaDevice.ERROR || newState == JumaDevice.STATE_DISCONNECTED){
				switchPage();
			}

		};

		@SuppressLint("SimpleDateFormat")
		public void onReciver(byte type, final byte[] message) {
			if(Math.abs(type) == 0){
				if(buffer != null)
					buffer.append(byteToHex(message));
			}else if(Math.abs(type) == 1){
				runOnUiThread(new Runnable() {

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

		};

	};




	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wakeLock.release();
		switchPage();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			switchPage();
		}
		return false;
	}

	private void switchPage(){
		jumaDevice.disconnect();
		Intent intent = new Intent();
		intent.setClass(TemperatureAty.this, ScanAty.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}

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


}
