package com.example.yahia.todoreminer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;





public class search_todoList extends AppCompatActivity {

    static Cursor cursor;
    static ArrayList<String> resultItems = new ArrayList<>();
    static ArrayList<String> resultLists = new ArrayList<>();
    static ArrayList<Boolean> statusList = new ArrayList<>();
    static ArrayList<Long> timeList = new ArrayList<>();
    static Calendar fromCal;
    static Calendar toCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_todo_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fromCal == null){
         fromCal = Calendar.getInstance();
         toCal = Calendar.getInstance();}
        final EditText search_byName = (EditText)findViewById(R.id.search_byName);
        final ListView searchList = (ListView) findViewById(R.id.searchList);
        final Intent intentToFire = new Intent(FreeTimeToDo.ALERT_INTENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final SQLiteDatabase toDoDataBase = getApplicationContext().openOrCreateDatabase(FreeTimeToDo.DATABASE_NAME, Context.MODE_PRIVATE, null);
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromCal.set(Calendar.MINUTE, minute);
            }
        };

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, fromCal.get(Calendar.HOUR_OF_DAY), fromCal.get(Calendar.MINUTE), false);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fromCal.set(Calendar.MONTH, month);
                fromCal.set(Calendar.YEAR, year);
                timePickerDialog.show();
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, fromCal.get(Calendar.YEAR), fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH));
        ImageButton searchFrom = (ImageButton)findViewById(R.id.searchFrom);
        searchFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        TimePickerDialog.OnTimeSetListener onTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                toCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                toCal.set(Calendar.MINUTE, minute);
            }
        };

        final TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, onTimeSetListener1, toCal.get(Calendar.HOUR_OF_DAY), toCal.get(Calendar.MINUTE), false);
        DatePickerDialog.OnDateSetListener onDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                toCal.set(Calendar.MONTH, month);
                toCal.set(Calendar.YEAR, year);
                timePickerDialog1.show();
            }
        };
        final DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, onDateSetListener1, toCal.get(Calendar.YEAR), toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH));
        ImageButton searchTo = (ImageButton)findViewById(R.id.searchTo);
        searchTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog1.show();
            }
        });
        Button search_button = (Button) findViewById(R.id.search_button);


        final ListAdapter adapter = new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {



            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return resultItems.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final String where = FreeTimeToDo.LISTS + " = ? AND " + FreeTimeToDo.ITEM_NAME + " = ?";
                final String [] whereArg = new String[]{FreeTimeToDo.chosenList, resultItems.get(position)};
                cursor = toDoDataBase.query(FreeTimeToDo.DATABASE_TABLE, new String[]{FreeTimeToDo.DETAILS}, where, whereArg, null, null, null);
                cursor.moveToFirst();
                final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.list_single, null, true);
                String hh;
                Long time = timeList.get(position) - Calendar.getInstance().getTimeInMillis();
                long days = TimeUnit.MILLISECONDS.toDays(time);
                time -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(time);
                time -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
                time -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
                if (Calendar.getInstance().getTimeInMillis() > timeList.get(position))
                    hh = "passed";
                else
                    hh = String.valueOf(days) + " Day " + String.valueOf(hours) + " Hour " + String.valueOf(minutes) + " Min " + String.valueOf(seconds) + " Sec" ;
                TextView text = (TextView)rowView.findViewById(R.id.iii);
                final CheckBox status = (CheckBox)rowView.findViewById(R.id.stat);
                text.setText(resultItems.get(position) + "\n" + hh);
                status.setChecked(statusList.get(position));
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Calendar.getInstance().getTimeInMillis() < timeList.get(position)){
                            if(status.isChecked()){

                                final Intent intentToFire = new Intent(FreeTimeToDo.ALERT_INTENT);
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) (long) timeList.get(position), intentToFire, 0);
                                alarmManager.cancel(alarmIntent);


                            }
                            else
                            {try {
                                intentToFire.putExtra("location_adress", resultItems.get(position));
                                intentToFire.putExtra("todo_discrip", cursor.getString(cursor.getColumnIndex(FreeTimeToDo.DETAILS)));
                                intentToFire.putExtra("list_name", FreeTimeToDo.chosenList);
                                PendingIntent alarmIntent2 = PendingIntent.getBroadcast(getApplicationContext(), (int)Calendar.getInstance().getTimeInMillis(), intentToFire, 0);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, timeList.get(position), alarmIntent2);
                            }catch(Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
                            }}
                        else{
                            if(status.isChecked())
                                rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(201, 198, 193));
                            else
                                rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(255, 192, 203));
                        }

                        ContentValues editedValues = new ContentValues();
                        //    editedValues.put(FreeTimeToDo.LISTS, chosenList);
                        //    editedValues.put(FreeTimeToDo.ITEM_NAME, MarketItems.get(position));
                        //    editedValues.put(FreeTimeToDo.DETAILS, itemDetails.getText().toString());
                        //     editedValues.put(FreeTimeToDo.TIME, calendar.getTimeInMillis());
                        editedValues.put(FreeTimeToDo.STATUS, status.isChecked());
                        toDoDataBase.update(FreeTimeToDo.DATABASE_TABLE, editedValues, where, whereArg);
                    }
                });
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FreeTimeToDo.chosenList = resultLists.get(position);
                        FreeTimeToDo.selectedItem = resultItems.get(position);
                        Intent intent = new Intent(search_todoList.this, Item_Details.class);
                        startActivity(intent);
                    }
                });
                if(!(status.isChecked()) && Calendar.getInstance().getTimeInMillis() > timeList.get(position))
                    rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(255, 192, 203));
                if(status.isChecked() && Calendar.getInstance().getTimeInMillis() > timeList.get(position))
                    rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(201, 198, 193));
                return rowView;


            }

            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

       /* resultItems.clear();
        resultLists.clear();
        statusList.clear();
        timeList.clear();*/

      //  searchList.removeAllViewsInLayout();
     //   searchList.setAdapter(adapter);

      if(search_button.performClick())
          Toast.makeText(getApplicationContext(), "work",Toast.LENGTH_LONG).show();



        toDoDataBase.execSQL(FreeTimeToDo.DATABASE_CREATE);


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    resultItems.clear();
                    resultLists.clear();
                    statusList.clear();
                    timeList.clear();
                String where = null;
                String[] whereArg = null;
                String r = search_byName.getText().toString();
                String orderBy = FreeTimeToDo.TIME + " DESC";

                if (r != null)
                    {where = "ITEM_NAME like ?";
                    r = "%" + r + "%";
                    whereArg = new String[]{r};
                    cursor = toDoDataBase.query(FreeTimeToDo.DATABASE_TABLE, new String[]{FreeTimeToDo.LISTS,FreeTimeToDo.ITEM_NAME, FreeTimeToDo.TIME, FreeTimeToDo.STATUS}, where, whereArg, null, null, orderBy);}
                else
                    cursor = toDoDataBase.query(FreeTimeToDo.DATABASE_TABLE, new String[]{FreeTimeToDo.LISTS,FreeTimeToDo.ITEM_NAME, FreeTimeToDo.TIME, FreeTimeToDo.STATUS}, null, null, null, null, orderBy);
                cursor.moveToFirst();
                int CI = cursor.getColumnIndex(FreeTimeToDo.TIME);
                do {
                String z = cursor.getString(cursor.getColumnIndex(FreeTimeToDo.ITEM_NAME));
                if (cursor.getLong(CI) > fromCal.getTimeInMillis() && cursor.getLong(CI) < toCal.getTimeInMillis())
                {resultItems.add(z); resultLists.add(cursor.getString(cursor.getColumnIndex(FreeTimeToDo.LISTS))); timeList.add(cursor.getLong(cursor.getColumnIndex(FreeTimeToDo.TIME))); statusList.add(cursor.getInt(cursor.getColumnIndex(FreeTimeToDo.STATUS)) > 0);}}
                while (cursor.moveToNext());
                    searchList.removeAllViewsInLayout();
                    searchList.setAdapter(adapter);
                }
                catch (Exception e)
                {Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_LONG).show();}
            }

        });




    }
}
