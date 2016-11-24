package com.youtube.sorcjc.fullday2016.ui.fragment;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.model.Photo;
import com.youtube.sorcjc.fullday2016.model.Question;
import com.youtube.sorcjc.fullday2016.model.Speaker;
import com.youtube.sorcjc.fullday2016.ui.activity.ChatActivity;
import com.youtube.sorcjc.fullday2016.ui.adapter.PhotoAdapter;
import com.youtube.sorcjc.fullday2016.ui.adapter.SpeakerAdapter;

import java.util.ArrayList;

import retrofit2.Call;

public class GalleryFragment extends Fragment implements ValueEventListener {

    private PhotoAdapter photoAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    public GalleryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        GridLayoutManager layoutManager;

        int screenLayout = getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                layoutManager = new GridLayoutManager(getContext(), 1);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                layoutManager = new GridLayoutManager(getContext(), 1);
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                layoutManager = new GridLayoutManager(getContext(), 2);
                break;
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                layoutManager = new GridLayoutManager(getContext(), 3);
                break;
            default:
                layoutManager = new GridLayoutManager(getContext(), 2);
                break;
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        photoAdapter = new PhotoAdapter();
        recyclerView.setAdapter(photoAdapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadLastPhotos();
    }

    private void loadLastPhotos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("photos").addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Photo> photos = new ArrayList<>();
        for (DataSnapshot photoSnap: dataSnapshot.getChildren()) {
            Photo photo = photoSnap.getValue(Photo.class);
            photos.add(photo);
        }

        photoAdapter.setDataSet(photos);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
