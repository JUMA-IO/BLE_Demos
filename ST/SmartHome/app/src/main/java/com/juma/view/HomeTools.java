package com.juma.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class HomeTools
{
    public static final String ACTION_START_SCAN = "com.juma.smarthome.ACTION_START_SCAN";
    public static final String ACTION_DEVICE_DISCOVERED = "com.juma.smarthome.ACTION_DEVICE_DISCOVERED";
    public static final String ACTION_ALL_DISCOVERED = "com.juma.smarthome.ACTION_ALL_DISCOVERED";
    public static final String ACTION_SEND_MESSAGE = "com.juma.smarthome.ACTION_SEND_MESSAGE";
    public static final String ACTION_TEXT_MESSAGE = "com.juma.smarthome.ACTION_TEXT_VIEW";
    public static final String ACTION_SCAN_DISABLE = "com.juma.smarthome.ACTION_SCAN_DISABLE";
    public static final String ACTION_SAVE_DATA = "com.juma.smarthome.ACTION_SAVE_DATA";
    public static final String ACTION_GET_DATA = "com.juma.smarthome.ACTION_GET_DATA";
    public static final String ACTION_CLOSE_SCENE = "com.juma.smarthome.ACTION_CLOSE_SCENE";
    public static final String ACTION_WEATHER = "com.juma.smarthome.ACTION_WEATHER";

    public static final String NAME_STR = "name";
    public static final String WEATHER_STR = "weather";
    public static final String UUID_STR = "uuid";
    public static final String TEXT_STR = "text";
    public static final String ACTIVITY_STR = "activity";
    public static final String MESSAGE_STR = "message";

    public  static final void  sendBroadcast(Context context, Intent intent){
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static final IntentFilter getIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START_SCAN);
        filter.addAction(ACTION_DEVICE_DISCOVERED);
        filter.addAction(ACTION_ALL_DISCOVERED);
        filter.addAction(ACTION_TEXT_MESSAGE);
        filter.addAction(ACTION_SEND_MESSAGE);
        filter.addAction(ACTION_SCAN_DISABLE);
        filter.addAction(ACTION_SAVE_DATA);
        filter.addAction(ACTION_GET_DATA);
        filter.addAction(ACTION_CLOSE_SCENE);
        filter.addAction(ACTION_WEATHER);
        return filter;
    }
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }
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
    @SuppressLint("DefaultLocale")
    public static String byteToHex(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    public class MessageItem {
        public String title;
        public SlideView slideView;
        public int tag;
    }
    public final class ViewHolder {
        public TextView txt;
        public Switch swi;
        public ImageView iv;
        public ViewGroup deleteHolder;
        public Button bt;
    }

}

