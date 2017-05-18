package com.aliv3.RickshawWalaUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SrcDestFragment extends Fragment {

    private android.app.ProgressDialog ProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Double destLat = getArguments().getDouble("destLat");
        final Double destLong = getArguments().getDouble("destLong");
        final Double originLat = getArguments().getDouble("originLat");
        final Double originLong = getArguments().getDouble("originLong");

        View rootView = inflater.inflate(R.layout.fragment_src_dest,container,false);

        final Button buttonConfirm = (Button) rootView.findViewById(R.id.buttonConfirmRide);
        final Button buttonCancel = (Button) rootView.findViewById(R.id.buttonCancel);

        TextView origin = (TextView) rootView.findViewById(R.id.textGetOrigin);
        origin.setText(originLat.toString() + ", " + originLong.toString());

        TextView destination = (TextView) rootView.findViewById(R.id.textGetDest);
        destination.setText(destLat.toString() + ", " + destLong.toString());

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ProgressDialog.setMessage("Creating a ride...");
                    ProgressDialog.show();
                    Helper.postRideCreate(originLat.toString(), originLong.toString(), destLat.toString(), destLong.toString(), callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonRequestRide = (Button) getActivity().findViewById(R.id.buttonConfirmRide);
                buttonRequestRide.setVisibility(View.VISIBLE);

                getFragmentManager().beginTransaction().remove(SrcDestFragment.this).commitAllowingStateLoss();
            }
        });

        return rootView;
    }

    private Callback callback () {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog.hide();
                        Toast.makeText(getActivity(), "Failed to create a ride request", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("JSON", jsonResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.has("success")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressDialog.hide();
                                    ConfirmedRideFragment fragmentOperationConfirmedRide = new ConfirmedRideFragment();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_main, fragmentOperationConfirmedRide);
                                    fragmentTransaction.commit();
                                }
                            });
                        } else if (jsonObject.has("error")) {
                            ProgressDialog.hide();
                            String error = jsonObject.getString("error");
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDialog.hide();
                            Toast.makeText(getActivity(), "Failed to create a ride request - Server Error", Toast.LENGTH_SHORT).show();
                        }
                    });
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

