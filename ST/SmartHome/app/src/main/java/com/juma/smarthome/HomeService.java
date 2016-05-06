package com.juma.smarthome;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Handler;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.ScanHelper.ScanCallback;
import com.juma.sdk.iBeaconClass;
import com.juma.view.HomeTools;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by juma_ming on 2016/3/1.
 */
public class HomeService extends Service {

    private boolean getTHK = true;
    private boolean toScan = true;
    private boolean canGet = true;
    private boolean doHandler = true;
    private boolean doNext = true;
    private boolean sendDiscover = true;

    private short x,y,z;
    private int t = 3;

    private Byte type = 0x01;
    private WakeLock wakeLock;
    private Intent intent;

    private ArrayList<String> uuidList = new ArrayList<>();
    private ArrayList<String> saveData = new ArrayList<>();
    private List<DeviceItem> connectDevice = new ArrayList<>();
    private List<JumaDevice> deviceList = new ArrayList<>();

    private Handler myHandler = new Handler();
    private Runnable toSend = new Runnable() {
        @Override
        public void run() {
            tosendBroadcast = true;
        }
    };
    private Runnable toEnd = new Runnable() {
        @Override
        public void run() {
            int time = connectDevice.get(0).doTime;
            for(int i = 1;i<connectDevice.size();i++){
                if(connectDevice.get(i).device.getUuid().equals(connectDevice.get(0).device.getUuid())){
                    time = 0;
                    break;
                }
            }
            if(time != 0){
                connectDevice.get(0).doTime = time-1;
                connectDevice.add(connectDevice.get(0));
            }
            connectDevice.remove(0);
            doHandler = true;
        }
    };
    private Runnable toNext = new Runnable() {
        @Override
        public void run() {
            if(connectDevice.get(0).device.isConnected())
                connectDevice.get(0).device.disconnect();
            int time = connectDevice.get(0).doTime;
            for(int i = 1;i<connectDevice.size();i++){
                if(connectDevice.get(i).device.getUuid().equals(connectDevice.get(0).device.getUuid())){
                    time = 0;
                    break;
                }
            }
            if(time != 0){
                connectDevice.add(connectDevice.get(0));
            }
            connectDevice.remove(0);
            doNext = true;
            if (toScan)
                myScan();
        }
    };

