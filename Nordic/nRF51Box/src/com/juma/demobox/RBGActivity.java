package com.juma.demobox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.juma.demobox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.widget.ColorPicker;
import com.juma.widget.ColorPicker.OnColorSelectedListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class RBGActivity extends Activity implements OnClickListener{

	private Spinner sp;
	private TableRow tb;
	private EditText editr,editg,editb;
	private Button btSet,btOff;
	private ColorPicker myView;
	private boolean back = false;
	private boolean ensend = false;
	private byte type = 0x01;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_rbg);
		sp = (Spinner)findViewById(R.id.spinner1);
		tb = (TableRow)findViewById(R.id.tableRow1);
		myView = (ColorPicker) findViewById(R.id.colorPicker);
		editr = (EditText)findViewById(R.id.rgb_r);
		editg = (EditText)findViewById(R.id.rgb_g);
		editb = (EditText)findViewById(R.id.rgb_b);
		btSet = (Button)findViewById(R.id.rgb_set);
		btOff = (Button)findViewById(R.id.rgb_off);
		tb.setOnClickListener(this);
		btSet.setOnClickListener(this);
		btOff.setOnClickListener(this);
		myView.setOnColorSelectedListener(new OnColorSelectedListener(){

			@Override
			public void onColorSelected(float[] colorHSV) {
				if(ensend){
					ensend = false;
			hsvtorgb(colorHSV);
				}
			}
			
		});
		setSpinner();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tableRow1:
			onStop();
			break;
		case R.id.rgb_set:
			if(ensend){
				ensend = false;
			int edR,edG,edB;
			edR = Integer.parseInt(editr.getText().toString());
			edG = Integer.parseInt(editg.getText().toString());
			edB = Integer.parseInt(editb.getText().toString());
			myView.setColor(edR,edG,edB);
			String r1,g1,b1;
			r1 = ((edR < 16) ? ("0"+Integer.toHexString(edR)) : Integer.toHexString(edR));
			g1 = ((edG < 16) ? ("0"+Integer.toHexString(edG)) : Integer.toHexString(edG));
			b1 = ((edB < 16) ? ("0"+Integer.toHexString(edB)) : Integer.toHexString(edB));
			myDevice.send(type, DemoboxActivity.hexToByte("01"+r1+g1+b1));
			}
			break;
		case R.id.rgb_off:
			if(ensend){
				ensend = false;
			myDevice.send(type, DemoboxActivity.hexToByte("00000000"));
			}
			break;
		}
	}
	public void hsvtorgb(float[] colorHSV){
		float h,s,v,f;
		int r = 255,g = 255,B = 255,i,a,b,c;
		String r1,g1,b1;
		h = colorHSV[0];
		s = colorHSV[1];
		v = colorHSV[2]*255;
		if(s == 0){
			r = g = B = (int)v;
		}else{
			h /= 60;
			i = (int)(h);
			f = h-i;
			a = (int)(v*(1-s));
			b = (int)(v*(1-s*f));
			c = (int)(v*(1-s*(1-f)));
			switch(i){
			case 0:
				r = (int)v; g = c; B = a;
				break;
			case 1:
				r = b; g = (int)v; B = a; 
				break;
			case 2: 
				r = a; g =(int) v; B = c; 
				break;
			case 3: 
				r = a; g = b; B = (int)v;
				break;
			case 4: 
				r = c; g = a; B = (int)v;
				break;
			case 5: 
				r = (int)v; g = a; B = b;
				break;
			}
			
		}
		r1 = ((r < 16) ? ("0"+Integer.toHexString(r)) : Integer.toHexString(r));
		g1 = ((g < 16) ? ("0"+Integer.toHexString(g)) : Integer.toHexString(g));
		b1 = ((B < 16) ? ("0"+Integer.toHexString(B)) : Integer.toHexString(B));
		myDevice.send(type, DemoboxActivity.hexToByte("01"+r1+g1+b1));
		
		
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
				ensend = true;
		}
			
		@Override
		public void onConnectionStateChange(int status,
				int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(status, newState);
			if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS){
				ensend = true;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
					}});
				
			}else if (newState == JumaDevice.STATE_DISCONNECTED){
				ensend = false;
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
				myDevice.disconnect();
			}else{
			if(scanner.isScanning()){
				scanner.stopScan();
			}
			finish();
			}
	     }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cube_activitty, menu);
		return true;
	}

}