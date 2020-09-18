package com.example.myminiproject ;

import android.content.Context ;
import android.database.Cursor ;
import android.database.sqlite.SQLiteDatabase ;
import android.os.Bundle ;
import android.support.v4.app.Fragment ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;

import com.google.android.gms.maps.CameraUpdateFactory ;
import com.google.android.gms.maps.GoogleMap ;
import com.google.android.gms.maps.OnMapReadyCallback ;
import com.google.android.gms.maps.SupportMapFragment ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory ;
import com.google.android.gms.maps.model.LatLng ;
import com.google.android.gms.maps.model.Marker ;
import com.google.android.gms.maps.model.MarkerOptions ;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerFragment extends Fragment implements OnMapReadyCallback
{
    GoogleMap.OnMarkerClickListener mCallback = null ;

    GoogleMap mMap ;

    String dbName = "data_information.db" ;
    int dbVersion = 1 ;
    private MySQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    String tableName = "datainfo" ;

    // SQLite를 이용하기 위해 선언

    public MarkerFragment()
    {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        return new MarkerFragment() ;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context) ;

        if( context instanceof GoogleMap.OnMarkerClickListener )
        {
            mCallback = (GoogleMap.OnMarkerClickListener) context ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_marker, container, false) ;

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map3) ;
        mapFragment.getMapAsync(this) ;

        // GoogleMap을 현재 Fragment에 실행

        return view ;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap ;

        LatLng Seoul = new LatLng(37.541, 126.986) ;

        helper = new MySQLiteOpenHelper(getActivity(), dbName, null, dbVersion) ;

        // SQLite를 사용하여 "data_information.db"에 Data 저장

        db = helper.getReadableDatabase() ;

        // Data 읽기기능을 사용할 것이므로 getReadableDatabase로 선언

        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

        // 'datainfo'의 table에 저장된 Data에 접근

        while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
        {
            float a = (float) Math.random() * 350 ;

            Double lat = c.getDouble(3) ;
            Double lng = c.getDouble(4) ;

            LatLng AddLocation = new LatLng(lat, lng) ;

            mMap.addMarker(new MarkerOptions().position(AddLocation)
                        .alpha(0.6f).icon(BitmapDescriptorFactory.defaultMarker(a))) ;
        }

        c.close() ;

        // 'datainfo' table 참조 close

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul, 3.29f)) ;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() // 만약 Map이 클릭된다면
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                if( mCallback != null )
                {
                    mCallback.onMarkerClick(marker) ;

                    // 이벤트 콜백을 만들어 부모 activity와 통신하도록 구현
                }
                return false ;
            }
        }) ;
    }
}
