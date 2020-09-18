package com.example.myminiproject ;

import android.app.AlertDialog ;
import android.content.DialogInterface ;
import android.content.Intent;
import android.database.Cursor ;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteStatement ;
import android.graphics.Bitmap ;
import android.graphics.BitmapFactory ;
import android.graphics.drawable.BitmapDrawable ;
import android.graphics.drawable.Drawable ;
import android.location.Address ;
import android.location.Geocoder ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.view.View ;
import android.widget.Button ;
import android.widget.ImageView ;
import android.widget.TextView ;
import android.widget.Toast ;

import com.google.android.gms.maps.CameraUpdateFactory ;
import com.google.android.gms.maps.GoogleMap ;
import com.google.android.gms.maps.OnMapReadyCallback ;
import com.google.android.gms.maps.SupportMapFragment ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory ;
import com.google.android.gms.maps.model.LatLng ;
import com.google.android.gms.maps.model.MarkerOptions ;
import com.kakao.kakaolink.v2.KakaoLinkResponse ;
import com.kakao.kakaolink.v2.KakaoLinkService ;
import com.kakao.message.template.ContentObject ;
import com.kakao.message.template.LinkObject ;
import com.kakao.message.template.LocationTemplate ;
import com.kakao.network.ErrorResult ;
import com.kakao.network.callback.ResponseCallback ;
import com.kakao.util.helper.log.Logger ;

import java.io.IOException ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Locale ;
import java.util.Map ;

