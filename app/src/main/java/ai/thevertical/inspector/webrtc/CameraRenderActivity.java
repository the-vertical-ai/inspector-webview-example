package ai.thevertical.inspector.webrtc;

import ai.thevertical.inspector.webrtc.databinding.ActivitySampleCameraRenderBinding;
import android.Manifest;
import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.*;


public class CameraRenderActivity extends AppCompatActivity {
    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setDisplayZoomControls(false);

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient());

        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Webview needs to be initialized after getting all permissions
//        As a workaround for now, just restart app after getting all permissions
        ActivityCompat.requestPermissions(CameraRenderActivity.this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, 1);

        ActivitySampleCameraRenderBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_camera_render);
        WebView mWebRTCWebView = binding.webview;

        setUpWebViewDefaults(mWebRTCWebView);
        mWebRTCWebView.setWebViewClient(new SSLTolerantWebViewClient());
        mWebRTCWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
//              https://medium.com/@xabaras/android-webview-handling-geolocation-permission-request-cc482f3de210
                callback.invoke(origin, true, true);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });

//        This url is just for webrtc testing
//        In future, replace with i.instaclaim.ai
        mWebRTCWebView.loadUrl("https://webrtc.github.io/samples/src/content/devices/input-output/");
    }
}
