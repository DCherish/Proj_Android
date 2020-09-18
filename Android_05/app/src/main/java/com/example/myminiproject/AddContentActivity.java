package com.example.myminiproject ;

import android.Manifest ;
import android.app.AlertDialog ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.pm.PackageManager ;
import android.database.Cursor ;
import android.database.SQLException ;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteStatement ;
import android.graphics.Bitmap ;
import android.graphics.BitmapFactory ;
import android.graphics.drawable.BitmapDrawable ;
import android.graphics.drawable.Drawable ;
import android.net.Uri ;
import android.provider.MediaStore ;
import android.support.annotation.NonNull ;
import android.support.annotation.Nullable ;
import android.support.v4.app.ActivityCompat ;
import android.support.v4.app.FragmentActivity ;
import android.os.Bundle ;
import android.support.v4.content.ContextCompat ;
import android.view.View ;
import android.widget.Button ;
import android.widget.EditText ;
import android.widget.ImageButton ;
import android.widget.Toast ;

import com.google.android.gms.maps.CameraUpdateFactory ;
import com.google.android.gms.maps.GoogleMap ;
import com.google.android.gms.maps.OnMapReadyCallback ;
import com.google.android.gms.maps.SupportMapFragment ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory ;
import com.google.android.gms.maps.model.LatLng ;
import com.google.android.gms.maps.model.MarkerOptions ;

import java.io.ByteArrayOutputStream ;

