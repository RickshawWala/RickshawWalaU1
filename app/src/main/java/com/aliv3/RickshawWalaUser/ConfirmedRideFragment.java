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

public class ConfirmedRideFragment extends Fragment {

    Button buttonPay;

    public ConfirmedRideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmed_ride,container,false);

//        final Button buttonStop = (Button) rootView.findViewById(R.id.buttonStop);
        buttonPay = (Button) rootView.findViewById(R.id.buttonPay);

/*        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Does nothing", Toast.LENGTH_SHORT).show();
            }
        });*/

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentsFragment fragmentOperationPayments = new PaymentsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, fragmentOperationPayments);
                fragmentTransaction.commit();
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

                        if(status.toUpperCase().equals("ACCEPTED")) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textViewStatus = (TextView) getActivity().findViewById(R.id.textRideStatus);
                                        textViewStatus.setText("Ride Accepted");
                                    }
                                });
                            }
                        } else if(status.toUpperCase().equals("STARTED")) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textViewStatus = (TextView) getActivity().findViewById(R.id.textRideStatus);
                                        textViewStatus.setText("Ride Started");
                                    }
                                });
                            }
                        } else if(status.toUpperCase().equals("PAYMENT PENDING")) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textViewStatus = (TextView) getActivity().findViewById(R.id.textRideStatus);
                                        textViewStatus.setText("Ride Over - Payment Pending");
                                        Button payBtn = (Button) getActivity().findViewById(R.id.buttonPay);
                                        payBtn.setVisibility(View.VISIBLE);
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
