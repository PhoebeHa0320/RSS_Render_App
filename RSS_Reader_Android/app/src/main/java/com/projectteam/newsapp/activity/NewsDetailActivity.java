package com.projectteam.newsapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.projectteam.newsapp.R;
import com.squareup.picasso.Picasso;


public class NewsDetailActivity extends AppCompatActivity {

    public WebView webView;
    public ImageView imgHeader;
    public String mUrl, mImg, mTitle, mSubTitle, mSource, mPubdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        webView = findViewById(R.id.webView);
        imgHeader = findViewById(R.id.backdrop);

        initCollapsingToolbar();

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        try
        {
            mImg = intent.getStringExtra("img");
            Picasso.get().load(mImg).into(imgHeader);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            int notavailable = R.drawable.not_available;
            imgHeader.setImageResource(notavailable);
        }
        mTitle = intent.getStringExtra("title");
        mSubTitle = intent.getStringExtra("title");
        mSource = intent.getStringExtra("source");
        mPubdate = intent.getStringExtra("pubdate");

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgHeader);
        initWebView(mUrl);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url)
    {
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Handler handler3 = new Handler(Looper.getMainLooper());
                handler3.post(() -> {
                    //UI Thread work here
                    //Hide all float button and Extended Float Button
                    WebView webView = findViewById(R.id.webView);
                    webView.setWebChromeClient(new MyWebChromeClient(this));
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            invalidateOptionsMenu();
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            webView.loadUrl(url);
                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            invalidateOptionsMenu();
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);
                            invalidateOptionsMenu();
                        }
                    });
                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setDisplayZoomControls(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(url);

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the txtPostTitle when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(mSource);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.topbar_webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sharebtn) {
            //share news
            try {
                Intent sharenews = new Intent(Intent.ACTION_SEND);
                sharenews.setType("text/plan");
                sharenews.putExtra(Intent.EXTRA_SUBJECT, mSource);
                String body = mTitle + "\n" + mUrl + "\n" + getString(R.string.mainBody) + "\n";
                sharenews.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(sharenews, getString(R.string.ShareTitle)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(NewsDetailActivity.this, R.string.ShareErrorMsg, Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == R.id.openbrowser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            //go to browser
            this.startActivity(browserIntent);
        }
        else if (item.getItemId() == R.id.printwebview) {
           //print webview
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

            String jobName = getString(R.string.app_name) + " " + mTitle;
            // Get a print adapter instance
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

            // Create a print job with name and adapter instance
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

            // Save the job object for later status checking

        }

        return super.onOptionsItemSelected(item);
    }

    private static class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }

    }
}