    private JumaDevice device = null;
    private JumaDeviceCallback myCallback = new JumaDeviceCallback() {
        private JumaDevice callbackDevice;
        @Override
        public void onConnectionStateChange(int status, int newState, JumaDevice jumaDevice,String Data) {
            super.onConnectionStateChange(status, newState, jumaDevice,Data);
            callbackDevice = jumaDevice;
            if (newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS) {
                    myHandler.removeCallbacks(toNext);
                    callbackDevice.send(type, HomeTools.hexToByte(Data));
            }else if(newState == JumaDevice.STATE_DISCONNECTED){
                if(!doNext){
                    doNext = true;
                    if (toScan)
                        myScan();
                }
            }

        }

        @Override
        public void onSend(int status,byte[] m) {
            super.onSend(status, m);
            if(status == JumaDevice.SUCCESS) {
                for(int i=0;i<deviceList.size();i++){
                    if(uuidList.get(i).equals(callbackDevice.getUuid().toString())) {
                        saveData.set(i, HomeTools.byteToHex(m));
                        break;
                    }
                }
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "已执行"+callbackDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                int time = connectDevice.get(0).doTime;
                for(int i = 1;i<connectDevice.size();i++){
                    if(connectDevice.get(i).device.getUuid().equals(connectDevice.get(0).device.getUuid())){
                        time = 0;
                        break;
                    }
                }
                if(time != 0){
                    connectDevice.add(connectDevice.get(0));
                }
            }
            connectDevice.remove(0);
            if(callbackDevice.isConnected())
                callbackDevice.disconnect();
        }
    };
    private boolean tosendBroadcast = true;
    private ScanHelper scanHelper;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onDiscover(JumaDevice jumaDevice, int i, iBeaconClass.iBeacon iBeacon) {
            if(iBeacon == null && jumaDevice.getName().length() == 3) {
                if (sendDiscover&&(connectDevice.size()==0||tosendBroadcast)) {
                    myHandler.removeCallbacks(toSend);
                    intent = new Intent(HomeTools.ACTION_DEVICE_DISCOVERED);
                    intent.putExtra(HomeTools.NAME_STR, jumaDevice.getName());
                    intent.putExtra(HomeTools.UUID_STR, jumaDevice.getUuid().toString());
                    HomeTools.sendBroadcast(HomeService.this, intent);
                }
                if (doHandler && connectDevice.size() > 1) {
                    doHandler = false;
                    myHandler.postDelayed(toEnd, 5000);
                }
                if (doNext && connectDevice.size() > 0 && jumaDevice.getUuid().equals(connectDevice.get(0).device.getUuid())) {
                    myHandler.removeCallbacks(toEnd);
                    doNext = false;
                    scanHelper.stopScan();
                    myHandler.postDelayed(toNext, 2000);
                    jumaDevice.connect(myCallback, connectDevice.get(0).sendData);

                } else if (jumaDevice.getName().equals("THK")) {
                    if (canGet && getTHK) {
                        getTHK = false;
                        device = jumaDevice;
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (device != null && device.isConnected())
                                    device.disconnect();
                                getTHK = true;
                                if (toScan && doNext)
                                    myScan();
                            }
                        }, 5000);
                        device.connect(new JumaDeviceCallback() {
                            @Override
                            public void onConnectionStateChange(int status, int newState, JumaDevice jumaDevice, String Data) {
                                super.onConnectionStateChange(status, newState, jumaDevice, Data);
                                if (newState == JumaDevice.STATE_CONNECTED)
                                    if (status == JumaDevice.SUCCESS) {
                                        device.send((byte) 0x01, HomeTools.hexToByte("01"));
                                    } else
                                        device.disconnect();
                                else if (toScan && doNext)
                                    myScan();
                            }

                            @Override
                            public void onReceive(byte type, byte[] message) {
                                super.onReceive(type, message);
                                x = 0;
                                y = 0;
                                z = 0;
                                x |= message[0];
                                y |= message[1];
                                z |= message[2];
                                intent = new Intent(HomeTools.ACTION_TEXT_MESSAGE);
                                intent.putExtra(HomeTools.TEXT_STR, "温度:" + x + "℃" + " 湿度:" + y + "%RH" + " 光照强度:" + z + "Lx");
                                HomeTools.sendBroadcast(HomeService.this, intent);
                            }
                        });
                        if (toScan && doNext)
                            myScan();
                    }
                } else if (!uuidList.contains(jumaDevice.getUuid().toString())) {
                    uuidList.add(jumaDevice.getUuid().toString());
                    deviceList.add(jumaDevice);
                    saveData.add(jumaDevice.getName().indexOf('c') != -1 ? "00" : "01");
                }
            }
        }

        @Override
        public void onScanStateChange(int i) {
            if(i == ScanHelper.STATE_START_SCAN)
                doHandler = true;
        }
    };
    public int onStartCommand(final Intent intent, int flags, int startId) {

        return START_STICKY;
    }
    public void onCreate() {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("JUMA智能家居")
                .setContentText("正在运行")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        startForeground(1235, notification);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, HomeTools.getIntentFilter());
        scanHelper = new ScanHelper(getApplication(),scanCallback );
        acquireWakeLock();
        myScan();
    }
    private void myScan(){
        if(!scanHelper.isEnabled()){
            intent = new Intent(HomeTools.ACTION_SCAN_DISABLE);
            HomeTools.sendBroadcast(HomeService.this, intent);
        }else if(!scanHelper.isScanning()){
            scanHelper.startScan(null);
        }
    }
    @Override
    public void onDestroy(){
        stopScan();
        stopForeground(true);
        this.stopSelf();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HomeTools.ACTION_START_SCAN)){
                stopScan();
                uuidList.clear();
                deviceList.clear();
                saveData.clear();
                if(intent.getStringExtra(HomeTools.ACTIVITY_STR).equals("main")){
                    canGet = true;
                    toScan = true;
                }
                myScan();
            }else if(intent.getAction().equals(HomeTools.ACTION_SEND_MESSAGE)){
                sendDiscover = false;
                tosendBroadcast = false;
                myHandler.postDelayed(toSend,10000);
                String get_uuid = intent.getStringExtra(HomeTools.UUID_STR),
                        get_msg = intent.getStringExtra(HomeTools.MESSAGE_STR);
                JumaDevice oneDevice = null;
                boolean addDevice = true;
                for(int i=0;i<uuidList.size();i++) {
                    if (uuidList.get(i).equals(get_uuid)) {
                        oneDevice = deviceList.get(i);
                        if(saveData.get(i).equals(get_msg)){
                            addDevice = false;
                        }
                        break;
                    }
                }
                if( oneDevice != null) {
                    if (connectDevice.size() > 1) {
                        for (int j = 1; j < connectDevice.size(); j++) {
                            if (connectDevice.get(j).device.getUuid().toString().equals(get_uuid)) {
                                addDevice = false;
                                connectDevice.get(j).device = oneDevice;
                                connectDevice.get(j).sendData = get_msg;
                                connectDevice.get(j).doTime = t;
                                break;
                            }
                        }
                    }
                    if (addDevice) {
                        DeviceItem item = new DeviceItem();
                        item.device = oneDevice;
                        item.sendData = get_msg;
                        item.doTime = t;
                        connectDevice.add(item);
                    }
                }
                sendDiscover = true;
            }else if(intent.getAction().equals(HomeTools.ACTION_GET_DATA)){
                intent = new Intent(HomeTools.ACTION_SAVE_DATA);
                intent.putStringArrayListExtra("uuidList", uuidList);
                intent.putStringArrayListExtra("saveData",saveData);
                HomeTools.sendBroadcast(HomeService.this, intent);
            }else if(intent.getAction().equals(HomeTools.ACTION_CLOSE_SCENE)){
                sendDiscover = false;
                tosendBroadcast = false;
                myHandler.postDelayed(toSend,10000);
                for(int i = 0;i<saveData.size();i++){
                    if(saveData.get(i).equals("00")){
                        DeviceItem item = new DeviceItem();
                        item.device = deviceList.get(i);
                        item.sendData = "01";
                        item.doTime = t;
                        connectDevice.add(item);
                    }
                }
                sendDiscover = true;
            }
        }
    };
    private void stopScan(){
        if(device != null && device.isConnected())
            device.disconnect();
        canGet = false;
        toScan = false;
        if(scanHelper.isScanning())
            scanHelper.stopScan();
    }
    public class DeviceItem {
        public JumaDevice device;
        public String sendData;
        public int doTime;
    }
}