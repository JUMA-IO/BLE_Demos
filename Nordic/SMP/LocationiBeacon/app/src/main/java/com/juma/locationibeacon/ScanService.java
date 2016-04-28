package com.juma.locationibeacon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.juma.sdk.JumaDevice;
import com.juma.sdk.JumaDeviceCallback;
import com.juma.sdk.ScanHelper;
import com.juma.sdk.iBeaconClass;

import java.util.HashMap;


/**
 * Created by juma_ming on 2016/4/20.
 */
public class ScanService extends Service {
    public static final String ACTION_DEVICE_DISCOVERED = "com.juma.iBeaconList.ACTION_DEVICE_DISCOVERED";
    public static final String SCAN_DISABLE = "com.juma.iBeaconList.SCAN_DISENABLE";
    public static final String TURN_ON = "com.juma.iBeaconList.TURN_ON";
    public static final String TURN_OFF = "com.juma.iBeaconList.TURN_OFF";

    public static final String UUID_STR = "uuid";
    public static final String RSSI_STR = "rssi";

    private HashMap<String, JumaDevice> jumaDevices = null;

    public static final IntentFilter getIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(TURN_ON);
        filter.addAction(TURN_OFF);
        return filter;
    }
    public  static final void  sendBroadcast(Context context, Intent intent){
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    private ScanHelper scanHelper;
    public int onStartCommand(final Intent intent, int flags, int startId) {

        return START_STICKY;
    }
    public void onCreate(){
        jumaDevices = new HashMap<>();
        scanHelper = new ScanHelper(getApplicationContext(), new ScanHelper.ScanCallback() {
            @Override
            public void onDiscover(JumaDevice jumaDevice, int rssi, iBeaconClass.iBeacon iBeacon) {
                if(iBeacon == null) {
                    if(!jumaDevices.containsKey(jumaDevice.getUuid().toString())){
                        jumaDevices.put(jumaDevice.getUuid().toString(), jumaDevice);
                    }
                    Intent intent = new Intent(ACTION_DEVICE_DISCOVERED);
                    intent.putExtra(UUID_STR, jumaDevice.getUuid().toString());
                    intent.putExtra(RSSI_STR, "" + rssi);
                    sendBroadcast(getApplicationContext(), intent);
                }
            }

            @Override
            public void onScanStateChange(int i) {

            }
        });
       StartScan();
    }
    private void StartScan(){
        if(scanHelper.isEnabled())
            scanHelper.startScan(null);
        else
            sendBroadcast(getApplicationContext(),new Intent(SCAN_DISABLE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, getIntentFilter());
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            scanHelper.stopScan();
            jumaDevices.get(intent.getStringExtra(UUID_STR)).connect(callback,
                    hexToByte(intent.getAction().equals(TURN_ON)?"00":"01"));
        }
    };
    private JumaDeviceCallback callback = new JumaDeviceCallback() {
        private JumaDevice jumaDevice;
        @Override
        public void onConnectionStateChange(int status, int newState, JumaDevice device, byte[] Data) {
            super.onConnectionStateChange(status, newState, device, Data);
            if(newState == JumaDevice.STATE_CONNECTED && status == JumaDevice.SUCCESS) {
                jumaDevice = device;
                device.send((byte) 0x00, Data);
            }
            else {
                StartScan();
            }
        }

        @Override
        public void onSend(int status, byte[] sendData) {
            super.onSend(status, sendData);
            if(status == JumaDevice.SUCCESS){
                jumaDevice.disconnect();
            }
        }
    };
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
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}