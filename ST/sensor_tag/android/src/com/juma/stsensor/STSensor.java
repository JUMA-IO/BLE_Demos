package com.juma.stsensor;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.stsensor.CustomDialog.Callback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;

public class STSensor extends Activity {
	private Button btStart;
	private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12;
	private SeekBar pb1,pb2,pb3,pb4,pb5,pb6,pb7,pb8,pb9,pb10,pb11,pb12;  
	private ScanHelper scanner;
	private JumaDevice myDevice;
	private boolean canReceive = true;
	private HashMap<UUID, JumaDevice> deviceList =  new HashMap<UUID, JumaDevice>();
	public static final String ACTION_DEVICE_DISCOVERED = "com.example.temperaturegatheringdemo.ACTION_DEVICE_DISCOVERED";
	DecimalFormat df;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_info);
		initView();
		scanDevice();
		clicked();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(myDevice != null && myDevice.isConnected())
			myDevice.disconnect();
	}

	private JumaDeviceCallback callback = new JumaDeviceCallback() {
		@Override
		public void onConnectionStateChange(int status, int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(status, newState);
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btStart.setText("Receiving");
						btStart.setBackgroundResource(R.drawable.bt_click);
						btStart.setEnabled(true);
					}
					
				});
			}else {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btStart.setText("Select Device");
						btStart.setBackgroundResource(R.drawable.bt_click);
						btStart.setEnabled(true);
						TextView[]	tv = {tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12};
						SeekBar[] pb = {pb1,pb2,pb3,pb4,pb5,pb6,pb7,pb8,pb9,pb10,pb11,pb12};
						for(int i=0;i<tv.length;i++){
							tv[i].setText("");
							pb[i].setProgress(0);
						}
					}
					
				});
			}
		}
	
		@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(canReceive){
				canReceive = false;
				x = 0;
				y = 0;
				z = 0;
				Type = type;
				if(message.length<3){
				x |= message[0];
				x <<= 8;
				x |= (message[1]&0x00FF);
				}else if(message.length == 3){
					x |= message[0];
					x <<= 8;
					x |= (message[1]&0x00FF);
					x <<= 8;
					x |= (message[2]&0x0000FF);
				}else{
					x |= message[0];
					x <<= 8;
					x |= (message[1]&0x00FF);
					y |= message[2];
					y <<= 8;
					y |= (message[3]&0x00FF);
					z |= message[4];
					z <<= 8;
					z |= (message[5]&0x00FF);
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						switch(Type){
						case 0x00:
							tv1.setText((double)x/100+"℃");
							pb1.setProgress(x/100);
							break;
						case 0x01:
							tv2.setText((double)x/100+"RH%");
							pb2.setProgress(x/100);
							break;
						case 0x02:
							tv3.setText((double)x/100+"hPa");
							pb3.setProgress((x/100)-260);
							break;
						case 0x03:
							tv4.setText(""+(double)x/100+"Gs");
							tv5.setText(""+(double)y/100+"Gs");
							tv6.setText(""+(double)z/100+"Gs");
							pb4.setProgress(x/100+500);
							pb5.setProgress(y/100+500);
							pb6.setProgress(z/100+500);
							break;
						case 0x04:
							tv7.setText((double)x/100+"m/s²");
							tv8.setText((double)y/100+"m/s²");
							tv9.setText((double)z/100+"m/s²");
							pb7.setProgress(x/100+20);
							pb8.setProgress(y/100+20);
							pb9.setProgress(z/100+20);
							break;
						case 0x05:
							tv10.setText(""+df.format((double)x/1000)+"mdps");
							tv11.setText(""+df.format((double)y/1000)+"mdps");
							tv12.setText(""+df.format((double)z/1000)+"mdps");
							pb10.setProgress(x/100+500);
							pb11.setProgress(y/100+500);
							pb12.setProgress(z/100+500);
							break;
						}
						canReceive = true;
					}
				});
			}
		}
	};

	private int x = 0,y = 0,z = 0;
	private byte Type;
	private void clicked(){
		btStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(btStart.getText().equals("Receiving")){
					myDevice.disconnect();
				}else{
					deviceList.clear();
					scanner.startScan(null);
					final CustomDialog scanDialog = new CustomDialog(STSensor.this, R.style.NobackDialog);
					scanDialog.setScanCallback(new Callback() {
						
						@Override
						public void onDevice(final UUID uuid, final String name) {
							scanner.stopScan();
							myDevice = deviceList.get(uuid);
							btStart.setEnabled(false);
							btStart.setBackgroundColor(Color.argb(0x20, 0x00, 0x00, 0x00));
							btStart.setText("Connecting");
							myDevice.connect(callback);
						}

						@Override
						public void onDismiss() {
							scanner.stopScan();
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
			}
		});
			
		
	}
	private void initView(){
		tv1 = (TextView)findViewById(R.id.tv1);
		tv2 = (TextView)findViewById(R.id.tv2);
		tv3 = (TextView)findViewById(R.id.tv3);
		tv4 = (TextView)findViewById(R.id.tv4);
		tv5 = (TextView)findViewById(R.id.tv5);
		tv6 = (TextView)findViewById(R.id.tv6);
		tv7 = (TextView)findViewById(R.id.tv7);
		tv8 = (TextView)findViewById(R.id.tv8);
		tv9 = (TextView)findViewById(R.id.tv9);
		tv10 = (TextView)findViewById(R.id.tv10);
		tv11 = (TextView)findViewById(R.id.tv11);
		tv12 = (TextView)findViewById(R.id.tv12);
		pb1 = (SeekBar)findViewById(R.id.pb1);
		pb2 = (SeekBar)findViewById(R.id.pb2);
		pb3 = (SeekBar)findViewById(R.id.pb3);
		pb4 = (SeekBar)findViewById(R.id.pb4);
		pb5 = (SeekBar)findViewById(R.id.pb5);
		pb6 = (SeekBar)findViewById(R.id.pb6);
		pb7 = (SeekBar)findViewById(R.id.pb7);
		pb8 = (SeekBar)findViewById(R.id.pb8);
		pb9 = (SeekBar)findViewById(R.id.pb9);
		pb10 = (SeekBar)findViewById(R.id.pb10);
		pb11 = (SeekBar)findViewById(R.id.pb11);
		pb12 = (SeekBar)findViewById(R.id.pb12);
		btStart = (Button)findViewById(R.id.bt_start);
		df = new DecimalFormat("######0.0");
	}
	
	private void scanDevice(){
		scanner = new ScanHelper(getApplicationContext(), new ScanCallback(){

			@Override
			public void onDiscover(JumaDevice device, int rssi) {
				// TODO Auto-generated method stub
				if(!deviceList.containsKey(device.getUuid())){
					deviceList.put(device.getUuid(), device);
				}
				Intent intent = new Intent(STSensor.ACTION_DEVICE_DISCOVERED);
				intent.putExtra("name", device.getName());
				intent.putExtra("uuid", device.getUuid().toString());
				intent.putExtra("rssi", rssi);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}

			@Override
			public void onScanStateChange(int arg0) {
				// TODO Auto-generated method stub
				
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
