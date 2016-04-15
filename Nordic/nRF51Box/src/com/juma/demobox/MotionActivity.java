package com.juma.demobox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.juma.demobox.R;
import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.widget.GLImage;
import com.juma.widget.GLRender;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
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

public class MotionActivity extends Activity {

	private Spinner sp;
	private TableRow tb;
    private boolean back = false;
    private XYSeries seriesX,seriesY,seriesZ;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    GLRender render = new GLRender(this);  
    GLSurfaceView glView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_motion);
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
	        LinearLayout layout = (LinearLayout)findViewById(R.id.chat_layout);
	        mDataset = new XYMultipleSeriesDataset();
	        seriesX = new XYSeries("X");   
	        seriesX.add(0, 0);
	        mDataset.addSeries(seriesX);   
	        seriesY = new XYSeries("Y");  
	        seriesY.add(0, 0);
	        mDataset.addSeries(seriesY);   
	        seriesZ = new XYSeries("Z");   
	        seriesZ.add(0, 0);
	        mDataset.addSeries(seriesZ);   
	        renderer = new XYMultipleSeriesRenderer();
	        renderer.addSeriesRenderer(setR(Color.RED,PointStyle.CIRCLE,true,3,true));
		    renderer.addSeriesRenderer(setR(Color.GREEN,PointStyle.CIRCLE,true,3,true));
		    renderer.addSeriesRenderer(setR(Color.YELLOW,PointStyle.CIRCLE,true,3,true));
	        setChartSettings(renderer, "X", "Y",0, 100, -500, 500);
	        chart = ChartFactory.getLineChartView(getApplicationContext(), mDataset, renderer);
	        layout.addView(chart);
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
   double xMin, double xMax, double yMin, double yMax) {
    renderer.setLabelsTextSize(30);
    renderer.setXAxisMin(xMin);
    renderer.setXAxisMax(xMax);
    renderer.setYAxisMin(yMin);
    renderer.setYAxisMax(yMax);
    renderer.setAxesColor(Color.BLACK);
    renderer.setLabelsColor(Color.BLACK);
    renderer.setShowGrid(true);
    renderer.setGridColor(Color.BLACK);
    renderer.setXLabels(10);
    renderer.setYLabels(10);
    renderer.setYLabelsAlign(Align.CENTER);
    renderer.setPointSize((float) 5);
    renderer.setShowLegend(false);
    renderer.setMarginsColor(Color.WHITE);
   }
    private int bx,by,bz;
    private float anglex = 0f,x,y,z;  
	private float angley = 0f;  
	private float anglez = 0f; 
    int[] xv = new int[100];
    int[] yv = new int[100];
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
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
					}});
				
			}else if (newState == JumaDevice.STATE_DISCONNECTED){
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
				x = (float)bx/255;
				y = (float)by/255;
				z = (float)bz/255;
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
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						updateChart();
						
					}
				});
				redata = true;
			}
			
		}
	
	};
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