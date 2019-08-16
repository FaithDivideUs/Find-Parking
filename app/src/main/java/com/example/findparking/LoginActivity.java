package com.example.findparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private ProgressBar pkLoadingIndicator;
    private FirebaseAuth firebaseAuth;
    private MenuInflater inflater;
    private ArrayList<String> areaItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout l = findViewById(R.id.linearLoginActivity);
        l.setBackgroundColor(0x00000000);
        pkLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator1);
        areaItems = new ArrayList<String>();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){ // user already logged in
            // profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));

        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSingUp);

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            // stopping the function execution further
            pkLoadingIndicator.setVisibility(View.INVISIBLE);
            return;
        }
        if(TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            // stopping the function execution further
            pkLoadingIndicator.setVisibility(View.INVISIBLE);
            return;
        }
        // if validations are ok

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference();

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.child("parking").getChildren()) {
                    //Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    String area = ds.getKey();
                    areaItems.add(area);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //todo
            }
        });

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // start the profile activity
                            pkLoadingIndicator.setVisibility(View.INVISIBLE);
                            finish();
                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            intent.putExtra("areaItems", areaItems);
                            startActivity(intent);
                        } else{
                            Toast.makeText(LoginActivity.this,"Could not logged in, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            pkLoadingIndicator.setVisibility(View.VISIBLE);
            userLogin();
        }
        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // Initiating Menu XML file (main1.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.option1: {
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
