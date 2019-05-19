package com.example.neighbourwatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neighbourwatch.controller.IncidentController;
import com.example.neighbourwatch.model.Incident;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder>{
    private ArrayList<Incident> incidents;
    private Context context;

    public IncidentAdapter() {
    }
    public IncidentAdapter(Context context){
        incidents = new ArrayList<>();
        this.context = context;
    }

    public void setIncidents(HashMap<String, Incident> incidents){
        this.incidents = new ArrayList<>(incidents.values());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_card,parent,false);
        return new ViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Incident incident = incidents.get(position);
        IncidentController incidentController = new IncidentController(context);
        holder.address.setText(incident.getAddress());
        holder.description.setText(incident.getDescription());
        holder.incidentName.setText(incident.getIncidentName());
        //holder.photo.set

        holder.dateTime.setText(incident.getTime()+","+
                                incident.getDate());
        holder.address.setText(incident.getAddress());
        //FireStore only stores image url, get image url, then download and display
        String photoUrl = incident.getPhoto();
        new DownloadImageTask(holder.photo)
                .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView incidentName, dateTime, address, description;
        public ImageView photo;
        public ViewHolder(View itemView) {
            super(itemView);
            incidentName = itemView.findViewById(R.id.cardIncidentName);
            dateTime = itemView.findViewById(R.id.cardDescription);
            address = itemView.findViewById(R.id.cardAddress);
            description= itemView.findViewById(R.id.cardDescription);
            photo = itemView.findViewById(R.id.cardPhoto);
        }
    }

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
