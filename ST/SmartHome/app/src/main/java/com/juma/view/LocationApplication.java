package com.juma.view;


import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class LocationApplication extends Application {
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }


    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            Log.e("location.getDistrict()",""+location.getDistrict());
            if(location.getDistrict() != null) {
                new HttpThread(location.getDistrict()).start();
            }
            
        }


    }
private class HttpThread extends Thread{
		
		String cityName = null;

		public HttpThread(String cityname) {
			cityName = cityname;
		}
		
		@Override
		public void run() {
				getUrldata(cityName);
				
		}
	}
ArrayList<String> list  = new ArrayList<String>();
    public void getUrldata(String cityName){
    	list.clear();
		JSONObject jsonObj;
			try {
				
				jsonObj = new JSONObject(readParse(cityName));
				Log.e("", jsonObj.toString());
				 jsonObj = jsonObj.getJSONArray("results").getJSONObject(0);
				 list.add(jsonObj.getString("currentCity"));
				 list.add(jsonObj.getString("pm25"));
				 jsonObj = jsonObj.getJSONArray("weather_data").getJSONObject(0);
				 list.add(jsonObj.getString("date"));
				 list.add(jsonObj.getString("weather"));
				 list.add(jsonObj.getString("wind"));
				 list.add(jsonObj.getString("temperature"));
				 list.add(jsonObj.getString("dayPictureUrl"));
				 list.add(jsonObj.getString("nightPictureUrl"));
                 Intent intent = new Intent(HomeTools.ACTION_WEATHER);
                 intent.putStringArrayListExtra(HomeTools.WEATHER_STR, list);
                 HomeTools.sendBroadcast(getApplicationContext(), intent);
//				 listener.onWeather(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
  }
public static String readParse(String cityName) throws Exception {  
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
    byte[] data = new byte[1024];  
     int len = 0;  
     URL url = new URL("http://api.map.baidu.com/telematics/v3/weather?location=" +
    		 URLEncoder.encode(cityName, "UTF-8") + "&output="+ "json" +"&ak="+ "iGs8rFvzh1e8c7C9DjXT5toK");
     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     InputStream inStream = conn.getInputStream();  
     while ((len = inStream.read(data)) != -1) {  
         outStream.write(data, 0, len);  
     }  
     inStream.close();  
     return new String(outStream.toByteArray());
 }

//private weatherCallback listener = null;
//public interface weatherCallback {
//    public void onWeather(List<String> list);
//}
//  public void setweatherCallback(weatherCallback listener) {
//      this.listener = listener;
//}
    /**
     * 显示请求字符串
     * @param str
     */

}
