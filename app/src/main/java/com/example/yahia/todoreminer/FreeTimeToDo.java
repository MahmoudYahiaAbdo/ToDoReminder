package com.example.yahia.todoreminer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class FreeTimeToDo extends AppCompatActivity {

    public static final String ALERT_INTENT = "com.example.yahia.todoreminer";
    public static String chosenList;
    public static String selectedItem;
    public static final String KEY_ID = "_id";
    public static final String LISTS = "LISTS";
    public static final String ITEM_NAME = "ITEM_NAME";
    public static final String DETAILS = "DETAILS";
    public static final String TIME = "TIME";
    public static final String STATUS = "STATUS";
    public  static SQLiteDatabase toDoDataBase;
    public static Cursor cursor;
    static ListView MarketList;
    public static final String DATABASE_NAME = "myDatabase.db";
    public static final String DATABASE_TABLE = "ToDoTables";
    public static final String DATABASE_CREATE = "create table if not exists " +
            DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            LISTS + " text not null, " +
            ITEM_NAME + " text, " +
            DETAILS + " text, " +
            TIME + " long, " +
            STATUS + " boolean);";
    public static MarketFragment xxx;
    public static EditText enterItem;
    public static EditText enterDetails;
    public static ArrayList<String> MarketItems;
    static ArrayList<Boolean> StatusList;
    static ArrayList<Long> TimeList;
    public static ListAdapter aa;
    public static ArrayList<Button> listButtons;
    public static LinearLayout buttonPanel;
    public static android.app.FragmentManager fm;
    public static android.app.FragmentTransaction fragmentTransaction;
    public static Button buttonDelete;
    public static String item;
    public static String details;
    public static long time;
    public static int ddd = 0;
    static int width;
    static int hight;
    static int listNo;
    static public class MarketFragment extends Fragment {


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.market, container, false);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }
        @Override
        public void onStart(){
            super.onStart();

        }
        @Override
        public void onResume(){
            super.onResume();


            enterItem = (EditText)getActivity().findViewById(R.id.enterMarketItem);
            enterDetails = (EditText) getActivity().findViewById(R.id.enterDetails);
            final Button Insert = (Button)getActivity().findViewById(R.id.Insert);
             MarketList = (ListView)getActivity().findViewById(R.id.marketList);
            final Intent intentToFire = new Intent(ALERT_INTENT);
            final AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            try{

                aa = new ListAdapter() {
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
                        return MarketItems.size();
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
                        final String where = LISTS + " = ? AND " + ITEM_NAME + " = ?";
                        final String [] whereArg = new String[]{chosenList, MarketItems.get(position)};
                        cursor = toDoDataBase.query(DATABASE_TABLE, new String[]{DETAILS}, where, whereArg, null, null, null);
                        cursor.moveToFirst();
                        final LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.list_single, null, true);
                        String hh;
                        Long time = TimeList.get(position) - Calendar.getInstance().getTimeInMillis();
                        long days = TimeUnit.MILLISECONDS.toDays(time);
                        time -= TimeUnit.DAYS.toMillis(days);
                        long hours = TimeUnit.MILLISECONDS.toHours(time);
                        time -= TimeUnit.HOURS.toMillis(hours);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
                        time -= TimeUnit.MINUTES.toMillis(minutes);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
                        if (Calendar.getInstance().getTimeInMillis() > TimeList.get(position))
                            hh = "passed";
                        else
                            hh = String.valueOf(days) + " Day " + String.valueOf(hours) + " Hour " + String.valueOf(minutes) + " Min " + String.valueOf(seconds) + " Sec" ;
                        TextView text = (TextView)rowView.findViewById(R.id.iii);
                        final CheckBox status = (CheckBox)rowView.findViewById(R.id.stat);
                        text.setText(MarketItems.get(position) + "\n" + hh);
                        status.setChecked(StatusList.get(position));
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Calendar.getInstance().getTimeInMillis() < TimeList.get(position)){
                                if(status.isChecked()){

                                        final Intent intentToFire = new Intent(FreeTimeToDo.ALERT_INTENT);
                                        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int) (long) TimeList.get(position), intentToFire, 0);
                                        alarmManager.cancel(alarmIntent);


                                }
                                else
                                {try {
                                    intentToFire.putExtra("location_adress", MarketItems.get(position));
                                    intentToFire.putExtra("todo_discrip", cursor.getString(cursor.getColumnIndex(DETAILS)));
                                    intentToFire.putExtra("list_name", chosenList);
                                    PendingIntent alarmIntent2 = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int)Calendar.getInstance().getTimeInMillis(), intentToFire, 0);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, TimeList.get(position), alarmIntent2);
                                }catch(Exception e){Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
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
                                    Intent i = new Intent(getActivity(), Item_Details.class);
                                    selectedItem = MarketItems.get(position);
                                    startActivity(i);
                            }
                        });
                        if(!(status.isChecked()) && Calendar.getInstance().getTimeInMillis() > TimeList.get(position))
                            rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(255, 192, 203));
                        if(status.isChecked() && Calendar.getInstance().getTimeInMillis() > TimeList.get(position))
                            rowView.findViewById(R.id.mmm).setBackgroundColor(Color.rgb(201, 198, 193));
                        return rowView;
                        /*
                        rowView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                try {   LocationsList.click = true;
                                    String r = MarketItems.get(position);
                                    MarketItems.remove(position);
                                    StatusList.remove(position);
                                    //     aa.notifyDataSetChanged();



                                    String where = "ITEM_NAME like ?";  //LISTS + "=" + chosenList;
                                    String whereArgs[] = new String[]{r};

                                    String groupBy = null;
                                    String having = null;
                                    String order = FreeTimeToDo.TIME + " ASC";
                                    String[] result_columns = new String[]{KEY_ID, LISTS, ITEM_NAME, DETAILS, TIME};

                                    cursor = toDoDataBase.query(DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);
                                    cursor.moveToLast();
                                    int xx = cursor.getColumnIndex(TIME);
                                    int requestCode = (int) cursor.getLong(xx);
                                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), requestCode, intentToFire, 0);
                                    alarmManager.cancel(alarmIntent);

                                    toDoDataBase.delete(DATABASE_TABLE, where, whereArgs);
                                    MarketList.removeViewAt(position);
                                }
                                catch(Exception e){
                                    Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                                return false;
                            }
                        });*/

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
                //  aa.notifyDataSetChanged();
                MarketList.removeAllViewsInLayout();
            MarketList.setAdapter(aa);

