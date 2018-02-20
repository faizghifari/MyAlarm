package com.example.fgh19.myalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TurnOffActivity extends AppCompatActivity {
    private TextView guideText;
    private TextView counterText;

    private ImageView shakeImage;
    private ImageView metalImage;

    private MediaPlayer player;

    private Button buttonStopAlarm;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_off);

        guideText = (TextView) findViewById(R.id.guideText);
        counterText = (TextView) findViewById(R.id.counterText);

        shakeImage = (ImageView) findViewById(R.id.shakeImage);
        metalImage = (ImageView) findViewById(R.id.metalImage);

        buttonStopAlarm = (Button) findViewById(R.id.stopAlarmButton);
        buttonStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });

        Toast.makeText(getBaseContext(), "Alarm aktif!", Toast.LENGTH_LONG).show();
        player = MediaPlayer.create(getBaseContext(),R.raw.alarm);
        player.start();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mShakeDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                shakeUnlock(count);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void shakeUnlock(int count){
        if(count > 4){
            guideText.setText("Put your phone into a guided directions");

            shakeImage.setVisibility(View.GONE);
            metalImage.setVisibility(View.VISIBLE);
            counterText.setVisibility(View.GONE);
        } else {
            counterText.setText(Integer.toString(count));
        }
    }


    private void stopAlarm() {
        player.stop();
        finish();
    }
}
