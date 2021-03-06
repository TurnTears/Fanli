package com.ascba.rebate.activities.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ascba.rebate.R;
import com.ascba.rebate.utils.NetUtils;
import com.ascba.rebate.view.MoneyBar;

public class WebViewBaseActivity extends AppCompatActivity {
    private String url;
    private WebView webView;
    private MoneyBar mb;
    private String name;
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_base);
        setStatusBar();
        initViews();
        getMsgFromBefore();

    }

    private void getMsgFromBefore() {
        Intent intent = getIntent();
        if (intent!=null){
            boolean netAva = NetUtils.isNetworkAvailable(this);
            if(!netAva){
                return;
            }
            name = intent.getStringExtra("name");
            url = intent.getStringExtra("url");
            mb.setTextTitle(name);
            if(url!=null){
                final ProgressDialog p=new ProgressDialog(this,R.style.dialog);
                p.setMessage("请稍后");
                webView.setWebViewClient(new WebViewClient()
                {
                    @Override
                    public void onPageFinished(WebView view,String url)
                    {
                        p.dismiss();
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                    }
                });
                webView.loadUrl(url);
                p.show();
            }
        }
    }

    private void initViews() {
        webView = ((WebView) findViewById(R.id.webView));
        mb = ((MoneyBar) findViewById(R.id.mb_protocol));

    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();  //返回上一页
        }
    }
    /**
     * 拦截实体键(MENU  BACK  POWER  VOLUME  HOME)的点击事件
     * @param keyCode  实体按键的编码
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  //按下实体的返回键
            if (webView.canGoBack()) {  //说明WebView有上一页
                webView.goBack();  //WebView返回上一页
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
