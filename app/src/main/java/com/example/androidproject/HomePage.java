package com.example.androidproject;

import static android.widget.SearchView.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    DatabaseReference reference;

    DatabaseReference ref;

    DatabaseReference user;

    Button logout;
    FloatingActionButton add;
    ListView link;


    ArrayList <String> linkLists = new ArrayList<>();

    final String[] keyVal = new String[1];
    String url;

    Boolean referRel = false;

    Boolean longClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        add = findViewById(R.id.addButton);
        link = findViewById(R.id.linkList);


        SharedPreferences pos = getSharedPreferences("storage.xml", 0);
        String finalPersonEmail = pos.getString("pwd", "");

        ArrayAdapter <String> linkListAdapter = new ArrayAdapter<>(HomePage.this, android.R.layout.simple_list_item_1,linkLists);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        user = FirebaseDatabase.getInstance().getReference().child("user");



        link.setAdapter(linkListAdapter);


        listUpdate(finalPersonEmail,linkListAdapter);


//        user.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                    if(messageSnapshot.getValue().equals(finalPersonEmail)){
//                        keyVal[0] = messageSnapshot.getKey();
//                    }
//                }
//                Log.d("HomePage","messageSnapshot "+ keyVal[0]);
//
//                reference = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");
//
//
//                reference.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String kVal = snapshot.getKey();
//                        linkLists.add(kVal);
//                        linkListAdapter.notifyDataSetChanged();
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        linkListAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        add.bringToFront();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                createAddDialog(finalPersonEmail);

                Intent intent = new Intent(HomePage.this,AddActivity.class);
                startActivity(intent);
            }
        });

        link.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                url = linkListAdapter.getItem(i);

                longClick = true;

                ref = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links").child(url);

                new AlertDialog.Builder(HomePage.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete")
                        .setMessage("Do you Want to Delete the item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ref.removeValue();

                                linkLists.clear();

                                reference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        String kVal = snapshot.getKey();
                                        linkLists.add(kVal);
                                        linkListAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        linkListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                        linkListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();




                return true;
            }
        });



        link.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                url = linkListAdapter.getItem(position);

                ref = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@Nullable DataSnapshot snapshot) {
                        for (DataSnapshot snap: snapshot.getChildren() ) {
                            if(snap.getKey().equals(url)){
                                if (!snap.hasChildren()){
                                    Log.d("HomePage",""+snap.getValue());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(snap.getValue(String.class)));
                                    startActivity(i);
                                }else{
                                    ArrayAdapter <String> linkListAdapter = new ArrayAdapter<>(HomePage.this, android.R.layout.simple_list_item_1,linkLists);
                                    Log.d("onDataChange","else executing");
                                    referRel = true;
                                    listUpdate(finalPersonEmail,linkListAdapter);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
            }
        });










//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//            }
//        });
    }



    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        Intent intent = new Intent(HomePage.this,MainActivity.class);
                        startActivity(intent);
                        System.exit(0);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.signout,menu);

        MenuItem signout = menu.findItem(R.id.action_signOut);
//        MenuItem search = menu.findItem(R.id.action_search);

//        SearchView searchView = (SearchView) search.getActionView();


//        searchView.setOnQueryTextListener(new OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return true;
//            }
//        });

        signout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                signOut();
                return false;
            }
        });

//        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return false;
//            }
//        });

        return true;

//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        int id = item.getItemId();


//        if (id == R.id.action_search){
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void listUpdate(String finalPersonEmail, ArrayAdapter <String> linkListAdapter){
        user.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    if(messageSnapshot.getValue().equals(finalPersonEmail)){
                        keyVal[0] = messageSnapshot.getKey();
                    }
                }
                Log.d("HomePage","messageSnapshot "+ keyVal[0]);

//                if(referRel == false){
                    reference = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");
//                }else{
//                    reference = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");
//                    reference = reference.child(url);
//                }




                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String kVal = snapshot.getKey();
                        linkLists.add(kVal);
                        linkListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        linkListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        linkListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void createAddDialog(String finalPersonEmail){
//        dialogBuilder = new AlertDialog.Builder(this);
//        final View dialogView = getLayoutInflater().inflate(R.layout.popup,null);
//
//
//        addPop = (Button)findViewById(R.id.addButtn);
//        linkPop = findViewById(R.id.linkAdd);
//        titlePop = findViewById(R.id.titleAdd);
//
//        dialogBuilder.setView(dialogView);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//
//
//        user = FirebaseDatabase.getInstance().getReference().child("user");
//        addPop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!TextUtils.isEmpty(linkPop.getText().toString()) || !TextUtils.isEmpty(titlePop.getText().toString())){
//
//
//                    user.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
//                                if (messageSnapshot.getValue().equals(finalPersonEmail)) {
//                                    keyVal[0] = messageSnapshot.getKey();
//                                }
//                            }
//
//                            reference = FirebaseDatabase.getInstance().getReference().child("data").child(keyVal[0]).child("links");
//
//                            reference.child(titlePop.getText().toString()).setValue(linkPop.getText().toString());
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                }
//            }
//        });



//    }
}