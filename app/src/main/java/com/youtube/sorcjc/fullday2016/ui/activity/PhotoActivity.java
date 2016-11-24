package com.youtube.sorcjc.fullday2016.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivPhoto;

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

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        try {
            Bitmap imageBitmap = Global.decodeFromBase64(imageBase64);
            ivPhoto.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShare:
                performShareAction();
                break;
        }
    }

    private void performShareAction() {
        String shareBody = "II Full Day sobre Gestión de TI en la UNT";

        Bitmap icon = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "II Full Day UNT");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }
}
