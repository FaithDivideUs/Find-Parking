package com.example.findparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    MenuInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout l = findViewById(R.id.linearActivityMain);
        l.setBackgroundColor(0x00000000);

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser() != null){ // user already logged in
            // if profile is ready goto SearchActivity
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    }else{ // otherwise goto ProfileActivity to apply the form
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //todo
                }
            });

        }

        progressBar = new ProgressBar(this);
        buttonRegister = (Button) findViewById(R.id.button1);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            // stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            // stopping the function execution further
            return;
        }
        // if validations are ok


        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // user is succefully registered and logged in
                                finish();
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            }else{
                                Toast.makeText(MainActivity.this,"Could not register, please try again", Toast.LENGTH_SHORT).show();

                            }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();
        }
        if(view == buttonSignIn){
            // will open login activity
            startActivity(new Intent(this, LoginActivity.class));
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
