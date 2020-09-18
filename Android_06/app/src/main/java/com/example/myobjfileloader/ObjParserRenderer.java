package com.example.myobjfileloader ;

import android.content.Context ;
import android.opengl.GLSurfaceView ;
import android.util.Log;

import com.example.myobjfileloader.Parser.ObjParser ;
import com.example.myobjfileloader.Parser.ObjStructure ;

import java.io.IOException ;

import javax.microedition.khronos.egl.EGLConfig ;
import javax.microedition.khronos.opengles.GL10 ;

public class ObjParserRenderer implements GLSurfaceView.Renderer
{
    private Context context ;

    int A = 1 ;

    float initscale ;
    float scalef ;

    private float[] eyeposition = {0.0f, 0.0f, 10.0f} ;
    ObjStructure obj1, obj2, tempobj ;

    public int angle = 180 ;
    public static boolean rot_flag = true ;

    public ObjParserRenderer(Context context) {
        this.context = context ;
    }

    public void Init(GL10 gl)
    {
        initscale = 0.5f ;
        scalef = 0.00f ;

        Log.d("DDDDDDDDDDDDDDD", "Hi?") ;

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE) ;
        gl.glEnable(GL10.GL_TEXTURE_2D) ;
        gl.glEnable(GL10.GL_DEPTH_TEST) ;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST) ;
        Init(gl) ;

        ObjParser objParser = new ObjParser(context) ;
        ObjParser objParser2 = new ObjParser(context) ;

        try
        {
            objParser.parse(R.raw.face_blender) ;
        }catch( IOException e ){
        }

        int group = objParser.getObjectIds().size() ;
        int[] texture = new int[group] ;
        texture[0] = R.drawable.face_eyer_hi ;
        texture[1] = R.drawable.face_eyel_hi ;
        texture[2] = R.drawable.face_sock ;
        texture[3] = R.drawable.face_skin_hi ;
        obj1 = new ObjStructure(objParser, gl, this.context, texture) ;

        try
        {
            objParser2.parse(R.raw.tank) ;
        }catch( IOException e ){
        }

        int group2 = objParser2.getObjectIds().size() ;
        int[] texture2 = new int[group2] ;
        texture2[0] = R.drawable.tank1 ;
        texture2[1] = R.drawable.tank2 ;
        texture2[2] = R.drawable.tank3 ;
        texture2[3] = R.drawable.tank4 ;
        texture2[4] = R.drawable.tank5 ;
        texture2[5] = R.drawable.tank6 ;
        obj2 = new ObjStructure(objParser2, gl, this.context, texture2) ;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height) ;

        float aspectRatio ;
        float zNear = .1f ;
        float zFar = 1000f ;
        float fieldOfView = 30.0f/57.3f ;
        float size ;

        aspectRatio = (float)width/(float)height ;
        gl.glMatrixMode(GL10.GL_PROJECTION) ;
        gl.glLoadIdentity() ;
        size = zNear * (float) (Math.tan((double)(fieldOfView/2.0f))) ;
        gl.glFrustumf(-size, size, -size /aspectRatio, size /aspectRatio, zNear, zFar) ;
        gl.glMatrixMode(GL10.GL_MODELVIEW) ;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT) ;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f) ;
        gl.glClearDepthf(1.0f) ;
        gl.glMatrixMode(GL10.GL_MODELVIEW) ;
        gl.glLoadIdentity() ;

        gl.glTranslatef(eyeposition[0], eyeposition[1], -eyeposition[2]) ;

        if( rot_flag )
        {
            angle++ ;
            if( angle > 360 ) angle = 0 ;
        }

        initscale = initscale + scalef ;

        gl.glRotatef(angle, 0, 1, 0) ;
        obj1.setScale( initscale ) ;
        obj1.draw(gl) ;

        scalef = 0.00f ;
    }

    public void change_obj()
    {
        if( A % 2 == 1 )
        {
            tempobj = obj1 ;
            obj1 = obj2 ;
            A++ ;
            initscale = 0.5f ;
        }else
        {
            obj1 = tempobj ;
            A++ ;
            initscale = 0.5f ;
        }
    }

    public void rotation()
    {
        if( rot_flag == true )
        {
            rot_flag = false ;
        }else
        {
            rot_flag = true ;
        }
    }

    public void objscale(float f)
    {
        scalef = f ;
    }
}
