package ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;

/**
 * Created by yingc on 2017/12/12 0012.
 */

public class WebViewActivity extends BaseActivity {
    @Bind(R.id.webview)
    WebView webView;
    String url="http://139.224.52.46:8010/snoppa/static/kylinguide.html";
//    String url="file:///android_asset/text.html";

    @Override
    public int getContentViewId() {
        return R.layout.activity_webview;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void initViews() {
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:window.showa()");
            }
        });
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);//开启JavaScript支持
        webView.setVerticalScrollBarEnabled(false);
        webView.addJavascriptInterface(new WebAppInterface(this), "kylin");
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * 参数处理
         *
         *
         */
        @JavascriptInterface
        public void EnterDevice() {
            Log.e("en","EnterDevice");
            startActivity(new Intent(WebViewActivity.this,MainActivity.class));

            }
        }
    }

