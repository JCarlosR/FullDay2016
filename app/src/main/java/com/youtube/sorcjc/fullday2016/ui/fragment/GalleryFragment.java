package com.youtube.sorcjc.fullday2016.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public GalleryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        photoAdapter = new PhotoAdapter();
        recyclerView.setAdapter(photoAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadLastPhotos();
    }

    private void loadLastPhotos() {
        final int user_id = Global.getIntFromSharedPreferences(getActivity(), "user_id");
        if (user_id == 0) {
            Toast.makeText(getContext(), R.string.session_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("photos/"+user_id).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Photo> photos = new ArrayList<>();
        for (DataSnapshot photoSnap: dataSnapshot.getChildren()) {
            Photo photo = photoSnap.getValue(Photo.class);
            photos.add(photo);
        }

        photoAdapter.setDataSet(photos);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