public class AddContentActivity extends FragmentActivity
        implements OnMapReadyCallback
{
    String dbName = "data_information.db" ;
    int dbVersion = 1 ;
    private MySQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    String tableName = "datainfo" ;

    // SQLite를 이용하기 위해 선언

    ImageButton imageButton ;
    Button postButton ;

    Double LATITUDE ;
    Double LONGITUDE ;

    byte[] IMGVIEW ;
    private GoogleMap mMap;

    final int MY_PERMISSIONS_REQUEST_STORAGE = 5000 ;
    final int MY_PERMISSIONS_REQUEST_CAMERA = 6000 ;

    public static final int REQUEST_IMAGE_CAPTURE = 7 ;
    public static final int REQUEST_IMAGE_FROM_GALLERY = 8 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_add_content) ;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map) ;
        mapFragment.getMapAsync(this) ;

        // GoogleMap을 현재 Activity에서 실행

        helper = new MySQLiteOpenHelper(this, dbName, null, dbVersion) ;

        // SQLite를 사용하여 "data_information.db"에 Data 저장

        try
        {
            db = helper.getWritableDatabase() ;
        }catch( SQLException e )
        {
            e.printStackTrace() ;
        }

        // Data 저장기능을 사용할 것이므로 getWritableDatabase로 선언

        imageButton = (ImageButton) findViewById(R.id.imageButton) ;
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) // 만약 현재 Activity에서 이미지 버튼이 눌린다면
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddContentActivity.this) ;
                builder.setTitle("Image Setting").setMessage("어떠한 기능을 이용하여\nImage를 Setting 하시겠습니까 ?") ;

                // Dialog 알림이 이러한 제목과 내용으로 출력

                builder.setPositiveButton("카메라 열기", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) // '카메라 열기' 버튼이 눌린다면
                    {
                        if( ContextCompat.checkSelfPermission(AddContentActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(AddContentActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA) ;
                        }else
                        {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE) ;
                        }
                    }
                }) ;

                // 사용자에게 만약 권한이 없다면 권한을 요청한 후 확인되면 카메라가 실행 (만약 권한이 있다면 바로 카메라가 실행)

                builder.setNegativeButton("갤러리 열기", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) // '갤러리 열기' 버튼이 눌린다면
                    {
                        if( ContextCompat.checkSelfPermission(AddContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(AddContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE) ;
                        }else
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
                            galleryIntent.setType("image/*") ;
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY) ;
                        }
                    }
                }) ;

                // 사용자에게 만약 권한이 없다면 권한을 요청한 후 확인되면 갤러리가 실행 (만약 권한이 있다면 바로 갤러리가 실행)

                AlertDialog dialog = builder.create() ;
                dialog.show() ;

                // Dialog Build
            }
        }) ;

        postButton = (Button) findViewById(R.id.postButton) ;
        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) // 만약 하단 Post 버튼을 누른다면
            {
                String TITLE ;
                String CONTENT ;

                EditText titleText = (EditText) findViewById(R.id.editText) ;
                EditText contentText = (EditText) findViewById(R.id.editText2) ;

                TITLE = titleText.getText().toString() ;
                CONTENT = contentText.getText().toString() ;

                insert(TITLE, CONTENT, LATITUDE, LONGITUDE, IMGVIEW) ; // SQQLite를 통하여 Data 입력

                Toast.makeText(AddContentActivity.this, "Insert, Success", Toast.LENGTH_LONG).show() ; // 삽입 성공 Toast 알림 출력

                finish() ; // 현재 Activity를 닫음
            }
        }) ;
    }

    public byte[] getByteArrayFromDrawable(Drawable d)
    {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap() ;
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] data = stream.toByteArray() ;

        return data ;
    }

    // SQLite에 이미지 저장을 위하여 Data Format을 변환하는 함수 구현

    void insert(String title, String content, Double lat, Double lng, byte[] imgview)
    {
        SQLiteStatement p = db.compileStatement("insert into " + tableName +  " values(?, ?, ?, ?, ?, ?) ;") ;

        p.bindString(2, title) ;
        p.bindString(3, content) ;
        p.bindDouble(4, lat) ;
        p.bindDouble(5, lng) ;
        p.bindBlob(6, imgview) ;
        p.execute() ;
    }

    // SQLite에 Data를 입력하게 하는 함수 구현

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data) ;

        if( requestCode == REQUEST_IMAGE_CAPTURE ) // 만약 카메라가 실행되었다면
        {
            if( resultCode == RESULT_OK )
            {
                Bundle extras = data.getExtras() ;
                Bitmap imageBitmap = (Bitmap) extras.get("data") ;

                ImageButton imageButton = findViewById(R.id.imageButton) ;
                imageButton.setImageBitmap(imageBitmap) ;

                Drawable drawable = new BitmapDrawable(getResources(), imageBitmap) ;
                IMGVIEW = getByteArrayFromDrawable(drawable) ;

                // 사진을 찍은 후 그것을 이미지 버튼의 이미지로 설정
            }else
            {
                Toast.makeText(AddContentActivity.this, "Failed", Toast.LENGTH_LONG).show() ;

                // 만약 취소할 시 Toast 알림 출력
            }
        }

        if( requestCode == REQUEST_IMAGE_FROM_GALLERY ) // 만약 갤러리가 실행되었다면
        {
            if( resultCode == RESULT_OK )
            {
                try
                {
                    Uri imgUri = data.getData() ;
                    String[] filePath = { MediaStore.Images.Media.DATA } ;
                    Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null) ;
                    cursor.moveToFirst() ;
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0])) ;
                    BitmapFactory.Options options = new BitmapFactory.Options() ;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888 ;
                    options.inSampleSize = 4 ;

                    Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath, options) ;
                    imageBitmap = ExifUtils.rotateBitmap(imagePath, imageBitmap) ;

                    ImageButton imageButton = findViewById(R.id.imageButton) ;
                    imageButton.setImageBitmap(imageBitmap) ;

                    Drawable drawable = new BitmapDrawable(getResources(), imageBitmap) ;
                    IMGVIEW = getByteArrayFromDrawable(drawable) ;

                    cursor.close() ;

                    // 갤러리에서 파일을 불러올 시 Landscape(Default값)된 사진들을 회전시켜
                    // 이미지 버튼의 이미지가 회전되어 추가가 되는 것이 아닌
                    // 원하던 제 방향으로 제대로 설정되도록 회전시키는 UtilClass를 적용
                }catch( Exception e )
                {
                    e.printStackTrace() ;
                }
            }else
            {
                Toast.makeText(AddContentActivity.this, "Failed", Toast.LENGTH_LONG).show() ;

                // 만약 취소할 시 Toast 알림 출력
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) ;

        if( requestCode == MY_PERMISSIONS_REQUEST_CAMERA )
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE) ;
            }else
            {
                Toast.makeText(AddContentActivity.this, "Failed", Toast.LENGTH_LONG).show() ;
            }
        }

        // 카메라 권한을 요청할 시, 권한을 받았다면 바로 카메라가 실행
        // 카메라 권한을 요청할 시, 권한이 거정되면 Toast 알림 출력

        if( requestCode == MY_PERMISSIONS_REQUEST_STORAGE )
        {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
                galleryIntent.setType("image/*") ;
                startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY) ;
            }else
            {
                Toast.makeText(AddContentActivity.this, "Failed", Toast.LENGTH_LONG).show() ;
            }
        }

        // 갤러리(저장장치) 권한을 요청할 시, 권한을 받았다면 바로 갤러리가 실행
        // 갤러리(저장장치) 권한을 요청할 시, 권한이 거정되면 Toast 알림 출력

        return ;
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
    public void onMapReady(GoogleMap googleMap) // 만약 GoogleMap이 제대로 Loading 되었다면
    {
        mMap = googleMap ;

        LatLng Seoul = new LatLng(37.541, 126.986) ;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul, 4.17f)) ; // 서울을 기준(중앙)으로 Map이 출력

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) // 만약 Map이 클릭된다면
            {
                mMap.clear() ;

                LatLng ClickLocation = new LatLng(latLng.latitude, latLng.longitude) ;

                LATITUDE = ClickLocation.latitude ;
                LONGITUDE = ClickLocation.longitude ;

                mMap.addMarker(new MarkerOptions().position(ClickLocation).draggable(true)
                        .alpha(0.7f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))) ;

                mMap.moveCamera(CameraUpdateFactory.newLatLng(ClickLocation)) ;

                // 그곳의 위도와 경도를 받아 Marker가 표시되며 그곳을 기준(중앙)으로 Map이 출력
                // Marker는 Drag가 되도록 하는 기능을 설정
            }
        }) ;
    }
}
