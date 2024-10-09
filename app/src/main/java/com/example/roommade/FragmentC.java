package com.example.roommade;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class FragmentC extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_c.xml 레이아웃 파일을 설정
        View view = inflater.inflate(R.layout.activity_c, container, false);

        Button deliveryButton = view.findViewById(R.id.deliveryBoardButton);
        deliveryButton.setOnClickListener(v -> {
            FragmentMyDeliveryPost fragmentMyDeliveryPost = new FragmentMyDeliveryPost();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentMyDeliveryPost)
                    .addToBackStack(null)
                    .commit();
        });

        Button exerciseButton = view.findViewById(R.id.sportsBoardButton);
        exerciseButton.setOnClickListener(v -> {
            FragmentMyExercisePost fragmentMyExercisePost = new FragmentMyExercisePost();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentMyExercisePost)
                    .addToBackStack(null)
                    .commit();
        });

        Button communityButton = view.findViewById(R.id.communityBoardButton);
        communityButton.setOnClickListener(v -> {
            FragmentMyCommunityPost fragmentMyCommunityPost = new FragmentMyCommunityPost();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentMyCommunityPost)
                    .addToBackStack(null)
                    .commit();
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }
}
