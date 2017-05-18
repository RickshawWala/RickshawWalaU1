package com.aliv3.RickshawWalaUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Register;
    private EditText Email;
    private EditText Name;
    private EditText MobileNumber;
    private TextInputLayout Password;
    private Button Signin;

    //Database
    private DatabaseReference databaseReference;

    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Register = (Button) findViewById(R.id.buttonregister);
        Email = (EditText) findViewById(R.id.editemail);
        Password = (TextInputLayout) findViewById(R.id.editpassword);
        Name = (EditText) findViewById(R.id.editname);
        MobileNumber = (EditText) findViewById(R.id.editmobile);
        Signin = (Button) findViewById(R.id.textsignin);

        ProgressDialog = new ProgressDialog(this);

        Register.setOnClickListener(this);
        Signin.setOnClickListener(this);
    }

    private void registerUser() {

        String email = Email.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String mobileNumber = MobileNumber.getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter your EmailID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "Please Enter your Phone", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog.setMessage("Registering...");
        ProgressDialog.show();

        try {
            Helper.postRegisterUser(name, email, mobileNumber, password, callback(email, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == Register) {
            registerUser();
        }
        if (view == Signin)
        {
            //open login activity here
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    private Callback callback(final String email, final String password) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
//                        Log.d("RESPONSE", jsonResponse);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(jsonResponse);
                    String error = "", success = "";
                    if(jsonObject.has("error")) {
                        error = jsonObject.getString("error");
                    } else if(jsonObject.has("success")) {
                        success = jsonObject.getString("success");
                    }
                    uiHandle(error, success, email, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void uiHandle(final String error, final String success, final String email, final String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(error != "") {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                } else if (success != "") {
                    Toast.makeText(RegisterActivity.this, success, Toast.LENGTH_SHORT).show();

                    Helper.setPreference("username", email);
                    Helper.setPreference("password", password);

                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
