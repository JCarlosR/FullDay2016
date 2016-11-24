package com.youtube.sorcjc.fullday2016.ui.activity;

import android.graphics.Bitmap;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;

import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String imageBase64 = getIntent().getStringExtra("imageBase64");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ImageView ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        try {
            Bitmap imageBitmap = Global.decodeFromBase64(imageBase64);
            ivPhoto.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
