package com.aliv3.RickshawWalaUser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Logout;
    private TextView Name;
    private TextView Email;
    private TextView MobileNumber;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        MobileNumber = (TextView) findViewById(R.id.mobileNumber);

        Logout = (Button) findViewById(R.id.buttonlogout);
        Logout.setOnClickListener(this);

        try {
            Helper.GETUser(callback());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Callback callback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("RESPONSE", jsonResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        final String name = jsonObject.getString("name");
                        final String email = jsonObject.getString("email");
                        final String mobileNumber = jsonObject.getString("mobile_number");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Name.setText(name);
                                Email.setText(email);
                                MobileNumber.setText(mobileNumber);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(response.code() == 401) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileActivity.this, "Authentication error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        if(view == Logout){
            Helper.clearAllPreferences();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

        finish();
    }
}