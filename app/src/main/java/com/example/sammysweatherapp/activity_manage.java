package com.example.sammysweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.sql.Array;
import java.util.Map;

public class activity_manage extends AppCompatActivity {

    ListView lvLocations;
    SharedPreferences sharedPref;
    ActionBar myActionBar;
    TextView tvSelected;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        lvLocations = findViewById(R.id.listView);
        registerForContextMenu(lvLocations);
        setListView();
    }

    private void setListView(){
        int i = 0;
        String cities[] = new String[getSizeOfSharedPreferences()];
        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry: keys.entrySet()){
            cities[i] = entry.getKey();
            i++;
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_view, R.id.listViewItems, cities);
        lvLocations.setAdapter(arrayAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        //get the info on which item was selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //Retrieve the item that was clicked on
        Object item = arrayAdapter.getItem(info.position);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Remove")){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object ob = arrayAdapter.getItem(info.position);
            String oldText = (String) ob;
            sharedPref.edit().remove(oldText).commit();
            setListView();
            Map<String,?> keys = sharedPref.getAll();
            for(Map.Entry<String,?> entry: keys.entrySet()){
                Toast.makeText(this, entry.getKey(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "TESTEST", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private int getSizeOfSharedPreferences(){
        int count = 0;
        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry: keys.entrySet()){
            count++;
        }
        return count;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }
}