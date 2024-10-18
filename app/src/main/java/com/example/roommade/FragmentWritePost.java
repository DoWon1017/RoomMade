package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentWritePost extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writepost, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        ImageView imageViewSelected = view.findViewById(R.id.imageViewSelected);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Button btnChooseImage = view.findViewById(R.id.btn_select_image);
        btnChooseImage.setOnClickListener(v -> chooseImage());

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextContent = view.findViewById(R.id.editTextContent);

        btnWritePost.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getActivity(), "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                savePostToFirestoreWithImage(title, content);
            }
        });

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            ImageView imageViewSelected = getView().findViewById(R.id.imageViewSelected);
            if (imageViewSelected != null) {
                imageViewSelected.setImageURI(imageUri);
                imageViewSelected.setVisibility(View.VISIBLE);
            }
        }
    }

    private void savePostToFirestoreWithImage(String title, String content) {
        // 현재 사용자 정보 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "사용자 정보가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        long timestamp = System.currentTimeMillis(); // 현재 시간 타임스탬프

        // 이미지 업로드
        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                        if (downloadUrlTask.isSuccessful()) {
                            Uri downloadUri = downloadUrlTask.getResult();
                            String imageUrl = downloadUri.toString();

                            FreeBoardPost post = new FreeBoardPost(title, content, userId, timestamp, "", imageUrl);
                            savePostToFirestore(post);
                        } else {
                            Toast.makeText(getActivity(), "이미지 URL을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    task.getException().printStackTrace();
                    Toast.makeText(getActivity(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace(); // 예외 출력
            });
        } else {
            Toast.makeText(getActivity(), "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePostToFirestore(FreeBoardPost post) {
        db.collection("freeboard_posts")
                .add(postToMap(post))
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String postId = task.getResult().getId();
                            Toast.makeText(getActivity(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            clearFields();

                            FragmentFreeBoardPost fragmentFreeBoardPost = new FragmentFreeBoardPost();
                            Bundle args = new Bundle();
                            args.putString("postId", postId);
                            args.putString("title", post.getTitle());
                            args.putString("content", post.getContent());
                            args.putString("userId", post.getUserId());
                            args.putLong("timestamp", post.getTimestamp());
                            args.putString("imageUrl", post.getImageUrl());
                            fragmentFreeBoardPost.setArguments(args);

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.containers, fragmentFreeBoardPost)
                                    .commit();
                        } else {
                            Toast.makeText(getActivity(), "게시글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Map<String, Object> postToMap(FreeBoardPost post) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("title", post.getTitle());
        postMap.put("content", post.getContent());
        postMap.put("userId", post.getUserId());
        postMap.put("timestamp", post.getTimestamp());
        postMap.put("imageUrl", post.getImageUrl());
        return postMap;
    }

    private void clearFields() {
        EditText editTextTitle = getView().findViewById(R.id.editTextTitle);
        EditText editTextContent = getView().findViewById(R.id.editTextContent);
        ImageView imageViewSelected = getView().findViewById(R.id.imageViewSelected);
        imageViewSelected.setImageURI(null);
        imageViewSelected.setVisibility(View.GONE);
        editTextTitle.setText("");
        editTextContent.setText("");
        imageUri = null; // 선택된 이미지 URI 초기화
    }
}
