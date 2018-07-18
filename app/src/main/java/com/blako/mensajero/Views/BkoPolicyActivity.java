package com.blako.mensajero.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.R;


public class BkoPolicyActivity extends BaseActivity {
    private String responseOrderStatus;
    private Button aceptBt;
    private WebView mWebview;
    private Toolbar mToolbar;
    private Boolean fromMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_activity);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        aceptBt = (Button) findViewById(R.id.aceptBt);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("fromMain")) {
            fromMain = true;
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(fromMain);
            getSupportActionBar().setDisplayHomeAsUpEnabled(fromMain);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mTitle.setText(getString(R.string.blako_privacy));

        }


        aceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromMain) {
                    finish();
                } else {
                    BkoDataMaganer.setPolicyReaded(true, BkoPolicyActivity.this);
                    finish();
                    Intent intent = new Intent(BkoPolicyActivity.this, BkoVehiclesActivity.class);
                    intent.putExtra("onRecover", true);
                    startActivity(intent);
                }

            }
        });

        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new WebViewClient());
        String pdf = "http://food.blako.com/public/upload/Reglamentodelrepartidor.pdf";
        mWebview.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + pdf);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fromMain) {
            finish();
        } else {
            finishAffinity();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoPolicyActivity.this.getLocalClassName());
    }

}