// ToDo cursor justification the set observer on it, listadapter.setdatasetobserver
            }
            catch(Exception e){
                Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            final Calendar calendar = Calendar.getInstance();
            final Calendar calSet = (Calendar) calendar.clone();
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calSet.set(Calendar.MINUTE, minute);
                    intentToFire.putExtra("location_adress", item);
                    intentToFire.putExtra("todo_discrip", details);
                    intentToFire.putExtra("list_name", chosenList);

                    int requestCode = (int)calSet.getTimeInMillis();
                    time = calSet.getTimeInMillis();
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), requestCode, intentToFire, 0);

                    ContentValues values = new ContentValues();
                    values.put(LISTS, chosenList);
                    values.put(ITEM_NAME, item);
                    values.put(DETAILS, details);
                    values.put(TIME, time);
                    values.put(STATUS, false);
                     String where = TIME + "=" + time;
                    cursor = toDoDataBase.query(DATABASE_TABLE, new String[]{TIME}, where, null, null, null, null);
                    if (toDoDataBase.isOpen() && (cursor.getCount() == 0))
                        try {
                            MarketItems.add(0, enterItem.getText().toString());
                            TimeList.add(time);
                            StatusList.add(false);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), alarmIntent);
                            toDoDataBase.insertOrThrow(DATABASE_TABLE, null, values);

                            //     aa.notifyDataSetChanged();
                            MarketList.removeAllViewsInLayout();
                            MarketList.setAdapter(aa);
                            enterItem.setText("");
                            enterDetails.setText("");
                        }
                        catch (Exception e){
                            Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    else
                        {Toast.makeText(getActivity().getBaseContext(), "There is item at the same time", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity().getBaseContext(), "Please set another time", Toast.LENGTH_LONG).show();}
                    cursor.close();
                }
            };
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calSet.set(Calendar.MONTH, month);
                    calSet.set(Calendar.YEAR, year);
                    timePickerDialog.show();
                }
            };
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));




            Insert.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    item = enterItem.getText().toString();
                         String where = LISTS + " = ? AND " + ITEM_NAME + " = ?";
                         String [] whereArg = new String[]{chosenList, item};
                    cursor = toDoDataBase.query(DATABASE_TABLE, new String[]{ITEM_NAME}, where, whereArg, null, null, null);
                    if(item.isEmpty())
                        Toast.makeText(getActivity().getApplicationContext(), "Please Enter Item Name", Toast.LENGTH_LONG).show();
                    else if(cursor.getCount() != 0)
                        Toast.makeText(getActivity().getApplicationContext(), item + " already exist in the same list", Toast.LENGTH_LONG).show();
                    else{
                    details = enterDetails.getText().toString();
                    datePickerDialog.show();
                    }
                }

            });

        }

        @Override
        public void onPause(){
            super.onPause();

        }

        @Override
        public void onSaveInstanceState(Bundle saveInstanceState) {
            super.onSaveInstanceState(saveInstanceState);

        }
        @Override
        public void onStop(){
            super.onStop();
        }
        @Override
        public void onDestroyView() {
            super.onDestroyView();

        }
        @Override
        public void onDestroy(){
            super.onDestroy();

        }
        @Override
        public void onDetach() {
            super.onDetach();

        }
    }
