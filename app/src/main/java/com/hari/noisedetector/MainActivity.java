package com.hari.noisedetector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference();

    public void PasswordRegenerate(View view){
        EditText editText =(EditText)findViewById(R.id.editText);
        EditText editText2 =(EditText)findViewById(R.id.editText2);
        String name,pass;
        name = editText.getText().toString();
        pass = editText2.getText().toString();
        DatabaseReference pref = databaseReference.child(name);

        pref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditText editText =(EditText)findViewById(R.id.editText);
                boolean found =false;
                //for (DataSnapshot d : dataSnapshot.getChildren()){
                    Map<String,String> map = (Map<String,String>)dataSnapshot.getValue();
                    // Map<String,String> m = (Map) dataSnapshot.getValue();
                    //Users u  = (Users) m.get();

                    String x = map.get(String.valueOf(editText.getText()));
                    if (x!=null){
                        found = true;
                        Toast.makeText(getApplicationContext(),"your password is "+x,Toast.LENGTH_LONG).show();
                    }
                    //method here
                //}
                if (!found)
                Toast.makeText(getApplicationContext(),"User not registered \n Plz Sign up",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void SignUp(View view){
        EditText editText =(EditText)findViewById(R.id.editText);
        EditText editText2 =(EditText)findViewById(R.id.editText2);
        String name,pass;
        name = editText.getText().toString();
        pass = editText2.getText().toString();
        Map<String,String> usersMap = new HashMap<String, String>();
        usersMap.put(name,pass);
        DatabaseReference uRef = databaseReference.child(name);
        uRef.setValue(usersMap);
        Toast.makeText(getApplicationContext(),"Data Stored successfully",Toast.LENGTH_SHORT).show();
       /* databaseReference.push().setValue(usersMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError==null){
                    Toast.makeText(getApplicationContext(),"Data Stored successfully",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Data store failed!!",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
    public void LogIn(View view){

        //final String name,pass;
        EditText editText =(EditText)findViewById(R.id.editText);
        EditText editText2 =(EditText)findViewById(R.id.editText2);
        String name,pass;
        name = editText.getText().toString();
        pass = editText2.getText().toString();
        DatabaseReference lref = databaseReference.child(name);
        if (lref.getRef()==null){
            Toast.makeText(getApplicationContext(),"Not a valid user plz sign up!",Toast.LENGTH_SHORT).show();
        }
    else {
            lref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean found = false;

                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    String x = map.get(String.valueOf(editText.getText()));
                    if (x != null) {
                        if (x.equals(String.valueOf(editText2.getText()))) {
                            Intent i = new Intent(MainActivity.this, Main2Activity.class);
                            i.putExtra("username", editText.getText());
                            MainActivity.this.startActivity(i);
                            found = true;
                            Toast.makeText(getApplicationContext(), "LOG IN SUCCESSFULL", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect password ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //method here
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
