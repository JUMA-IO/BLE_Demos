package com.example.temperaturegatheringdemo;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.temperaturegatheringdemo.CustomDialog.Callback;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

public class ScanAty extends Activity {

	public static final String ACTION_DEVICE_DISCOVERED = "com.example.temperaturegatheringdemo.ACTION_DEVICE_DISCOVERED";
	private ScanHelper scanHelper = null;
	private static JumaDevice jumaDevice = null;
	private HashMap<UUID, JumaDevice> deviceList = null;
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);  
		wakeLock =  powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

		deviceList = new HashMap<UUID, JumaDevice>();

		scanHelper = new ScanHelper(getApplicationContext(), new ScanCallback() {

			@Override
			public void onScanStateChange(int newState) {
				// TODO Auto-generated method stub
				if(newState == ScanHelper.STATE_STOP_SCAN)
					deviceList.clear();
			}

			@Override
			public void onDiscover(JumaDevice device, int rssi) {
				// TODO Auto-generated method stub
				System.out.println("device "+device.getName() );
				if(!deviceList.containsKey(device.getUuid())){
					deviceList.put(device.getUuid(), device);

					Intent intent = new Intent(ScanAty.ACTION_DEVICE_DISCOVERED);
					intent.putExtra("name", device.getName());
					intent.putExtra("uuid", device.getUuid().toString());
					intent.putExtra("rssi", rssi);
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}

			}
		});

		findViewById(R.id.btnScan).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				scanHelper.startScan(null);

				final CustomDialog scanDialog = new CustomDialog(ScanAty.this);
				scanDialog.setScanCallback(new Callback() {

					@Override
					public void onDevice(final UUID uuid, final String name) {

						scanHelper.stopScan();

						jumaDevice = deviceList.get(uuid);

						switchPage();

					}

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						if(scanHelper.isScanning())
							scanHelper.stopScan();
					}
				});

				scanDialog.setNegativeButton(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						scanDialog.dismiss();

					}
				});
				scanDialog.show();
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(scanHelper.isScanning())
			scanHelper.stopScan();
		wakeLock.release();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wakeLock.acquire();
	}

	public static JumaDevice getDevice(){
		if(jumaDevice != null)
			return jumaDevice;
		else
			return null;
	}

	private void switchPage(){
		Intent intent = new Intent();
		intent.setClass(ScanAty.this , TemperatureAty.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return false;
		else 
			return true;
	}

}
