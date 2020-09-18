package com.example.myopencv;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.HashMap;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";

    private JavaCameraView mOpenCvCameraView;
    private Augmenting3D glRenderer;

    /* TODO: Fill up the camera intrinsic parameters and used calibration image's width, height */
    /* Focal length */
    private float fx = 3366.38482f ;
    private float fy = 3362.44789f ;

    /* Principal point */
    private float cx = 2074.85418f ;
    private float cy = 1647.32061f ;

    /* Radial distortion */
    private float k1 = 0.13215f ;
    private float k2 = -0.33991f ;

    /* Tangential distortion */
    private float p1 = 0.00142f ;
    private float p2 = 0.00304f ;

    /* Calibration image's width, height */
    private float calib_width = 4128.0f ;
    private float calib_height = 3096.0f ;

    /* Chessboard's informations */
    private static int chess_col_points = 8;
    private static int chess_row_points = 6;
    private static float square_size = 0.028f;
    private Size board_size = new Size(chess_col_points, chess_row_points);

    /* Variables used in PnP algorithms */
    private Vector<Point3> obj_points = new Vector<>();
    private MatOfPoint2f img_points = new MatOfPoint2f();
    private MatOfPoint3f obj_points_PnP = new MatOfPoint3f();
    private boolean patt_found = false;

    /* Matrices & array for save pose*/
    private Mat cam_mat = new Mat(3, 3, CvType.CV_64FC1, Scalar.all(0));    // for intrinsic parameter
    private Mat mv_mat = new Mat(4, 4, CvType.CV_64FC1, Scalar.all(0));    // for extrinsic parameter & openGL pose
    private Mat dist_coeffs = new Mat(1, 4, CvType.CV_64FC1);
    private MatOfDouble rot = new MatOfDouble();
    private MatOfDouble rot_rod = new MatOfDouble();
    private MatOfDouble trans = new MatOfDouble();
    private MatOfDouble dist_coeffs_double;
    private float[] pose = new float[16];

    /* Check for resetting matrices */
    private boolean reset = true;

    /* Checking OpenCV */
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("native-lib");
        }
    }

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

        /* Setting camera view */
        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        /* Setting OpenGL renderer */
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        addContentView(glSurfaceView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        glRenderer = new Augmenting3D(this, fx, fy, calib_width, calib_height);
        glSurfaceView.setRenderer(glRenderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();
        reset = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    public void resetVariables() {
        Log.i(TAG, "Scale factor (width) : " + glRenderer.scale_width);
        Log.i(TAG, "Scale factor (height) : " + glRenderer.scale_height);

        /* Scaling because of the resolution difference between
           used calibration image and viewing camera resolution */
        fx /= glRenderer.scale_width;
        fy /= glRenderer.scale_height;
        cx /= glRenderer.scale_width;
        cy /= glRenderer.scale_height;

        /* TODO: Complete the camera intrinsic matrix */
        /* Camera intrinsic matrix */
        cam_mat.put(0, 0, fx);
        cam_mat.put(1, 1, fy);
        cam_mat.put(0, 2, cx);
        cam_mat.put(1, 2, cy);
        cam_mat.put(2, 2, 1);

        /* Distortion coefficients */
        dist_coeffs.put(0, 0, k1);
        dist_coeffs.put(0, 1, k2);
        dist_coeffs.put(0, 2, p1);
        dist_coeffs.put(0, 3, p2);

        dist_coeffs_double = new MatOfDouble(dist_coeffs);

        Log.i(TAG, "Camera matrix : " + cam_mat.dump());
        Log.i(TAG, "Distortion matrix : " + dist_coeffs_double.dump());

        /* Save chessboard's points */
        for (int i = 0; i < board_size.height; i++) {
            for (int j = 0; j < board_size.width; j++) {
                obj_points.add(new Point3((j * square_size), (i * square_size), 0));
            }
        }

        obj_points_PnP.fromList(obj_points);

        /* Reset is done */
        reset = false;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (reset) {
            resetVariables();
        }

        /* Find chessboard pattern */
        patt_found = Calib3d.findChessboardCorners(inputFrame.rgba(), board_size, img_points, Calib3d.CALIB_CB_FAST_CHECK);

        /* Chessboard found */
        if (patt_found) {
            Mat output = inputFrame.rgba();

            /* Draw chessboard's corner points */
            Calib3d.drawChessboardCorners(output, board_size, img_points, patt_found);

            /* PnP algorithms */
            // Find the target's Euler angles and XYZ coordinates.
            Calib3d.solvePnP(obj_points_PnP, img_points, cam_mat, dist_coeffs_double, rot, trans);
            //Calib3d.solvePnPRansac(obj_points_PnP,img_points,cam_mat,dist_coeffs_double,rot,trans);

            Log.i(TAG, "Rotation matrix : " + rot.dump());
            Log.i(TAG, "Translation matrix : " + trans.dump());

            rot.put(0,0,rot.get(0, 0)[0] * -1);    // negate x angle => * cos(180')
            trans.put(1, 0, trans.get(1, 0)[0] * -1);  // negate y axis
            trans.put(2, 0, trans.get(2, 0)[0] * -1); // negate z axis

            /* Convert Euler angles to Rodrigues angles */
            Calib3d.Rodrigues(rot, rot_rod);

            /* TODO: Complete the camera extrinsic matrix */
            /* Camera extrinsic matrix */
            mv_mat.put(0, 0, rot_rod.get(0, 0)[0]);
            mv_mat.put(0, 1, rot_rod.get(0, 1)[0]);
            mv_mat.put(0, 2, rot_rod.get(0, 2)[0]);
            mv_mat.put(0, 3, trans.get(0, 0)[0]);

            mv_mat.put(1, 0, rot_rod.get(1, 0)[0]);
            mv_mat.put(1, 1, rot_rod.get(1, 1)[0]);
            mv_mat.put(1, 2, rot_rod.get(1, 2)[0]);
            mv_mat.put(1, 3, trans.get(1, 0)[0]);

            mv_mat.put(2, 0, rot_rod.get(2, 0)[0]);
            mv_mat.put(2, 1, rot_rod.get(2, 1)[0]);
            mv_mat.put(2, 2, rot_rod.get(2, 2)[0]);
            mv_mat.put(2, 3, trans.get(2, 0)[0]);

            mv_mat.put(3, 3, 1);

            // make transposed matrix
            for(int i=0; i<3; i++){
                double tmp = mv_mat.get(i, 3)[0];
                mv_mat.put(i, 3, mv_mat.get(3, i)[0]);
                mv_mat.put(3, i, tmp);
            }

            /* Save extrinsic parameters into array */
            for(int row=0; row<4; row++){
                for(int col=0; col<4; col++){
                    pose[row * 4 + col] = (float)mv_mat.get(row, col)[0];
                }
            }

            /* Send to OpenGL */
            glRenderer.pose = pose;

            /* Render cube */
            glRenderer.dispOBJ = true;

            return output;
        }
        /* Chessboard not found */
        else {
            glRenderer.dispOBJ = false;
            return inputFrame.rgba();
        }
    }
}
