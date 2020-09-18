package com.example.mynewsolarsystem ;

import android.content.Context ;
import android.opengl.GLSurfaceView ;
import android.opengl.GLU ;

import java.io.IOException ;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig ;
import javax.microedition.khronos.opengles.GL10 ;

import com.example.mynewsolarsystem.Parser.ObjParser ;
import com.example.mynewsolarsystem.Parser.ObjStructure ;

public class SolarSystemRenderer implements GLSurfaceView.Renderer
{
    private Context context ;

    /* For obj(planet) & texture */
    private ObjStructure[] planet = new ObjStructure[10] ;

    public int[] texture_id = new int[]{    // 태양, 수성, 금성, 지구, 달, 화성, 목성, 토성, 천왕성, 해왕성
            R.drawable.sunlow, R.drawable.mercurylow, R.drawable.venuslow,
            R.drawable.earthlow, R.drawable.moonlow, R.drawable.marslow,
            R.drawable.jupiterlow, R.drawable.saturnlow, R.drawable.uranuslow,
            R.drawable.neptunelow} ;
    public float scaler = 0.25f ;  // 태양 크기 결정
    public float[] scalefactor = new float[]{   // 태양으로부터 상대적 크기 결정
            scaler, scaler*0.06f, scaler*0.18f,   // 태양, 수성, 금성
            scaler*0.2f, scaler*0.05f, scaler*0.1f,    // 지구, 달, 화성
            scaler*0.6f, scaler*0.5f,scaler*0.35f,scaler*0.33f} ;  // 목성, 토성, 천왕성, 해왕성

    /* For rotation */
    public boolean rot_flag = false ;

    private float angle_sun = 0.0f ;
    private float rotating_sun = 0.5f ; // 태양의 자전속도

    private float angle_earth = 0.0f ;
    private float orbital_earth = 0.1f ; // 지구의 공전속도

    private float angle_mercury = 0.0f ;
    private float orbital_mercury = 0.2f ; // 수성의 공전속도

    private float angle_venus = 0.0f ;
    private float orbital_venus = 0.15f ; // 금성의 공전속도

    private float angle_moon = 0.0f ;
    private float orbital_moon = 0.5f ; // 달의 공전속도

    private float angle_mars = 0.0f ;
    private float orbital_mars = 0.06f ; // 화성의 공전속도

    private float angle_jupiter = 0.0f ;
    private float orbital_jupiter = 0.03f ; // 목성의 공전속도

    private float angle_saturn = 0.0f ;
    private float orbital_saturn = 0.02f ; // 토성의 공전속도

    private float angle_uranus = 0.0f ;
    private float orbital_uranus = 0.01f ; // 천왕성의 공전속도

    private float angle_neptune = 0.0f ;
    private float orbital_neptune = 0.005f ; // 해왕성의 공전속도

    /* For camera setting */
    private double distance ;
    public volatile double elev ;
    public volatile double azim ;

    private float[] cam_eye = new float[3] ;
    private float[] cam_center = new float[3] ;
    private float[] cam_up = new float[3] ;
    private float[] cam_vpn = new float[3] ;
    private float[] cam_x_axis = new float[3] ;

    private float[] uv_py = new float[3] ;
    private float[] uv_ny = new float[3] ;

    /* For texture on, off */
    public boolean texture_on_off = true ;

    // For light and materials
    private final static int SUN_LIGHT = GL10.GL_LIGHT0 ;

    private float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
    private float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    private float[] specular = {0.8f, 0.8f, 0.8f, 1.0f};

