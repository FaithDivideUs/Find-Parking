package com.example.findparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity implements AreaAdapter.AreaAdapterOnClickHandler {

    private MenuInflater inflater;
    private RecyclerView mRecyclerView;
    private AreaAdapter mAreaAdapter;
    private ProgressBar mLoadingIndicator;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private int counter=0,counter1=0,count=-1;
    private TextView textview1,textview2;
    private String address;
    private String name;
    private String[] data;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator3);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderDetailRef = databaseReference.child("persons").child(firebaseAuth.getCurrentUser().getUid());
        Log.i("address", orderDetailRef.toString());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(counter==0){
                        address = (String) ds.getValue();
                        counter++;
                    }else{
                        name = (String) ds.getValue();
                        // TODO check address of user like address.contains("blabla")
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




        /** ME VASI THN PERIOXH THA TOU VGAZEI
         * 1. PITHANOTHTA EYRESHS PARKING
         * 2. PROTEINOMENA PARKING KONTA TOY
         * 3. KSEXWRISTO ACTIVITY----PANW PANW THA YPARXEI MPARA ANAZHTHSHS ALLHS PERIOXHS STHN ATTIKH
         */
    }

    private void fill(){
        textview1 = (TextView) findViewById(R.id.textView3);
        textview2 = (TextView) findViewById(R.id.textView4);
        textview1.setText("Καλώς όρισες " + name + "\nΑυτά είναι τα προτεινόμενα\nparking για σένα");
        textview2.setText(address);

        DatabaseReference orderDetailRef = databaseReference.child("Πατήσια");
        // todo check what happens if there is not available parking in the area
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
        // todo ανοιγμα google maps με destination τη διεύθυνση της περιοχής
    }
}
