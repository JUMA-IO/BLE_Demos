package com.juma.widget;
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Timer;  
import java.util.TimerTask;  
  
import android.annotation.SuppressLint;
import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Paint.Align;  
import android.graphics.Paint.Style;  
import android.graphics.Path;  
import android.os.Handler;  
import android.os.Message;  
import android.util.AttributeSet;  
import android.view.View;  

@SuppressLint("HandlerLeak")
public class WaveView extends View  
{  
  
    private int mViewWidth;  
    private int mViewHeight;  
    private float AX = 0;
    private float mLevelLine,LL = 1f;  
    private float mWaveHeight = 80,WH = 0.1f;  
    private float mWaveWidth = 200,WW = 1;  
    private float mLeftSide;  
    private float mMoveLen;  
    private float SPEED = 0,SN = 0.005f;  
  
    private List<Point> mPointsList;  
    private Paint mPaint;  
    private Paint mTextPaint;  
    private Path mWavePath;  
    private boolean isMeasured = false,SC = true,SP = false;
    private int n = 0;
    private int wC = Color.argb(0x80, 0x36, 0xba, 0xf8),tC = Color.WHITE;
    private Timer timer;  
    private MyTimerTask mTask;  
    Handler updateHandler = new Handler()  
    {  
  
        @Override  
        public void handleMessage(Message msg)  
        {  
           
        	if(SC){
            if(mMoveLen >= 0 && mMoveLen < mWaveWidth && n!=1){
            	n = 0;
            }
            if(mMoveLen >= mWaveWidth){
            	n = 1;
            }
            if (mMoveLen == 1 && n ==1) {  
            	n = 2;
            }  
            switch(n){
            case 0:
            	  mMoveLen += SPEED;  
                  mLeftSide += SPEED; 
                 if(mMoveLen < mWaveWidth/2){
                	 SPEED+=SN;
                 }else{
                	 SPEED-=SN;
                 }
            	 for (int i = 0; i < mPointsList.size(); i++)  
                 {  
                     mPointsList.get(i).setX(mPointsList.get(i).getX() + SPEED); 
                 }
            	break;
            case 1:
            	  mMoveLen -= SPEED;  
                  mLeftSide -= SPEED;  
                  if(mMoveLen > mWaveWidth/2){
                 	 SPEED+=SN;
                  }else{
                 	 SPEED-=SN;
                  }
            	 for (int i = 0; i < mPointsList.size(); i++)  
                 {  
                     mPointsList.get(i).setX(mPointsList.get(i).getX() - SPEED);  
                 }
            	break;
            case 2:
            	  mMoveLen = 0; 
            	  SPEED = 0;
                  resetPoints();  
            	break;
            }
        	}else{
        		  mMoveLen += SPEED;  
                  mLeftSide += SPEED; 
                 for (int i = 0; i < mPointsList.size(); i++)  
                 {  
                     mPointsList.get(i).setX(mPointsList.get(i).getX() + SPEED);  
                 }
                 if(mMoveLen >= mWaveWidth){
                	  mMoveLen = 0; 
                      resetPoints();  
                 }
        	}
            invalidate();  
        }  
    };  
    private void resetPoints()  
    {  
        mLeftSide = -2*mWaveWidth+AX*mWaveWidth;  
        for (int i = 0; i < mPointsList.size(); i++)  
        {  
            mPointsList.get(i).setX(i * mWaveWidth / 4 - 2*mWaveWidth+AX*mWaveWidth);  
        }  
    }  
  
    public WaveView(Context context)  
    {  
        super(context);  
        init();  
    }  
  
    public WaveView(Context context, AttributeSet attrs)  
    {  
        super(context, attrs);  
        init();  
    }  
  
    public WaveView(Context context, AttributeSet attrs, int defStyle)  
    {  
        super(context, attrs, defStyle);  
        init();  
    }  
  
    private void init()  
    {  
        mPointsList = new ArrayList<Point>();  
        timer = new Timer();  
        mWavePath = new Path();  
    }  
  
    @Override  
    public void onWindowFocusChanged(boolean hasWindowFocus)  
    {  
        super.onWindowFocusChanged(hasWindowFocus);  
        start();  
    }  
  
