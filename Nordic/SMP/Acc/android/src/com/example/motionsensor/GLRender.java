package com.example.motionsensor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

public class GLRender implements Renderer  
{  
    public Context context;  
    private int[] textureids;  
    private IntBuffer vertexBuffer;  
    private IntBuffer texBuffer;  
    private float xrot, yrot, zrot;  
    private int one = 0x15000;  
    private int s = 0x10000;
    private int[] vertices = {  
            one, one, -one,    
            -one, one, -one,  
            one, one, one,  
             
            -one, one, one,  
            one, -one,one,  
            -one, -one, one,  
             
            one, -one, -one,  
            -one, -one, -one,  
            one, one,one,  
             
            -one, one, one,  
            one, -one, one,  
            -one, -one, one,  
             
            one, -one,-one,  
            -one, -one, -one,  
            one, one, -one,  
             
            -one, one, -one,  
            -one, one,one,  
            -one, one, -one,  
             
            -one, -one, one,  
            -one, -one, -one,  
            one, one,-one,  
             
            one, one, one,  
            one, -one, -one,  
            one, -one, one  
        };  
  
    private int[] texCoords = {    
            s, 0,                  
            0, 0,  
            s, s,  
            0, s  
    };  
    public void setXYZ(float x,float y,float z){
	  xrot = x;  
      yrot = y;   
      zrot = z;   
    }
    public GLRender(Context context) {  
        this.context = context; 
        textureids = new int[1];  
  
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);  
        vbb.order(ByteOrder.nativeOrder());  
        vertexBuffer = vbb.asIntBuffer();  
        vertexBuffer.put(vertices);  
        vertexBuffer.position(0);  
  
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 6);  
        tbb.order(ByteOrder.nativeOrder());  
        texBuffer = tbb.asIntBuffer();  
        for (int i = 0; i < 6; i++) {  
            texBuffer.put(texCoords);  
        }  
        texBuffer.position(0);  
    }  
  
    @Override  
    public void onDrawFrame(GL10 gl)  
    {  
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  
        gl.glLoadIdentity();  
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBuffer);  
        gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, texBuffer);  
        gl.glTranslatef(0.0f, 0.0f, -5.0f);  
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);  
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);  
        for (int i = 0; i < 6; i++) {  
            switch(i)  
            {  
            case 0:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;  
            case 1:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;  
            case 2:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;  
            case 3:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;  
            case 4:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;  
            case 5:  
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);  
                break;                                
            }  
                  
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);  
        }  
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);  
          
    }  
  
    @Override  
    public void onSurfaceChanged(GL10 gl, int width, int height)  
    {  
        float ratio = (float) (width)/height;  
        gl.glViewport(0, 0, width, height);  
        gl.glMatrixMode(GL10.GL_PROJECTION);  
        gl.glLoadIdentity();  
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        gl.glMatrixMode(GL10.GL_MODELVIEW);  
        gl.glLoadIdentity();      
          
    }  
  
    @Override  
    public void onSurfaceCreated(GL10 gl, EGLConfig config)  
    {  
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);    
        gl.glClearColor(0, 0, 0, 0);  
        gl.glEnable(GL10.GL_CULL_FACE);  
        gl.glShadeModel(GL10.GL_SMOOTH);  
        gl.glEnable(GL10.GL_DEPTH_TEST);  
        gl.glClearDepthf(1.0f);  
        gl.glDepthFunc(GL10.GL_LEQUAL);  
        gl.glEnable(GL10.GL_TEXTURE_2D);  
        gl.glGenTextures(1, textureids, 0);  
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureids[0]);  
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.iBitmap, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);  
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);  
              
    }  
  
}  