    private float[] sun_ambient = {1.0f, 0.0f, 0.0f, 1.0f};
    private float[] sun_diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] sun_specular = {1.0f, 0.0f, 0.0f, 1.0f};

    private float[] mercury_ambient = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mercury_diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] mercury_specular = {0.7f, 0.7f, 0.0f, 1.0f};

    private float[] venus_ambient = {0.7f, 0.0f, 0.0f, 1.0f};
    private float[] venus_diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] venus_specular = {0.6f, 0.6f, 0.6f, 1.0f};

    private float[] earth_ambient = {0.0f, 0.0f, 1.0f, 1.0f};
    private float[] earth_diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] earth_specular = {0.5f, 0.5f, 0.5f, 1.0f};

    private float[] moon_ambient = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] moon_diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] moon_specular = {0.5f, 0.5f, 0.0f, 1.0f};

    private float[] mars_ambient = {1.0f, 0.0f, 0.0f, 1.0f};
    private float[] mars_diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    private float[] mars_specular = {1.0f, 0.0f, 0.0f, 1.0f};

    private float[] jupiter_ambient = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] jupiter_diffuse = {0.3f, 0.3f, 0.3f, 1.0f};
    private float[] jupiter_specular = {0.3f, 0.3f, 0.0f, 1.0f};

    private float[] saturn_ambient = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] saturn_diffuse = {0.2f, 0.2f, 0.2f, 1.0f};
    private float[] saturn_specular = {0.2f, 0.2f, 0.0f, 1.0f};

    private float[] uranus_ambient = {0.0f, 0.0f, 1.0f, 1.0f};
    private float[] uranus_diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] uranus_specular = {0.6f, 0.6f, 0.6f, 1.0f};

    private float[] neptune_ambient = {0.0f, 0.0f, 1.0f, 1.0f};
    private float[] neptune_diffuse = {0.1f, 0.1f, 0.1f, 1.0f};
    private float[] neptune_specular = {0.1f, 0.1f, 0.1f, 1.0f};

    public SolarSystemRenderer(Context context )
    {
        this.context = context ;
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
        //gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(spec));
        //gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shine);
    }

    private void calcCross(float[] vector1, float[] vector2, float[] cp_vector)
    {
        cp_vector[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1] ;
        cp_vector[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2] ;
        cp_vector[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0] ;
    }

    private void vNorm(float[] vector)
    {
        float scale = (float) Math.sqrt(Math.pow((double) vector[0], 2) + Math.pow((double) vector[1], 2) + Math.pow((double) vector[2], 2)) ;

        vector[0] = vector[0] / scale ;
        vector[1] = vector[1] / scale ;
        vector[2] = vector[2] / scale ;
    }

    private void calcUpVector()
    {
        double r_elev = elev * Math.PI / 180.0 ;
        double r_azim = azim * Math.PI / 180.0 ;

        cam_eye[0] = (float) distance * (float) Math.sin(r_elev) * (float) Math.sin(r_azim) ;
        cam_eye[1] = (float) distance * (float) Math.cos(r_elev) ;
        cam_eye[2] = (float) distance * (float) Math.sin(r_elev) * (float) Math.cos(r_azim) ;

        cam_vpn[0] = cam_eye[0] - cam_center[0] ;
        cam_vpn[1] = cam_eye[1] - cam_center[1] ;
        cam_vpn[2] = cam_eye[2] - cam_center[2] ;
        vNorm(cam_vpn) ;

        if( elev >= 0 && elev < 180 )
        {
            calcCross(uv_py, cam_vpn, cam_x_axis) ;
        }
        else
        {
            calcCross(uv_ny, cam_vpn, cam_x_axis) ;
        }
        calcCross(cam_vpn, cam_x_axis, cam_up) ;
        vNorm(cam_up) ;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_FASTEST) ;
        gl.glEnable(gl.GL_DEPTH_TEST) ;
        gl.glEnable(gl.GL_CULL_FACE) ;
        gl.glCullFace(gl.GL_BACK) ;

        distance = 10.0 ;
        elev = 90.0 ;
        azim = 0.0 ;

        uv_py[0] = 0.0f ;
        uv_py[1] = 1.0f ;
        uv_py[2] = 0.0f ;

        uv_ny[0] = 0.0f ;
        uv_ny[1] = -1.0f ;
        uv_ny[2] = 0.0f ;

        cam_center[0] = 0.0f ;
        cam_center[1] = 0.0f ;
        cam_center[2] = 0.0f ;

        calcUpVector() ;

        for( int i = 0 ; i < 10 ; i++ )
        {
            ObjParser objParser = new ObjParser(context) ; // obj 파일 Parser 생성
            try
            {
                objParser.parse(R.raw.planet) ; // obj 파일 parsing
            }catch( IOException e ) {
            }
            int group = objParser.getObjectIds().size() ; // 몇 개의 obj 파일이 있는지 확인
            int[] texture = new int[group] ;
            texture[0] = texture_id[i] ; // texture 파일 설정

            planet[i] = new ObjStructure(objParser, gl, this.context, texture) ; // objstructure 생성
        }

        // texture activate
        gl.glEnable(GL10.GL_TEXTURE) ;
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE) ;

        // light & material activate
        gl.glEnable(GL10.GL_LIGHTING) ;
        gl.glEnable(GL10.GL_COLOR_MATERIAL) ;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        float zNear = 0.1f ;
        float zFar = 1000f ;
        float fovy = 45.0f ;
        float aspect = (float) width / (float) height ;

        gl.glMatrixMode(GL10.GL_PROJECTION) ;
        gl.glLoadIdentity() ;

        GLU.gluPerspective(gl, fovy, aspect, zNear, zFar) ;
        gl.glViewport(0, 0, width, height) ;
    }

    public void onDrawFrame(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT) ;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f) ;
        gl.glClearDepthf(1.0f) ;

        gl.glMatrixMode(GL10.GL_MODELVIEW) ;
        gl.glLoadIdentity() ;

        calcUpVector() ;

        GLU.gluLookAt(gl, cam_eye[0], cam_eye[1], cam_eye[2], cam_center[0], cam_center[1], cam_center[2], cam_up[0], cam_up[1], cam_up[2]) ;

        if( texture_on_off )
        {
            gl.glEnable(GL10.GL_TEXTURE_2D) ;
        }else
        {
            gl.glDisable(GL10.GL_TEXTURE_2D) ;
        }

        gl.glColor4f(.3f,.3f,.3f,1.f);
        addLight(gl, SUN_LIGHT, ambient, diffuse, specular, new float[]{0.f, 0.f, 0.f, 1.f});

        gl.glPushMatrix() ;
            gl.glRotatef(angle_sun, 0.0f, 1.0f, 0.0f) ;
            // draw Sun
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, sun_ambient, sun_diffuse, sun_specular, 10.0f);
            gl.glMaterialf(GL10.GL_BACK, GL10.GL_EMISSION, 1.f);
            planet[0].setScale(scalefactor[0]) ;
            planet[0].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_mercury, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(1.0f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*0.5f, 0.0f, 1.0f, 0.0f) ;
            // draw Mercury
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, mercury_ambient, mercury_diffuse, mercury_specular, 10.0f);
            planet[1].setScale(scalefactor[1]) ;
            planet[1].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_venus, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(1.5f, 0.0f, 0.0f) ;
            gl.glRotatef(-angle_sun*0.5f, 0.0f, 1.0f, 0.0f) ;
            // draw Venus
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, venus_ambient, venus_diffuse, venus_specular, 10.0f);
            planet[2].setScale(scalefactor[2]) ;
            planet[2].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_earth, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(2.0f, 0.0f, 0.0f) ;

            gl.glPushMatrix() ;
                gl.glRotatef(angle_moon, 0.0f, 1.0f, 0.0f) ;
                gl.glTranslatef(0.2f, 0.0f, 0.0f) ;
                gl.glRotatef(angle_sun, 0.0f, 1.0f, 0.0f) ;
                // draw Moon
                gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
                initMaterial(gl, moon_ambient, moon_diffuse, moon_specular, 10.0f);
                planet[4].setScale(scalefactor[4]) ;
                planet[4].draw(gl) ;
            gl.glPopMatrix() ;

            gl.glRotatef(angle_sun*30.0f, 0.0f, 1.0f, 0.0f) ;
            // draw Earth
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, earth_ambient, earth_diffuse, earth_specular, 10.0f);
            planet[3].setScale(scalefactor[3]) ;
            planet[3].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_mars, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(2.5f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*30.0f, 0.0f, 1.0f, 0.0f) ;
            // draw Mars
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, mars_ambient, mars_diffuse, mars_specular, 10.0f);
            planet[5].setScale(scalefactor[5]) ;
            planet[5].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_jupiter, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(4.0f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*60.0f, 0.0f, 1.0f, 0.0f) ;
            // draw Jupiter
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, jupiter_ambient, jupiter_diffuse, jupiter_specular, 10.0f);
            planet[6].setScale(scalefactor[6]) ;
            planet[6].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_saturn, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(5.0f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*50.0f, 0.0f, 1.0f, 0.0f) ;
            // draw Saturn
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, saturn_ambient, saturn_diffuse, saturn_specular, 10.0f);
            planet[7].setScale(scalefactor[7]) ;
            planet[7].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_uranus, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(6.0f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*42.3f, 0.0f, 1.0f, 0.0f) ;
            // draw Uranus
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, uranus_ambient, uranus_diffuse, uranus_specular, 10.0f);
            planet[8].setScale(scalefactor[8]) ;
            planet[8].draw(gl) ;
        gl.glPopMatrix() ;

        gl.glPushMatrix() ;
            gl.glRotatef(angle_neptune, 0.0f, 1.0f, 0.0f) ;
            gl.glTranslatef(7.0f, 0.0f, 0.0f) ;
            gl.glRotatef(angle_sun*45.0f, 0.0f, 1.0f, 0.0f) ;
            // draw Neptune
            gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.f);
            initMaterial(gl, neptune_ambient, neptune_diffuse, neptune_specular, 10.0f);
            planet[9].setScale(scalefactor[9]) ;
            planet[9].draw(gl) ;
        gl.glPopMatrix() ;

        if( rot_flag )
        {
            angle_sun += rotating_sun ;
            angle_earth += orbital_earth ;

            angle_mercury += orbital_mercury ;
            angle_venus += orbital_venus ;
            angle_moon += orbital_moon ;
            angle_mars += orbital_mars ;
            angle_jupiter += orbital_jupiter ;
            angle_saturn += orbital_saturn ;
            angle_uranus += orbital_uranus ;
            angle_neptune += orbital_neptune ;

            if( angle_earth >= 360.0f )
            {
                angle_earth -= 360.0f ;
            }

            if( angle_mercury >= 360.0f )
            {
                angle_mercury -= 360.0f ;
            }

            if( angle_venus >= 360.0f )
            {
                angle_venus -= 360.0f ;
            }

            if( angle_moon >= 360.0f )
            {
                angle_moon -= 360.0f ;
            }

            if( angle_mars >= 360.0f )
            {
                angle_mars -= 360.0f ;
            }

            if( angle_jupiter >= 360.0f )
            {
                angle_jupiter -= 360.0f ;
            }

            if( angle_saturn >= 360.0f )
            {
                angle_saturn -= 360.0f ;
            }

            if( angle_uranus >= 360.0f )
            {
                angle_uranus -= 360.0f ;
            }

            if( angle_neptune >= 360.0f )
            {
                angle_neptune -= 360.0f ;
            }
        }

        gl.glFlush() ;
    }
}