    private void start()  
    {  
        if (mTask != null)  
        {  
            mTask.cancel();  
            mTask = null;  
        }  
        mTask = new MyTimerTask(updateHandler);  
        timer.schedule(mTask, 0, 10);  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        if (!isMeasured)  
        {  
            isMeasured = true;  
            mViewHeight = getMeasuredHeight();  
            mViewWidth = getMeasuredWidth(); 
            
            mLevelLine = mViewHeight*LL;  
            mWaveHeight = mViewWidth*WH;  
            mWaveWidth = mViewWidth*WW;  
            mLeftSide = -mWaveWidth;  
            
            mPaint = new Paint();  
            mPaint.setAntiAlias(true);  
            mPaint.setStyle(Style.FILL);  
            mPaint.setColor(wC);  
      
            mTextPaint = new Paint();  
            mTextPaint.setColor(tC);  
            mTextPaint.setTextAlign(Align.CENTER);  
            mTextPaint.setTextSize(60);
            
            int N = (int) Math.round(mViewWidth / mWaveWidth + 0.5); 
            mPointsList.clear();
          
            for (int i = 0; i < (4 * N + 9); i++)  
            {  
                float x = i * mWaveWidth / 4 - 2*mWaveWidth;  
                float y = 0;  
               
                switch (i % 4)  
                {  
                case 0:  
                
                case 2:  
                    y = mLevelLine;  
                    break;  
                case 1:  
                    y = mLevelLine - mWaveHeight;  
                    break; 
                case 3:  
                    y = mLevelLine + mWaveHeight;  
                    break; 
                }
                mPointsList.add(new Point(x+AX*mWaveWidth, y));  
            }  
        }  
    }
   
    public void gtePrecent(int precent){ 
    	 mLevelLine = mViewHeight*precent/100;
    	 invalidate();  
    }
    public void setWave(boolean speedChange,float speed,float LevelLine,float WaveHeight,float WaveWidth,int waveColor,float addx){
    	SC = speedChange;
    	if(SC){
    	SN = speed;
    	}else{
        SPEED = speed;
    	}
    	//LL = LevelLine;  
    	WH = WaveHeight;  
    	WW = WaveWidth;
    	wC = waveColor;
    	AX = addx;
        invalidate();
    }
    public void setProgress(boolean Progress,float LevelLine,int textcolor){
    	SP = Progress;
    	LL = LevelLine;
    	tC = textcolor;
    	float line = mViewHeight*(1-LL) - mLevelLine;
    	mLevelLine += line;
    	  List<Point> nPointsList;       
          nPointsList = mPointsList;
          for(int i = 0;i<mPointsList.size();i++){
        	  mPointsList.set(i, new Point(nPointsList.get(i).getX(),nPointsList.get(i).getY()+line));
          }
 
    	invalidate();
    }
    @Override  
    protected void onDraw(Canvas canvas)  
    {  
  
        mWavePath.reset();  
        int i = 0;  
        mWavePath.moveTo(mPointsList.get(0).getX(), mPointsList.get(0).getY());  
        for (; i < mPointsList.size() - 2; i = i + 2)  
        {  
            mWavePath.quadTo(mPointsList.get(i + 1).getX(), mPointsList.get(i + 1).getY(), mPointsList.get(i + 2).getX(), mPointsList.get(i + 2).getY());  
        }  
        mWavePath.lineTo(mPointsList.get(i).getX(), mViewHeight);
        mWavePath.lineTo(mLeftSide, mViewHeight);  
        mWavePath.close();  
  
        canvas.drawPath(mWavePath, mPaint); 
       if(SP){
    	   canvas.drawText("" + ((int) ((1-(mLevelLine / mViewHeight)) * 100)) + "%RH", mViewWidth / 2, (mLevelLine + mWaveHeight + mViewHeight) / 2, mTextPaint);  
       }
    }  
  
    class MyTimerTask extends TimerTask  
    {  
        Handler handler;  
  
        public MyTimerTask(Handler handler)  
        {  
            this.handler = handler;  
        }  
  
        @Override  
        public void run()  
        {  
            handler.sendMessage(handler.obtainMessage());  
        }  
  
    }  
  
    class Point  
    {  
        private float x;  
        private float y;  
  
        public float getX()  
        {  
            return x;  
        }  
  
        public void setX(float x)  
        {  
            this.x = x;  
        }  
  
        public float getY()  
        {  
            return y;  
        }  
  
        public void setY(float y)  
        {  
            this.y = y;  
        }  
  
        public Point(float x, float y)  
        {  
            this.x = x;  
            this.y = y;  
        }  
  
    }  
  
}  