package com.example.mysensor ;

import android.content.Context ;
import android.hardware.Sensor ;
import android.hardware.SensorEvent ;
import android.hardware.SensorEventListener ;
import android.hardware.SensorManager ;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle ;
import android.view.View ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.ListView ;
import android.widget.TextView ;

import java.util.ArrayList ;
import java.util.List ;

public class SensorActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager mSensorManager ;
    private Sensor mSensor1 ;
    private Sensor mSensor2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_sensor) ;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE) ;

        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL) ;

        if( mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null )
        {
            mSensor1 = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ;
        }

        if( mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null )
        {
            mSensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ;
        }

        ListView listView = (ListView) findViewById(R.id.listView) ;

        ArrayList<String> arrayList = new ArrayList<>() ;

        for( int i = 0 ; i < deviceSensors.size() ; i++ )
        {
            arrayList.add(deviceSensors.get(i).getName()) ;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList) ;

        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            }
        }) ;
    }

    @Override
    protected void onResume()
    {
        super.onResume() ;
        mSensorManager.registerListener(this, mSensor1, SensorManager.SENSOR_DELAY_NORMAL) ;
        mSensorManager.registerListener(this, mSensor2, SensorManager.SENSOR_DELAY_NORMAL) ;
    }

    @Override
    protected void onPause()
    {
        super.onPause() ;
        mSensorManager.unregisterListener(this) ;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if( sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
        {
            TextView textView = findViewById(R.id.textView1) ;
            textView.setText(String.valueOf(sensorEvent.values[0])) ;
            textView = findViewById(R.id.textView2) ;
            textView.setText(String.valueOf(sensorEvent.values[1])) ;
            textView = findViewById(R.id.textView3) ;
            textView.setText(String.valueOf(sensorEvent.values[2])) ;
        }

        if( sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD )
        {
            TextView textView = findViewById(R.id.textView4) ;
            textView.setText(String.valueOf(sensorEvent.values[0])) ;
            textView = findViewById(R.id.textView5) ;
            textView.setText(String.valueOf(sensorEvent.values[1])) ;
            textView = findViewById(R.id.textView6) ;
            textView.setText(String.valueOf(sensorEvent.values[2])) ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Do something here if sensor accuracy changes.
    }
}