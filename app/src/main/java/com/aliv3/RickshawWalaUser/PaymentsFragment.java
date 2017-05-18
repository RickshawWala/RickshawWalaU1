package com.aliv3.RickshawWalaUser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaymentsFragment extends Fragment {


    public PaymentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payments,container,false);

//        final ImageButton buttonCash = (ImageButton) rootView.findViewById(R.id.buttonCash);
//        final ImageButton buttonPaytm = (ImageButton) rootView.findViewById(R.id.buttonPaytm);
        final Button buttonCloseApp = (Button) rootView.findViewById(R.id.buttonCloseApp);

//        buttonCash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Pay Cash", Toast.LENGTH_SHORT).show();
//            }
//        });

        /*buttonPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Paytm", Toast.LENGTH_SHORT).show();
            }
        });*/

        buttonCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Helper.getRideStatus(callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5*1000);

        return rootView;
    }

    private Callback callback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RideActivity", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("JSON", jsonResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String status = jsonObject.getString("status");
                        final Double fare = jsonObject.getDouble("fare");

                        if(status.toUpperCase().equals("PAYMENT COMPLETED")) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textViewStatus = (TextView) getActivity().findViewById(R.id.textRideStatus1);
                                        textViewStatus.setText("Payment Complete");
                                        Button buttonCloseApp = (Button) getActivity().findViewById(R.id.buttonCloseApp);
                                        buttonCloseApp.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } else{
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView getFare = (TextView) getActivity().findViewById(R.id.getFareIs);
                                        getFare.setText("₹ " + fare.toString());
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("FAILED RESPONSE", jsonResponse);
                    Log.d("RideActivity", "Failed to get status update for the ride from the api");
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
