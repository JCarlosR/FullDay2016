package com.youtube.sorcjc.fullday2016.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.io.response.LoginResponse;
import com.youtube.sorcjc.fullday2016.ui.activity.ChatActivity;
import com.youtube.sorcjc.fullday2016.ui.fragment.AboutFragment;
import com.youtube.sorcjc.fullday2016.ui.fragment.EventFragment;
import com.youtube.sorcjc.fullday2016.ui.fragment.PollsFragment;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "PanelActivity";

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
                        // Close all activities and open the login activity
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        final String newToken = loginResponse.getToken();
                        Global.saveInSharedPreferences(activity, "token", newToken);
                        Global.saveInSharedPreferences(activity, "lastTime", currentTime);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(PanelActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        Log.d("MyFirebase", "Token => " + FirebaseInstanceId.getInstance().getToken());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
                Global.saveInSharedPreferences(activity, "token", "");
                Global.saveInSharedPreferences(activity, "user_id", 0);
                Global.saveInSharedPreferences(activity, "name", "");

                // Close all activities and open the login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("Cancelar", null);
        adb.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_info:
                fragment = new EventFragment();
                break;

            case R.id.nav_polls:
                fragment = new PollsFragment();
                break;

            case R.id.nav_camera:
                break;

            case R.id.nav_gallery:
                break;

            case R.id.nav_settings:
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
}
