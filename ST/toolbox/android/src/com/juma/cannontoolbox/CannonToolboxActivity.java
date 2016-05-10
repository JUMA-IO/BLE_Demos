package com.juma.cannontoolbox;

import com.juma.cannontoolbox.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CannonToolboxActivity extends Activity implements OnClickListener{

	private ImageView iv1,iv2,iv3,iv4,iv5,iv6;
	private BluetoothAdapter mBluetoothAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cannon_toolbox);
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		initView();
	}
	private void initView(){
		iv1 = (ImageView)findViewById(R.id.barometer);
		iv2 = (ImageView)findViewById(R.id.TRH);
		iv3 = (ImageView)findViewById(R.id.rbg);
		iv4 = (ImageView)findViewById(R.id.magnetometer);
		iv5 = (ImageView)findViewById(R.id.cube);
		iv6 = (ImageView)findViewById(R.id.car);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		iv4.setOnClickListener(this);
		iv5.setOnClickListener(this);
		iv6.setOnClickListener(this);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cannon_toolbox, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(mBluetoothAdapter.isEnabled()){
		Intent intent=new Intent();
		switch(v.getId()){
		case R.id.magnetometer:
			intent.setClass(CannonToolboxActivity.this, MagnetometerActivity.class);
			break;
		case R.id.barometer:
			intent.setClass(CannonToolboxActivity.this, BarometerActivity.class);
			break;
		case R.id.cube:
			intent.setClass(CannonToolboxActivity.this, CubeActivity.class);
			break;
		case R.id.car:
			intent.setClass(CannonToolboxActivity.this, OTAActivity.class);
			break;
		case R.id.rbg:
			intent.setClass(CannonToolboxActivity.this, RBGActivity.class);
			break;
		case R.id.TRH:
			intent.setClass(CannonToolboxActivity.this, TRHActivity.class);
			break;
		}
		startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(), "Please open bluetooth", Toast.LENGTH_SHORT).show();
		}
		
	}

}
