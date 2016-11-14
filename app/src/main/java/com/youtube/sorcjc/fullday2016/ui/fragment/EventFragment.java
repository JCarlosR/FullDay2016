package com.youtube.sorcjc.fullday2016.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.model.Speaker;
import com.youtube.sorcjc.fullday2016.ui.adapter.SpeakerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements Callback<ArrayList<Speaker>> {

    private RecyclerView recyclerView;
    private SpeakerAdapter speakerAdapter;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        speakerAdapter = new SpeakerAdapter();
        recyclerView.setAdapter(speakerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadSpeakers();
    }

    private void loadSpeakers() {
        Call<ArrayList<Speaker>> call =  FullDayApiAdapter.getApiService().getSpeakers();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ArrayList<Speaker>> call, Response<ArrayList<Speaker>> response) {
        if (response.isSuccessful()) {
            ArrayList<Speaker> speakers = response.body();
            speakerAdapter.setDataSet(speakers);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Speaker>> call, Throwable t) {
        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
