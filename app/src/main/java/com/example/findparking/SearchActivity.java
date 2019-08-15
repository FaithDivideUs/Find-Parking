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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements AreaAdapter.AreaAdapterOnClickHandler,View.OnClickListener {

    private MenuInflater inflater;
    private RecyclerView mRecyclerView;
    private AreaAdapter mAreaAdapter;
    private ProgressBar mLoadingIndicator;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private int counter=0,counter1=0,count=-1;
    private TextView textview1,textview2;
    private Button button_to_otherareas;
    private String address,destination_address;
    private String name;
    private ArrayList<String> array_list;
    private String noparking="not available parking!";
    private String[] data;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator3);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();

        button_to_otherareas = (Button) findViewById(R.id.button_findArea);
        Intent intentMain = getIntent();
        if(intentMain.hasExtra("areaItems")){ // take areas from other activities
            array_list = intentMain.getStringArrayListExtra("areaItems");
        }

        /** THIS IS FOR  RecyclerView **/
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderDetailRef = databaseReference.child("persons").child(firebaseAuth.getCurrentUser().getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(counter==0){
                        address = (String) ds.getValue();
                        counter++;
                    }else{
                        name = (String) ds.getValue();
                        fill();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e( "onCancelled", String.valueOf(databaseError.toException()));
            }
        };
        orderDetailRef.addListenerForSingleValueEvent(eventListener);

        button_to_otherareas.setOnClickListener(this);

    }

    private void fill(){
        textview1 = (TextView) findViewById(R.id.textView3);
        textview2 = (TextView) findViewById(R.id.textView4);
        textview1.setText("Καλώς όρισες " + name + "\nΑυτά είναι τα προτεινόμενα\nparking για σένα");
        textview2.setText(address);

        DatabaseReference orderDetailRef = databaseReference.child("parking").child(address);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount(); // this is the size of list
                data = new String[count];

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    result = (String) ds.getKey() + "~~yHashUi3~~" + ds.getValue(); // store infos of each parking

                    data[counter1] = result;
                    counter1++;
                }
                showDataView();
                mAreaAdapter.setAreaData(data);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e( "onCancelled", String.valueOf(databaseError.toException()));
            }
        };
        orderDetailRef.addListenerForSingleValueEvent(eventListener);
        counter1=0;
        // todo check what happens if there is not available parking in the area
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
     * This method will make the error message visible and hide the parking area View.
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

        destination_address = specific + ",+" + address;
        String map = "http://maps.google.co.in/maps?q=" + destination_address; // this url provides us the view of parking via google maps
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        getApplicationContext().startActivity(intent);

    }

    @Override
    public void onClick(View view) { // about button
        if(view == button_to_otherareas){
            finish();
            Intent intent = new Intent(getApplicationContext(), SearchAllAreas.class);
            intent.putExtra("areaItems", array_list);
            startActivity(intent);
        }

    }

    // Initiating Menu XML file (main1.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.update_profile, menu);
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
