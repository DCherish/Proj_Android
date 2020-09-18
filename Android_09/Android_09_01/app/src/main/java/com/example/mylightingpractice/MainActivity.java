package com.example.mylightingpractice;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private ObjParserRenderer objParserRenderer = new ObjParserRenderer(this);

    private final double TOUCH_SCALE_FACTOR = 0.1;
    private double preAzim;
    private double preElev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);

        // GLSurfaceView 초기화
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setRenderer(objParserRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setZOrderOnTop(true);

        addContentView(glSurfaceView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        double azim = e.getX();
        double elev = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                double dAzim = azim - preAzim;
                double dElev = elev - preElev;

                objParserRenderer.azim += dAzim * TOUCH_SCALE_FACTOR;
                objParserRenderer.elev += dElev * TOUCH_SCALE_FACTOR;

                if(objParserRenderer.azim > 360.0f) {
                    objParserRenderer.azim -= 360.0f;
                }

                if(objParserRenderer.azim < 0.0f) {
                    objParserRenderer.azim += 360.0f;
                }

                if(objParserRenderer.elev > 360.0f) {
                    objParserRenderer.elev -= 360.0f;
                }

                if(objParserRenderer.elev < 0.0f) {
                    objParserRenderer.elev += 360.0f;
                }

                glSurfaceView.requestRender();
        }

        preAzim = azim;
        preElev = elev;
        return true;
    }
}
