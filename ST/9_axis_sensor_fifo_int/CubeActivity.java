package com.juma.cannontoolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.cannontoolbox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.widget.GLImage;
import com.juma.widget.GLRender;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class CubeActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
	GLRender render = new GLRender(this);  
    GLSurfaceView glView;
    private boolean back = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cube_activity);
		sp = (Spinner)findViewById(R.id.spinner1);
		setSpinner();
		tb = (TableRow)findViewById(R.id.tableRow1);
		tb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onStop();
			}
		});
		 GLImage.load(this.getResources());
	        glView = new GLSurfaceView(this);  
	        glView.setRenderer(render); 
	        LinearLayout layout1 = (LinearLayout)findViewById(R.id.ddd);
	        layout1.addView(glView);
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
				myDevice.send((byte)0x01, CannonToolboxActivity.hexToByte("04"));
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
		private int bx,by,bz;
		private float anglex = 0f,x,y,z;  
		private float angley = 0f;  
		private float anglez = 0f; 
		@Override
		public void onReceive(byte type, byte[] message) {
			// TODO Auto-generated method stub
			super.onReceive(type, message);
			if(redata){
				redata = false;
				bx = 0;
				by = 0;
				bz = 0;
				bx |= message[0];
				bx <<= 8;
				bx |= (message[1]&0x00FF);
				by |= message[2];
				by <<= 8;
				by |= (message[3]&0x00FF);
				bz |= message[4];
				bz <<= 8;
				bz |= (message[5]&0x00FF);
					
					x = (float)bx/1010;
					y = (float)by/1010;
					z = (float)bz/1010;
					if(Math.abs(x)<=1&&Math.abs(y)<=1&&Math.abs(z)<=1){
						anglex = -(float) ((float) Math.asin(x)*180/Math.PI);
						angley = -(float) ((float) Math.asin(y)*180/Math.PI);
						if(bz < 0){
							if(Math.abs(anglex)>Math.abs(angley)){
							anglex = 180-anglex;
							}else{
								angley = 180-angley;
							}
						}
						render.setXYZ(anglex, angley, anglez);
					}
				
					redata = true;
				
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cube_activitty, menu);
		return true;
	}

}