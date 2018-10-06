package com.lpzahd.essay.context.web.waiter;

import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.web.WebActivity;
import com.lpzahd.waiter.consumer.State;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class WebWaiter extends ToneActivityWaiter<WebActivity> {

    public static final String TAG = "com.lpzahd.essay.context.web.waiter.WebWaiter";

    @BindView(R.id.web_vew)
    WebView webView;

    private Transmitter<String> mTransmitter;

    public WebWaiter(WebActivity webActivity) {
        super(webActivity);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void initView() {
        setupWebview();

        Disposable disposable = mTransmitter.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(url -> webView.loadUrl(url));
        context.addDispose(disposable);
    }

    private void setupWebview() {
        WebSettings webSettings = webView.getSettings();
        //支持缩放，默认为true。
        webSettings.setSupportZoom(false);
        //调整图片至适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //设置默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        webSettings.setLoadsImagesAutomatically(true);

        //多窗口
        webSettings.supportMultipleWindows();
        //获取触摸焦点
        webView.requestFocusFromTouch();
        //允许访问文件
        webSettings.setAllowFileAccess(true);
        //开启javascript
        webSettings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //提高渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //关闭webview中缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    protected int backPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return State.STATE_PREVENT;
        } else {
            return super.backPressed();
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        webView.clearHistory();
        webView.destroy();
    }
}
