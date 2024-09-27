package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentCommunity extends Fragment {

    private Button btnNotice;
    private Button btnFreeBoard;
    private Button btnOrderDelivery;
    private Button btnExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        btnNotice = view.findViewById(R.id.btn_notice);
        btnFreeBoard = view.findViewById(R.id.btn_free_board);
        btnOrderDelivery = view.findViewById(R.id.btn_delivery);
        btnExercise = view.findViewById(R.id.btn_exercise);

        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FragmentNotice());
            }
        });

        btnFreeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FragmentFreeBoard());
            }
        });

        btnOrderDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FragmentOrderDelivery());
            }
        });

        btnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FragmentExercise());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containers, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}





