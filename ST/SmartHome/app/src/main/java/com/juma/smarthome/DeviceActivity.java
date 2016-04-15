package com.juma.smarthome;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.juma.view.HomeTools;
import com.juma.view.LocationApplication;
import com.juma.view.MyViewPagerAdapter;
import com.juma.view.PagerSlidingTabStrip;

public class DeviceActivity extends FragmentActivity {
    private ViewPager m_vp;
    private DeviceFragment mfragment1, mfragment2;
    private SceneFragment mfragment3;
    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> titleList = new ArrayList<>();
    private PagerSlidingTabStrip tabs;
    private Intent intService;
    private TextView indoorEnvironment, outdoorEnvironment;
    private LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor="gcj02";
    private MyViewPagerAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        m_vp = (ViewPager)findViewById(R.id.viewpager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.pagertab);
        tabs.setShouldExpand(true);
        indoorEnvironment = (TextView)findViewById(R.id.thk);
        outdoorEnvironment = (TextView)findViewById(R.id.tq);
        mLocationClient = ((LocationApplication)getApplication()).mLocationClient;

        mfragment1 = new DeviceFragment();
        mfragment2 = new DeviceFragment();
        mfragment3 = new SceneFragment();
        mfragment2.setTAG("5");

        fragmentList = new ArrayList<>();
        fragmentList.add(mfragment1);
        fragmentList.add(mfragment2);
        fragmentList.add(mfragment3);

        titleList.add("照明");
        titleList.add("环境");
        titleList.add("场景");

        m_adapter = new MyViewPagerAdapter(getSupportFragmentManager(),fragmentList,titleList);
        m_vp.setAdapter(m_adapter);
        tabs.setViewPager(m_vp);
        intService = new Intent(DeviceActivity.this, HomeService.class);
        startService(intService);
        initLocation();
        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        mLocationClient.requestLocation();

    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType(tempcoor);//可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, HomeTools.getIntentFilter());
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HomeTools.ACTION_SCAN_DISABLE)){
                Toast.makeText(getApplicationContext(),"蓝牙未开启",Toast.LENGTH_SHORT).show();
                stopService(intService);
            }else if(intent.getAction().equals(HomeTools.ACTION_TEXT_MESSAGE)){
                indoorEnvironment.setText(intent.getStringExtra(HomeTools.TEXT_STR));
            }else if(intent.getAction().equals(HomeTools.ACTION_WEATHER)){
                ArrayList<String> list = intent.getStringArrayListExtra(HomeTools.WEATHER_STR);
                if (list.get(1).equals(""))
                    list.set(1, "**");
                outdoorEnvironment.setText(list.get(2) + list.get(0) + "：\n温度:" + list.get(5) + "  PM2.5:" + list.get(1) +
                        "μg/m³  天气:" + list.get(3));
                Toast.makeText(getApplicationContext(), "" + outdoorEnvironment.getText(), Toast.LENGTH_SHORT).show();
               mLocationClient.stop();

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intService);
    }


}
