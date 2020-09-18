package com.example.myapplication3 ;

import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.view.View ;
import android.widget.AdapterView ;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener
{
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Test2Fragment fragment = (Test2Fragment)
               getSupportFragmentManager().findFragmentById(R.id.view_fragment) ;

        if( fragment != null )
        {
            fragment.setContentId(position) ;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_main) ;
    }
}

