package com.example.mycamera ;

import android.graphics.Bitmap ;
import android.graphics.BitmapFactory ;
import android.hardware.Camera ;
import android.os.Environment ;
import android.support.constraint.ConstraintLayout ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.util.Log ;
import android.view.View ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.ImageView ;
import android.widget.ListView ;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Date ;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ;

public class CameraActivity extends AppCompatActivity
{
    Camera mCamera ;
    CameraPreview mPreview ;
    File mediaStorageDir ;
    ArrayAdapter<String> adapter ;
    ArrayList<String> list ;
    String filename ;

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            mCamera.stopPreview(); ;
            File file = getOutputMediaFile(MEDIA_TYPE_IMAGE) ;

            try
            {
                FileOutputStream fos = new FileOutputStream(file) ;
                fos.write(data) ;
                fos.close() ;

                list.add(filename) ;
                adapter.notifyDataSetChanged() ;
            }catch( IOException e )
            {
                Log.d("MainActivity", e.getMessage()) ;
            }
            mCamera.startPreview() ;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_camera) ;

        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyCamera") ;

        String[] pictureList = new File(mediaStorageDir.getPath() + File.separator).list() ;

        ListView listView = (ListView) findViewById(R.id.listView) ;

        list = new ArrayList<>() ;

        try
        {
            for( int i = 0 ; i < pictureList.length ; i++ )
            {
                list.add(pictureList[i]) ;
            }
        }catch( Exception e )
        {
            Log.d("error", e.getMessage()) ;
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) ;

        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                String selectItem = (String)adapterView.getItemAtPosition(position) ;

                File select = new File(mediaStorageDir.getPath() + File.separator + selectItem) ;

                Bitmap MyBit = BitmapFactory.decodeFile(select.getPath()) ;

                ImageView imageView = (ImageView) findViewById(R.id.imageView) ;
                imageView.setImageBitmap(MyBit) ;
            }
        }) ;
    }

    @Override
    protected void onResume()
    {
        super.onResume() ;

        ConstraintLayout camView = findViewById(R.id.camView) ;

        if( safeCameraOpen(0) )
        {
            mPreview = new CameraPreview(this, mCamera) ;
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(600, 600) ;

            camView.addView(mPreview, params) ;
        }
    }

    private boolean safeCameraOpen(int id)
    {
        boolean qOpen = false ;
        releaseCameraAndPreview() ;
        mCamera = Camera.open(id) ;
        qOpen = (mCamera != null) ;

        return qOpen ;
    }

    private  void releaseCameraAndPreview()
    {
        if( mPreview != null )
        {
            mPreview.setCamera(null) ;
        }

        if( mCamera != null )
        {
            mCamera.release() ;
            mCamera = null ;
        }
    }

    public void CameraButton(View view)
    {
        mCamera.takePicture(null, null, mPictureCallback) ;
    }

    private File getOutputMediaFile(int type)
    {
        if( ! mediaStorageDir.exists() )
        {
            if( ! mediaStorageDir.mkdirs() )
            {
                Log.d("CameraApp", "failed to create directory") ;
                return null ;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) ;

        File imageFile ;
        if( type == MEDIA_TYPE_IMAGE )
        {
            imageFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg") ;
        }else{
            return null ;
        }

        filename = "IMG_" + timeStamp + ".jpg" ;
        return imageFile ;
    }
}
