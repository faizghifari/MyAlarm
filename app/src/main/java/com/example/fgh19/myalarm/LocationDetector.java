package com.example.fgh19.myalarm;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by fgh19 on 2/23/2018.
 */

public class LocationDetector implements LocationListener {

    Double latitude;
    Double longitude;

    private OnLocationListener mListener;

    public void setOnLocationListener(OnLocationListener listener) { this.mListener = listener; }

    public interface OnLocationListener {
        public void OnLocation(Double latitude,Double longitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        mListener.OnLocation(latitude,longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}
}
