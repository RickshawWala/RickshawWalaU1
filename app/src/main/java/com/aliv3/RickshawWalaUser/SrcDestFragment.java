package com.aliv3.RickshawWalaUser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SrcDestFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_src_dest,container,false);

        final Button buttonConfirm = (Button) rootView.findViewById(R.id.buttonRequestRide);
        final Button buttonCancel = (Button) rootView.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonRequestRide = (Button) getActivity().findViewById(R.id.buttonRequestRide);
                buttonRequestRide.setVisibility(View.VISIBLE);

                getFragmentManager().beginTransaction().remove(SrcDestFragment.this).commitAllowingStateLoss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmedRideFragment fragmentOperationConfirmedRide = new ConfirmedRideFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, fragmentOperationConfirmedRide);
                fragmentTransaction.commit();
            }
        });

        return rootView;
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

