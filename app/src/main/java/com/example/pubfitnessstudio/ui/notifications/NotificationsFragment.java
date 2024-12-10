package com.example.pubfitnessstudio.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.Fragment;
import com.example.pubfitnessstudio.R;


// For Videos

public class NotificationsFragment extends Fragment {

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Find the WebView from the layout
        WebView webView = view.findViewById(R.id.webview1);

        // Enable JavaScript in the WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Set WebViewClient to handle navigation inside WebView
        webView.setWebViewClient(new WebViewClient());

        // Optional: Set WebChromeClient for enhanced functionality (e.g., video full-screen)
        webView.setWebChromeClient(new WebChromeClient());

        // Load the YouTube video URL (replace with your desired YouTube video URL)
        String videoUrl = "https://www.youtube.com/watch?v=m1UF4RgGoY0"; // Replace VIDEO_ID with the actual video ID
        webView.loadUrl(videoUrl);

        return view;
    }
}
