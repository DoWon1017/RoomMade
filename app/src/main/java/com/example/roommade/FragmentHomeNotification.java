package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentHomeNotification extends Fragment {

    private WebView myWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_notification, container, false);

        // 웹뷰 초기화
        myWebView = view.findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://www.hs.ac.kr/domitory/8191/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZG9taXRvcnklMkYxMzcxJTJGYXJ0Y2xMaXN0LmRvJTNG"); // 원하는 URL로 변경

        return view; // 수정된 위치


    }

    public void goBack() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        }
    }





}