package com.example.neighbourwatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neighbourwatch.controller.IncidentController;


public class ProgrammeFragment extends Fragment {


    RecyclerView incidentRecycler;
    IncidentAdapter incidentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.programme_fragment, container, false);

        return rootView;
    }
}
