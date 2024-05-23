package com.example.roommade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private final int GOOGLE_LOGIN_CODE = 9001;
    private final String ALLOWED_DOMAIN = "hs.ac.kr"; // 허용된 도메인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        auth = FirebaseAuth.getInstance();

        findViewById(R.id.login).setOnClickListener(view -> googleLogin());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        findViewById(R.id.logout).setOnClickListener(view -> logout());
    }

    private void googleLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        String email = account.getEmail();
        if (email != null && email.endsWith("@" + ALLOWED_DOMAIN)) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                moveMainPage(task.getResult().getUser());
                            } else {
                                Log.e(TAG, "Firebase sign-in with credential failed", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Email domain not allowed");
            Toast.makeText(this, "허용되지 않은 도메인입니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_LOGIN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void logout() {
        // Firebase 로그아웃
        auth.signOut();

        // Google 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                // 로그아웃 후 로그인 화면으로 이동
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, Mainmenu.class));
            finish();
        }
    }
}
