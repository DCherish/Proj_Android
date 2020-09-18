package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity
{
    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE" ;
    public static final int REQUEST_IMAGE_CAPTURE = 2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_main) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == REQUEST_VOTE )
        {
            if( resultCode == RESULT_OK )
            {
                String result = data.getStringExtra(VoteActivity.EXTRA_RESULT) ;
                TextView textView = findViewById(R.id.textView2) ;
                textView.setText(result) ;
            }else // RESULT_CANCEL
            {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show() ;
            }
        }

        if( requestCode == REQUEST_IMAGE_CAPTURE )
        {
            if( resultCode == RESULT_OK )
            {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(imageBitmap);

                String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/Share.png" ;
                File file = new File(path) ;

                FileOutputStream out ;

                try
                {
                    out = new FileOutputStream(file) ;
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out) ;
                    out.flush() ;
                    out.close() ;
                }catch( Exception e )
                {
                    e.printStackTrace();
                }
            }else
            {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show() ;
            }
        }
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view)
    {
        Intent intent = new Intent(this, DisplayMessageActivity.class) ;
        EditText editText = findViewById(R.id.editText) ;
        String message = editText.getText().toString() ;
        intent.putExtra(EXTRA_MESSAGE, message) ;
        startActivity(intent) ;
    }

    int REQUEST_VOTE = 1000 ;

    public void startVote(View view)
    {
        Intent intent = new Intent(this, VoteActivity.class) ;
        startActivityForResult(intent, REQUEST_VOTE) ;
    }

    public void shareMsgNPicWithIntent(View view)
    {
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/Share.png" ;
        File file = new File(path) ;

        Uri bmpUri = FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication.fileprovider", file) ;
        Intent sendIntent = new Intent() ;
        sendIntent.setAction(Intent.ACTION_SEND) ;
        EditText editText = findViewById(R.id.editText) ;
        String textMessage = editText.getText().toString() ;
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage) ;
        sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri) ;
        sendIntent.setType("image/*") ;

        String title = "Share this text and picture with" ;
        Intent chooser = Intent.createChooser(sendIntent, title) ;

        if( sendIntent.resolveActivity(getPackageManager()) != null )
        {
            startActivity(chooser) ;
        }
    }

    public void takePicture(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
        if( takePictureIntent.resolveActivity(getPackageManager()) != null )
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE) ;
        }
    }
}