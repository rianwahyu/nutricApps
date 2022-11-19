package com.rigadev.nutricapps.pages.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityBlogDetailBinding;

public class BlogDetailActivity extends AppCompatActivity {

    Context context = this;
    ActivityBlogDetailBinding  binding;

    String blogID, titleBlog, shortDescription, urlBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlogDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Detail Konten");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        blogID = getIntent().getStringExtra("blogID");
        titleBlog = getIntent().getStringExtra("titleBlog");
        shortDescription = getIntent().getStringExtra("shortDescription");
        urlBlog = getIntent().getStringExtra("urlBlog");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        /*webView.loadUrl("file:///android_asset/about.html");*/
        String url = urlBlog;

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Memuat Data ";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };
        ProgressLoadingJIGB.startLoading(context);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ProgressLoadingJIGB.setupLoading = (setup) ->  {
                    setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
                    setup.message = "Memuat Data";//  Center Message
                    setup.timer = 0;   // Time of live for progress.
                    setup.width = 200; // Optional
                    setup.hight = 200; // Optional
                };

                ProgressLoadingJIGB.startLoading(context);
                view.loadUrl(url);
                return true;
                //return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressLoadingJIGB.finishLoadingJIGB(context);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                ProgressLoadingJIGB.finishLoadingJIGB(context);
            }
        });

        binding.webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(binding.webView!= null && binding.webView.canGoBack())
            binding.webView.goBack();// if there is previous page open it
        else
            super.onBackPressed();
    }
}