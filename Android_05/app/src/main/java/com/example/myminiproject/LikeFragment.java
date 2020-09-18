package com.example.myminiproject ;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeFragment extends Fragment
{
    String dbName = "data_information.db" ;
    int dbVersion = 1 ;
    private MySQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    String tableName = "datainfo" ;

    // SQLite를 이용하기 위해 선언

    public LikeFragment()
    {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        return new LikeFragment() ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like, container, false) ;

        ListView listView ;
        LikeListViewAdapter adapter ;
        adapter = new LikeListViewAdapter() ;
        listView = (ListView) view.findViewById(R.id.listview2) ;

        helper = new MySQLiteOpenHelper(getActivity(), dbName, null, dbVersion) ;

        // SQLite를 사용하여 "data_information.db"에 Data 저장

        db = helper.getReadableDatabase() ;

        // Data 읽기기능을 사용할 것이므로 getReadableDatabase로 선언

        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

        // 'datainfo'의 table에 저장된 Data에 접근

        int i = 0 ;

        while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
        {
            Double latitude = c.getDouble(3) ;
            Double longitude = c.getDouble(4) ;

            Integer swState = SharedPreferenceUtil.getSharedPreference(getActivity(), "swstate"+String.valueOf(i)) ;
            // SharedPreference(또 다른 Database 저장방식)를 이용하여
            // 각각의 Switch의 상태(On / Off)를 저장 혹은 읽어올 수 있음

            // 이 부분은 Switch의 상태가 어떠했는지를 읽어와

            i = i + 1 ;

            adapter.addlikeItem(latitude, longitude, getAddress(latitude, longitude), swState) ;

            // 각 행의 Data를 각 List에 추가하여 설정
            // 이때, 다른 값들과 함께 Switch의 상태 또한 같이 설정
        }

        c.close() ;

        // 'datainfo' table 참조 close

        listView.setAdapter(adapter) ;
        adapter.notifyDataSetChanged() ;

        // listview 갱신

        return view ;
    }

    public String getAddress(Double Latitude, Double Longitude)
    {
        String address = null ;

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault()) ;

        List<Address> list = null ;

        try
        {
            list = geocoder.getFromLocation(Latitude, Longitude, 1) ;
        }catch( IOException e )
        {
            e.printStackTrace() ;
        }

        if( list == null )
        {
            Toast.makeText(getActivity(), "Failed on getting Address", Toast.LENGTH_LONG).show() ;
            return null ;
        }

        if( list.size() > 0 )
        {
            Address addr = list.get(0) ;

            if( String.valueOf(addr.getCountryName()) != "null" )
            {
                address = addr.getCountryName() + " " ;
            }

            if( String.valueOf(addr.getAdminArea()) != "null" )
            {
                address = address + addr.getAdminArea() + " " ;
            }

            if( String.valueOf(addr.getLocality()) != "null" )
            {
                address = address + addr.getLocality() + " " ;
            }

            if( String.valueOf(addr.getThoroughfare()) != "null" )
            {
                address = address + addr.getThoroughfare() + " " ;
            }

            if( String.valueOf(addr.getFeatureName()) != "null" )
            {
                address = address + addr.getFeatureName() + " " ;
            }
        }

        return address ;
    }

    // Geocoder를 이용하여 마커 위치의 위도, 경도를 주소로 변환하는 함수 구현
}