public class DetailContentActivity extends AppCompatActivity
        implements OnMapReadyCallback
{
    String dbName = "data_information.db" ;
    int dbVersion = 1 ;
    private MySQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    String tableName = "datainfo" ;

    // SQLite를 이용하기 위해 선언

    private GoogleMap mMap ;

    Button shareButton ;
    Button deleteButton ;

    private String temptitle = null ;
    private String tempcontent = null ;
    private Double templat = 0.0 ;
    private Double templng = 0.0 ;
    private int temppos = 0 ;

    private int P = 0 ;
    private int Q = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_detail_content) ;

        Intent intent = getIntent() ;
        temppos = intent.getIntExtra("position", -7) ;

        // 넘어온 데이터들을 Open

        if( temppos == -7 ) // 만약 OnMarkerClick 으로 위도와 경도 값이 넘어왔다면
        {
            templat = intent.getDoubleExtra("latitude", 100) ;
            templng = intent.getDoubleExtra("longitude", 100) ;

            P = 0 ;
            Q = 1 ;

            // 그 위도와 경도 Data값을 임시적으로 저장하며
            // 밑에서 조건부 실행 시 P와 Q값을 이용하여 기능이 실행됨
        }else // 만약 OnItemClick 으로 position 값이 넘어왔다면
        {
            P = 1 ;
            Q = 0 ;

            // 그 position Data값을 임시적으로 저장하며
            // 밑에서 조건부 실행 시 P와 Q값을 이용하여 기능이 실행됨
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2) ;
        mapFragment.getMapAsync(this) ;

        // GoogleMap을 현재 Activity에서 실행

        int i = 0 ; // Initialize

        helper = new MySQLiteOpenHelper(this, dbName, null, dbVersion) ;
        db = helper.getWritableDatabase() ;

        // Data 저장기능을 사용할 것이므로 getWritableDatabase로 선언

        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

        // 'datainfo'의 table에 저장된 Data에 접근

        if( P == 1 ) // 만약 OnItemClick으로 position 값이 넘어왔다면
        {
            while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
            {
                temptitle = c.getString(1) ;
                tempcontent = c.getString(2) ;
                templat = c.getDouble(3) ;
                templng = c.getDouble(4) ;
                byte[] imgview = c.getBlob(5) ;

                if( i == temppos ) // 현재 i번째 Row, 즉 position번째 Row에 해당하면
                {
                    TextView titletext = (TextView) findViewById(R.id.textView4) ;
                    titletext.setText(temptitle) ;

                    TextView contenttext = (TextView) findViewById(R.id.textView5) ;
                    contenttext.setText(tempcontent) ;

                    Drawable drawable = new BitmapDrawable(getResources(), getDrawableFromByteArray(imgview)) ;
                    ImageView imageView = (ImageView) findViewById(R.id.imageView) ;
                    imageView.setImageDrawable(drawable) ;

                    // position번째의 Row의 Data값으로 TitleTextView, ContentTextView, ... 등을 설정

                    break ; // 그 후 Loop 탈출
                }else i = i + 1 ; // i를  Increase
            }
        }else if( Q == 1 ) // 만약 OnMarkerClick으로 위도와 경도 값이 넘어왔다면
        {
            while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
            {
                temptitle = c.getString(1) ;
                tempcontent = c.getString(2) ;
                byte[] imgview = c.getBlob(5) ;

                if( c.getDouble(3) == templat && c.getDouble(4) == templng ) // 넘어온 위도 값과 경도 값과 일치하는 Row 검색
                {
                    TextView titletext = (TextView) findViewById(R.id.textView4) ;
                    titletext.setText(temptitle) ;

                    TextView contenttext = (TextView) findViewById(R.id.textView5) ;
                    contenttext.setText(tempcontent) ;

                    Drawable drawable = new BitmapDrawable(getResources(), getDrawableFromByteArray(imgview)) ;
                    ImageView imageView = (ImageView) findViewById(R.id.imageView) ;
                    imageView.setImageDrawable(drawable) ;

                    // 조건에 해당하는 Row의 Data값으로 TitleTextView, ContentTextView, ... 등을 설정

                    break ; // 그 후 Loop 탈출
                }
            }
        }

        shareButton = (Button) findViewById(R.id.button3) ;
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) // 만약 'Share' 버튼이 눌린다면
            {
                AlertDialog.Builder Sharebuilder = new AlertDialog.Builder(DetailContentActivity.this) ;
                Sharebuilder.setTitle("데이터 공유").setMessage("KaKaoLink를 이용하여 공유하시겠습니까 ?") ;

                // Dialog 알림이 이러한 제목과 내용으로 출력

                Sharebuilder.setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) // '확인' 버튼이 눌린다면
                    {
                        String getAddr = getAddress(templat, templng) ; // Address를 받아와

                        LocationTemplate params = LocationTemplate.newBuilder(getAddr,
                                // 카카오링크가 실행되어 홈페이지(카카오맵) 상에 Address로 검색하며
                                ContentObject.newBuilder("공유된 위치",
                                        // 카카오톡에서의 보일 제목을 '공유된 위치'로 설정
                                        "https://tistory2.daumcdn.net/tistory/2247780/skin/images/rectangle.jpg",
                                        LinkObject.newBuilder()
                                                .setWebUrl("https://developers.kakao.com")
                                                .setMobileWebUrl("https://developers.kakao.com")
                                                .build())
                                        .setDescrption(getAddr + "입니다. :)").build())
                                // 카카오톡에서의 부제목을 설정
                                .setAddressTitle(getAddr)
                                // 위치 보기에서의 제목을 설정
                                .build() ;

                        Map<String, String> serverCallbackArgs = new HashMap<String, String>() ;

                        serverCallbackArgs.put("user_id", "${current_user_id}") ;
                        serverCallbackArgs.put("product_id", "${shared_product_id}") ;

                        KakaoLinkService.getInstance().sendDefault(DetailContentActivity.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>()
                        {
                            @Override
                            public void onFailure(ErrorResult errorResult)
                            {
                                Logger.e(errorResult.toString()) ;
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) // 정상적으로 기능이 실행되었다면
                            {
                                Toast.makeText(DetailContentActivity.this, "Share, Success", Toast.LENGTH_LONG).show() ; // 공유 성공 Toast 알림 출력
                            }
                        }) ;
                    }
                }) ;

                // 카카오링크 위치템플릿 사용하여 설정

                Sharebuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() // '취소' 버튼이 눌린다면
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }) ; // 단순하게 Dialog 알림이 꺼지며 변화 X

                AlertDialog dialog = Sharebuilder.create() ;
                dialog.show() ;

                // Dialog Build
            }
        }) ;

        deleteButton = (Button) findViewById(R.id.button4) ;
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) // 만약 'Delete' 버튼이 눌린다면
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailContentActivity.this) ;
                builder.setTitle("데이터 삭제").setMessage("정말로 삭제하시겠습니까 ?") ;

                // Dialog 알림이 이러한 제목과 내용으로 출력

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() // '확인' 버튼이 눌린다면
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int i = 0 ; // Initialize

                        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

                        // 'datainfo'의 table에 저장된 Data에 접근

                        while( c.moveToNext() ) // 처음 Row부터 마지막 Row까지 Data를 확인
                        {
                            if( c.getDouble(3) == templat && c.getDouble(4) == templng ) // 해당하는 item의 위도 값과 경도 값으로 일치하는 Row 검색
                            {
                                switchallstate(i) ; // 스위치 상태들을 유지
                                break ;
                            }
                            i = i + 1 ;
                        }

                        delete(templat, templng) ; // SQQLite를 통하여 Data 삭제

                        Toast.makeText(DetailContentActivity.this, "Delete, Success", Toast.LENGTH_LONG).show() ; // 삭제 성공 Toast 알림 출력

                        finish() ; // 현재 Activity를 닫음
                    }
                }) ;

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() // '취소' 버튼이 눌린다면
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }); // 단순하게 Dialog 알림이 꺼지며 변화 X

                AlertDialog dialog = builder.create() ;
                dialog.show() ;

                // Dialog Build
            }
        }) ;

        c.close() ;

        // 'datainfo' table 참조 close
    }

    @Override
    public void onMapReady(GoogleMap googleMap) // 만약 GoogleMap이 제대로 Loading 되었다면
    {
        mMap = googleMap ;

        LatLng DetailLocation = new LatLng(templat, templng) ;
        mMap.addMarker(new MarkerOptions().position(DetailLocation)
                .alpha(0.516f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))) ;

        // 해당하는 좌표에 Marker가 추가

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DetailLocation, 09.12f)) ;

        // 해당하는 위도와 경도로 Marker가 표시되며 그곳을 기준(중앙)으로 Map이 출력
    }

    public String getAddress(Double Latitude, Double Longitude)
    {
        String address = null ;

        Geocoder geocoder = new Geocoder(DetailContentActivity.this, Locale.getDefault()) ;

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
            Toast.makeText(DetailContentActivity.this, "Failed on getting Address", Toast.LENGTH_LONG).show() ;
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

    private void delete(Double latitude, Double longitude)
    {
        SQLiteStatement p = db.compileStatement("delete from datainfo where lat = ? AND lng = ? ;") ;

        p.bindDouble(1, latitude) ;
        p.bindDouble(2, longitude) ;
        p.execute() ;
    }

    // SQLite에 Data를 삭제하게 하는 함수 구현

    public Bitmap getDrawableFromByteArray(byte[] b)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length) ;
        return bitmap ;
    }

    // SQLite에 저장되어 있던 이미지를 imageView로 설정하기 위하여 Data Format을 변환하는 함수 구현

    public void switchallstate(int i)
    {
        db = helper.getWritableDatabase() ;

        // Data 저장기능을 사용할 것이므로 getWritableDatabase로 선언

        Cursor c = db.query(tableName, null, null, null, null, null, null) ;

        // 'datainfo'의 table에 저장된 Data에 접근

        for( int a = i ; a < c.getCount() ; a = a + 1 )
        {
            int p = a + 1 ;
            int k = SharedPreferenceUtil.getSharedPreference(DetailContentActivity.this, "swstate"+p) ;
            SharedPreferenceUtil.putSharedPreference(DetailContentActivity.this, "swstate"+a, k) ;
        }

        SharedPreferenceUtil.putSharedPreference(DetailContentActivity.this, "swstate"+c.getCount(), 0) ;

        c.close() ;

        // 'datainfo' table 참조 close
    }

    // 해당 항목들의 스위치들의 상태를 유지
}