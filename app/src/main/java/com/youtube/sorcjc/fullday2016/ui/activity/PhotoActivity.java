package com.youtube.sorcjc.fullday2016.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivPhoto;
    private ProgressBar progressBar;
    private Button btnShare, btnReTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadImageFromStorage();

        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        btnReTry = (Button) findViewById(R.id.btnReTry);
        btnReTry.setOnClickListener(this);
    }

    private void loadImageFromStorage() {
        System.gc();

        // Create a storage reference
        String imageKey = Global.getFromSharedPreferences(this, "imageKey");
        if (imageKey.isEmpty())
            finish();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://full-day-2016.appspot.com/");
        storageRef.child("images/"+imageKey+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString())
                    .into(ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            btnShare.setEnabled(true);
                            btnReTry.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            ivPhoto.setImageResource(R.drawable.background_splash);
                            btnReTry.setVisibility(View.VISIBLE);
                            btnReTry.setEnabled(true);
                        }
                    });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ivPhoto.setImageResource(R.drawable.background_splash);
                btnReTry.setVisibility(View.VISIBLE);
                btnReTry.setEnabled(true);
            }
        });
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
            case R.id.btnReTry:
                loadImageFromStorage();
                btnReTry.setEnabled(false);
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
