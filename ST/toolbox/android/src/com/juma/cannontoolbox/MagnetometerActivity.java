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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class MagnetometerActivity extends Activity {
	private Spinner sp;
	private ImageView iv1;
	private TableRow tb;
	private Bitmap b;
	private boolean back = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_magnetometer);
		sp = (Spinner)findViewById(R.id.spinner1);
		iv1 = (ImageView)findViewById(R.id.imageView2);
		tb = (TableRow)findViewById(R.id.tableRow1);
		tb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onStop();
			}
		});
		b = BitmapFactory.decodeResource(getResources(), R.drawable.compass2);
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
	public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {  
		 if (degree == 0 || bitmap == null) {  
			         return bitmap;  
			     }  
			     Matrix matrix = new Matrix();  
			     matrix.setRotate(degree, bitmap.getWidth() / 2, bitmap.getHeight() / 2);  
			     Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
			     return bmp;  
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
				myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("01"));
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
		private int mx = 0,my = 0,mz = 0,ax = 0,ay = 0,az = 0,r = 0;
		double ra,rb,hy,hx;
		@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(redata){
				redata = false;
					mx = 0;
					mx |= message[0];
					mx <<= 8;
					mx |= (message[1]&0x00FF);
					my = 0;
					my |= message[2];
					my <<= 8;
					my |= (message[3]&0x00FF);
					mz = 0;
					mz |= message[4];
					mz <<= 8;
					mz |= (message[5]&0x00FF);
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
					Log.e("",mx+"T"+my+"T"+mz+"T"+ax+"T"+ay+"T"+az);
					ra = Math.atan(ax/(Math.sqrt(ay*ay+az*az)));
					rb = Math.atan(ay/(Math.sqrt(ax*ax+az*az)));
					
					hy = my*Math.cos(rb)+mx*Math.sin(rb)*Math.sin(ra)-mz*Math.cos(ra)*Math.sin(rb);
					hx = mx*Math.cos(ra)+mz*Math.sin(ra);
					
					r = (int)(Math.atan2(hy,hx)*180/Math.PI);
					
					if(r<0){
						r += 360;
					}
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							iv1.setImageBitmap(rotateBitmap(r,b));
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
				if(!back){
				myDevice.connect(callback);
				}
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
