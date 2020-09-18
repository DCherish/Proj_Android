package com.example.mylocation ;

import android.Manifest ;
import android.content.Intent ;
import android.content.pm.PackageManager ;
import android.support.annotation.NonNull ;
import android.support.v4.app.ActivityCompat ;
import android.support.v4.content.ContextCompat ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.view.View ;
import android.widget.Button ;
import android.widget.Toast ;

public class MainActivity extends AppCompatActivity
{
    Button Button1 ;

    Intent GPSintent ;

    final int MY_PERMISSIONS_REQUEST_GPS = 3000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_main) ;

        Toast.makeText(getApplicationContext(), "원활한 앱 실행을 위해\n위치 권한을 허용해주세요.", Toast.LENGTH_LONG).show() ;

        Button1 = (Button) findViewById(R.id.Button1) ;
        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPSintent = new Intent(MainActivity.this, LocationActivity.class) ;

                if( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS) ;
                }else
                {
                    startActivity(GPSintent) ;
                }
            }
        }) ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch( requestCode )
        {
            case MY_PERMISSIONS_REQUEST_GPS :
            {
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    startActivity(GPSintent) ;
                }else{
                }
            }
            return ;
        }
    }
}
