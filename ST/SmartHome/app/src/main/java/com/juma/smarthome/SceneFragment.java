package com.juma.smarthome;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.juma.view.HomeTools;
import com.juma.view.ListViewCompat;
import com.juma.view.SlideView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SceneFragment extends Fragment implements SlideView.OnSlideListener
{
    private MyAdapter myAdapter;
    private Dialog dialog;
    private ListViewCompat lv;
    private List<HomeTools.MessageItem> mMessageItems = new ArrayList<>();
    private List<Switch> switchList = new ArrayList<>();
    private SlideView mLastSlideViewWithStatusOn;
    private SharedPreferences scene;
    private SharedPreferences.Editor edit;
    private int n = 0;
    private  HomeTools homeTools =  new HomeTools();
    private String SceneName = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scene = getContext().getSharedPreferences("scene", Activity.MODE_PRIVATE);
        edit = scene.edit();
        mMessageItems.clear();
        HomeTools.MessageItem item = homeTools.new MessageItem();
        item.title = "保存当前状态";
        item.tag = 0;
        mMessageItems.add(item);
        if(!scene.getString("n","").equals("")) {
            n = Integer.parseInt(scene.getString("n",""));
            for(int i=1;i<n+1;i++){
                item = homeTools.new MessageItem();
                item.title = scene.getString("name"+i,"");
                item.tag = i;
                mMessageItems.add(item);
            }
        }
        View v =  inflater.inflate(R.layout.activity_fragment, container, false);
        lv = (ListViewCompat)v.findViewById(R.id.listViewCompat);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Switch s = (Switch)view.findViewById(R.id.switch1);
                    if (!s.isChecked()) {
                        ArrayList<String> uuidList = new ArrayList<>(), saveData = new ArrayList<>();
                        uuidList.addAll(Arrays.asList(scene.getString("uuid" + position, "").split(",")));
                        saveData.addAll(Arrays.asList(scene.getString("data" + position, "").split(",")));
                        for (int i = 0; i < uuidList.size(); i++) {
                            Intent intent = new Intent(HomeTools.ACTION_SEND_MESSAGE);
                            intent.putExtra(HomeTools.UUID_STR, uuidList.get(i));
                            intent.putExtra(HomeTools.MESSAGE_STR, saveData.get(i));
                            HomeTools.sendBroadcast(getContext(), intent);
                        }
                        for(int j=0;j<switchList.size();j++)
                            if(switchList.get(j).isChecked())
                                switchList.get(j).setChecked(false);
                        s.setChecked(true);
                    } else {
                        HomeTools.sendBroadcast(getContext(), new Intent(HomeTools.ACTION_CLOSE_SCENE));
                        s.setChecked(false);
                    }
                }
        });
        switchList.clear();
        myAdapter = new MyAdapter(getContext(),R.layout.list_item);
        myAdapter.addAll(mMessageItems);
        lv.setAdapter(myAdapter);
        homeTools.setListViewHeightBasedOnChildren(lv);
        return v;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("aaaaaaaaaa",""+isVisibleToUser);
        if (isVisibleToUser && myAdapter!=null) {
            myAdapter.clear();
            myAdapter.addAll(mMessageItems);
            lv.setAdapter(myAdapter);
            homeTools.setListViewHeightBasedOnChildren(lv);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, HomeTools.getIntentFilter());
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HomeTools.ACTION_SAVE_DATA)){
                ArrayList<String> uuidList = intent.getStringArrayListExtra("uuidList");
                ArrayList<String> saveData = intent.getStringArrayListExtra("saveData");
                String uuid = null,data = null;
                for(int i = 0;i<uuidList.size();i++){
                    if(i == (uuidList.size()-1)){
                        uuid = uuid + uuidList.get(i);
                        data = data + saveData.get(i);
                    }else{
                        uuid = uuidList.get(i)+"," + uuid;
                        data = saveData.get(i) + "," + data;
                    }
                }
                n++;
                edit.putString("uuid"+n,uuid);
                edit.putString("data" + n, data);
                edit.putString("name" + n, SceneName);
                edit.putString("n", "" + n);
                edit.commit();
                HomeTools.MessageItem item = homeTools.new MessageItem();
                item.title = SceneName;
                item.tag = n;
                mMessageItems.add(item);
                Toast.makeText(getContext(),"已保存场景"+ SceneName,Toast.LENGTH_SHORT).show();
                myAdapter.clear();
                switchList.clear();
                myAdapter.addAll(mMessageItems);
                lv.setAdapter(myAdapter);
                homeTools.setListViewHeightBasedOnChildren(lv);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
    public class MyAdapter extends ArrayAdapter<HomeTools.MessageItem> {
        private LayoutInflater mInflater;

        public MyAdapter(Context context, int resourceId) {
            super(context, resourceId);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HomeTools.ViewHolder holder;
            SlideView slideView = (SlideView) convertView;
            if (convertView == null) {
                View itemView = mInflater.inflate(R.layout.list_item, null);
                slideView = new SlideView(getContext());
                slideView.setContentView(itemView);
                holder = homeTools.new ViewHolder();
                holder.txt = (TextView) slideView.findViewById(R.id.textView);
                holder.swi = (Switch)slideView.findViewById(R.id.switch1);
                holder.deleteHolder = (ViewGroup)slideView.findViewById(R.id.holder);
                holder.bt = (Button)slideView.findViewById(R.id.button2);
                slideView.setOnSlideListener(SceneFragment.this);
                slideView.setTag(holder);
            }else {
                holder = (HomeTools.ViewHolder)convertView.getTag();
            }
            HomeTools.MessageItem item = mMessageItems.get(position);
            holder.txt.setText(item.title);
            if(item.tag > 0) {
                holder.bt.setVisibility(View.GONE);
                holder.swi.setVisibility(View.VISIBLE);
                item.slideView = slideView;
                item.slideView.shrink();
                holder.swi.setTag(position);
                switchList.add(holder.swi);
                TextView set = (TextView)holder.deleteHolder.findViewById(R.id.tvset);
                TextView delet = (TextView)holder.deleteHolder.findViewById(R.id.delete);
                delet.setOnClickListener(new btDelet(position));
                set.setOnClickListener(new btSet(position));
            }else{
                holder.swi.setVisibility(View.GONE);
                holder.bt.setVisibility(View.VISIBLE);
                holder.bt.setOnClickListener(new View.OnClickListener() {
                    @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext(), R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_save);
                Button bok = (Button)dialog.findViewById(R.id.btnOk);
                Button bCancel = (Button)dialog.findViewById(R.id.btnCancel);
                final EditText getName = (EditText)dialog.findViewById(R.id.etInputMessage);
                bok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        SceneName = getName.getText().toString();
                        if(!SceneName.equals("")) {
                            HomeTools.sendBroadcast(getContext(), new Intent(HomeTools.ACTION_GET_DATA));
                            dialog.cancel();
                        }else
                            Toast.makeText(getContext(),"未输入场景名称",Toast.LENGTH_SHORT).show();

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
                });
            }
            return slideView;
        }
    }
    public class btDelet implements View.OnClickListener {
        int mPosition;
        public btDelet(int position){
            mPosition = position+1;
        }
        @Override
        public void onClick(View v) {
            dialog = new Dialog(getContext(), R.style.MyDialog);
            dialog.setContentView(R.layout.dialog_save);
            Button bok = (Button) dialog.findViewById(R.id.btnOk);
            Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
            TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            EditText getName = (EditText) dialog.findViewById(R.id.etInputMessage);
            tvTitle.setText("提示");
            tvMessage.setText("是否删除当前场景？");
            bok.setText("删除");
            bok.setTextColor(Color.RED);
            getName.setVisibility(View.GONE);
            bok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    for (int i = mPosition; i < n; i++) {
                        edit.putString("uuid" + i, scene.getString("uuid" + (i + 1), ""));
                        edit.putString("data" + i, scene.getString("data" + (i + 1), ""));
                        edit.putString("name" + i, scene.getString("name" + (i + 1), ""));
                    }
                    n--;
                    edit.putString("n", "" + n);
                    edit.commit();
                    mMessageItems.remove(n + 1);
                    myAdapter.clear();
                    switchList.clear();
                    myAdapter.addAll(mMessageItems);
                    lv.setAdapter(myAdapter);
                    homeTools.setListViewHeightBasedOnChildren(lv);
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
    public class btSet implements View.OnClickListener {
        int mPosition;
        public btSet(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            dialog = new Dialog(getContext(), R.style.MyDialog);
            dialog.setContentView(R.layout.dialog_save);
            Button bok = (Button) dialog.findViewById(R.id.btnOk);
            Button bCancel = (Button) dialog.findViewById(R.id.btnCancel);
            TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            final EditText getName = (EditText) dialog.findViewById(R.id.etInputMessage);
            tvTitle.setText("更改场景名称");
            tvMessage.setText("请输入当前场景的名称：");
            bok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (!getName.getText().toString().equals("")) {
                        edit.putString("name" + mPosition, "" + getName.getText());
                        edit.commit();
                        dialog.cancel();
                        mMessageItems.get(mPosition).title = getName.getText().toString();
                        myAdapter.clear();
                        myAdapter.addAll(mMessageItems);
                        lv.setAdapter(myAdapter);
                        homeTools.setListViewHeightBasedOnChildren(lv);
                    } else
                        Toast.makeText(getContext(), "未输入场景名称", Toast.LENGTH_SHORT).show();
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
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
