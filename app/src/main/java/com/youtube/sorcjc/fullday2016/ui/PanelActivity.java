package com.youtube.sorcjc.fullday2016.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.ui.activity.SurveyActivity;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.io.response.LoginResponse;
import com.youtube.sorcjc.fullday2016.io.response.SurveyResponse;
import com.youtube.sorcjc.fullday2016.model.Photo;
import com.youtube.sorcjc.fullday2016.model.Survey;
import com.youtube.sorcjc.fullday2016.ui.activity.ChatActivity;
import com.youtube.sorcjc.fullday2016.ui.fragment.AboutFragment;
import com.youtube.sorcjc.fullday2016.ui.fragment.EventFragment;
import com.youtube.sorcjc.fullday2016.ui.fragment.GalleryFragment;
import com.youtube.sorcjc.fullday2016.ui.fragment.PollsFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Callback<SurveyResponse> {

    private static final String TAG = "PanelActivity";
    ArrayList<Survey> arrayList;

    @Override
    protected void onStart() {
        super.onStart();

        long currentTime = new Date().getTime();
        // Last time saved in shared preference
        final long lastTime = Global.getLongFromSharedPreferences(this, "lastTime");

        if (lastTime == 0) {
            // Save immediately if there is no a preference
            Global.saveInSharedPreferences(this, "lastTime", currentTime);
        } else {
            // Difference in seconds
            double diff = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastTime);

            if (diff >= 60*30) // 30 minutes
                requestNewToken(currentTime);
        }
    }

    private void requestNewToken(final long currentTime) {
        final String currentToken = Global.getFromSharedPreferences(this, "token");
        final Activity activity = this;

        Call<LoginResponse> call = FullDayApiAdapter.getApiService().getNewToken(currentToken);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isError()) {
                        clearAndGoToLogin(activity);
                    } else {
                        final String newToken = loginResponse.getToken();
                        Global.saveInSharedPreferences(activity, "token", newToken);
                        Global.saveInSharedPreferences(activity, "lastTime", currentTime);
                    }
                } else {
                    clearAndGoToLogin(activity);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(PanelActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAndGoToLogin(Activity activity) {
        // Clear shared preferences
        Global.clearSharedPreferences(activity);
        Toast.makeText(PanelActivity.this, R.string.session_expired, Toast.LENGTH_SHORT).show();

        // Close all activities and open the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tvInstructions = (TextView) findViewById(R.id.tvInstructions);
        tvInstructions.setText(Html.fromHtml(getString(R.string.instructions)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        final Activity activity = this;

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Confirmar para salir");
        adb.setMessage("¿Está seguro que desea cerrar sesión?");

        adb.setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Clear shared preferences
                Global.clearSharedPreferences(activity);

                // Close all activities and open the login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("Cancelar", null);
        adb.show();
    }

    private  void fetchQuestions(){
        Call<SurveyResponse> call = FullDayApiAdapter.getApiService().getSurvey(Global.getFromSharedPreferences(this, "token"));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
        if (response.isSuccessful()) {
            SurveyResponse surveyResponse = response.body();
            arrayList = surveyResponse.getSurvey();

            Intent intent = new Intent(this, SurveyActivity.class);
            intent.putExtra("arrayList", arrayList);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(Call<SurveyResponse> call, Throwable t) {
        Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Calendar c = Calendar.getInstance();

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_info:
                fragment = new EventFragment();
                break;

            case R.id.nav_polls:
                int dia = c.get(Calendar.DATE);
                if (dia==26) {
                    fetchQuestions();
                    Toast.makeText(this, R.string.loading_questions, Toast.LENGTH_SHORT).show();
                } else {
                    fragment = new PollsFragment();
                }
                break;

            case R.id.nav_camera:
                startCamera();
                break;

            case R.id.nav_gallery:
                fragment = new GalleryFragment();
                break;

            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
        }

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_panel, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
                break;
        }
    }

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private final String DEFAULT_PHOTO_EXTENSION = "jpg";
    private String currentPhotoPath; // location of the last photo taken

    private void startCamera() {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createDestinationFile();
        } catch (IOException ex) {
            return;
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createDestinationFile() throws IOException {
        // Path for the temporary image and its name
        final File storageDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        final String imageFileName = "" + System.currentTimeMillis();

        File image = File.createTempFile(
                imageFileName,          // prefix
                "." + DEFAULT_PHOTO_EXTENSION, // suffix
                storageDirectory              // directory
        );

        // Save a the file path
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap fullBitmap = BitmapFactory.decodeFile(currentPhotoPath); // full size image
            postPicture(fullBitmap);
            boolean deleted = new File(currentPhotoPath).delete();
            if (! deleted) {
                Toast.makeText(this, R.string.optional_photo_delete, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void postPicture(Bitmap bitmap) {
        final byte[] imageData = Global.getDataFromBitmap(bitmap);
        final byte[] thumbnailData = Global.getThumbnailFromBitmap(bitmap);

        // Take the user data
        final int userId = Global.getIntFromSharedPreferences(this, "user_id");
        if (userId == 0) {
            Toast.makeText(PanelActivity.this, R.string.session_expired, Toast.LENGTH_SHORT).show();
            return;
        }
        final String name = Global.getFromSharedPreferences(this, "name");

        // Create a photo object
        Photo newPhoto = new Photo();
        newPhoto.setName(name);
        newPhoto.setUserId(userId);
        // Store into the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("images");
        final String newPhotoKey = ref.push().getKey();
        // newPhoto.setKey(newPhotoKey); // it is redundant
        ref.child(newPhotoKey).setValue(newPhoto, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference ref) {
                if (databaseError != null) {
                    Toast.makeText(PanelActivity.this, R.string.error_photo_upload, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PanelActivity.this, R.string.success_photo_uploading, Toast.LENGTH_SHORT).show();
                    uploadToStorage(imageData, thumbnailData, newPhotoKey);
                }
            }
        });
    }

    private void uploadToStorage(byte[] image, byte[] thumbnail, String key) {
        // Create a storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://full-day-2016.appspot.com/");

        // Specific references
        StorageReference imagesRef = storageRef.child("images/"+key+".jpg");
        StorageReference thumbnailsRef = storageRef.child("thumbnails/"+key+".jpg");

        UploadTask uploadImageTask = imagesRef.putBytes(image);
        uploadImageTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PanelActivity.this, R.string.failure_image_upload, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(PanelActivity.this, R.string.success_image_upload, Toast.LENGTH_SHORT).show();
            }
        });

        UploadTask uploadThumbnailTask = thumbnailsRef.putBytes(thumbnail);
        uploadThumbnailTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PanelActivity.this, R.string.failure_thumbnail_upload, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(PanelActivity.this, R.string.success_thumbnail_upload, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
