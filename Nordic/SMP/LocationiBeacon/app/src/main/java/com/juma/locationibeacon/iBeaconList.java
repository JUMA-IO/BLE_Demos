package com.juma.locationibeacon;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juma.view.ListViewCompat;
import com.juma.view.SlideView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class iBeaconList extends AppCompatActivity implements SlideView.OnSlideListener {
    private ListViewCompat lv;
    private MyAdapter myAdapter;
    private Intent intService;
    private SharedPreferences iBeaconName;
    private SharedPreferences.Editor edit;
    private iBeaconitem item;
    private Button addItem;
    private List<iBeaconitem> iBeaconItems = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private boolean isContain = false;
    private Dialog dialog;
    private SelectItem selectItem = new SelectItem();
    private SlideView mLastSlideViewWithStatusOn;
    public class iBeaconitem {
        public String newname = null,battery = null,rssi = null,uuid = null;
        public SlideView slideView;
    }
    public  class SelectItem{
        public TextView tvFirst, tvLast,tvNext;
        public String uuid;
        public EditText ed;
        public int position = -1;
    }
    private float x1,x2;
    private boolean doLong = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeaconlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        iBeaconName = this.getSharedPreferences("iBeaconName", Activity.MODE_PRIVATE);
        edit = iBeaconName.edit();
        if(!iBeaconName.getString("uuidList","").equals(""))
            keyList =new ArrayList<>(Arrays.asList(iBeaconName.getString("uuidList","").split(",")));
        lv = (ListViewCompat)findViewById(R.id.listView);
        addItem = (Button)findViewById(R.id.additem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem.position = -2;
                dialog = new Dialog(iBeaconList.this, R.style.MyDialog);
                dialog.setContentView( R.layout.dialog_save);
                selectItem.tvFirst = (TextView) dialog.findViewById(R.id.tvMessage);
                selectItem.tvLast = (TextView) dialog.findViewById(R.id.tvTitle);
                Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
                final EditText ed = (EditText) dialog.findViewById(R.id.etInputMessage);
                selectItem.tvFirst.setText("请靠近要添加的设备...");
                selectItem.tvLast.setText("添加设备");
                ed.setVisibility(View.GONE);
                selectItem.ed = ed;
                Button bOk = (Button) dialog.findViewById(R.id.btnOk);
                bOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectItem.position == -1) {
                            if(!ed.getText().equals("")) {
                                keyList.add(selectItem.uuid);
                                initSaveList(keyList);
                                edit.putString("" + selectItem.uuid, ed.getText().toString());
                                edit.commit();
                                dialog.cancel();
                            }else{
                                Toast.makeText(getApplicationContext(), "未输入设备名称", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "还未发现设备", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem.position = -1;
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem.position = position;
                dialog = new Dialog(iBeaconList.this, R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_item);
                selectItem.tvFirst = (TextView) dialog.findViewById(R.id.name);
                selectItem.tvLast = (TextView) dialog.findViewById(R.id.rssi);
                selectItem.tvNext = (TextView) dialog.findViewById(R.id.battery);
                Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
                dialog.setCanceledOnTouchOutside(false);
                item = iBeaconItems.get(position);
                Intent intent = new Intent(ScanService.TURN_ON);
                intent.putExtra(ScanService.UUID_STR, item.uuid);
                ScanService.sendBroadcast(getApplicationContext(), intent);
                selectItem.tvFirst.setText(item.newname);
                selectItem.tvLast.setText(item.rssi);
                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem.position = -1;
                        Intent intent = new Intent(ScanService.TURN_OFF);
                        intent.putExtra(ScanService.UUID_STR, item.uuid);
                        ScanService.sendBroadcast(getApplicationContext(), intent);
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        doLong = true;
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x2 = x1 - event.getX();
                        if (Math.abs(x2) > 10) {
                            doLong = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            private iBeaconitem newitem;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(doLong) {
                    newitem = iBeaconItems.get(position);
                    newitem.slideView.shrink();
                    dialog = new Dialog(iBeaconList.this, R.style.MyDialog);
                    dialog.setContentView(R.layout.dialog_save);
                    Button bok = (Button) dialog.findViewById(R.id.btnOk);
                    Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
                    final EditText getName = (EditText) dialog.findViewById(R.id.etInputMessage);
                    bok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            if (!getName.getText().toString().equals("")) {
                                edit.putString(newitem.uuid, "" + getName.getText());
                                edit.commit();
                                dialog.cancel();
                                newitem.newname = getName.getText().toString();
                                myAdapter.clear();
                                myAdapter.addAll(iBeaconItems);
                            } else
                                Toast.makeText(getApplicationContext(), "未输入名称", Toast.LENGTH_SHORT).show();
                        }
                    });
                    bCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
                return true;
            }
        });
        myAdapter = new MyAdapter(getApplicationContext(),R.layout.list_item);
        intService = new Intent(iBeaconList.this, ScanService.class);
        startService(intService);
    }
    public void initSaveList(List<String> l){
        String newList = "juma";
        for (int i = 0; i < l.size(); i++) {
            newList = newList + "," + l.get(i);
        }
        edit.putString("uuidList", newList);
        edit.commit();

    }
    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.clear();
        myAdapter.addAll(iBeaconItems);
        lv.setAdapter(myAdapter);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, getIntentFilter());
    }
    private boolean initlv = true;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ScanService.SCAN_DISABLE)){
                Toast.makeText(getApplicationContext(), "蓝牙未开启", Toast.LENGTH_SHORT).show();
                stopService(intService);
            }else {
                String uuid = intent.getStringExtra(ScanService.UUID_STR);
                String rssi = intent.getStringExtra(ScanService.RSSI_STR);
                String name = iBeaconName.getString(uuid, "");
                if(keyList.contains(uuid)) {
                    isContain = false;
                    String newname = name.equals("") ? "*****" : name;
                    for (int i = 0; i < iBeaconItems.size(); i++) {
                        item = iBeaconItems.get(i);
                        if (item.uuid.equals(uuid)) {
                            item.newname = newname;
                            item.rssi = rssi;
                            isContain = true;
                            break;
                        }
                    }
                    if (!isContain) {
                        item = new iBeaconitem();
                        item.newname = newname;
                        item.rssi = rssi;
                        item.uuid = uuid;
                        iBeaconItems.add(item);
                    }
                    if(initlv) {
                        initlv = false;
                        myAdapter.clear();
                        myAdapter.addAll(iBeaconItems);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initlv = true;
                            }
                        }, 500);
                    }
                    if (selectItem.position>=0) {
                        item = iBeaconItems.get(selectItem.position);
                        selectItem.tvLast.setText(item.rssi);
                    }
                }else  if(selectItem.position == -2 && Integer.parseInt(rssi)>-55){
                    selectItem.position = -1;
                    selectItem.uuid = uuid;
                    selectItem.tvFirst.setText("发现新的设备请命名：");
                    selectItem.ed.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    public class MyAdapter extends ArrayAdapter<iBeaconitem> {
        private LayoutInflater mInflater;

        public MyAdapter(Context context, int resourceId) {
            super(context, resourceId);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            SlideView slideView = (SlideView) convertView;
            if (slideView== null) {
                slideView = new SlideView(iBeaconList.this);
                slideView.setContentView(mInflater.inflate(R.layout.list_item, null));

                holder.myname = (TextView)slideView.findViewById(R.id.name);
                holder.rssi = (TextView)slideView.findViewById(R.id.rssi);
                holder.deleteHolder = (ViewGroup)slideView.findViewById(R.id.holder);
                slideView.setOnSlideListener(iBeaconList.this);
                slideView.setTag(holder);
            }else {
                holder = (ViewHolder)slideView.getTag();
            }
            item = iBeaconItems.get(position);
            holder.myname.setText("物品："+item.newname);
            holder.rssi.setText("信号强度：" + item.rssi);
//            holder.tvset.setOnClickListener(new btSet(position));
            holder.deleteHolder.setOnClickListener(new btDelet(position));

            item.slideView = slideView;
            return slideView;
                }
            }

//    public class btSet implements View.OnClickListener {
//        private iBeaconitem newitem;
//        int position;
//        public btSet(int mPosition){
//            position = mPosition;
//        }
//        @Override
//        public void onClick(View v) {
//            newitem = iBeaconItems.get(position);
//            newitem.slideView.shrink();
//            dialog = new Dialog(iBeaconList.this, R.style.MyDialog);
//            dialog.setContentView(R.layout.dialog_save);
//            Button bok = (Button) dialog.findViewById(R.id.btnOk);
//            Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
//            final EditText getName = (EditText) dialog.findViewById(R.id.etInputMessage);
//            bok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
//                    if (!getName.getText().toString().equals("")) {
//                        edit.putString(newitem.uuid, "" + getName.getText());
//                        edit.commit();
//                        dialog.cancel();
//                        newitem.newname = getName.getText().toString();
//                        myAdapter.clear();
//                        myAdapter.addAll(iBeaconItems);
//                    } else
//                        Toast.makeText(getApplicationContext(), "未输入名称", Toast.LENGTH_SHORT).show();
//                }
//            });
//            bCancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//            dialog.show();
//        }
//    }
    public class btDelet implements View.OnClickListener {
        private iBeaconitem newitem;
        int position;
        public btDelet(int mPosition){
            position = mPosition;
        }
        @Override
        public void onClick(View v) {
            newitem = iBeaconItems.get(position);
            newitem.slideView.shrink();
            dialog = new Dialog(iBeaconList.this, R.style.MyDialog);
            dialog.setContentView(R.layout.delet_item);
            TextView title = (TextView) dialog.findViewById(R.id.tvTitle);
            title.setText(newitem.newname);
            Button bOk = (Button) dialog.findViewById(R.id.btnOk);
            Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
            bOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < keyList.size(); i++) {
                        if (keyList.get(i).equals(newitem.uuid)) {
                            edit.remove(newitem.uuid);
                            edit.commit();
                            keyList.remove(i);
                            initSaveList(keyList);
                            iBeaconItems.remove(position);
                            myAdapter.clear();
                            myAdapter.addAll(iBeaconItems);
                            break;
                        }
                    }
                    dialog.cancel();
                }
            });
            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
    public final class ViewHolder {
        private TextView myname,rssi;
        private ViewGroup deleteHolder;
    }
    public static final IntentFilter getIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanService.ACTION_DEVICE_DISCOVERED);
        filter.addAction(ScanService.SCAN_DISABLE);
        return filter;
    }
    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intService);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ibeaconlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }
}
