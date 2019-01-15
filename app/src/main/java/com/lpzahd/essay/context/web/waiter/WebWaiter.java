package com.lpzahd.essay.context.web.waiter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.web.WebActivity;
import com.lpzahd.waiter.consumer.State;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

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
//        WebSettings webSettings = webView.getSettings();
//        //支持缩放，默认为true。
//        webSettings.setSupportZoom(false);
//        //调整图片至适合webview的大小
//        webSettings.setUseWideViewPort(true);
//        // 缩放至屏幕的大小
//        webSettings.setLoadWithOverviewMode(true);
//        //设置默认编码
//        webSettings.setDefaultTextEncodingName("utf-8");
//        //设置自动加载图片
//        webSettings.setLoadsImagesAutomatically(true);
//
//        //多窗口
//        webSettings.supportMultipleWindows();
//        //获取触摸焦点
//        webView.requestFocusFromTouch();
//        //允许访问文件
//        webSettings.setAllowFileAccess(true);
//        //开启javascript
//        webSettings.setJavaScriptEnabled(true);
//        //支持通过JS打开新窗口
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        //提高渲染的优先级
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        //支持内容重新布局
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        //关闭webview中缓存
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//
//        //配置支持domstorage
//        webSettings.setDomStorageEnabled(true);//启用或禁用DOM缓存
//        webSettings.setAppCacheEnabled(false);//关闭/启用应用缓存
//        webSettings.setSupportZoom(true);//是否可以缩放，默认true
//        //settings.setBuiltInZoomControls(false);//是否显示缩放按钮，默认false
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAllowContentAccess(true);
//        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        webSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


//        webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
//                handler.proceed();
//            }
//        });
//        webView.setWebChromeClient(new WebChromeClient());

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSettings.setBlockNetworkImage(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

//            @Override
//            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
//                                       JsResult arg3) {
//                return super.onJsConfirm(arg0, arg1, arg2, arg3);
//            }
//
//            View myVideoView;
//            View myNormalView;
//            IX5WebChromeClient.CustomViewCallback callback;
//
//            // /////////////////////////////////////////////////////////
//            //
//            /**
//             * 全屏播放配置
//             */
//            @Override
//            public void onShowCustomView(View view,
//                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
//                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
//                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
//                viewGroup.removeView(normalView);
//                viewGroup.addView(view);
//                myVideoView = view;
//                myNormalView = normalView;
//                callback = customViewCallback;
//            }
//
//            @Override
//            public void onHideCustomView() {
//                if (callback != null) {
//                    callback.onCustomViewHidden();
//                    callback = null;
//                }
//                if (myVideoView != null) {
//                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
//                    viewGroup.removeView(myVideoView);
//                    viewGroup.addView(myNormalView);
//                }
//            }
//
//            @Override
//            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
//                                     JsResult arg3) {
//                /**
//                 * 这里写入你自定义的window alert
//                 */
//                return super.onJsAlert(null, arg1, arg2, arg3);
//            }
        });

        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
//        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
//                .getPath());
//        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
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
    }
}
