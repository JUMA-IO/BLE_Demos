package com.example.motionsensor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GLImage {
	 public static Bitmap iBitmap;  
	    public static Bitmap jBitmap;  
	    public static Bitmap kBitmap;  
	    public static Bitmap lBitmap;  
	    public static Bitmap mBitmap;  
	    public static Bitmap nBitmap;  
	    public static Bitmap close_Bitmap;  
	      
	      
	    public static void load(Resources resources)  
	    {  
	        iBitmap = BitmapFactory.decodeResource(resources, R.drawable.juma1);  
	        jBitmap = BitmapFactory.decodeResource(resources, R.drawable.back_2);  
	        kBitmap = BitmapFactory.decodeResource(resources, R.drawable.back_3);  
	        lBitmap = BitmapFactory.decodeResource(resources, R.drawable.back_4);  
	        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.back_1);  
	        nBitmap = BitmapFactory.decodeResource(resources, R.drawable.back_2);  
	        close_Bitmap = BitmapFactory.decodeResource(resources, R.drawable.back_2);  
	    }  
}
