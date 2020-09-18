package com.example.myobjfileloader ;

import android.graphics.PixelFormat ;
import android.opengl.GLSurfaceView ;
import android.support.v7.app.ActionBar ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.util.Log ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.WindowManager ;
import android.widget.RelativeLayout ;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView glSurfaceView ;
    private ObjParserRenderer objParserRenderer = new ObjParserRenderer(this) ;

    public double[] prev_x, prev_y ;
    public double prev_x_diff, prev_y_diff, prev_diff ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) ;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) ;
        View decorView = getWindow().getDecorView() ;
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN ;
        decorView.setSystemUiVisibility(uiOptions) ;
        ActionBar actionBar = getSupportActionBar() ;
        actionBar.hide() ;
        setContentView(R.layout.activity_main) ;

        glSurfaceView = new GLSurfaceView(this) ;
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) ;
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT) ;
        glSurfaceView.setRenderer(objParserRenderer) ;
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY) ;
        glSurfaceView.setZOrderOnTop(true) ;

        addContentView(glSurfaceView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)) ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int finger = event.getPointerCount() ;
        double[] cur_x = new double[finger] ;
        double[] cur_y = new double[finger] ;

        prev_x = new double[finger] ;
        prev_y = new double[finger] ;

        if( finger == 2 )
        {
            switch( event.getAction() & MotionEvent.ACTION_MASK )
            {
                case MotionEvent.ACTION_POINTER_DOWN :
                    cur_x[0] = event.getX(0) ;
                    cur_x[1] = event.getX(1) ;
                    cur_y[0] = event.getY(0) ;
                    cur_y[1] = event.getY(1) ;
                    break ;
                case MotionEvent.ACTION_POINTER_UP :
                    break ;
                case MotionEvent.ACTION_MOVE :
                    cur_x[0] = event.getX(0) ;
                    cur_x[1] = event.getX(1) ;
                    cur_y[0] = event.getY(0) ;
                    cur_y[1] = event.getY(1) ;
                    double x_difference = cur_x[1] - cur_x[0] ;
                    double y_difference = cur_y[1] - cur_y[0] ;
                    double cur_diff = Math.sqrt(x_difference * x_difference + y_difference * y_difference) ;

                    if( cur_diff > prev_diff )
                    {
                        objParserRenderer.objscale((float) (cur_diff-prev_diff) * 0.0005f) ;
                    }else
                    {
                        objParserRenderer.objscale((float) (cur_diff-prev_diff) * 0.0005f) ;
                    }
                    break ;
            }

            prev_x_diff = cur_x[1] - cur_x[0] ;
            prev_y_diff = cur_y[1] - cur_y[0] ;

            prev_diff = Math.sqrt(prev_x_diff * prev_x_diff + prev_y_diff * prev_y_diff) ;
        }

        return super.onTouchEvent(event) ;
    }

    public void change_obj(View view)
    {
        objParserRenderer.change_obj() ;
    }

    public void rotation(View view)
    {
        objParserRenderer.rotation() ;
    }
}
