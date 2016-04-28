package com.juma.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {
private int ang=0,x,y,cx,cy,cy1,cy2,cx1,cx2,tem;
private double d,d1,d2;
private float circleX,circleY,R,x1,y1;
private OnTemChangedListener listener = null;
	public MyView(Context context) {
		super(context);
	}
public void anglevalue (double nb1){
	
	ang=(int)nb1;
	invalidate();
	
}
	@SuppressLint("DrawAllocation")
	@Override
	 protected void onDraw(Canvas canvas) {
	  super.onDraw(canvas);
	  super.onDraw(canvas); 
	  circleX = getWidth()/2;
	  circleY = getHeight()/2;
      R = getWidth()*3/10;
	  Paint paint = new Paint();
	    paint.setAntiAlias(true);    
	    paint.setStrokeWidth((float) 1.0); 
	    paint.setStyle(Style.STROKE);  
	    paint.setColor(0x60ffaa25);
	    RectF oval=new RectF();                    
	    oval.left = circleX-R;                              
	    oval.top = circleY-R;                               
	    oval.right = circleX+R;                         
	    oval.bottom = circleY+R;  
	    canvas.drawArc(oval, 0,360, false, paint);
	    oval.left = oval.left-20;                         
	    oval.top = oval.top-20;                                 
	    oval.right = oval.right+20;                       
	    oval.bottom = oval.bottom+20; 
	    canvas.drawArc(oval, 0,360, false, paint);
	    paint.setStrokeWidth((float) 20.0); 
	    paint.setColor(0x40ffaa25);
	    oval.left = oval.left+10;                                
	    oval.top = oval.top+10;                                     
	    oval.right = oval.right-10;                            
	    oval.bottom = oval.bottom-10; 
	    canvas.drawArc(oval, 0,360, false, paint);
	    paint.setColor(0x60ffaa25);
	    canvas.drawArc(oval, 120,ang, false, paint);
	    RectF oval1=new RectF();
	    oval1.left = circleX-(R+10)/2-10;
	    oval1.top = circleY+((R+10)*(float)Math.sqrt(3)/2)-10;
	    oval1.right = circleX-(R+10)/2+10;
	    oval1.bottom = circleY+((R+10)*(float)Math.sqrt(3)/2)+10;
	    paint.setStrokeWidth((float) 1.0);
	    paint.setStyle(Style.FILL); 
	    canvas.drawArc(oval1, 300,180, false, paint);
	    oval1.left = circleX+(R+10)/2-10;
	    oval1.top = circleY+((R+10)*(float)Math.sqrt(3)/2)-10;
	    oval1.right = circleX+(R+10)/2+10;
	    oval1.bottom = circleY+((R+10)*(float)Math.sqrt(3)/2)+10;
	    canvas.drawArc(oval1, 0,360, false, paint); 
	    paint.setColor(0xffffaa25);
	    tem = (ang/3);
	    if(listener != null){
	    listener.onTemChange(tem);
	    }
	   if(ang < 60){
		  x1 = (float) (circleX - (R+10)*Math.cos((60-ang)*Math.PI/180));
		  y1 = (float) (circleY + (R+10)*Math.sin((60-ang)*Math.PI/180));
	   }else if(ang < 150){
		   x1 = (float) (circleX - (R+10)*Math.cos((ang-60)*Math.PI/180));
		   y1 = (float) (circleY - (R+10)*Math.sin((ang-60)*Math.PI/180));
	   }else if(ang < 240){
		   x1 = (float) (circleX + (R+10)*Math.cos((240-ang)*Math.PI/180));
		   y1 = (float) (circleY - (R+10)*Math.sin((240-ang)*Math.PI/180));
	   }else{
		   x1 = (float) (circleX + (R+10)*Math.cos((ang-240)*Math.PI/180));
		   y1 = (float) (circleY + (R+10)*Math.sin((ang-240)*Math.PI/180)); 
	   }
	    
	    	 oval1.left = x1-15;
		 	    oval1.top = y1-15;
		 	    oval1.right = x1+15;
		 	    oval1.bottom =y1+15;
		 	    canvas.drawArc(oval1, 0,360, false, paint); 
	   
	    paint.setTextSize(R*7/10);
	    paint.setTextAlign(Align.CENTER); 
	    FontMetrics fontMetrics = paint.getFontMetrics(); 
	    float fontHeight = fontMetrics.bottom - fontMetrics.top; 
	    float textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom; 
	    canvas.drawText(""+tem, getWidth() / 2, textBaseY, paint);
	    paint.setStrokeWidth((float) 7.0); 
	    paint.setStyle(Style.STROKE); 
	    oval1.left = (float) (getWidth()/2+7*R*(Math.sqrt(3))/24-R/20);
 	    oval1.top = (float) (getHeight()/2-7*R/24-R/20);
 	    oval1.right = (float) (getWidth()/2+7*R*(Math.sqrt(3))/24+R/20);
 	    oval1.bottom = (float) (getHeight()/2-7*R/24+R/20);
 	    canvas.drawArc(oval1, 0,360, false, paint); 
	    paint.setStrokeWidth((float) 13.0); 
// 	    canvas.drawLine(getWidth()*19/40,getHeight()/2-R/2-10, getWidth()*21/40,getHeight()/2-R/2-10, paint);
//	    canvas.drawLine(getWidth()*19/40,getHeight()/2+R/2+10, getWidth()*21/40,getHeight()/2+R/2+10, paint);
//	    canvas.drawLine(getWidth()/2,getHeight()/2-R/2-10-getWidth()/40, getWidth()/2,getHeight()/2-R/2-10+getWidth()/40, paint);
	 }
	  @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	 
		  setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
	    }
	  @SuppressLint("ClickableViewAccessibility")
