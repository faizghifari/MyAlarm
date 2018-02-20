package com.example.fgh19.myalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TimePicker myTimePicker;
    Button buttonstartSetDialog;
    Button buttonCancelAlarm;
    Button buttonStopAlarm;
    TextView textAlarmPrompt;

    TimePickerDialog timePickerDialog;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    final static int RQS_1 = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAlarmPrompt = (TextView) findViewById(R.id.alarmPromptText);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        buttonstartSetDialog = (Button) findViewById(R.id.setAlarmButton);
        buttonstartSetDialog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textAlarmPrompt.setText("Kepencet");
                openTimePickerDialog(false);

            }
        });

        buttonCancelAlarm = (Button) findViewById(R.id.cancelAlarmButton);
        buttonCancelAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

        buttonStopAlarm = (Button) findViewById(R.id.stopAlarmButton);
        buttonStopAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });

    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(MainActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

    OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
                Log.i("hasil", " =<0");
            } else if (calSet.compareTo(calNow) > 0) {
                Log.i("hasil", " > 0");
            } else {
                Log.i("hasil", " else ");
            }

            setAlarm(calSet);
        }
    };

    private void setAlarm(Calendar targetCal) {

        textAlarmPrompt.setText("Alarm set successfully");

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("Sign", 1);
        pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }

    private void cancelAlarm() {
        if (alarmManager!= null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void stopAlarm() {
        Intent intent = new Intent("stopAlarm");
        intent.putExtra("Sign", 0);

        sendBroadcast(intent);
//        Calendar calendarNow = Calendar.getInstance();
//        pendingIntent = PendingIntent.getBroadcast(
//                getBaseContext(), RQS_1, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarNow.getTimeInMillis(),
//                pendingIntent);
    }
}
