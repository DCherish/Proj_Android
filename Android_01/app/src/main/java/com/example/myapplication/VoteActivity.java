package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VoteActivity extends AppCompatActivity
{
    public static final String EXTRA_RESULT = "com.example.myapplication.vote.RESULT" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
    }

    public void sendYes(View view)
    {
        Intent intent = new Intent() ;
        intent.putExtra(EXTRA_RESULT, "YES") ;
        setResult(RESULT_OK, intent) ;
        finish() ;
    }

    public void sendNo(View view)
    {
        Intent intent = new Intent() ;
        intent.putExtra(EXTRA_RESULT, "No") ;
        setResult(RESULT_OK, intent) ;
        finish() ;
    }
}
