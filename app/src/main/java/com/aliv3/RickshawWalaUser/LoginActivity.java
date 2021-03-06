package com.aliv3.RickshawWalaUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private final int LOCATION_REQUEST_CODE = 2; //Can be any value, non zero

    private Button Login;
    private EditText Email;
    private TextInputLayout Password;
    private Button Register;
    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = Helper.getPreference("access_token");
        String username = Helper.getPreference("username");
        String password = Helper.getPreference("password");

        if (accessToken != null) {
            completeLogin();
        } else { // to auto login after registration
            if(username != null && password != null) {
                try {
                    Helper.postGetToken(username, password, "is_client", callback(username, password));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setContentView(R.layout.activity_login);

        askPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);

        Email = (EditText) findViewById(R.id.editsignemail);
        Password = (TextInputLayout) findViewById(R.id.editsignpassword);
        Login = (Button) findViewById(R.id.buttonlogin);
        Register = (Button) findViewById(R.id.textregister);
        ProgressDialog = new ProgressDialog(this);

        Login.setOnClickListener(this);
        Register.setOnClickListener(this);
    }

    private void userLogin()
    {
        String email = Email.getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please Enter your EmailID", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;

        }
        if (TextUtils.isEmpty(password)) {
            //password is empty

            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }

        ProgressDialog.setMessage("Signing In...");
        ProgressDialog.show();

        try {
            Helper.postGetToken(email, password, "is_client", callback(email, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if(view == Login){
            userLogin();
        }
        if(view == Register) {
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }
    private Callback callback(final String username, final String password) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        ProgressDialog.hide();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
//                            Log.d("RESPONSE", jsonResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String accessToken = jsonObject.getString("access_token");
                        String refreshToken = jsonObject.getString("refresh_token");

                        Helper.setPreference("username", username);
                        Helper.setPreference("password", password);
                        Helper.setPreference("access_token", accessToken);
                        Helper.setPreference("refresh_token", refreshToken);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                completeLogin();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(response.code() == 401) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Incorrect Username and Password", Toast.LENGTH_SHORT).show();
                            ProgressDialog.hide();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            ProgressDialog.hide();
                        }
                    });
                }
            }
        };
    }
    
    private void completeLogin() {
        Intent i = new Intent(LoginActivity.this, RideActivity.class);
        startActivity(i);
        finish();
    }
    private void askPermission (String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "Permission is already granted", Toast.LENGTH_SHORT).show();
        }
    }

}
