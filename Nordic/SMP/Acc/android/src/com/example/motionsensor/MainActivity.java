/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Author YZM, JUMA Inc.
 *
*/
package com.example.motionsensor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import juma.sdk.lite.JumaDevice;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;





import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	    private String title = "Motion Sensor";
	    private XYSeries seriesX,seriesY,seriesZ;
	    private XYMultipleSeriesDataset mDataset;
	    private GraphicalView chart;
	    private XYMultipleSeriesRenderer renderer;
	    private Context context;
	    private Spinner spChoose;
		private JumaDevice device;
		private byte type = 0x02;
		private UUID deviceUuid=null;
		private List<String> listName = new ArrayList<String>();  
		private List<String> listUuid = new ArrayList<String>(); 
		private ArrayAdapter<String> apUuid,apName;    
		private BluetoothAdapter mBluetoothAdapter;
		private boolean mConnected = false,stopScan = true,startScan = true;
		private Message msg;
		private Handler handler;
		private short bx = 0,by = 0,bz = 0;
		private float anglex = 0f;  
		private float angley = 0f;  
		private float anglez = 0f; 
		GLRender render = new GLRender(this);  
	    GLSurfaceView glView;
	    
	    
	    int[] xv = new int[100];
	    int[] yv = new int[100];
	    @SuppressLint("HandlerLeak")
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        spChoose = (Spinner)findViewById(R.id.chat_choose);
	        setTitle("Line Chart");
	        apName = new ArrayAdapter<String>(this,R.layout.message);  
			 apUuid = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
			 apName.add("Scanning");
			 apUuid.add("Scanning");
			 spChoose.setAdapter(apName);
			 initDevice();
	        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			if (mBluetoothAdapter == null) {
				 Toast.makeText(this, "NO BLE", Toast.LENGTH_SHORT).show();
				 return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				if (!mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.enable();
					Thread.currentThread();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				 }
			 }
	        context = getApplicationContext();
	        GLImage.load(this.getResources());
	        glView = new GLSurfaceView(this);  
	        glView.setRenderer(render); 
	        LinearLayout layout1 = (LinearLayout)findViewById(R.id.ddd);
	        layout1.addView(glView);
	        LinearLayout layout = (LinearLayout)findViewById(R.id.chat_layout);
	        mDataset = new XYMultipleSeriesDataset();
	        seriesX = new XYSeries("X");   
	        mDataset.addSeries(seriesX);   
	        seriesY = new XYSeries("Y");   
	        mDataset.addSeries(seriesY);   
	        seriesZ = new XYSeries("Z");   
	        mDataset.addSeries(seriesZ);   
	        renderer = new XYMultipleSeriesRenderer();
	        renderer.addSeriesRenderer(setR(Color.RED,PointStyle.CIRCLE,true,3,true));
		    renderer.addSeriesRenderer(setR(Color.GREEN,PointStyle.CIRCLE,true,3,true));
		    renderer.addSeriesRenderer(setR(Color.YELLOW,PointStyle.CIRCLE,true,3,true));
	        setChartSettings(renderer, "X", "Y",0, 100, -500, 500, Color.WHITE, Color.WHITE);
	        chart = ChartFactory.getLineChartView(context, mDataset, renderer);
	        layout.addView(chart);
	        handler = new Handler() {
		        @SuppressLint("HandlerLeak")
				@Override
		        public void handleMessage(Message msg) 
		        {
		         super.handleMessage(msg);
		         switch(msg.what)  
                 {  
                 case 0:  
                     Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                  break; 
                 case 1:
  					stopScan = true;
  					Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
  					apName.clear();
  					apUuid.clear();
  					apName.add("Scanning");
  					apUuid.add("Scanning");
  					apName.addAll(listName);
  					apUuid.addAll(listUuid);
  					spChoose.setAdapter(apName);
  					if(startScan){
  					device.scan(null);
  					}

  					break; 
  				case 2:
  					apName.clear();
  					apUuid.clear();
  					apName.add("Scanning");
  					apUuid.add("Scanning");
  					apName.addAll(listName);
  					apUuid.addAll(listUuid);
  					spChoose.setAdapter(apName);
  					break;
                 case 3:
                	 updateChart();
               	  break; 
                 case 4:
                	 Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                	  break; 
             
                 }  
		        
		        }
	        };
	     
	        	  
	    }
	   
	    @Override
	    protected void onStart() {
	        super.onStart();
	        startScan = true;
	        if(mBluetoothAdapter.isEnabled()){
	        	device.scan(null);
	        }else{
	        	Toast.makeText(this, "Bluetooth disable", Toast.LENGTH_SHORT).show();
	        }
	     }
	    @Override
	    protected void onResume() {
	        super.onResume();
	        spChoose.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					
					
					if(!apName.getItem(position).equals("Scanning") && !mConnected){
						stopScan = false;
					deviceUuid = UUID.fromString(apUuid.getItem(position));
					device.connect(deviceUuid);
					}else if(mConnected){
						device.disconnect(deviceUuid);
						
						}
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}});
	     }
	 @Override
	    protected void onStop() {
	        super.onStop();
	        startScan = false;
	        device.stopScan();
	        if(mConnected){
	        	device.disconnect(deviceUuid);
	        }
	     }
	    @Override
	    public void onDestroy() {
	     super.onDestroy();
	    }
	    private void initDevice(){
	    	device = new JumaDevice(){

				@Override
				public void onConnect(UUID arg0, int status) {
					if(status == JumaDevice.STATE_SUCCESS){
					mConnected = true;
					msg = new Message();
					msg.what = 0;
					handler.sendMessage(msg);
					}else{
						
					}
				}

				@Override
				public void onDisconnect(UUID arg0, int status) {
					if(status == JumaDevice.STATE_SUCCESS){
						
						mConnected = false;
						msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}

				@Override
				public void onDiscover(String name, UUID uuid, int rssi) {
					if(stopScan ){
						getDevice(name,uuid);	
					}
				}

				@Override
				public void onError(Exception arg0, int arg1) {
					if(arg1 == JumaDevice.ERROR_SCAN){
						device.scan(null);
					}else if(arg1 == JumaDevice.ERROR_DISCONNECT){
						mConnected = false;
						msg = new Message();
						msg.what = 4;
						handler.sendMessage(msg);
					}else if(arg1 == JumaDevice.ERROR_CONNECT){
						msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}
					
				}

				@Override
				public void onMessage(byte type, byte[] arg1) {
					getSeries(arg1);
					
				}

				@Override
				public void onSend(int arg0) {
					
				}

				@Override
				public void onStopScan() {
					
				}

				@Override
				public void onUpdateFirmware(int arg0) {
					
				}
	    		
	    	};
	    	device.init(getApplicationContext());
	    }
	    public synchronized  void getSeries(byte[] arg1){ 
	    	bx = 0;
			by = 0;
			bz = 0;
			bx |= arg1[0];
			bx <<= 8;
			bx |= (arg1[1]&0x00FF);
			by |= arg1[2];
			by <<= 8;
			by |= (arg1[3]&0x00FF);
			bz |= arg1[4];
			bz <<= 8;
			bz |= (arg1[5]&0x00FF);
			angley = (bx*90)/255;
			anglex = -(by*90)/255;
			anglez = -(bz*90)/255;
			
			render.setXYZ(anglex, angley, anglez);
			msg = new Message();
			msg.what = 3;
			handler.sendMessage(msg);
    } 
	 
	    public synchronized  void getDevice(String name,UUID uuid){ 
	    	if(!listUuid.contains(uuid.toString())){
				listUuid.add(uuid.toString());
				listName.add(name);
				msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
		}
    } 
	    private void addSeries(XYSeries series,int y){
	        int length = series.getItemCount();
	        if (length > 85) {
	         length = 85;
	        	  for (int i = 0; i+1 < length; i++) {
	        		     xv[i] = (int) series.getX(i);
	        		     yv[i] = (int) series.getY(i+1);
	        		     }
	        		     series.clear();
	        		     for (int k = 0; k+1 < length; k++) {
	        		         series.add(xv[k], yv[k]);
	        		         }
	        }
	   
	     series.add(length, y);
	     mDataset.addSeries(series);
	    }
	    private void updateChart() {
	        mDataset.removeSeries(seriesX);
	        mDataset.removeSeries(seriesY);
	        mDataset.removeSeries(seriesZ);
	        addSeries(seriesX,bx);
	        addSeries(seriesY,by);
	        addSeries(seriesZ,bz);	     
	        chart.invalidate();
	         }
	   
	   
	    protected XYSeriesRenderer setR(int color,PointStyle style, boolean FillPoints,int LineWidth,boolean ChartValues){
	    	 XYSeriesRenderer r = new XYSeriesRenderer();
		     
		     r.setColor(color);
		     r.setPointStyle(style);
		     r.setFillPoints(FillPoints);
		     r.setLineWidth(3);
		     r.setDisplayChartValues(true);
		     return r;
	    }
	    
	    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
	    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
	     renderer.setChartTitle(title);
	     renderer.setXTitle(xTitle);
	     renderer.setYTitle(yTitle);
	     renderer.setAxisTitleTextSize(20);
	     renderer.setChartTitleTextSize(30);
	     renderer.setXAxisMin(xMin);
	     renderer.setXAxisMax(xMax);
	     renderer.setYAxisMin(yMin);
	     renderer.setYAxisMax(yMax);
	     renderer.setAxesColor(axesColor);
	     renderer.setLabelsColor(labelsColor);
	     renderer.setShowGrid(true);
	     renderer.setGridColor(Color.WHITE);
	     renderer.setXLabels(10);
	     renderer.setYLabels(10);
	     renderer.setXTitle("Point");
	     renderer.setYTitle("a");
	     renderer.setYLabelsAlign(Align.RIGHT);
	     renderer.setPointSize((float) 2);
	     renderer.setShowLegend(false);
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
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			int id = item.getItemId();
			if (id == R.id.on_check_1) {
				if(mConnected){
				device.send(type, hexToByte("01"));
				}
				return true;
			}
			if (id == R.id.on_check_2) {
				if(mConnected){
				device.send(type, hexToByte("02"));
				}
				return true;
			}
			if (id == R.id.off_check) {
				if(mConnected){
			device.send(type, hexToByte("00"));
				}
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	   
	}