//	@Override
//      public boolean onTouchEvent(MotionEvent event) {
//              switch (event.getAction()) {
//              case MotionEvent.ACTION_DOWN: 
//            	  x = (int) event.getX();
//            	  y = (int) event.getY();
//            	  cx1 = x - getWidth() / 2;
//                  cy1 = (int) (y - (getHeight()/2-R/2-10));
//                  cx2 = x - getWidth() / 2;
//                  cy2 = (int) (y - (getHeight()/2+R/2+10));
//                  d1 = Math.sqrt(cx1 * cx1 + cy1 * cy1);
//                  d2 = Math.sqrt(cx2 * cx2 + cy2 * cy2);
//                  if(d1 < getWidth()/20 && ang < 300){
//                	  ang = ang+18;
//                	  if(ang > 282){
//                		  ang = 300;
//                	  }
//                  }
//                  if(d2 < getWidth()/20 && ang > 0){
//                	  ang = ang-18;
//                	  if(ang < 18){
//                		  ang = 0;
//                	  }
//                	 
//                  }
//                  invalidate();
//                      break;
//              case MotionEvent.ACTION_MOVE: 
//            	  x = (int) event.getX();
//            	  y = (int) event.getY();
//                  cx = x - getWidth() / 2;
//                  cy = y - getHeight() / 2;
//                  d = Math.sqrt(cx * cx + cy * cy);
//                
//                 
//                  double r =  Math.toDegrees(Math.atan2(cy, cx));
//                 
//            	  if((r >120 || r < 60) && d > R-20){
//            	  x1 = (int) (cx*(R+10)/d+getWidth()/2);
//            	  y1 = (int) (cy*(R+10)/d+getWidth()/2); 
//                 
//                	  if(r < 0){
//                		 
//                		  ang = (int) (360+r-120);
//                		 
//                	  }else if(r > 0 && r < 60){
//                		
//                		  ang = (int) (240+r);
//                	  }else if (r > 120){
//                		 
//                		  ang = (int) (r-120);
//                	  }
//                	 
//                  
//                	  invalidate();
//            	  }
//                      break;
//
//              default:
//                      break;
//              }
//              return true;
//      }
	  public interface OnTemChangedListener {
         public void onTemChange(int tem);
  }
	   public void setOnTemChangedListener(OnTemChangedListener listener) {
           this.listener = listener;
   }
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		}
	
	
	

}
