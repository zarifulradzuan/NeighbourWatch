package com.example.neighbourwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.neighbourwatch.controller.IncidentController;
import com.example.neighbourwatch.model.Incident;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ImageView imageView;
    EditText email;
    EditText password;
    Incident testIncident;
    Uri imageURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        imageView = findViewById(R.id.loginTest);
        sharedPreferences = getSharedPreferences("NeighbourhoodApp",MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        if(false){//!username.equals(null)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent,0);
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==0){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else if (requestCode==1){
                imageURI = data.getData();
                Toast.makeText(getApplicationContext(),"URI:"+ imageURI.toString(), Toast.LENGTH_SHORT);
                Incident incident = new Incident();
                incident.setAddress("Address 1");
                incident.setDate("13/06/1997");
                incident.setTime("21:00");
                incident.setDescription("Description 1");
                incident.setIncidentName("Rompakan Hati");
                incident.setLocation(new GeoPoint(0,101));
                IncidentController incidentController = new IncidentController(getApplicationContext());
                incidentController.insertIncident(incident, imageURI);
            }

        }
    }

    public void btnLogin(View view){
        IncidentController incidentController = new IncidentController(getApplicationContext());
        testIncident = new Incident();
        incidentController.getIncident(email.getText().toString(), imageView, testIncident);

    }

    public void testGet(View view){
        Toast.makeText(getApplicationContext(),testIncident.getIncidentName()+" is name", Toast.LENGTH_SHORT).show();
        email.setText(testIncident.getIncidentName());
    }
}
