package com.youtube.sorcjc.fullday2016.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
        // text view
        TextView tvName;
        // buttons
        ImageButton btnLook;
        // data
        String imageBase64;
        
        ViewHolder(View v) {
            super(v);
            context = v.getContext();

            ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
            tvName = (TextView) v.findViewById(R.id.tvName);

            btnLook = (ImageButton) v.findViewById(R.id.btnLook);
        }

        void loadImage(String imageBase64) {
            this.imageBase64 = imageBase64;

            try {
                Bitmap imageBitmap = Global.decodeFromBase64(imageBase64);
                ivPhoto.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void loadEvents() {
            btnLook.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnLook) {
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("imageBase64", imageBase64);
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

        holder.loadImage(currentPhoto.getImageBase64());
        holder.tvName.setText(currentPhoto.getName());
        holder.loadEvents();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}