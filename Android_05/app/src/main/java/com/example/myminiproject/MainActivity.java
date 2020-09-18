package com.example.myminiproject ;

import android.content.Context ;
import android.content.Intent ;
import android.content.pm.PackageInfo ;
import android.content.pm.PackageManager ;
import android.content.pm.Signature ;
import android.support.annotation.NonNull ;
import android.support.design.widget.BottomNavigationView ;
import android.support.design.widget.FloatingActionButton ;
import android.support.v4.app.Fragment ;
import android.support.v4.app.FragmentManager ;
import android.support.v4.app.FragmentTransaction ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.util.Base64 ;
import android.util.Log ;
import android.view.MenuItem ;
import android.view.View ;
import android.widget.AdapterView ;

import com.google.android.gms.maps.GoogleMap ;
import com.google.android.gms.maps.model.Marker ;

import java.security.MessageDigest ;
import java.security.NoSuchAlgorithmException ;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, GoogleMap.OnMarkerClickListener
{
    private int state = 1 ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) // 밑 하단 Tab의 어떠한 menu가 선택된다면
        {
            switch( menuItem.getItemId() )
            {
                case R.id.Menu_First_Tab : // 그 menu가 첫번째 탭이였다면
                    replaceFragment(ListFragment.newInstance()) ; // 첫번째 탭에 맞는 ListFragment를 출력
                    state = 1 ; // 현재 상태가 첫번째 탭이었음을 저장
                    return true ;
                case R.id.Menu_Second_Tab : // 그 menu가 두번째 탭이였다면
                    replaceFragment(MarkerFragment.newInstance()) ; // 두번째 탭에 맞는 MarkerFragment를 출력
                    state = 2 ; // 현재 상태가 두번째 탭이었음을 저장
                    return true ;
                case R.id.Menu_Third_Tab : // 그 menu가 세번째 탭이였다면
                    replaceFragment(LikeFragment.newInstance()) ; // 첫번째 탭에 맞는 LikeFragment를 출력
                    state = 3 ; // 현재 상태가 세번째 탭이었음을 저장
                    return true ;
            }
            return false ;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_main) ;

        /*
        try
        {
            Log.d("MapsActivity", "Key hash is " + getKeyHash(this)) ; // Log 창에 출력 KeyHash를 받아 설정
        }catch( PackageManager.NameNotFoundException ex )
        {
            // handle exception
        }
        */

        FloatingActionButton FAB = findViewById(R.id.FAB) ;

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu) ;

        FAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) // 만약 FAB버튼이 클릭된다면 (우측 하단 + 모양의 버튼)
            {
                Intent intent = new Intent(MainActivity.this, AddContentActivity.class) ;
                startActivity(intent) ;
                // AddContentActivity가 실행
            }
        }) ;

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener) ;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction() ;
        fragmentTransaction.add(R.id.fragment_container, ListFragment.newInstance()).commit() ;

        // 처음 실행 시, 기본으로 ListFragment가 출력되도록 설정함
    }

    @Override
    protected void onResume() // 만약 다른 Activity가 실행되었다 종료되어 MainActivity가 재실행되었다면
    {
        super.onResume() ;

        // 다른 Activity가 실행되기 전의 상태를 보고

        if( state == 1 ) // 첫번째 탭이 실행 중이었다면
        {
            replaceFragment(ListFragment.newInstance()) ; // ListFragment를 재실행
        }else if( state == 2 ) // 두번째 탭이 실행 중이었다면
        {
            replaceFragment(MarkerFragment.newInstance()) ; // MarkerFragment를 재실행
        }else if( state == 3 ) // 세번째 탭이 실행 중이었다면
        {
            replaceFragment(LikeFragment.newInstance()) ; // LikeFragment를 재실행
        }

        // Data가 추가되거나 삭제되었을 때 새로고침의 기능을 구현하는 부분
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit() ;
    }

    // 메인 화면의 Container 부분에 어떠한 Fragment가 뜨게 하도록 하는 함수 구현

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailContentActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    // ListFragment의 onAttach() lifecycle에서 저장하는 그 interface, OnItemClick interface 구현
    // ListFragment로 부터 필요한 데이터를 받아와 (position 값) intent의 해당 데이터(position 값)를 포함하여
    // DetailContentActivity를 실행함

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        Intent intent = new Intent(MainActivity.this, DetailContentActivity.class) ;
        intent.putExtra("latitude", marker.getPosition().latitude) ;
        intent.putExtra("longitude", marker.getPosition().longitude) ;
        startActivity(intent) ;

        return false ;
    }

    // MarkerFragment의 onAttach() lifecycle에서 저장하는 그 interface, OnMarkerClick interface 구현
    // MarkerFragment로 부터 필요한 데이터를 받아와 (해당 marker) intent의 해당 데이터(marker의 위도 및 경도 값)를 포함하여
    // DetailContentActivity를 실행함

    /*
    public static String getKeyHash(final Context context) throws PackageManager.NameNotFoundException
    {
        PackageManager pm = context.getPackageManager() ;
        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES) ;
        if( packageInfo == null ) return null ;
        for( Signature signature : packageInfo.signatures )
        {
            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA") ;
                md.update(signature.toByteArray()) ;
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP) ;
            }catch( NoSuchAlgorithmException e )
            {
                Log.w("MainActivity", "Unable to get MessageDigest. signature=" + signature, e) ;
            }
        }
        return null ;
    }

    // KaKao Open API를 사용하기 위하여 필요한 KeyHash를 받는 함수 구현
    */
}
