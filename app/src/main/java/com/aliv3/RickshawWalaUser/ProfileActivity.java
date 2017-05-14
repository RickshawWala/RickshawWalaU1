package com.aliv3.RickshawWalaUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Logout;
    private TextView Name;
    private TextView Email;
    private TextView MobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //print to log
        System.out.println("\n\n\n\t\tPROFILE ACTIVITY \n\n\n");

        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        MobileNumber = (TextView) findViewById(R.id.mobileNumber);

        Logout = (Button) findViewById(R.id.buttonlogout);
        Logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == Logout){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}