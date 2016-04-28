package com.juma.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCompat extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView mFocusedItemView;

    private int position = 0;
    public ListViewCompat(Context context) {
        super(context);
    }

    public ListViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
    public void toGetParent( boolean b){
        getParent().requestDisallowInterceptTouchEvent(b);
    }
    public int getPosition(){
    	return position;
    }
    int x,y,x1,y1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        if (mFocusedItemView != null) {
            mFocusedItemView.getLc(this);
            mFocusedItemView.onRequireTouchEvent(event);
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            x = (int) event.getX();
            y = (int) event.getY();
            position = pointToPosition(x, y);
            if (position != INVALID_POSITION) {
                HomeTools.MessageItem data = (HomeTools.MessageItem) getItemAtPosition(position);
                mFocusedItemView = data.slideView;
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            x1 = (int) event.getX();
            if(Math.abs(x1 - x)>10) {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

}
