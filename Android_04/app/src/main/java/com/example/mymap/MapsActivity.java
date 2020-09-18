package com.example.mymap ;

import android.app.AlertDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.pm.PackageInfo ;
import android.content.pm.PackageManager ;
import android.content.pm.Signature ;
import android.support.v4.app.FragmentActivity ;
import android.os.Bundle ;
import android.util.Base64 ;
import android.util.Log ;
import android.view.View ;
import android.widget.AdapterView ;
import android.widget.EditText ;
import android.widget.LinearLayout ;
import android.widget.ListView ;
import android.widget.SimpleAdapter ;
import android.widget.TextView ;

import com.google.android.gms.maps.CameraUpdateFactory ;
import com.google.android.gms.maps.GoogleMap ;
import com.google.android.gms.maps.OnMapReadyCallback ;
import com.google.android.gms.maps.SupportMapFragment ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory ;
import com.google.android.gms.maps.model.LatLng ;
import com.google.android.gms.maps.model.Marker ;
import com.google.android.gms.maps.model.MarkerOptions ;
import com.kakao.kakaolink.v2.KakaoLinkResponse ;
import com.kakao.kakaolink.v2.KakaoLinkService ;
import com.kakao.message.template.ContentObject ;
import com.kakao.message.template.FeedTemplate ;
import com.kakao.message.template.LinkObject ;
import com.kakao.network.ErrorResult ;
import com.kakao.network.callback.ResponseCallback ;
import com.kakao.util.helper.log.Logger ;

import java.security.MessageDigest ;
import java.security.NoSuchAlgorithmException ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.Map ;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap mMap ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_maps) ;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map) ;
        mapFragment.getMapAsync(this) ;
        try
        {
            Log.d("MapsActivity", "Key hash is " + getKeyHash(this)) ;
        } catch (PackageManager.NameNotFoundException ex)
        {
            // handle exception
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap ;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID) ;

        final ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>() ;

        final ArrayList<Double> getlatitude = new ArrayList<Double>() ;
        final ArrayList<Double> getlongitude = new ArrayList<Double>() ;

        final SimpleAdapter simpleadapter
                = new SimpleAdapter(MapsActivity.this, items,
                android.R.layout.simple_list_item_2,
                new String[]{"Title", "Snippet"},
                new int[]{android.R.id.text1, android.R.id.text2}) ;

        final ListView listView = (ListView) findViewById(R.id.listView) ;
        listView.setAdapter(simpleadapter) ;

        // Add a marker in Sydney and move the camera

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(final LatLng latLng)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this) ;

                LinearLayout layout = new LinearLayout(MapsActivity.this) ;
                layout.setOrientation(LinearLayout.VERTICAL); ;

                final TextView Title = new TextView(MapsActivity.this) ;
                Title.setText("Title") ;
                layout.addView(Title) ;

                final EditText EditT = new EditText(MapsActivity.this) ;
                layout.addView(EditT) ;

                final TextView Snippet = new TextView(MapsActivity.this) ;
                Snippet.setText("\nSnippet") ;
                layout.addView(Snippet) ;

                final EditText EditS = new EditText(MapsActivity.this) ;
                layout.addView(EditS) ;

                builder.setView(layout) ;

                builder.setPositiveButton("추가", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        final HashMap<String, String> Data = new HashMap<>() ;

                        LatLng ClickLocation = new LatLng(latLng.latitude, latLng.longitude) ;
                        mMap.addMarker(new MarkerOptions().position(ClickLocation)
                                .title(EditT.getText().toString()).snippet(EditS.getText().toString())
                                .alpha(0.7f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))) ;

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ClickLocation)) ;

                        Data.put("Title", EditT.getText().toString()) ;
                        Data.put("Snippet", EditS.getText().toString()) ;
                        items.add(Data) ;

                        getlatitude.add(latLng.latitude) ;
                        getlongitude.add(latLng.longitude) ;

                        simpleadapter.notifyDataSetChanged() ;
                    }
                }) ;

                AlertDialog dialog = builder.create() ;
                dialog.show() ;
            }
        }) ;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                final AlertDialog.Builder Sharebuilder = new AlertDialog.Builder(MapsActivity.this) ;
                Sharebuilder.setTitle("Title & Snippet 공유").setMessage("KaKaoLink를 이용하여\n공유하시겠습니까 ?") ;

                Sharebuilder.setPositiveButton("공유", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        FeedTemplate.Builder fbuilder = FeedTemplate
                                .newBuilder(ContentObject.newBuilder("Title & Snippet 공유\n",
                                        "https://cdn.pixabay.com/photo/2016/11/04/14/13/google-maps-1797882_960_720.png",
                                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com/").setMobileWebUrl("https://developers.kakao.com/").build())
                                        .setDescrption("Title : " + marker.getTitle() + "\n" + "Snippet : " + marker.getSnippet()).build()) ;

                        FeedTemplate params = fbuilder.build() ;

                        Map<String, String> serverCallbackArgs = new HashMap<String, String>() ;
                        serverCallbackArgs.put("user_id", "${current_user_id}") ;
                        serverCallbackArgs.put("product_id", "${shared_product_id}") ;

                        KakaoLinkService.getInstance().sendDefault(MapsActivity.this, params,
                                serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>()
                        {
                            @Override
                            public void onFailure(ErrorResult errorResult)
                            {
                                Logger.e(errorResult.toString()) ;
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) {
                            }
                        }) ;
                    }
                }) ;

                Sharebuilder.setNegativeButton("취소", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do something
                    }
                }) ;

                AlertDialog dialog = Sharebuilder.create() ;
                dialog.show() ;

                return false ;
            }
        }) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                LatLng ThisLocation = new LatLng(getlatitude.get(position), getlongitude.get(position)) ;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ThisLocation)) ;
            }
        }) ;
    }

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
            } catch (NoSuchAlgorithmException e)
            {
                Log.w("MapsActivity", "Unable to get MessageDigest. signature=" + signature, e) ;
            }
        }
        return null ;
    }
}