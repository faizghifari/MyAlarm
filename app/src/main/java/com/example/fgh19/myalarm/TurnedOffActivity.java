package com.example.fgh19.myalarm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TurnedOffActivity extends AppCompatActivity {
    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationDetector mLocationDetector;

    private TextView cityText;
    private TextView countryText;

    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turned_off);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        cityText = (TextView) findViewById(R.id.cityText);
        countryText = (TextView) findViewById(R.id.countryText);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocationDetector = new LocationDetector();
        mLocationDetector.setOnLocationListener(new LocationDetector.OnLocationListener() {
            @Override
            public void OnLocation(Double latitude, Double longitude) {
                showLocation(latitude,longitude);
            }
        });

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationDetector);

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();

            showLocation(latitude,longitude);
        }
    }

    private void showLocation(Double latitude, Double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {



                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.d("showLocation", "getAddress:  address" + address);
                Log.d("showLocation", "getAddress:  city" + city);
                Log.d("showLocation", "getAddress:  state" + state);
                Log.d("showLocation", "getAddress:  postalCode" + postalCode);
                Log.d("showLocation", "getAddress:  knownName" + knownName);

                cityText.setText(city);
                countryText.setText(country);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
