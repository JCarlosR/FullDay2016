package com.youtube.sorcjc.fullday2016;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.youtube.sorcjc.fullday2016.io.fcm.MyFirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MyFirebase", FirebaseInstanceId.getInstance().getToken());
    }
}
