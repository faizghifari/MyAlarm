package com.example.fgh19.myalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer player;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        int value = intent.getIntExtra("Sign",0);
        if (value == 1) {
            Toast.makeText(context, "Alarm aktif!", Toast.LENGTH_LONG).show();
            player = MediaPlayer.create(context, R.raw.alarm);
            player.start();
        } else {
            player.stop();
        }
    }

}
