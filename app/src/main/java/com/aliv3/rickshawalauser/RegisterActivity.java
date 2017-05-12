package com.aliv3.rickshawalauser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Register;
    private EditText Email;
    private EditText Name;
    private EditText MobileNumber;
    private EditText Password;
    private TextView Signin;

    //Database
    private DatabaseReference databaseReference;

    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //print to log
        System.out.println("\n\n\n\t\tREGISTER ACTIVITY \n\n\n");

        Register = (Button) findViewById(R.id.buttonregister);
        Email = (EditText) findViewById(R.id.editemail);
        Password = (EditText) findViewById(R.id.editpassword);
        Name = (EditText) findViewById(R.id.editname);
        MobileNumber = (EditText) findViewById(R.id.editmobile);
        Signin = (TextView) findViewById(R.id.textsignin);

        ProgressDialog = new ProgressDialog(this);

        Register.setOnClickListener(this);
        Signin.setOnClickListener(this);
    }

    private void registerUser() {

        String email = Email.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String mobileNumber = MobileNumber.getText().toString().trim();
        String password = Password.getText().toString().trim();

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
            postRegister(name, email, mobileNumber, password, "http://139.59.70.223/api/register");
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
    private void postRegister(String name, String email, String mobileNumber, String password, String url) throws IOException, IllegalArgumentException {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("mobile_number", mobileNumber)
                .add("password", password)
                .add("is_driver", "false")
                .add("is_client", "true")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
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
                            uiHandle(error, success);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void uiHandle(final String error, final String success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(error != "") {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                } else if (success != "") {
                    Toast.makeText(RegisterActivity.this, success, Toast.LENGTH_SHORT).show();

                    //Go to map after saving details
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
