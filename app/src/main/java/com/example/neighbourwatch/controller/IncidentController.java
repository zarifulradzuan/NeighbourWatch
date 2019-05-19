package com.example.neighbourwatch.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.neighbourwatch.IncidentAdapter;
import com.example.neighbourwatch.model.Incident;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class IncidentController {
    private FirebaseFirestore db;
    private Context context;
    private Incident incident;
    private StorageReference storageReference;
    public IncidentController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setIncident(final Incident incident){

        this.incident = incident;
    }

    public String generateFilename(Incident incident){
        LocalTime time = LocalTime.parse(incident.getTime());
        return incident.getIncidentName()+"_"+time.getHour()+"."+time.getMinute();
    }
    public void getIncidents(final IncidentAdapter incidentAdapter){
        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidents")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Incident incident = documentSnapshot.toObject(Incident.class);
                            incident.setIncidentId(documentSnapshot.getId());
                            incidents.put(incident.getIncidentId(), incident);
                        }
                    }
                });
        incidentAdapter.setIncidents(incidents);
    }

    public void getIncidents(){
        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidents")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Incident incident = documentSnapshot.toObject(Incident.class);
                            incident.setIncidentId(documentSnapshot.getId());
                            incidents.put(incident.getIncidentId(),incident);
                        }
                        ArrayList<Incident> incidentsArr = new ArrayList<>(incidents.values());
                        Toast.makeText(context, incidentsArr.get(0).getIncidentId(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getIncident(String id, final ImageView bitmap, final Incident incident){
        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidents").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Incident retrievedIncident = documentSnapshot.toObject(Incident.class);
                        incident.setPhoto(retrievedIncident.getPhoto());
                        incident.setIncidentId(documentSnapshot.getId());
                        incident.setIncidentName(retrievedIncident.getIncidentName());
                        incident.setDescription(retrievedIncident.getDescription());
                        incident.setAddress(retrievedIncident.getAddress());
                        incident.setDate(retrievedIncident.getDate());
                        incident.setTime(retrievedIncident.getTime());
                        //masukkan method untuk refresh view etc
                        new DownloadImageTask(bitmap).execute(incident.getPhoto());
                    }
                }
            }
        });

    }

    public void getIncidentsLocation(final GoogleMap googleMap){
        final HashMap<String, Marker> markers = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("incidents")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Incident incident = documentSnapshot.toObject(Incident.class);
                            Marker marker = googleMap.addMarker(getPlaceMarker());
                            marker.setTag(incident.getIncidentId());
                            markers.put(incident.getIncidentId(), marker);
                        }
                    }
        });
    }

    public void insertIncident(final Incident incident, Uri bitmapUri){
        storageReference = FirebaseStorage.getInstance().getReference();


        storageReference = storageReference.child("images/"+generateFilename(incident));
        storageReference.putFile(bitmapUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                incident.setPhoto(uri.toString());
                                db.collection("incidents")
                                        .add(incident)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(context, "Incident reported successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to add report..", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }

    /*public static void getPlace(String placeId, final PlaceInfoFragment placeInfoFragment) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("places");
        Query query = databaseReference.orderByChild("placeId").startAt(placeId).endAt(placeId);
        ChildEventListener childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Incident incident = dataSnapshot.getValue(Incident.class);
                placeInfoFragment.setPlace(incident);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    public MarkerOptions getPlaceMarker(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(this.incident.getIncidentName());
        markerOptions.position(new LatLng(this.incident.getLocation().getLatitude(),this.incident.getLocation().getLongitude()));
        return markerOptions;
    }
    //to download image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
