package ran.delhiautometer;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Track extends Activity {

    LocationManager l;
    LocationProvider p;
    Location previouslocation;
    String provider;
    float distance = 0, prevdistance = 0, fare = 0, speed = 0;
    boolean meterRunning = false;
    Criteria c;
    TextView faretext, distancetext, currentSpeed;

    public Track() {
        c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setPowerRequirement(Criteria.POWER_LOW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            provider = l.getBestProvider(c, false);
            p = l.getProvider(provider);
            if (!l.isProviderEnabled(provider)) {
                Toast.makeText(this, "Please turn ON the Location services", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e) {
     //       Catches any exception here
        }
        faretext = (TextView) findViewById(R.id.faretext);
        distancetext = (TextView) findViewById(R.id.distancetext);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
    }


    // On Start button Click
    public void StartTracking(View v) {
        meterRunning = true;
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            l.requestLocationUpdates(provider, 5000, 10, mylocationlistener);
        } catch (Exception e) {
            //       Catches any exception here
        }
    }

    // On Reset button Click
    public void ResetMeter(View v) {
        meterRunning = true;
        distance = 0;
        setDistanceFare(0);
        previouslocation = null;
    }

    // On Close button click
    public void CloseTracking(View v) {
        meterRunning = false;
        prevdistance = 0;
        distance = 0;
        previouslocation = null;
        finish();
    }

    LocationListener mylocationlistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (previouslocation == null) {
                previouslocation = location;
            } else {
                prevdistance = distance;
                distance = distance*1000;
                distance += previouslocation.distanceTo(location);
                distance = distance / 1000;
                previouslocation = location;
            }
            setDistanceFare(distance);
            speed = distance - prevdistance;
            setSpeed(speed);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            l.requestLocationUpdates(provider, 5000, 10, mylocationlistener);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (meterRunning) {
                Toast.makeText(getApplicationContext(), "DO NOT DISABLE THE LOCATION SERVICES", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void setSpeed(float speed) {
        speed = (speed / 5) * 3600;
        speed = (float) (Math.floor(speed * 100) / 100);
        String speedString = speed + " KM/H";
        currentSpeed.setText(speedString);
    }

    public void setDistanceFare(float distance) {
        if (distance == 0)
            fare = 0;
        else {
            if (distance <= 2) {
                fare = 25;
            } else
                fare = (distance - 2) * 8 + 25;
        }
        fare = (float) (Math.floor(fare * 100) / 100);
        distance = (float) (Math.floor(distance * 100) / 100);
        String distanceString = distance + " KM";
        distancetext.setText(distanceString);
        String fareString = "Rs. " + fare;
        faretext.setText(fareString);
    }

    @Override
    public void onBackPressed() {
        if (!meterRunning) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Your meter is running Please use home button to minimize", Toast.LENGTH_LONG).show();
        }
    }
}