public void chosingList(){
    try {

        if (ddd == 1) {
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, xxx);
            fragmentTransaction.commit();
            ddd = 0;
        }
        if (!MarketItems.isEmpty()) {
            MarketItems.clear();
            StatusList.clear();
            TimeList.clear();
      //      aa.notifyDataSetChanged();
            MarketList.removeAllViewsInLayout();
            MarketList.setAdapter(aa);
            enterItem.setText("");
        }
        String where = "LISTS like ?";  //LISTS + "=" + chosenList;
        String whereArgs[] = new String[]{chosenList};
        String groupBy = null;
        String having = null;
        String order = FreeTimeToDo.TIME + " ASC";;
        String[] result_columns = new String[]{KEY_ID, LISTS, ITEM_NAME, TIME, STATUS};


        cursor = toDoDataBase.query(DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order); //rawQuery(sql, null);
        cursor.moveToLast();

        try {
            do {
                MarketItems.add(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME)));
                StatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(STATUS)) > 0);
                TimeList.add(cursor.getLong(cursor.getColumnIndexOrThrow(TIME)));
            }
            while (cursor.moveToPrevious());
       //     aa.notifyDataSetChanged();
            MarketList.removeAllViewsInLayout();
            MarketList.setAdapter(aa);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), chosenList + " list is empty", Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }catch (Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}


    for (int i = 0; i < listButtons.size(); i++) {
        final Button dynamicButton = listButtons.get(i);
        dynamicButton.setBackground((getResources().getDrawable(R.drawable.round_button)));}
    buttonDelete.setBackgroundColor(Color.rgb(192, 192, 192));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        xxx = new MarketFragment();
        ddd = 1;
        setContentView(R.layout.freetime_activity);
        listButtons = new ArrayList<>();
        buttonPanel = (LinearLayout)findViewById(R.id.buttonsPanel);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Set<String> Lists = pref.getStringSet("todo_Lists", new HashSet<String>());
        final Iterator iList = Lists.iterator();
        for(listNo = 0; listNo < Lists.size(); listNo++) {
            String g = iList.next().toString();
            final Button dynamicB = new Button(getApplicationContext());
            dynamicB.setText(g);
            buttonPanel.addView(dynamicB);
            listButtons.add(dynamicB);
            if(g.equals(chosenList))
                buttonDelete = dynamicB;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        MarketItems = new ArrayList<String>();
        StatusList = new ArrayList<Boolean>();
        TimeList = new ArrayList<Long>();
   //     aa = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, MarketItems);
        toDoDataBase = getApplicationContext().openOrCreateDatabase(DATABASE_NAME,
              Context.MODE_PRIVATE,
              null);

          toDoDataBase.execSQL(DATABASE_CREATE);

          if(buttonDelete != null)
              chosingList();

        fragmentTransaction = fm.beginTransaction();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = pref.edit();
        final Button addList = (Button) findViewById(R.id.addList);
        width = addList.getWidth();
        hight = addList.getHeight();
        final EditText newList = (EditText) findViewById(R.id.newList);
        final Set<String> Lists = pref.getStringSet("todo_Lists", new HashSet<String>());

        for (int i = 0; i < listButtons.size(); i++) {
            final Button dynamicButton = listButtons.get(i);
            dynamicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    buttonDelete = dynamicButton;
                    chosenList = dynamicButton.getText().toString();
                    chosingList();


                }
            });
        }


        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String f = newList.getText().toString();
                cursor = toDoDataBase.query(DATABASE_TABLE, new String[]{LISTS}, LISTS + "= ?", new String[]{f}, null, null, null);

                if (f.isEmpty())
                    Toast.makeText(getApplicationContext(), "Please Enter List Name", Toast.LENGTH_LONG).show();
                else if (cursor.getCount() != 0)
                    Toast.makeText(getApplicationContext(), f + " list already exist", Toast.LENGTH_LONG).show();
                else{
                    newList.setText("");
                final Button dynamicB = new Button(getApplicationContext());
                dynamicB.setText(f);
                buttonPanel.addView(dynamicB);
                listButtons.add(dynamicB);
                dynamicB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonDelete = dynamicB;
                        chosenList = dynamicB.getText().toString();
                        chosingList();

                    }
                });

                Lists.add(f);
                editor.remove("todo_Lists");
                editor.apply();
                editor.putStringSet("todo_Lists", Lists);
                editor.apply();

            } }
                });
                final Button deleteList = (Button) findViewById(R.id.deleteList);
                deleteList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                        final AlarmManager alarmManag = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intentToFire = new Intent(ALERT_INTENT);

                            String where = "LISTS like ?";
                            String whereArgs[] = new String[]{chosenList};
                            String groupBy = null;
                            String having = null;
                            String order = FreeTimeToDo.TIME + " ASC";
                            String[] result_columns = new String[]{TIME};
                            cursor = toDoDataBase.query(DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);
                            if(cursor.getCount() != 0) {
                                int index = cursor.getColumnIndex(TIME);
                                cursor.moveToFirst();
                                do {
                                    int requestCode = (int) cursor.getLong(index);
                                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intentToFire, 0);
                                    alarmManag.cancel(alarmIntent);
                                } while (cursor.moveToNext());
                            }
                        toDoDataBase.delete(DATABASE_TABLE, where, whereArgs);
                        buttonPanel.removeView(buttonDelete);
                        MarketItems.clear();
                        StatusList.clear();
                        TimeList.clear();
                     //   aa.notifyDataSetChanged();
                            MarketList.removeAllViewsInLayout();
                            MarketList.setAdapter(aa);
                        enterItem.setText("");
                        Lists.remove(buttonDelete.getText().toString());
                        editor.remove("todo_Lists");
                        editor.apply();
                        editor.putStringSet("todo_Lists", Lists);
                        editor.apply();
                        fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.remove(xxx).commit();
                        ddd = 1;
                        chosenList = null;
                        buttonDelete = null;


                    }
                    catch(Exception e) {Toast.makeText(getApplicationContext(), "Please choose list", Toast.LENGTH_LONG).show();}}
                });

            }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
    }
    @Override

    public  void onPause(){
        super.onPause();
        toDoDataBase.close();
    }

    public void onStop(){
        super.onStop();

    }

    public void onDestroy(){
        super.onDestroy();
    }


}
