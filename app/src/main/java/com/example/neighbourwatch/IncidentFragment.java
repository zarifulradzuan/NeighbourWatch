package com.example.neighbourwatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neighbourwatch.controller.IncidentController;


public class IncidentFragment extends Fragment {


    RecyclerView incidentRecycler;
    IncidentAdapter incidentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.incident_fragment, container, false);
        incidentRecycler = rootView.findViewById(R.id.incidentRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        incidentAdapter = new IncidentAdapter(getContext());
        IncidentController incidentController = new IncidentController(getContext());
        incidentController.getIncidents(incidentAdapter);
        incidentRecycler.setLayoutManager(layoutManager);
        incidentRecycler.setAdapter(incidentAdapter);
        return rootView;
    }
}
