package com.example.fgh19.myalarm;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.text.DecimalFormat;

public class TurnOffActivity extends AppCompatActivity {
    static final String STATE_PLAYER = "playerState";
    static final String STATE_COUNTER_TEXT = "counterTextState";
    static final String STATE_GUIDE_TEXT = "guideTextState";
    static final String STATE_METAL_IMAGE = "metalImageState";
    static final String STATE_SHAKE_IMAGE = "shakeImageState";

    private TextView guideText;
    private TextView counterText;

    private ImageView shakeImage;
    private ImageView metalImage;
    private boolean imageStatus;

    private MediaPlayer player;
    private boolean playerStatus;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;
    private ShakeDetector mShakeDetector;
    private CompassDetector mCompassDetector;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_off);

        turnOnScreen();

        guideText = (TextView) findViewById(R.id.guideText);
        counterText = (TextView) findViewById(R.id.counterText);

        shakeImage = (ImageView) findViewById(R.id.shakeImage);
        metalImage = (ImageView) findViewById(R.id.metalImage);

        if(savedInstanceState != null) {
            playerStatus = false;
            imageStatus = false;

            guideText.setText(savedInstanceState.getString(STATE_GUIDE_TEXT));
            counterText.setText(savedInstanceState.getString(STATE_COUNTER_TEXT));

            shakeImage.setVisibility(savedInstanceState.getInt(STATE_SHAKE_IMAGE));
            metalImage.setVisibility(savedInstanceState.getInt(STATE_METAL_IMAGE));
        } else {
            playerStatus = true;
            imageStatus = true;
        }

        if (playerStatus) {
            Toast.makeText(getBaseContext(), "Alarm aktif!", Toast.LENGTH_LONG).show();
            player = MediaPlayer.create(getBaseContext(),R.raw.alarm);
            player.start();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mShakeDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mCompassDetector,mMagnetic,SensorManager.SENSOR_DELAY_UI);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                shakeUnlock(count);
            }
        });

        mCompassDetector = new CompassDetector();
        mCompassDetector.setOnCompassListener(new CompassDetector.OnCompassListener() {
            @Override
            public void onDirections(Double azimut) {
                compassUnlock(azimut);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // save media player status
        savedInstanceState.putBoolean(STATE_PLAYER,playerStatus);

        // save text status
        savedInstanceState.putString(STATE_COUNTER_TEXT,counterText.getText().toString());
        savedInstanceState.putString(STATE_GUIDE_TEXT,guideText.getText().toString());

        // save image status
        savedInstanceState.putInt(STATE_SHAKE_IMAGE,shakeImage.getVisibility());
        savedInstanceState.putInt(STATE_METAL_IMAGE,metalImage.getVisibility());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        int visibility = shakeImage.getVisibility();
        if (visibility == 0) {
            mSensorManager.registerListener(mShakeDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
        } else {
            mSensorManager.registerListener(mCompassDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mCompassDetector,mMagnetic,SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onPause() {

        int visibility = shakeImage.getVisibility();
        if (visibility == 0) {
            mSensorManager.unregisterListener(mShakeDetector);
        } else {
            mSensorManager.unregisterListener(mCompassDetector);
        }
        super.onPause();
    }

    private void turnOnScreen() {
        PowerManager.WakeLock screenlock = null;
        KeyguardManager km = null;
        if((getSystemService(POWER_SERVICE)) != null){
            screenlock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "TAG");
            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);
            keyguardLock.disableKeyguard();
            screenlock.acquire(10*60*1000L);

            screenlock.release();
        }
    }

    private void shakeUnlock(int count){
        if(count > 4){
            guideText.setText("Point the top edge of your phone to the east (90)");

            shakeImage.setVisibility(View.GONE);
            metalImage.setVisibility(View.VISIBLE);

            mSensorManager.unregisterListener(mShakeDetector);
            mSensorManager.registerListener(mCompassDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mCompassDetector,mMagnetic,SensorManager.SENSOR_DELAY_UI);

        } else counterText.setText(Integer.toString(count));
    }

    private void compassUnlock(Double azimut) {
        if((azimut >= 90) && (azimut <= 91)){
            stopAlarm();
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            counterText.setText(decimalFormat.format(azimut));
        }
    }

    private void stopAlarm() {
        player.stop();
        finish();
    }
}
