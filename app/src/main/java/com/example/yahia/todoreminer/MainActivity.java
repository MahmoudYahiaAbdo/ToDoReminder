package com.example.yahia.todoreminer;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private final Criteria criteria= new Criteria();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = new Intent(this, FreeTimeToDo.class);
        final Intent locationIntent = new Intent(this, MapsActivity.class);
        final Intent locationlistIntent = new Intent(this, LocationsList.class);
        final Button Location = (Button)findViewById(R.id.picklocation);
        Location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {startActivity(locationIntent);}});

        final Button LocationsList = (Button)findViewById(R.id.locations_List);
        LocationsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(locationlistIntent);
            }
        });


            final Button FreeTime = (Button) findViewById(R.id.freetime);
            FreeTime.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    try {
                    startActivity(intent);
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
                    }
                }
            });



        String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        if (bestAvailableProvider != null && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            int t = 5000;
            int distance = 5;
            locationManager.requestLocationUpdates(bestAvailableProvider, t, distance, new LocationListener() {
                public void onLocationChanged(android.location.Location location) {
                    Double lat = location.getLatitude();
                    Double lng = location.getLongitude();
                    String x = "Your Current Location Lat : "  + lat + "  Long :" + lng;
                    TextView currentLoc = (TextView)findViewById(R.id.checktest);
                    currentLoc.setText(x);
                }
                public void onProviderDisabled(String provider){
                }
                public void onProviderEnabled(String provider){
                }
                public void onStatusChanged(String provider, int status,
                                            Bundle extras){
                }
            });
        }


        Button searchToDoList = (Button) findViewById(R.id.searchToDoList);
        searchToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, search_todoList.class);
                startActivity(intent);
            }
        });
    }
}



