package com.aliv3.RickshawWalaUser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    double originLatitude, originLongitude;
    double destinationLatitude, destinationLongitude;
    boolean doubleBackToExitPressedOnce = false;
    AutoCompleteTextView destination;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        /*
        * PlacesAutoCompleteAdapter is used to get the suggestion form google map api.

        * */
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getApplicationContext(),
                R.layout.autocomplete_list_text);

        destination = (AutoCompleteTextView) findViewById(R.id.destination);
        //setting the adapter for auto completion
        destination.setAdapter(placesAutoCompleteAdapter);

        destination.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                try {
                    Helper.getLatLongFromPlaceId(destination.getText().toString(), callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Log.d("eee", destination.getText().toString());
            }
            return false;
            }
        });

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();

        if(mLocation!= null){
            originLatitude = mLocation.getLatitude();
            originLongitude = mLocation.getLongitude();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button buttonBookRide = (Button) findViewById(R.id.buttonConfirmRide);
        buttonBookRide.setText("Request Ride");

        buttonBookRide.setVisibility(View.VISIBLE);

        buttonBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Replacing fragments on frame_main using transactions
                Bundle bundle = new Bundle();
                bundle.putDouble("destLat", destinationLatitude);
                bundle.putDouble("destLong", destinationLongitude);
                bundle.putDouble("originLat", originLatitude);
                bundle.putDouble("originLong", originLongitude);

                SrcDestFragment fragmentOperationSrcDest = new SrcDestFragment();
                fragmentOperationSrcDest.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, fragmentOperationSrcDest);
                fragmentTransaction.commit();

                buttonBookRide.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //Set specific loation (lat, long)
/*      LatLng mlore = new LatLng(12.9108, 74.8986);
        mGoogleMap.addMarker(new MarkerOptions().position(mlore).title("Pointer"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mlore));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlore, 18.0f));
*/        //Get current GPS location of user/device
        LatLng curLoc = new LatLng(originLatitude, originLongitude);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(curLoc)
                .title("Pick up Location")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.loc_marker))));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc , 18.0f));

    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pickup_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.pickup_img);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.myprofile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;

            case R.id.settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
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

    private Callback callback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
                if (response.isSuccessful()) {
//                    Log.d("RESPONSE", );
                    try {
                        JSONArray results = new JSONObject(jsonResponse).getJSONArray("results");
                        JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        destinationLatitude = location.getDouble("lat");
                        destinationLongitude = location.getDouble("lng");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
