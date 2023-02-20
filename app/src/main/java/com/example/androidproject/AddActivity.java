package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    DatabaseReference reference;


    DatabaseReference user;



    private Button addPop;
    private EditText titlePop;
    private EditText linkPop;


    final String[] keyVal = new String[1];

    String finalPersonEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        SharedPreferences pos = getSharedPreferences("storage.xml", 0);
        finalPersonEmail = pos.getString("pwd", "");


        addPop = (Button)findViewById(R.id.addButtn);
        linkPop = findViewById(R.id.linkAdd);
        titlePop = findViewById(R.id.titleAdd);




        user = FirebaseDatabase.getInstance().getReference().child("user");
        addPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(linkPop.getText().toString()) || !TextUtils.isEmpty(titlePop.getText().toString())){


                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                                if (messageSnapshot.getValue().equals(finalPersonEmail)) {
                                    keyVal[0] = messageSnapshot.getKey();
                                }
                            }

                            reference = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");

                            reference.child(titlePop.getText().toString()).setValue(linkPop.getText().toString());

                            Toast.makeText(AddActivity.this, "Sucessfully Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddActivity.this,HomePage.class);
                            startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}