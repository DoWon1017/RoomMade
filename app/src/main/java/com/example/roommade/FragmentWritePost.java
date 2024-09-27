package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class FragmentWritePost extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writepost, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextContent = view.findViewById(R.id.editTextContent);

        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getActivity(), "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 게시글 저장 로직을 여기에 추가
                    Toast.makeText(getActivity(), "게시글이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 입력 필드 초기화
                    editTextTitle.setText("");
                    editTextContent.setText("");
                }
            }
        });

        return view;
    }
}

