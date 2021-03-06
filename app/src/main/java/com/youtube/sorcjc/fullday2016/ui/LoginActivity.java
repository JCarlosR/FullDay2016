package com.youtube.sorcjc.fullday2016.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.io.fcm.MyFirebaseInstanceIDService;
import com.youtube.sorcjc.fullday2016.io.response.LoginResponse;
import com.youtube.sorcjc.fullday2016.ui.PanelActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Callback<LoginResponse> {

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        Button btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);

        // There is an active session?
        final String token = Global.getFromSharedPreferences(this, "token");
        final int user_id = Global.getIntFromSharedPreferences(this, "user_id");
        final String name = Global.getFromSharedPreferences(this, "name");
        if (!token.isEmpty() && user_id!=0 && !name.isEmpty()) {
            Intent intent = new Intent(this, PanelActivity.class);
            startActivity(intent);
            finish();
        }

        // So sad... in that case just fill with the latest email input
        etEmail.setText(Global.getFromSharedPreferences(this, "email"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                // Store the latest email input
                Global.saveInSharedPreferences(this, "email", email);

                if (password.length() < 6) {
                    Toast.makeText(this, R.string.password_min_length, Toast.LENGTH_SHORT).show();
                    return;
                }

                Call<LoginResponse> call = FullDayApiAdapter.getApiService().getLogin(
                        email, password, FirebaseInstanceId.getInstance().getToken()
                );
                call.enqueue(this);

                break;

            case R.id.btnRegister:
                Uri uriRegister = Uri.parse("http://fulldayunt.com/register"); // missing 'http://' will cause crashed
                Intent intentRegister = new Intent(Intent.ACTION_VIEW, uriRegister);
                startActivity(intentRegister);
                break;

            case R.id.btnResetPassword:
                Uri uri = Uri.parse("http://fulldayunt.com/password/reset");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        if (response.isSuccessful()) {
            LoginResponse loginResponse = response.body();

            if (loginResponse.isError()) {
                Toast.makeText(this, R.string.message_incorrect_credentials, Toast.LENGTH_SHORT).show();
            } else {
                Global.saveInSharedPreferences(this, "token", loginResponse.getToken());
                Global.saveInSharedPreferences(this, "user_id", loginResponse.getUserId());
                Global.saveInSharedPreferences(this, "name", loginResponse.getName());

                Intent intent = new Intent(this, PanelActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, R.string.message_incorrect_format, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
