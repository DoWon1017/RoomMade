package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class FragmentNotice extends Fragment {

    private WebView myWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_notification, container, false);

        myWebView = view.findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://www.hs.ac.kr/domitory/8191/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZG9taXRvcnklMkYxMzcxJTJGYXJ0Y2xMaXN0LmRvJTNG"); // 원하는 URL로 변경

        return view;
    }

    public void goBack() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        }
    }
}









