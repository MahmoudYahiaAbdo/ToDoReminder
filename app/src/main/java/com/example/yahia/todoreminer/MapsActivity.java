package com.example.yahia.todoreminer;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TREASURE_PROXIMITY_ALERT = "com.example.yahia.todoreminer";
    static List<Address> locations = null;
    static Location currentLoc;
    static Calendar fromCal = (Calendar)Calendar.getInstance();
    static Calendar toCal =  (Calendar)Calendar.getInstance();
    static int xxx;
    private GoogleMap mMap;
    static float radius = 500;
    static int requestCode;
    static String bestAvailableProvider;
    static Address resultloc;
    static View.OnClickListener onClickListener;

    private void animatePosition(Double l, Double n){
        LatLng target = new LatLng(l , n);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(target).zoom(14.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }


    private void refreshMap(Set<String> alarmsLa){
        Iterator iLat = alarmsLa.iterator();
        mMap.clear();
        for(int i = 0; i < alarmsLa.size(); i++) {
            String lat = iLat.next().toString();
            String[] parts = lat.split("---");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            String name = parts[2];
            radius = Float.parseFloat(parts[4]);
            LatLng B = new LatLng(latitude, longitude);
            CircleOptions co = new CircleOptions();
            co.strokeColor(Color.rgb(151,204,251));
            co.fillColor(Color.argb(100,151,204,251));
            co.center(B);
            co.radius(radius);
            mMap.addMarker(new MarkerOptions().position(B).title(name));
            mMap.addCircle(co);
        }}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent y = this.getIntent();
        if(y.getStringExtra("trans") != null){
            LocationsList.p = 1;
            LocationsList.pi = y.getStringExtra("trans");
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        final Context z = getApplicationContext();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(z);
        final SharedPreferences.Editor editor = pref.edit();
        final Set<String> alarmsLat = pref.getStringSet("Alarms_Lat", new HashSet<String>());
        requestCode = pref.getInt("requestCode", 1);
        final EditText enterPlace = (EditText)findViewById(R.id.Enter_Place);
        final Button Search = (Button)findViewById(R.id.Search);
        final EditText todo = (EditText)findViewById(R.id.todo);
        final EditText enterRaduis = (EditText) findViewById(R.id.radius);
        enterRaduis.setText(String.valueOf(radius));
        final View radiusDetails = findViewById(R.id.radiusDetails);
        final ViewGroup radio = (ViewGroup) findViewById(R.id.radio);
        final ViewGroup fromto = (ViewGroup)findViewById(R.id.fromto);
        final RadioButton anyTime = (RadioButton) findViewById(R.id.anyTime);
        final RadioButton selectTime = (RadioButton) findViewById(R.id.selectTime);
        final View ConfirmLoc = findViewById(R.id.ConfirmLoc);
        final Button cancel = (Button)findViewById(R.id.Cancel);
        final Button AddAlert = (Button)findViewById(R.id.AddAlert);

        final String TAG = "DYNAMIC_LOCATION_PROVIDER";
        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final ImageButton from = (ImageButton)findViewById(R.id.from);
        final ImageButton to = (ImageButton)findViewById(R.id.to);
        final Geocoder fwdGeocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);






        anyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xxx = 0;
                fromto.setVisibility(View.GONE);
            }
        });
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xxx = 1;
                fromto.setVisibility(View.VISIBLE);
                from.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                             new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    fromCal.set(Calendar.YEAR, year);
                                    fromCal.set(Calendar.MONTH, month);
                                    fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(MapsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            fromCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            fromCal.set(Calendar.MINUTE, minute);
                                        }
                                    }, fromCal.get(Calendar.HOUR_OF_DAY), fromCal.get(Calendar.MINUTE), false).show();
                                }
                            }, fromCal.get(Calendar.YEAR), fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                to.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                            new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    toCal.set(Calendar.YEAR, year);
                                    toCal.set(Calendar.MONTH, month);
                                    toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(MapsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            toCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            toCal.set(Calendar.MINUTE, minute);
                                        }
                                    }, toCal.get(Calendar.HOUR_OF_DAY), toCal.get(Calendar.MINUTE), false).show();
                                }
                            }, toCal.get(Calendar.YEAR), toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            }
        });

        refreshMap(alarmsLat);
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);


     try {
             bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        }
        catch (Exception g){
            Toast.makeText(this, "Exception : Please enable GPS", Toast.LENGTH_LONG).show();
        }



          currentLoc = locationManager.getLastKnownLocation(bestAvailableProvider);

            locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, new LocationListener() {
                        public void onLocationChanged(Location l) {
                            currentLoc = l;
                            Double lat = l.getLatitude();
                            Double lng = l.getLongitude();
                            LatLng yourloc = new LatLng(lat, lng);
                        }
                        public void onProviderDisabled(String provider) {}
                        public void onProviderEnabled(String provider) {}
                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {}
                    }
            );







            final ArrayList<String> locList = new ArrayList<>();
            final ListView resultList = (ListView)findViewById(R.id.search_result);

            final ArrayAdapter<String> AA;
            AA = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locList);
            resultList.setAdapter(AA);
                enterPlace.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View V){
                        enterPlace.setText("");
                        locations = null;
                        locList.clear();
                        AA.notifyDataSetChanged();
                    }
                });

    onClickListener = new View.OnClickListener(){
    public void onClick(View v){
        enterPlace.setEnabled(false);
        Search.setEnabled(false);
        (findViewById(R.id.search_result)).setVisibility(View.GONE);
        radiusDetails.setVisibility(View.VISIBLE);
        enterRaduis.setText(String.valueOf(radius));
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(resultloc.getLatitude(), resultloc.getLongitude()));
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.rgb(151,204,251));
        circleOptions.fillColor(Color.argb(100,151,204,251));
        mMap.addCircle(circleOptions);
        mMap.addMarker(new MarkerOptions().position(new LatLng(resultloc.getLatitude(), resultloc.getLongitude())).title(resultloc.getAddressLine(0)));
        enterRaduis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    Float c = Float.parseFloat(enterRaduis.getText().toString());
                    if(c < 5000)
                    {   refreshMap(alarmsLat);
                        radius = c;
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(new LatLng(resultloc.getLatitude(), resultloc.getLongitude()));
                        circleOptions.radius(radius);
                        circleOptions.strokeColor(Color.rgb(151,204,251));
                        circleOptions.fillColor(Color.argb(100,151,204,251));
                        mMap.addCircle(circleOptions);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(resultloc.getLatitude(), resultloc.getLongitude())).title(resultloc.getAddressLine(0)));
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Please enter distance less than 5000", Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }}
        });
        radio.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Please add ToDo Item Description", Toast.LENGTH_LONG).show();
        AddAlert.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int expiration = -1;
                String todoAdded = todo.getText().toString();

                if(!todoAdded.isEmpty()){

                    requestCode++;
                    editor.putInt("requestCode", requestCode);
                    String addressSt = resultloc.getAddressLine(0);
                    try{addressSt.isEmpty(); }
                    catch (Exception e){addressSt = todoAdded;}
                    String trans = Double.toString(resultloc.getLatitude()) + "---" + Double.toString(resultloc.getLongitude()) + "---" + addressSt + "---" + todoAdded + "---" + String.valueOf(radius) + "---" + String.valueOf(requestCode);

                    if(xxx == 1)
                        trans = trans.concat("---" + Long.toString(fromCal.getTimeInMillis()) + "---" + Long.toString(toCal.getTimeInMillis()));


                    Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
                    intent.putExtra("location_adress", resultloc.getAddressLine(0));
                    intent.putExtra("todo_discrip", todoAdded);
                    intent.putExtra("trans",trans);

                    PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);


                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                        locationManager.addProximityAlert(resultloc.getLatitude(), resultloc.getLongitude(), radius, expiration, proximityIntent);



                    alarmsLat.add(trans);
                    editor.remove("Alarms_Lat");
                    editor.commit();
                    editor.putStringSet("Alarms_Lat",alarmsLat);
                    editor.commit();
                    todo.setText("");
                    radiusDetails.setVisibility(View.GONE);
                    radio.setVisibility(View.GONE);
                    ConfirmLoc.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Location's Alarm Added", Toast.LENGTH_LONG).show();
                    enterPlace.setEnabled(true);
                    Search.setEnabled(true);
                }
                else
                    Toast.makeText(getApplicationContext(), "Please Add ToDo Item", Toast.LENGTH_LONG).show();}

        });



    }
};

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng) {
                ConfirmLoc.setVisibility(View.VISIBLE);
                resultloc = new Address(Locale.getDefault());
                resultloc.setLatitude(latLng.latitude);
                resultloc.setLongitude(latLng.longitude);
                refreshMap(alarmsLat);
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(new LatLng(resultloc.getLatitude(), resultloc.getLongitude()));
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.rgb(151,204,251));
                circleOptions.fillColor(Color.argb(100,151,204,251));
                mMap.addCircle(circleOptions);
                mMap.addMarker(new MarkerOptions().position(new LatLng(resultloc.getLatitude(), resultloc.getLongitude())).title("Selected Location"));
                AddAlert.setOnClickListener(onClickListener);

            }
        });



                Search.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                    String place =  enterPlace.getText().toString();
                    enterPlace.setText("");
                    locations = null;
                    locList.clear();
                    AA.notifyDataSetChanged();
                    Geocoder fwdGeocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);


                        try {
                            if(currentLoc != null)
                            locations = fwdGeocoder.getFromLocationName(place, 5, currentLoc.getLatitude() - 1, currentLoc.getLongitude() - 1, currentLoc.getLatitude() + 1, currentLoc.getLongitude() + 1);
                            while (locations.size() == 0){
                                locations = fwdGeocoder.getFromLocationName(place, 5, currentLoc.getLatitude() - 1, currentLoc.getLongitude() - 1, currentLoc.getLatitude() + 1, currentLoc.getLongitude() + 1);
                            }
                            (findViewById(R.id.search_result)).setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Please enaple GPS and Internet", Toast.LENGTH_LONG).show();
                        }

                        if (locations != null) {
                            for(int i = 0; i < locations.size(); i++){
                            String s = locations.get(i).getAddressLine(0);
                            locList.add(s);
                            }
                            AA.notifyDataSetChanged();
                            resultList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    resultloc = locations.get(position);
                                    animatePosition(resultloc.getLatitude(), resultloc.getLongitude());
                                    refreshMap(alarmsLat);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(resultloc.getLatitude(), resultloc.getLongitude())).title(resultloc.getAddressLine(0)));
                                    ConfirmLoc.setVisibility(View.VISIBLE);
                                    AddAlert.setOnClickListener(onClickListener);
                                }
                            });

                        }
                    }
                });

                                    if (LocationsList.p == 1000){
                                    cancel.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            enterPlace.setText("");
                                            todo.setText("");
                                            locations = null;
                                            locList.clear();
                                            AA.notifyDataSetChanged();
                                            ConfirmLoc.setVisibility(View.GONE);
                                            radiusDetails.setVisibility(View.GONE);
                                            radio.setVisibility(View.GONE);
                                            refreshMap(alarmsLat);
                                            enterPlace.setEnabled(true);
                                            Search.setEnabled(true);
                                        }
                                    });}


        if (LocationsList.p != 1000){
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                }
            });
            enterPlace.setVisibility(View.GONE);
            Search.setVisibility(View.GONE);
            radiusDetails.setVisibility(View.VISIBLE);
            radio.setVisibility(View.VISIBLE);
            ConfirmLoc.setVisibility(View.VISIBLE);
            enterRaduis.setEnabled(false);
            anyTime.setEnabled(false);
            selectTime.setEnabled(false);
            final String[] todotext = (LocationsList.pi).split("---");
            enterRaduis.setText(todotext[4]);
            if(todotext.length == 6)
                anyTime.performClick();
            if(todotext.length != 6){
                fromCal.setTimeInMillis(Long.decode(todotext[6]));
                toCal.setTimeInMillis(Long.decode(todotext[7]));
                selectTime.performClick();
            }
            final Double l = Double.parseDouble(todotext[0]);
            final Double n = Double.parseDouble(todotext[1]);
            animatePosition(l, n);
            todo.setText(todotext[3]);
            cancel.setText("ToDo Item Done");
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    alarmsLat.remove(LocationsList.pi);
                    editor.remove("Alarms_Lat");
                    editor.apply();
                    editor.putStringSet("Alarms_Lat",alarmsLat);
                    editor.apply();
                    Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
                    PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(todotext[5]), intent, 0);
                    locationManager.removeProximityAlert(proximityIntent);
                    refreshMap(alarmsLat);
                    radiusDetails.setVisibility(View.GONE);
                    radio.setVisibility(View.GONE);
                    ConfirmLoc.setVisibility(View.GONE);
                    LocationsList.p = 1000;
                }
            });
            AddAlert.setText("ToDo Edited");
            AddAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] todotext = (LocationsList.pi).split("---");
                    String todoDetailsEdited = todo.getText().toString();
                    if(todotext[2].equals(todotext[3]))
                        todotext[2] = todoDetailsEdited;
                    String edited;
                    if(todotext.length ==6)
                        edited = todotext[0] + "---" + todotext[1] + "---" + todotext[2] + "---" + todoDetailsEdited + "---" + String.valueOf(radius) + "---" + todotext[5];
                    else
                        edited =  todotext[0] + "---" + todotext[1] + "---" + todotext[2] + "---" + todoDetailsEdited + "---" + String.valueOf(radius) + "---" +  todotext[5] + "---" + Long.toString(fromCal.getTimeInMillis()) + "---" + Long.toString(toCal.getTimeInMillis());
                    alarmsLat.remove(LocationsList.pi);
                    alarmsLat.add(edited);
                    editor.remove("Alarms_Lat");
                    editor.commit();
                    editor.putStringSet("Alarms_Lat",alarmsLat);
                    editor.commit();
                    LocationsList.pi = edited;
                }
            });
            LocationsList.p = 1000;
        }






        }

}

