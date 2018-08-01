package com.example.yahia.todoreminer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Item_Details extends AppCompatActivity {

    static int listPos;
    static int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__details);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        final Spinner spinner = (Spinner) findViewById(R.id.lists_Spinner);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase(FreeTimeToDo.DATABASE_NAME,
                Context.MODE_PRIVATE,
                null);
        sqLiteDatabase.execSQL(FreeTimeToDo.DATABASE_CREATE);

        final ArrayList<String> Lists = new ArrayList<String>();

        try {   Cursor cursor = sqLiteDatabase.query(FreeTimeToDo.DATABASE_TABLE, new String[]{FreeTimeToDo.LISTS}, null, null, null, null, null);
            cursor.moveToFirst();
            final Set<String> ListsSet = sharedPreferences.getStringSet("todo_Lists", new HashSet<String>());
            Lists.addAll(ListsSet);
            for(int i = 0; i<Lists.size(); i++) {
                if(Lists.get(i).equals(FreeTimeToDo.chosenList))
                    listPos = i;
            }


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Lists);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(listPos, true);
            arrayAdapter.notifyDataSetChanged();
        }
        catch(Exception e){
          Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        };



        final EditText selected_Item = (EditText)findViewById(R.id.selected_Item);
        selected_Item.setText(FreeTimeToDo.selectedItem);
        final ImageButton editCal = (ImageButton)findViewById(R.id.editCal);
        final String where = FreeTimeToDo.LISTS + " = ? AND " + FreeTimeToDo.ITEM_NAME + " = ?";
        final String [] whereArg = new String[]{FreeTimeToDo.chosenList, FreeTimeToDo.selectedItem};
        final Cursor cursor = sqLiteDatabase.query(FreeTimeToDo.DATABASE_TABLE, new String[]{FreeTimeToDo.LISTS, FreeTimeToDo.DETAILS, FreeTimeToDo.TIME, FreeTimeToDo.STATUS}, where, whereArg, null, null, null);
        cursor.moveToFirst();
        int xx = cursor.getColumnIndex(FreeTimeToDo.TIME);
        requestCode = (int) cursor.getLong(xx);


        try {
        String itemDetailsString = cursor.getString(cursor.getColumnIndex(FreeTimeToDo.DETAILS));
        final Long timeLong = cursor.getLong(cursor.getColumnIndex(FreeTimeToDo.TIME));
        boolean statusbool = cursor.getInt(cursor.getColumnIndex(FreeTimeToDo.STATUS)) > 0;

        final EditText itemDetails = (EditText)findViewById(R.id.item_Details);
        final TextView item_Time = (TextView)findViewById(R.id.item_Time);
        final CheckBox status = (CheckBox) findViewById(R.id.status_Checkbox);
        Button editItem = (Button) findViewById(R.id.editItem);
        Button deleteItem = (Button) findViewById(R.id.deleteItem);
        Button cancelItem = (Button) findViewById(R.id.cancelItem);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);

        itemDetails.setText(itemDetailsString);
        item_Time.setText(String.format("%1$tA %1$tb %1$td %1$ty at %1$tI:%1$tM %1$Tp", calendar));
        status.setChecked(statusbool);


        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                item_Time.setText(String.format("%1$tA %1$tb %1$td %1$ty at %1$tI:%1$tM %1$Tp", calendar));
            }
        };

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                timePickerDialog.show();
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        editCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.show();

            }
        });
        editItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis() || calendar.getTimeInMillis() == timeLong){
                    final Intent intentToFire = new Intent(FreeTimeToDo.ALERT_INTENT);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intentToFire, 0);
                    alarmManager.cancel(alarmIntent);
                    FreeTimeToDo.chosenList = (String) spinner.getSelectedItem();
                    intentToFire.putExtra("location_adress", selected_Item.getText().toString());
                    intentToFire.putExtra("todo_discrip", itemDetails.getText().toString());
                    intentToFire.putExtra("list_name", FreeTimeToDo.chosenList);
                    int editedRequestCode = (int) calendar.getTimeInMillis();
                    if(!status.isChecked() && (calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()))
                    {PendingIntent alarmIntent2 = PendingIntent.getBroadcast(getApplicationContext(), editedRequestCode, intentToFire, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent2);}
                    ContentValues editedValues = new ContentValues();
                    editedValues.put(FreeTimeToDo.LISTS, Lists.get(spinner.getSelectedItemPosition()));
                    editedValues.put(FreeTimeToDo.ITEM_NAME, selected_Item.getText().toString());
                    editedValues.put(FreeTimeToDo.DETAILS, itemDetails.getText().toString());
                    editedValues.put(FreeTimeToDo.TIME, calendar.getTimeInMillis());
                    editedValues.put(FreeTimeToDo.STATUS, status.isChecked());
                    sqLiteDatabase.update(FreeTimeToDo.DATABASE_TABLE, editedValues, where, whereArg);
                    Toast.makeText(getApplicationContext(), "Item Edited", Toast.LENGTH_LONG).show();
                    requestCode = editedRequestCode;}
                else
                    Toast.makeText(getApplicationContext(), "This time in the past", Toast.LENGTH_LONG).show();
            }
        });
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase.delete(FreeTimeToDo.DATABASE_TABLE, where, whereArg);
                final Intent intentToFire = new Intent(FreeTimeToDo.ALERT_INTENT);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intentToFire, 0);
                alarmManager.cancel(alarmIntent);
                Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Item_Details.this, FreeTimeToDo.class));
            }
        });

        cancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Item_Details.this, FreeTimeToDo.class);
                startActivity(intent);
            }
        });
}catch (Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}

    }
}
