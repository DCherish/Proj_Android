package com.example.mylightingpractice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.mylightingpractice.Parser.ObjStructure;
import com.example.mylightingpractice.Parser.ObjParser;

class ObjParserRenderer implements GLSurfaceView.Renderer {
    private Context context;

    /* teapot object */
    private ObjStructure teapot;

    /* For camera setting */
    public volatile double distance;
    public volatile double elev;
    public volatile double azim;

    private float[] cam_eye = new float[3];
    private float[] cam_center = new float[3];
    private float[] cam_up = new float[3];
    private float[] cam_vpn = new float[3];
    private float[] cam_x_axis = new float[3];

    private float[] uv_py = new float[3];
    private float[] uv_ny = new float[3];

    private float[] ambient_L0 = {.2f, .2f, .2f, 1.f} ;
    private float[] diffuse_L0 = {1.f, 1.f, 1.f, 1.f} ;
    private float[] specular_L0 = {1.f, 1.f, 1.f, 1.f} ;

    private float[] ambient_L1 = {.15f, .15f, .15f, 1.f} ;
    private float[] diffuse_L1 = {.7f, .7f, .7f, 1.f} ;
    private float[] specular_L1 = {.7f, .7f, .7f, 1.f} ;

    private float[] teapot_ambient = {.5f, .5f, .5f, 1.f} ;
    private float[] teapot_diffuse = {1.f, 0.f, 0.f, 1.f} ;
    private float[] teapot_specular = {.5f, .5f, .5f, 1.f} ;
    private float[] teapot_emission = {1.f, 0.f, 0.f, 1.f} ;

    public ObjParserRenderer(Context context) {
        this.context = context;
    }

    private void addLight(GL10 gl, int Light_ID, float[] ambient, float[] diff, float[] spec, float[] pos)
    {
        gl.glLightfv(Light_ID, GL10.GL_POSITION, makeFloatBuffer(pos)) ;
        gl.glLightfv(Light_ID, GL10.GL_AMBIENT, makeFloatBuffer(ambient));
        gl.glLightfv(Light_ID, GL10.GL_DIFFUSE, makeFloatBuffer(diff));
        gl.glLightfv(Light_ID, GL10.GL_SPECULAR, makeFloatBuffer(spec));
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(Light_ID);
    }

    private static FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4) ;
        bb.order(ByteOrder.nativeOrder()) ;
        FloatBuffer fb = bb.asFloatBuffer() ;
        fb.put(arr) ;
        fb.position(0) ;
        return fb ;
    }

    private void initMaterial(GL10 gl, float[] ambient, float[] diff, float[] spec, float shine)
    {
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, makeFloatBuffer(ambient));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(diff));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(spec));
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shine);
    }

    private void calcCross(float[] vector1, float[] vector2, float[] cp_vector) {
        cp_vector[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
        cp_vector[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
        cp_vector[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];
    }

    private void vNorm(float[] vector) {
        float scale = (float) Math.sqrt(Math.pow((double) vector[0], 2) + Math.pow((double) vector[1], 2) + Math.pow((double) vector[2], 2));

        vector[0] = vector[0] / scale;
        vector[1] = vector[1] / scale;
        vector[2] = vector[2] / scale;
    }

    private void calcUpVector() {
        double r_elev = elev * Math.PI / 180.0;
        double r_azim = azim * Math.PI / 180.0;

        cam_eye[0] = (float) distance * (float) Math.sin(r_elev) * (float) Math.sin(r_azim);
        cam_eye[1] = (float) distance * (float) Math.cos(r_elev);
        cam_eye[2] = (float) distance * (float) Math.sin(r_elev) * (float) Math.cos(r_azim);

        cam_vpn[0] = cam_eye[0] - cam_center[0];
        cam_vpn[1] = cam_eye[1] - cam_center[1];
        cam_vpn[2] = cam_eye[2] - cam_center[2];
        vNorm(cam_vpn);

        if (elev >= 0 && elev < 180) {
            calcCross(uv_py, cam_vpn, cam_x_axis);
        }
        else {
            calcCross(uv_ny, cam_vpn, cam_x_axis);

        }
        calcCross(cam_vpn, cam_x_axis, cam_up);
        vNorm(cam_up);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_FASTEST);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_COLOR_MATERIAL);
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glCullFace(gl.GL_BACK);

        distance = 10.0;
        elev = 90.0;
        azim = 0.0;

        uv_py[0] = 0.0f;
        uv_py[1] = 1.0f;
        uv_py[2] = 0.0f;

        uv_ny[0] = 0.0f;
        uv_ny[1] = -1.0f;
        uv_ny[2] = 0.0f;

        cam_center[0] = 0.0f;
        cam_center[1] = 0.0f;
        cam_center[2] = 0.0f;

        calcUpVector();

        ObjParser objParser = new ObjParser(context); // obj 파일 Parser 생성
        try {
            objParser.parse(R.raw.teapot); // obj 파일 parsing

        } catch (IOException e) {

        }

       teapot = new ObjStructure(objParser, gl, this.context, null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float zNear = 0.1f;
        float zFar = 1000f;
        float fovy = 45.0f;
        float aspect = (float) width / (float) height;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, fovy, aspect, zNear, zFar);
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        calcUpVector();

        addLight(gl, GL10.GL_LIGHT0, ambient_L0, diffuse_L0, specular_L0, new float[]{-10.0f, -10.0f, -3.0f, 1.0f});
        addLight(gl, GL10.GL_LIGHT1, ambient_L1, diffuse_L1, specular_L1, new float[]{10.0f, 10.0f, -3.0f, 1.0f});
        GLU.gluLookAt(gl, cam_eye[0], cam_eye[1], cam_eye[2], cam_center[0], cam_center[1], cam_center[2], cam_up[0], cam_up[1], cam_up[2]);

        // draw teapot
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(teapot_emission));
        initMaterial(gl, teapot_ambient, teapot_diffuse, teapot_specular, 10.0f);

        gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
        teapot.setScale(2f);
        teapot.draw(gl);

        gl.glFlush();
    }
}
