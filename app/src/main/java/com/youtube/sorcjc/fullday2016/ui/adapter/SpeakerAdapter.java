package com.youtube.sorcjc.fullday2016.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Speaker;

import java.util.ArrayList;

public class SpeakerAdapter extends RecyclerView.Adapter<SpeakerAdapter.ViewHolder> {
    private ArrayList<Speaker> dataSet;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // context
        Context context;

        // text views
        TextView tvName;
        TextView tvPosition;
        TextView tvCompany;
        // image
        ImageView ivPhoto;
        // buttons
        Button btnMore, btnContact;
        // data
        String email, description;

        ViewHolder(View v) {
            super(v);
            context = v.getContext();

            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPosition = (TextView) v.findViewById(R.id.tvPosition);
            tvCompany = (TextView) v.findViewById(R.id.tvCompany);

            ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);

            btnMore = (Button) v.findViewById(R.id.btnMore);
            btnContact = (Button) v.findViewById(R.id.btnContact);
        }

        void setOnClickListeners() {
            btnMore.setOnClickListener(this);
            btnContact.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnMore:
                    Log.d("SpeakerAdapter", "Mostrar más datos");
                    break;
                case R.id.btnContact:
                    Log.d("SpeakerAdapter", "Mostrar dato de contacto");
                    break;
            }
        }

        void loadImage(String imageUrl) {
            final String fullPath = Global.getFullPathImage(imageUrl);
            Picasso.with(context).load(fullPath).into(ivPhoto);
        }
    }

    public SpeakerAdapter() {
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(ArrayList<Speaker> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }


    @Override
    public SpeakerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_speaker, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Speaker currentSpeaker = dataSet.get(position);

        holder.tvName.setText(currentSpeaker.getName());
        holder.tvPosition.setText(currentSpeaker.getPosition());
        holder.tvCompany.setText(currentSpeaker.getCompany());
        holder.loadImage(currentSpeaker.getImage());

        // set events
        holder.setOnClickListeners();

        // params needed to show the details
        holder.email = currentSpeaker.getEmail();
        holder.description = currentSpeaker.getDescription();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}