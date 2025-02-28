package com.example.roommade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

public class FragmentMypage extends Fragment {

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);



        // 이미지 클릭 리스너 설정
        view.findViewById(R.id.infoButton).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityA.class);
            startActivity(intent);
        });

        view.findViewById(R.id.penaltyButton).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityB.class);
            startActivity(intent);
        });

        AppCompatImageButton button = view.findViewById(R.id.communityButton);
        button.setOnClickListener(v -> {
            FragmentC fragmentC = new FragmentC();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentC)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
