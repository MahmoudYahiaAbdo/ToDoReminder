package com.example.yahia.todoreminer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;




public class LocationsList extends AppCompatActivity {
           static int p = 1000;
            static String pi = null;
            static boolean click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);

    }
    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = pref.edit();
        final Set<String> savedLocs = pref.getStringSet("Alarms_Lat", new HashSet<String>());
        final Iterator savedLoc = savedLocs.iterator();
        final ArrayList<String> locList = new ArrayList<String>();
        final ArrayList<String> locsList = new ArrayList<String>();
        String svcName = Context.LOCATION_SERVICE;
        final LocationManager locationManager = (LocationManager)getSystemService(svcName);
        for(int i = 0; i < savedLocs.size(); i++) {
            String saved = savedLoc.next().toString();
            locsList.add(saved);
            String[] parts = saved.split("---");
            String name;
            if(parts[2].equals(parts[3]))
                name = parts[2];
            else
                name = parts[2] + "\n" + parts[3];
            locList.add(name);
        };

        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locList);
        final ListView LocationsList = (ListView)findViewById(R.id.LocationsList);
        LocationsList.setAdapter(adapter);
        LocationsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < (locList.size()-1))
                    click = true;
                String j = locsList.get(position);
                String[] todotext = j.split("---");
                Double l = Double.parseDouble(todotext[0]);
                Double n = Double.parseDouble(todotext[1]);
                String TREASURE_PROXIMITY_ALERT = "com.example.yahia.todoreminer";
                Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
                int requestCode = Integer.parseInt(todotext[5]);
                PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);
                locationManager.removeProximityAlert(proximityIntent);
                locList.remove(position);
                locsList.remove(position);
                adapter.notifyDataSetChanged();
                savedLocs.remove(j);
                editor.remove("Alarms_Lat");
                editor.commit();
                editor.putStringSet("Alarms_Lat", savedLocs);
                editor.commit();



                return false;
            }
        });
       
        LocationsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (click == false){
                p = position;
                pi = locsList.get(position);
                Intent k = new Intent(LocationsList.this, MapsActivity.class);
                startActivity(k);}
                else click = false;
            }});

    }
}
