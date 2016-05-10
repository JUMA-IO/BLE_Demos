package com.e.light;

import java.util.Scanner;
import java.util.UUID;

import com.e.light.CustomDialog.Callback;
import com.e.light.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanActivity extends Activity implements OnClickListener{
	
	public static final String ACTION_DEVICE_DISCOVERED = "com.e.light.ACTION_DEVICE_DISCOVERED";
	
	private Button btScan,btConnect;
	private ImageView iv1,iv2,iv3,iv4;
	private ScanHelper searcher;
	private byte type = 0x00,a = 0x00,b = 0x00,c = 0x00,d = 0x00;
	private Handler handler = new Handler();
	private JumaDevice myDevice = null;
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			searcher.stopScan();
			btScan.setEnabled(true);
			btConnect.setEnabled(true);
			btConnect.setBackgroundResource(R.drawable.bt_click);
			btScan.setBackgroundResource(R.drawable.bt_click);
		}
	};
	private TextView tv1,tv2;
	private String DeviceName = null;
	private Builder builder;
	
	private JumaDeviceCallback myCallback  = new JumaDeviceCallback() {
		@Override
		public void onConnectionStateChange(int status, int newState) {
			super.onConnectionStateChange(status, newState);
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						btConnect.setText("Disconnect");
//						btv.setEnabled(true);
//						btv.setBackgroundResource(R.drawable.bt_click);
//						btConnect.setEnabled(true);
//						btConnect.setBackgroundResource(R.drawable.bt_click);
//					}
//				});
				myDevice.send(type, new byte[]{0x00,(byte) (a|b|c|d)});
			}
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.ERROR){
				runOnUiThread(new Runnable() {
			
					@Override
					public void run() {
						// TODO Auto-generated method stub
						btScan.setEnabled(true);
						btConnect.setEnabled(true);
						btConnect.setBackgroundResource(R.drawable.bt_click);
						btScan.setBackgroundResource(R.drawable.bt_click);
					}
				});
			}
			if(newState == JumaDevice.STATE_DISCONNECTED){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
//						btv.setEnabled(false);
						btScan.setEnabled(true);
						btConnect.setEnabled(true);
//						btv.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
						btConnect.setBackgroundResource(R.drawable.bt_click);
						btScan.setBackgroundResource(R.drawable.bt_click);
					}
				});
			}
		}
	
		@Override
		public void onSend(int status) {
			
			super.onSend(status);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					btv.setEnabled(false);
					btScan.setEnabled(true);
					btConnect.setEnabled(true);
//					btv.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
					btConnect.setBackgroundResource(R.drawable.bt_click);
					btScan.setBackgroundResource(R.drawable.bt_click);
				}
			});
			
		}		
		
	};

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan);
		builder = new Builder(this);
		initView();
		initDevice();
	}
	private void initView(){
		btScan = (Button)findViewById(R.id.bt_scan);
		tv1 = (TextView)findViewById(R.id.tvN);
		tv2 = (TextView)findViewById(R.id.tvU);
		iv2 = (ImageView)findViewById(R.id.iv2);
		iv3 = (ImageView)findViewById(R.id.iv3);
		iv4 = (ImageView)findViewById(R.id.iv4);
		iv1 = (ImageView)findViewById(R.id.iv1);
//		btv = (Button)findViewById(R.id.bt_v);
		btConnect = (Button)findViewById(R.id.bt_c);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		iv4.setOnClickListener(this);
		btScan.setOnClickListener(this);
//		btv.setOnClickListener(this);
		btConnect.setOnClickListener(this);
//		btv.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
//		btv.setEnabled(false);
		btConnect.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
		btConnect.setEnabled(false);
	}
	private void initDevice(){
		searcher = new ScanHelper(this, new ScanCallback() {
			
						@Override
						public void onDiscover(JumaDevice device, int rssi) {
							if(btScan.isEnabled()){
								Intent intent = new Intent(ACTION_DEVICE_DISCOVERED);
								intent.putExtra("name", device.getName());
								intent.putExtra("uuid", device.getUuid().toString());
								intent.putExtra("rssi", rssi);
								LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
							}else{
								handler.removeCallbacks(r);
								searcher.stopScan();
								myDevice = device;
								Log.e("send", (byte)0x0f+"-"+(byte) (a|b|c|d));
								myDevice.connect(myCallback);
							}
						}
			
						@Override
						public void onScanStateChange(int status) {
						}
					
		});
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
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
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	     }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_scan:
			if(!searcher.isEnabled()){
				openBuilder();
			}else{
			searcher.startScan(null);
			final CustomDialog scanDialog = new CustomDialog(this,R.style.NobackDialog);
			scanDialog.setScanCallback(new Callback() {

				@Override
				public void onDevice(final UUID uuid, final String name) {
					searcher.stopScan();
					DeviceName = name;
					tv1.setText(name);
					tv2.setText(uuid.toString());
					btConnect.setBackgroundResource(R.drawable.bt_click);
					btConnect.setEnabled(true);
				}
				@Override
				public void onDismiss() {
					if(searcher.isScanning())
						searcher.stopScan();
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
			break;
		case R.id.iv1:
			if(a == 0x00){
				a = 0x01;
				iv1.setImageResource(R.drawable.light2);
			}else{
				a = 0x00;
				iv1.setImageResource(R.drawable.light1);
			}
			break;
		case R.id.iv2:
			if(b == 0x00){
				b = 0x02;
				iv2.setImageResource(R.drawable.light2);
			}else{
				b = 0x00;
				iv2.setImageResource(R.drawable.light1);
			}
			break;
		case R.id.iv3:
			if(c == 0x00){
				c = 0x04;
				iv3.setImageResource(R.drawable.light2);
			}else{
				c = 0x00;
				iv3.setImageResource(R.drawable.light1);
			}
			break;
		case R.id.iv4:
			if(d == 0x00){
				d = 0x08;
				iv4.setImageResource(R.drawable.light2);
			}else{
				d = 0x00;
				iv4.setImageResource(R.drawable.light1);
			}
			break;
		case R.id.bt_c:
			if(!searcher.isEnabled()){
				openBuilder();
			}else {
				handler.postDelayed(r, 10000);
				btScan.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
 				btConnect.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
				btConnect.setEnabled(false);
				btScan.setEnabled(false);
				searcher.startScan(DeviceName);
			}
			
			break;
//		case R.id.bt_v:
//			if(!searcher.isEnabled()){
//				openBuilder();
//			}else {
//				myDevice.send(type, new byte[]{0x00,(byte) (a|b|c|d)});
//			}
//			break;
	
		}
		
	}
	private void openBuilder(){
		builder.setTitle("Open the bluetooth?");
		builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if(!searcher.isEnabled())
					searcher.enable();
			}
		});
		builder.setNegativeButton("Cancle", null);
		builder.create().show();
	}
}
