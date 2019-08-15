package com.example.findparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView textViewUserEmail;
    MenuInflater inflater;
    private ProgressBar pkLoadingIndicator2;
    private EditText editTextName, editTextAddress;
    private Button buttonSave;
    private ArrayList<String> array_list;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        listView = findViewById(R.id.listview);
        Intent intentMain = getIntent();
        if(intentMain.hasExtra("areaItems")){ // take areas from other activities
            array_list = intentMain.getStringArrayListExtra("areaItems");
        }

        firebaseAuth = firebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
        // TODO USER CAN BE UPDATE HIS PERSONAL INFORMATIONS---mallon de tha ginei kan ayto
        // TODO NA TO KANW KAI SEARCH BAR
        adapter = new ArrayAdapter<String>(this,R.layout.area_list_item, R.id.item, array_list);

        listView.setAdapter(adapter);

        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                editTextAddress.setText(adapter.getItem(position));
                listView.setVisibility(View.GONE);
            }
        });

        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        pkLoadingIndicator2 = (ProgressBar) findViewById(R.id.pb_loading_indicator2);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome\n" +user.getEmail()+ "\nplease apply the form to make you a profile");
        // adding listener to button
        buttonSave.setOnClickListener(this);
        editTextAddress.setOnClickListener(this);
    }

    private void saveUserInformation(){
            String name = editTextName.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            UserInformation userInfo = new UserInformation(name,address);

            FirebaseUser user = firebaseAuth.getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("persons");

            if (user != null) {
                databaseReference.child(user.getUid()).setValue(userInfo);
                pkLoadingIndicator2.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Information saved..", Toast.LENGTH_LONG).show();
                /** NOW USER IS READY */
                finish();
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("areaItems", array_list);
                startActivity(intent);
            }

    }

    @Override
    public void onClick(View view) {
        if(view == buttonSave){
            if(!editTextAddress.getText().toString().isEmpty()) {
                pkLoadingIndicator2.setVisibility(View.VISIBLE);
                saveUserInformation();
            } else {
                Toast.makeText(this,"Please fill any empty fields", Toast.LENGTH_LONG).show();
            }
        }
        if(view == editTextAddress){
            if(editTextAddress.getText().toString().isEmpty()) {  // check if EditTextAddress is empty
                listView.setVisibility(View.VISIBLE);
            }

        }
    }


    // Initiating Menu XML file (main1.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main_user, menu);
        return true;
    }
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option0: {
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.option1: {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
