package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    SharedPreferences pos;
    public String fileName = "storage.xml";

    SignInButton signInButton;

    GoogleSignInClient mGoogleSignInClient;

    private static int RC_SIGN_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        signInButton.setSize(SignInButton.SIZE_STANDARD);



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNode = FirebaseDatabase.getInstance();
                signIn1();
            }
        });
    }

    private void signIn1() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personEmail = acct.getEmail();
//                String val;
//                int iend = personEmail.indexOf("@");
//                personEmail = personEmail.replace(".","%");
                final boolean[] exists = {false};
//                personEmail = personEmail.substring(0,iend);
                Log.d("String",personEmail);


                pos = getSharedPreferences(fileName, 0);
                SharedPreferences.Editor editor = pos.edit();
                editor.putString("pwd",personEmail);
                editor.commit();

                reference = rootNode.getReference();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                String finalPersonEmail = personEmail;

                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("user");


                postRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                            if(messageSnapshot.getValue().equals(finalPersonEmail))
                                exists[0] = true;


                            Log.d("MainActivity","messageSnapshot "+messageSnapshot.getValue());

                        }

                        if (exists[0]==true) {
                            Log.d("MainActivity","Inside true function");
                            Log.d("MainActivity",""+dataSnapshot);
                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            Toast.makeText(MainActivity.this, "already exists", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            Log.d("MainActivity","Inside false function");
//                            Toast.makeText(MainActivity.this, ""+dataSnapshot, Toast.LENGTH_SHORT).show();
                            reference.child("user").push().setValue(finalPersonEmail);
                            Log.d("MainActivity",""+dataSnapshot.getValue());
                            Toast.makeText(MainActivity.this, "creating new", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            startActivity(intent);
                        }
                    }


//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//
//                        if (dataSnapshot.getValue().equals(finalPersonEmail)) {
//                            Log.d("MainActivity","Inside true function");
//                            Log.d("MainActivity",""+dataSnapshot);
//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            Toast.makeText(MainActivity.this, "already exists", Toast.LENGTH_SHORT).show();
//                            startActivity(intent);
//                        } else {
//                            Log.d("MainActivity","Inside false function");
////                            Toast.makeText(MainActivity.this, ""+dataSnapshot, Toast.LENGTH_SHORT).show();
//                            reference.child("user").push().setValue(finalPersonEmail);
//                            Log.d("MainActivity",""+dataSnapshot.getValue());
//                            Toast.makeText(MainActivity.this, "creating new", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            startActivity(intent);
//                        }
//                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        Log.d("MainActivity","Inside onDataChange");
//                        if (snapshot.hasChild(finalPersonEmail)) {
//                            exists[0] = true;
//                        }
//                        if (exists[0] == true){
//                            Log.d("MainActivity","Inside true function");
//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            startActivity(intent);
//                        }
//                        else{
//                            Log.d("MainActivity","Inside false function");
//                            reference.child(finalPersonEmail).setValue("");
////                            reference.child(finalPersonEmail).child("Links").setValue("");
////                            reference.child(finalPersonEmail).child("Bookmark").setValue("");
////                            reference.child(finalPersonEmail).child("Shared").setValue("");
//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            startActivity(intent);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("onCancelled","Error has occured");
//                    }
//                });








            }
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Message", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
}