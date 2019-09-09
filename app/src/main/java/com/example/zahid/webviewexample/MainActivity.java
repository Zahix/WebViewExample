package com.example.zahid.webviewexample;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.example.zahid.webviewexample.Fragment.NoInternetScreen;
import com.example.zahid.webviewexample.Fragment.WelcomeScreen;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static String link = "https://www.google.com";

    boolean loadingFinished = true;
    boolean redirect = false;

    android.support.v4.app.FragmentManager fragmentManager;

    Context context;
    ProgressDialog progressDialog;

    static  int state ;

    NetworkInfo networkInfo;

    Handler handler;
    Runnable runnable;

    MyChrome myChrome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full Screen code ******************************************
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Full Screen code ******************************************

        setContentView(R.layout.activity_main);

        context = this;

        fragmentManager = getSupportFragmentManager();

        webView = (WebView) findViewById(R.id.webview);

        if(savedInstanceState !=null){
            webView.restoreState(savedInstanceState);
        }else {
            getWebView();
        }


        progressDialog = new ProgressDialog(context,1);

          handler = new Handler();

          // myChrome = new MyChrome();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    void getWebView() {


        state = 1;

//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (hasInternet()) {

            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            fragmentManager.beginTransaction().replace(R.id.frameLayout, new WelcomeScreen()).addToBackStack("WelcomeScreen").commit();



            //link = "https://www.google.com/maps/@30.5279618,72.6919699,12z";
            webView.loadUrl(link);

            webView.getSettings().setAppCacheMaxSize(10 * 1024 * 1024);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, final String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    //link = urlNewString;

                    if(!hasInternet()){

                        fragmentManager.beginTransaction().replace(R.id.frameLayout, new NoInternetScreen()).addToBackStack("NoInternet").commit();

                        handler.post(runnable = new Runnable() {
                            @Override
                            public void run() {

                                view.loadUrl(urlNewString);

                                handler.postDelayed(this,5000);


                                if(hasInternet()){
                                    handler.removeCallbacks(runnable);

                                    if (getSupportFragmentManager().getBackStackEntryCount() > 0){
//                            getSupportFragmentManager().popBackStack("WelcomeScreen",getSupportFragmentManager().getBackStackEntryCount());
                                        getSupportFragmentManager().popBackStack("NoInternet",getSupportFragmentManager().getBackStackEntryCount());
                                    }
                                }
                            }
                        });



                    }

                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    if(state == 1){

//                        fragmentManager.beginTransaction().replace(R.id.frameLayout, new WelcomeScreen()).addToBackStack("WelcomeScreen").commit();

                        state =0;

                    }else {


                        progressDialog.setCancelable(true);
                        progressDialog.show();

                    }


                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(!redirect){
                        loadingFinished = true;
                    }

                    if(loadingFinished && !redirect){
                        //HIDE LOADING IT HAS FINISHED

                        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
//                            getSupportFragmentManager().popBackStack("WelcomeScreen",getSupportFragmentManager().getBackStackEntryCount());
                            getSupportFragmentManager().popBackStack("WelcomeScreen",1);
                        }
                        progressDialog.dismiss();


                    } else{
                        redirect = false;
                    }

                }
            });

            webView.setWebChromeClient(myChrome = new MyChrome());
        }
    }


    public boolean hasInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(myChrome.mCustomView != null){
                        myChrome.onHideCustomView();
                    }
                    else if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyChrome extends WebChromeClient {

        public View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }



    }
}
