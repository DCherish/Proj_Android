package com.example.mylocation ;

import android.location.Location ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.util.Log ;
import android.view.View ;
import android.widget.TextView ;

import com.google.android.gms.location.FusedLocationProviderClient ;
import com.google.android.gms.location.LocationCallback ;
import com.google.android.gms.location.LocationRequest ;
import com.google.android.gms.location.LocationResult ;
import com.google.android.gms.location.LocationServices ;
import com.google.android.gms.tasks.OnSuccessListener ;

public class LocationActivity extends AppCompatActivity
{

    FusedLocationProviderClient mFusedLocationClient ;
    TextView textView1, textView2 ;

    LocationCallback mCallback ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_location) ;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this) ;
        textView1 = findViewById(R.id.textView1) ;
        textView2 = findViewById(R.id.textView2) ;

        mCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult) ;
                for(Location location : locationResult.getLocations())
                {
                    Log.d("MyLocation", "location : " + location.getLatitude() + ", " + location.getLongitude()) ;
                    textView1.setText(Double.toString(location.getLatitude())) ;
                    textView2.setText(Double.toString(location.getLongitude())) ;
                }
            }
        } ;
    }

    public void ButtonLastLocation(View view)
    {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if( location != null )
                        {
                            Log.d("MyLocation", "location : " + location.getLatitude() + ", " + location.getLongitude()) ;
                            textView1.setText(Double.toString(location.getLatitude())) ;
                            textView2.setText(Double.toString(location.getLongitude())) ;
                        }
                    }
                }) ;
    }

    public void ButtonUpdateLocation(View view)
    {
        LocationRequest request = new LocationRequest() ;
        request.setInterval(1000) ;
        request.setFastestInterval(500) ;
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) ;

        mFusedLocationClient.requestLocationUpdates(request, mCallback, null) ;
    }

    public void ButtonStopLocation(View view)
    {
        mFusedLocationClient.removeLocationUpdates(mCallback) ;

        textView1.setText("STOP") ;
        textView2.setText("STOP") ;
    }
}