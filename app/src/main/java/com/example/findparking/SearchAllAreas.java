package com.example.findparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchAllAreas extends AppCompatActivity implements AreaAdapter.AreaAdapterOnClickHandler{

    private MenuInflater inflater;
    private ArrayList<String> array_list = null;
    private RecyclerView mRecyclerView;
    private Spinner spinner;
    private AreaAdapter mAreaAdapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private   List<String> areas_list = new ArrayList<String>();
    private ProgressBar bar;
    private int counter=0,count=-1,position_spinner;
    private String[] data;
    private String result;
    private String noparking="not available parking!";
    private String destination_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_areas);
        bar = (ProgressBar) findViewById(R.id.pb_loading_indicator4);
        bar.setVisibility(View.VISIBLE); // enable progress bar
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intentMain = getIntent();
        if(intentMain.hasExtra("areaItems")){ // take areas from other activities
            array_list = intentMain.getStringArrayListExtra("areaItems");
            if (array_list != null) {
                areas_list.addAll(array_list);
            }
        }

        /** THIS IS FOR  RecyclerView **/
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        /**
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages. */
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        /* The AreaAdapter is responsible for linking our parking data with the Views that
         * will end up displaying our area data.*/
        mAreaAdapter = new AreaAdapter((AreaAdapter.AreaAdapterOnClickHandler) this);
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mAreaAdapter);

        // initialize spinner and add data
        spinner = (Spinner)findViewById(R.id.mySpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areas_list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setSelection(0,false);
        /** --------only the first time-------- */
        receive_parkingdata(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                bar.setVisibility(View.VISIBLE); // enable progress bar

                position_spinner=position;
                receive_parkingdata(position);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                // nothing to do here
            }
        });



    }


    private void receive_parkingdata(Integer pos){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderDetailRef = databaseReference.child("parking").child(array_list.get(pos));
        // todo check what happens if there is not available parking in the area
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount(); // this is the size of list
                data = new String[count];
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    result = (String) ds.getKey() + "~~yHashUi3~~" + ds.getValue(); // store infos of each parking

                    data[counter] = result;
                    counter++;
                }
                showDataView();
                mAreaAdapter.setAreaData(data);
                bar.setVisibility(View.INVISIBLE); // disable progress bar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e( "onCancelled", String.valueOf(databaseError.toException()));
            }
        };
        orderDetailRef.addListenerForSingleValueEvent(eventListener);
        counter=0;
    }

    // Initiating Menu XML file (main1.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lastactivity, menu);
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
                finish();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("persons");
                databaseReference.child(user.getUid()).removeValue();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("areaItems", array_list);
                startActivity(intent);
                return true;
            }
            case R.id.option2: {
                finish();
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("areaItems", array_list);
                startActivity(intent);
                return true;
            }
            case R.id.option3: {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This method will make the View for the article data visible and
     * hide the error message.
     */
    private void showDataView() {
        /* Then, make sure the parking data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    /**
     * This method will make the error message visible and hide the article View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(String specific) {


        if(specific.equals(noparking)){ // if field is empty
            Toast.makeText(this, specific, Toast.LENGTH_SHORT)
                    .show();
        } else {
            String[] bits = specific.split("~~yHashUi3~~");
            specific = bits[1]; // we only need the address
            specific.replaceAll(" ","+");
        }
        destination_address = specific + ",+" + array_list.get(position_spinner);
        String map = "http://maps.google.co.in/maps?q=" + destination_address;  // this url provides us the view of parking via google maps
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        getApplicationContext().startActivity(intent);
    }


}
