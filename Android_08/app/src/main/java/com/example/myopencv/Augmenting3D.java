package com.example.myopencv;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.myopencv.Parser.ObjParser;
import com.example.myopencv.Parser.ObjStructure;

public class Augmenting3D  implements GLSurfaceView.Renderer {
    private static final String TAG = "Augmenting3D";
    private Context context;

    /* Wire cube */
    private Cube cube = new Cube(context);
    private final float lineScalef = 0.05f;

    private Line x_axis = new Line(
            new float[]{
                    0.0f, 0.0f, 0.0f,
                    1.0f * lineScalef, 0.0f, 0.0f
            },
            new float[]{1.0f, 0.0f, 0.0f, 1.0f});

    private Line y_axis = new Line(
            new float[]{
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f * lineScalef, 0.0f
            }, new float[]{0.0f, 1.0f, 0.0f, 1.0f});

    private Line z_axis = new Line(
            new float[]{
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f * lineScalef
            }, new float[]{0.0f, 0.0f, 1.0f, 1.0f});

    /* For perspective projection */
    private float fovy;
    private float aspect;
    private float zNear = 0.1f;
    private float zFar = 10000.0f;

    /* Focal lengths from camera intrinsic parameters */
    private float fx;
    private float fy;

    /* Used calibration image's width, height */
    private float calib_width;
    private float calib_height;

    /* Scale factors */
    public float scale_width;
    public float scale_height;

    /* Got from OpenCV */
    public boolean dispOBJ = false;
    public float[] pose = new float[16];

    public Augmenting3D(Context context, float fx, float fy, float calib_width, float calib_height) {
        this.context = context;
        this.fx = fx;
        this.fy = fy;
        this.calib_width = calib_width;
        this.calib_height = calib_height;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glLineWidth(5.0f);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //test
        Log.d("onSurfaceChanged","width : "+Integer.toString(width)+", height : "+Integer.toString(height));

        /* Calculate scaling factor */
        scale_width = calib_width / width;
        scale_height = calib_height / height;

        /* Scaling because of the resolution difference between
           used calibration image and viewing camera resolution */
        fx /= scale_width;
        fy /= scale_height;

        /* TODO: Set the fovy (need to convert radian to degree) and aspect ratio */
        fovy = ( 2.0f * (float) Math.toDegrees((float) Math.atan2((float) height, 2.0f * fy)));
        // Math.toDegrees(double), Math.atan2(double, double), type-casting(float) 사용
        aspect = ((float) width * fy) / ((float) height * fx) ;
        // type-casting(float) 사용

        Log.i(TAG, "Height = " + String.valueOf(height) + ", Width = " + String.valueOf(width));
        Log.i(TAG, "fy = " + String.valueOf(fy) + " fovy = " + String.valueOf(fovy) + " aspect = " + String.valueOf(aspect));

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, fovy, aspect, zNear, zFar);
        gl.glViewport(0, 0, width, height);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Color buffer 초기화
        gl.glClearDepthf(1.0f); // Depth buffer 초기화

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        if (dispOBJ) {

            /* Load pose matrix */
            gl.glLoadMatrixf(pose, 0);
            gl.glRotatef(90, 1, 0, 0);

            /* Draw a wireframe cube */
            drawAxis(gl);

            gl.glTranslatef(0.028f*2f, 0, 0.028f*2f);
            cube.draw(gl);
        }
    }

    public void drawAxis(GL10 gl){
        x_axis.draw(gl);    // red
        y_axis.draw(gl);    // green
        z_axis.draw(gl);    // blue
    }
}
