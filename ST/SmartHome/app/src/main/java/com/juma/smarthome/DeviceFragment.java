package com.juma.smarthome;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.juma.view.HomeTools;

import java.util.ArrayList;
import java.util.List;

public class DeviceFragment extends Fragment
{
    private String TAG = "4";
    private List<String> uuidList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Switch> switchList = new ArrayList<>();
    private boolean allSwitch = false;
    private Dialog dialog;
    private SharedPreferences scene;
    private SharedPreferences.Editor edit;
    private myData switchData = new myData(),electricalData = new myData();
    private HomeTools homeTools =  new HomeTools();
    public final class myData {
        private ArrayAdapter myAdapter;
        private ListView lv;
        private List<String> uuidList = new ArrayList<>();
        private List<String> nameList = new ArrayList<>();
        private int tag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scene = getContext().getSharedPreferences("scene", Activity.MODE_PRIVATE);
        edit = scene.edit();
        View v =  inflater.inflate(R.layout.activity_fragment, container, false);
        switchData.tag = 1;
        electricalData.tag = 2;
        switchData.lv = (ListView)v.findViewById(R.id.listView);
        electricalData.lv = (ListView)v.findViewById(R.id.listView1);
        switchData.myAdapter = new myAdapter(getContext(),R.layout.list_item,switchData);
        electricalData.myAdapter = new myAdapter(getContext(),R.layout.list_item,electricalData);
        switchData.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               LongItemClick(switchData,position);
                return true;
            }
        });
       electricalData.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LongItemClick(electricalData,position);
                return true;
            }
        });
        electricalData.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String modeData;
                dialog = new Dialog(getContext(), R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_device);
                Button bok = (Button) dialog.findViewById(R.id.btnOk);
                Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
                Button tcut = (Button) dialog.findViewById(R.id.tcut);
                Button tadd = (Button) dialog.findViewById(R.id.tadd);
                Button wcut = (Button) dialog.findViewById(R.id.wcut);
                Button wadd = (Button) dialog.findViewById(R.id.wadd);
                TextView tvT = (TextView) dialog.findViewById(R.id.tvT);
                TextView tvW = (TextView) dialog.findViewById(R.id.tvW);
                Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
                modeData = spinner.getSelectedItem().toString();
                bok.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                bCancel.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                tcut.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                tadd.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                wcut.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                wadd.setOnClickListener(new dialogClick(tvT,tvW,modeData));
                dialog.show();
            }
        });
        switchData.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Switch item_switch = (Switch)view.findViewById(R.id.switch1);
                boolean isChecked = !item_switch.isChecked();
                String s = (isChecked ? "00" : "01");
                item_switch.setChecked(isChecked);

                oneTime = false;
                if (position > 0) {
                    boolean openAll = true;
                    Intent intent = new Intent(HomeTools.ACTION_SEND_MESSAGE);
                    intent.putExtra(HomeTools.UUID_STR, switchData.uuidList.get(position));
                    intent.putExtra(HomeTools.MESSAGE_STR, s);
                    HomeTools.sendBroadcast(getContext(), intent);
                    for (int i = 0; i < switchList.size(); i++)
                        if (switchList.get(i).getTag() != switchList.get(0).getTag()  && switchList.get(i).getTag() != position && switchList.get(i).isChecked() != isChecked) {
                            openAll = false;
                            break;
                        }
                    if (openAll) {
                        allSwitch = isChecked;
                        switchList.get(0).setChecked(isChecked);
                    }
                } else {
                    allSwitch = isChecked;
                    for (int i = 1; i < switchData.uuidList.size(); i++) {
                        if (isChecked) {
                            Intent intent = new Intent(HomeTools.ACTION_SEND_MESSAGE);
                            intent.putExtra(HomeTools.UUID_STR, switchData.uuidList.get(i));
                            intent.putExtra(HomeTools.MESSAGE_STR, "00");
                            HomeTools.sendBroadcast(getContext(), intent);
                        } else {
                            Intent intent = new Intent(HomeTools.ACTION_SEND_MESSAGE);
                            intent.putExtra(HomeTools.UUID_STR, switchData.uuidList.get(i));
                            intent.putExtra(HomeTools.MESSAGE_STR, "01");
                            HomeTools.sendBroadcast(getContext(), intent);
                        }
                    }
                }
            }
        });
        return v;
    }
    int temp = 24,windItem = 0;
    public class dialogClick implements View.OnClickListener{
        TextView tvTemp,tvWind;
        String mode;
        String[] windMode = new String[]{"慢","中","快"};
        public dialogClick(TextView tvT,TextView tvW,String modeData){
            tvTemp = tvT;
            tvWind = tvW;
            mode = modeData;
        }

        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.btnOk:
                   Toast.makeText(getContext(),tvTemp.getText()+" 风速"+tvWind.getText()+" 模式"+mode,Toast.LENGTH_SHORT).show();
                   dialog.cancel();
                   break;
               case R.id.btnCancel:
                   dialog.cancel();
                   break;
               case R.id.tcut:
                   if(temp >18) {
                       temp--;
                       tvTemp.setText(temp + "℃");
                   }
                   break;
               case R.id.tadd:
                   if(temp<32) {
                       temp++;
                       tvTemp.setText(temp + "℃");
                   }
                   break;
               case R.id.wcut:
                   if(windItem>0){
                       windItem--;
                       tvWind.setText(windMode[windItem]);
                   }
                   break;
               case R.id.wadd:
                   if(windItem<2){
                       windItem++;
                       tvWind.setText(windMode[windItem]);
                   }
                   break;
           }
        }
    }

    public void LongItemClick(final myData adapter,final int position){
        dialog = new Dialog(getContext(), R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_save);
        Button bok = (Button) dialog.findViewById(R.id.btnOk);
        Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        final EditText getName = (EditText) dialog.findViewById(R.id.etInputMessage);
        tvTitle.setText("更改设备名称");
        tvMessage.setText("请输入当前设备的名称：");
        bok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!getName.getText().toString().equals("")) {
                    edit.putString("" + adapter.nameList.get(position).toString().substring(0, 2), "" + getName.getText());
                    edit.commit();
                    adapter.myAdapter.clear();
                    adapter.myAdapter.addAll(adapter.nameList);
                    adapter.lv.setAdapter(adapter.myAdapter);
                    dialog.cancel();
                } else
                    Toast.makeText(getContext(), "未输入设备名称", Toast.LENGTH_SHORT).show();
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
    public void setTAG(String tag){
        TAG = tag;
    }
    @Override
    public void onStart() {
        super.onStart();
        switchList.clear();
        oneTime = true;
        switchData.myAdapter.clear();
        switchData.myAdapter.addAll(switchData.nameList);
        switchData.lv.setAdapter(switchData.myAdapter);
        homeTools.setListViewHeightBasedOnChildren(switchData.lv);
        electricalData.myAdapter.clear();
        electricalData.myAdapter.addAll(electricalData.nameList);
        electricalData.lv.setAdapter(electricalData.myAdapter);
        homeTools.setListViewHeightBasedOnChildren(electricalData.lv);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, HomeTools.getIntentFilter());
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && switchData.myAdapter!=null) {
            oneTime = true;
            switchList.clear();
            switchData.myAdapter.clear();
            switchData.myAdapter.addAll(switchData.nameList);
            switchData.lv.setAdapter(switchData.myAdapter);
            homeTools.setListViewHeightBasedOnChildren(switchData.lv);
            electricalData.myAdapter.clear();
            electricalData.myAdapter.addAll(electricalData.nameList);
            electricalData.lv.setAdapter(electricalData.myAdapter);
            homeTools.setListViewHeightBasedOnChildren(electricalData.lv);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HomeTools.ACTION_DEVICE_DISCOVERED)){
                String name = intent.getStringExtra(HomeTools.NAME_STR);
                if(intent.getStringExtra(HomeTools.NAME_STR).indexOf(TAG) != -1
                        && !nameList.contains(name)){
                    String uuid = intent.getStringExtra(HomeTools.UUID_STR);
                    if(uuidList.contains(uuid)) {
                        for (int i = 0; i < uuidList.size(); i++) {
                            if (uuidList.get(i).equals(uuid)) {
                                nameList.set(i, name);
                                break;
                            }
                        }
                        if(switchData.uuidList.contains(uuid)) {
                            for (int i = 0; i < switchData.uuidList.size(); i++) {
                                if (switchData.uuidList.get(i).equals(uuid)) {
                                    switchData.nameList.set(i, name);
                                    break;
                                }
                            }
                            switchData.myAdapter.clear();
                            switchList.clear();
                            switchData.myAdapter.addAll(switchData.nameList);
                            switchData.lv.setAdapter(switchData.myAdapter);
                        }
                    }else {
                        uuidList.add(uuid);
                        nameList.add(name);
                        if(name.substring(0,2).endsWith("5w")){
                            electricalData.uuidList.add(uuid);
                            electricalData.nameList.add(name);
                            electricalData.myAdapter.clear();
                            electricalData.myAdapter.addAll(electricalData.nameList);
                            electricalData.lv.setAdapter(electricalData.myAdapter);
                            homeTools.setListViewHeightBasedOnChildren(electricalData.lv);
                        }else {
                            if (switchData.nameList.size() == 0) {
                                switchData.uuidList.add("全部设备");
                                switchData.nameList.add("全部设备");
                            }
                            switchData.uuidList.add(uuid);
                            switchData.nameList.add(name);
                            switchData.myAdapter.clear();
                            switchList.clear();
                            switchData.myAdapter.addAll(switchData.nameList);
                            switchData.lv.setAdapter(switchData.myAdapter);
                            homeTools.setListViewHeightBasedOnChildren(switchData.lv);
                        }
                    }

                }
            }
        }
    };
    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private boolean oneTime = true;
    public class myAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;
        private myData data;

        public myAdapter(Context context, int resourceId,myData adapter) {
            super(context, resourceId);
            this.mInflater = LayoutInflater.from(context);
            this.data = adapter;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HomeTools.ViewHolder holder;
            String itemName ,name = data.nameList.get(position),saveName = scene.getString(name.substring(0, 2),"");
            itemName = (saveName.equals("")?name:saveName);
            if (convertView == null) {
                holder = homeTools.new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder.txt = (TextView)convertView.findViewById(R.id.textView);
                holder.swi = (Switch)convertView.findViewById(R.id.switch1);
                holder.iv = (ImageView)convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            }else {
                holder = (HomeTools.ViewHolder)convertView.getTag();
            }
            holder.txt.setText(itemName);
            if(data.tag == 1) {
                boolean sChecked = (name.indexOf('c') != -1 ? true : false);
                holder.swi.setTag(position);
                switchList.add(holder.swi);
                if (position > 0) {
                    holder.swi.setChecked(sChecked);
                } else {
                    holder.swi.setChecked(allSwitch);
                }
                if (oneTime && switchList.get(switchList.size() - 1).getTag() != switchList.get(0).getTag()) {
                    boolean openAll = true;
                    for (int i = 1; i < switchList.size(); i++)
                        if (switchList.get(i).getTag() != switchList.get(0).getTag() && switchList.get(i).isChecked() == false) {
                            openAll = false;
                            break;
                        }
                    allSwitch = openAll;
                    switchList.get(0).setChecked(openAll);
                }
            }else{
                holder.swi.setVisibility(View.GONE);
                holder.iv.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}