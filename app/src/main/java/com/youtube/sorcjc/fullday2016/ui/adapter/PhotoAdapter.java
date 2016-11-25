package com.youtube.sorcjc.fullday2016.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Photo;
import com.youtube.sorcjc.fullday2016.ui.activity.PhotoActivity;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private ArrayList<Photo> dataSet;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // context
        Context context;
        // image
        ImageView ivPhoto;
        ProgressBar progressBar;
        // text view
        TextView tvName;
        // buttons
        ImageButton btnLook;
        // data
        String imageKey;
        
        ViewHolder(View v) {
            super(v);
            context = v.getContext();

            ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            tvName = (TextView) v.findViewById(R.id.tvName);

            btnLook = (ImageButton) v.findViewById(R.id.btnLook);
        }

        void loadImage(String imageKey) {
            this.imageKey = imageKey;

            // Create a storage reference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://full-day-2016.appspot.com/");
            storageRef.child("thumbnails/"+imageKey+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri.toString())
                        .into(ivPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                            }
                        });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    ivPhoto.setImageResource(R.drawable.logo_app);
                }
            });
        }

        void loadEvents() {
            btnLook.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnLook) {
                Global.saveInSharedPreferences((Activity) context, "imageKey", this.imageKey);
                Intent intent = new Intent(context, PhotoActivity.class);
                context.startActivity(intent);
            }
        }
    }

    public PhotoAdapter() {
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(ArrayList<Photo> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }


    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo currentPhoto = dataSet.get(position);

        holder.loadImage(currentPhoto.getKey());
        holder.tvName.setText(currentPhoto.getName());
        holder.loadEvents();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}