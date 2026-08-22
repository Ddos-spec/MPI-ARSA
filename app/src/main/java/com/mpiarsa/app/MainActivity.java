package com.mpiarsa.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.webkit.WebResourceErrorCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final String TAG = "MPI-ARSA";
    private static final String ASSET_HOST = "appassets.androidplatform.net";
    private static final String START_URL = "https://" + ASSET_HOST + "/assets/www/index.html";
    private static final String ENHANCEMENT_CSS =
            "https://" + ASSET_HOST + "/assets/arsa-enhancements.css";
    private static final String ENHANCEMENT_JS =
            "https://" + ASSET_HOST + "/assets/arsa-enhancements.js";
    private static final int STORAGE_PERMISSION_REQUEST = 4107;
    private static final long LOAD_TIMEOUT_MS = 18_000L;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private FrameLayout rootView;
    private LinearLayout statusOverlay;
    private ProgressBar progressBar;
    private TextView statusText;
    private Button retryButton;
    private WebView webView;
    private Runnable loadTimeout;
    private boolean contentReady;
    private long lastBackPressedAt;

    private String pendingDownloadUrl;
    private String pendingUserAgent;
    private String pendingContentDisposition;
    private String pendingMimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideSystemUi();
        buildRootView();

        try {
            createWebView(savedInstanceState);
        } catch (Throwable error) {
            Log.e(TAG, "WebView startup failed", error);
            showError(
                    "MPI ARSA tidak dapat dimulai. Pastikan Android System WebView atau Google Chrome aktif dan terbaru.",
                    true
            );
        }
    }

    private void buildRootView() {
        rootView = new FrameLayout(this);
        rootView.setBackgroundColor(Color.BLACK);

        statusOverlay = new LinearLayout(this);
        statusOverlay.setOrientation(LinearLayout.VERTICAL);
        statusOverlay.setGravity(Gravity.CENTER);
        statusOverlay.setPadding(dp(32), dp(24), dp(32), dp(24));
        statusOverlay.setBackgroundColor(Color.rgb(18, 18, 18));

        progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        progressParams.bottomMargin = dp(18);
        statusOverlay.addView(progressBar, progressParams);

        statusText = new TextView(this);
        statusText.setTextColor(Color.WHITE);
        statusText.setTextSize(16f);
        statusText.setGravity(Gravity.CENTER);
        statusText.setText("Memuat MPI ARSA…");
        statusOverlay.addView(
                statusText,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        retryButton = new Button(this);
        retryButton.setText("COBA LAGI");
        retryButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams retryParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        retryParams.topMargin = dp(18);
        statusOverlay.addView(retryButton, retryParams);
        retryButton.setOnClickListener(view -> recreateWebView());

        rootView.addView(
                statusOverlay,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        setContentView(rootView);
        showLoading("Memuat MPI ARSA…");
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void createWebView(Bundle savedInstanceState) throws Exception {
        try (InputStream ignored = getAssets().open("www/index.html")) {
            // Storyline entry point exists.
        }

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        WebView.setWebContentsDebuggingEnabled(false);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true);
        }

        rootView.addView(
                webView,
                0,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setDefaultTextEncodingName("UTF-8");

        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.addJavascriptInterface(new NativeBridge(), "MPIARSA");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setDownloadListener(this::requestDownload);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.setWebViewClient(new StorylineClientApi26(assetLoader));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.setWebViewClient(new StorylineClientApi23(assetLoader));
        } else {
            webView.setWebViewClient(new StorylineClient(assetLoader));
        }

        if (savedInstanceState == null || webView.restoreState(savedInstanceState) == null) {
            webView.loadUrl(START_URL);
        } else {
            showLoading("Memulihkan sesi…");
            scheduleLoadTimeout();
        }
    }

    private void injectEnhancements(WebView view) {
        String script =
                "(function(){"
                        + "if(document.getElementById('arsa-native-enhancements'))return;"
                        + "var c=document.createElement('link');"
                        + "c.id='arsa-native-enhancements';"
                        + "c.rel='stylesheet';"
                        + "c.href='" + ENHANCEMENT_CSS + "';"
                        + "(document.head||document.documentElement).appendChild(c);"
                        + "var s=document.createElement('script');"
                        + "s.src='" + ENHANCEMENT_JS + "';"
                        + "s.defer=true;"
                        + "(document.body||document.documentElement).appendChild(s);"
                        + "})();";
        view.evaluateJavascript(script, null);
    }

    private boolean handleNavigation(Uri uri) {
        if (uri == null) return true;

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if ("https".equalsIgnoreCase(scheme) && ASSET_HOST.equalsIgnoreCase(host)) {
            return false;
        }

        if ("intent".equalsIgnoreCase(scheme)) {
            try {
                Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                startActivity(intent);
            } catch (Exception error) {
                Log.w(TAG, "Unable to open intent URL: " + uri, error);
                toast("Tautan tidak dapat dibuka.");
            }
            return true;
        }

        if ("http".equalsIgnoreCase(scheme)
                || "https".equalsIgnoreCase(scheme)
                || "mailto".equalsIgnoreCase(scheme)
                || "tel".equalsIgnoreCase(scheme)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception error) {
                Log.w(TAG, "Unable to open external link: " + uri, error);
                toast("Tautan tidak dapat dibuka.");
            }
            return true;
        }

        return false;
    }

    private void requestDownload(
            String url,
            String userAgent,
            String contentDisposition,
            String mimeType,
            long contentLength
    ) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();

        if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            toast("Format download ini belum didukung. Gunakan tombol share/open jika tersedia.");
            return;
        }

        pendingDownloadUrl = url;
        pendingUserAgent = userAgent;
        pendingContentDisposition = contentDisposition;
        pendingMimeType = mimeType;

        if (needsLegacyStoragePermission()) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST
            );
            return;
        }

        performPendingDownload();
    }

    private boolean needsLegacyStoragePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void performPendingDownload() {
        if (pendingDownloadUrl == null) return;

        Uri uri = Uri.parse(pendingDownloadUrl);
        if (ASSET_HOST.equalsIgnoreCase(uri.getHost()) && uri.getPath() != null
                && uri.getPath().startsWith("/assets/")) {
            saveLocalAsset(uri, pendingContentDisposition, pendingMimeType);
        } else {
            enqueueRemoteDownload(
                    pendingDownloadUrl,
                    pendingUserAgent,
                    pendingContentDisposition,
                    pendingMimeType
            );
        }

        clearPendingDownload();
    }

    private void enqueueRemoteDownload(
            String url,
            String userAgent,
            String contentDisposition,
            String mimeType
    ) {
        try {
            String fileName = safeFileName(URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName);
            request.setDescription("Mengunduh dari MPI ARSA");
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            if (mimeType != null && !mimeType.trim().isEmpty()) {
                request.setMimeType(mimeType);
            }
            if (userAgent != null && !userAgent.trim().isEmpty()) {
                request.addRequestHeader("User-Agent", userAgent);
            }

            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null && !cookies.trim().isEmpty()) {
                request.addRequestHeader("Cookie", cookies);
            }

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            manager.enqueue(request);
            toast("Download dimulai: " + fileName);
        } catch (Exception error) {
            Log.e(TAG, "Download failed", error);
            toast("Download gagal dimulai.");
        }
    }

    private void saveLocalAsset(Uri uri, String contentDisposition, String mimeType) {
        String path = uri.getPath();
        if (path == null || !path.startsWith("/assets/")) {
            toast("File tidak dapat disimpan.");
            return;
        }

        String assetPath = path.substring("/assets/".length());
        String fileName = safeFileName(
                URLUtil.guessFileName(uri.toString(), contentDisposition, mimeType)
        );

        new Thread(() -> {
            try (InputStream input = getAssets().open(assetPath)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Api29Downloads.save(MainActivity.this, input, fileName, mimeType);
                } else {
                    saveToLegacyDownloads(input, fileName);
                }
                runOnUiThread(() -> toast("Tersimpan di Downloads: " + fileName));
            } catch (Exception error) {
                Log.e(TAG, "Unable to save local asset: " + assetPath, error);
                runOnUiThread(() -> toast("File gagal disimpan."));
            }
        }).start();
    }

    @SuppressWarnings("deprecation")
    private void saveToLegacyDownloads(InputStream input, String fileName) throws IOException {
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloads.exists() && !downloads.mkdirs()) {
            throw new IOException("Unable to create Downloads directory");
        }

        File destination = uniqueFile(downloads, fileName);
        try (OutputStream output = new FileOutputStream(destination)) {
            copy(input, output);
        }
    }

    private File uniqueFile(File directory, String fileName) {
        File candidate = new File(directory, fileName);
        if (!candidate.exists()) return candidate;

        int dot = fileName.lastIndexOf('.');
        String base = dot > 0 ? fileName.substring(0, dot) : fileName;
        String extension = dot > 0 ? fileName.substring(dot) : "";
        int index = 2;
        while (candidate.exists()) {
            candidate = new File(directory, base + " (" + index + ")" + extension);
            index += 1;
        }
        return candidate;
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[16 * 1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        output.flush();
    }

    private String safeFileName(String fileName) {
        String safe = fileName == null ? "mpi-arsa-download" : fileName;
        safe = safe.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        return safe.isEmpty() ? "mpi-arsa-download" : safe;
    }

    private void clearPendingDownload() {
        pendingDownloadUrl = null;
        pendingUserAgent = null;
        pendingContentDisposition = null;
        pendingMimeType = null;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != STORAGE_PERMISSION_REQUEST) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performPendingDownload();
        } else {
            clearPendingDownload();
            toast("Izin penyimpanan diperlukan untuk menyimpan file di Android versi ini.");
        }
    }

    private void scheduleLoadTimeout() {
        if (loadTimeout != null) mainHandler.removeCallbacks(loadTimeout);
        loadTimeout = () -> {
            if (!contentReady && webView != null) {
                Log.e(TAG, "CONTENT_READY timeout");
                showError("Materi terlalu lama dimuat. Tekan COBA LAGI.", true);
            }
        };
        mainHandler.postDelayed(loadTimeout, LOAD_TIMEOUT_MS);
    }

    private void showLoading(String message) {
        if (statusOverlay == null) return;
        statusText.setText(message);
        progressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        statusOverlay.setVisibility(View.VISIBLE);
        statusOverlay.bringToFront();
    }

    private void showError(String message, boolean retry) {
        if (loadTimeout != null) mainHandler.removeCallbacks(loadTimeout);
        statusText.setText(message);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(retry ? View.VISIBLE : View.GONE);
        statusOverlay.setVisibility(View.VISIBLE);
        statusOverlay.bringToFront();
    }

    private void hideStatus() {
        if (loadTimeout != null) mainHandler.removeCallbacks(loadTimeout);
        statusOverlay.setVisibility(View.GONE);
    }

    private void recreateWebView() {
        destroyWebView();
        showLoading("Memuat ulang MPI ARSA…");
        try {
            createWebView(null);
        } catch (Throwable error) {
            Log.e(TAG, "WebView restart failed", error);
            showError(
                    "MPI ARSA belum dapat dimuat. Perbarui Android System WebView atau Chrome lalu coba lagi.",
                    true
            );
        }
    }

    private void destroyWebView() {
        if (loadTimeout != null) mainHandler.removeCallbacks(loadTimeout);
        if (webView == null) return;

        try {
            rootView.removeView(webView);
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.removeJavascriptInterface("MPIARSA");
            webView.destroy();
        } catch (Throwable ignored) {
            // WebView may already be dead after renderer termination.
        }
        webView = null;
        contentReady = false;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressedAt < 1800L) {
            super.onBackPressed();
            return;
        }

        lastBackPressedAt = now;
        toast("Tekan kembali sekali lagi untuk keluar.");
    }

    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

    private final class NativeBridge {
        @JavascriptInterface
        public void contentReady() {
            runOnUiThread(() -> {
                if (!contentReady) {
                    contentReady = true;
                    Log.i(TAG, "CONTENT_READY");
                }
                hideStatus();
            });
        }
    }

    private class StorylineClient extends WebViewClientCompat {
        final WebViewAssetLoader assetLoader;

        StorylineClient(WebViewAssetLoader assetLoader) {
            this.assetLoader = assetLoader;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(
                WebView view,
                WebResourceRequest request
        ) {
            return assetLoader.shouldInterceptRequest(request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation")
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return assetLoader.shouldInterceptRequest(Uri.parse(url));
        }

        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
        ) {
            return handleNavigation(request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleNavigation(Uri.parse(url));
        }

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            contentReady = false;
            showLoading("Memuat materi…");
            scheduleLoadTimeout();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            injectEnhancements(view);
        }
    }

    private class StorylineClientApi23 extends StorylineClient {
        StorylineClientApi23(WebViewAssetLoader assetLoader) {
            super(assetLoader);
        }

        @Override
        public void onReceivedError(
                WebView view,
                WebResourceRequest request,
                WebResourceErrorCompat error
        ) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                Log.e(TAG, "Main frame load failed: " + error);
                showError("Materi gagal dimuat. Tekan COBA LAGI untuk memuat ulang.", true);
            }
        }

        @Override
        public void onReceivedHttpError(
                WebView view,
                WebResourceRequest request,
                WebResourceResponse errorResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (request.isForMainFrame()) {
                Log.e(TAG, "Main frame HTTP error: " + errorResponse.getStatusCode());
                showError("Materi tidak tersedia dengan benar. Tekan COBA LAGI.", true);
            }
        }
    }

    private final class StorylineClientApi26 extends StorylineClientApi23 {
        StorylineClientApi26(WebViewAssetLoader assetLoader) {
            super(assetLoader);
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            Log.e(
                    TAG,
                    "WebView renderer gone. crashed=" + detail.didCrash()
                            + " priority=" + detail.rendererPriorityAtExit()
            );
            destroyWebView();
            showError("Mesin tampilan berhenti. Tekan COBA LAGI untuk memulihkan aplikasi.", true);
            return true;
        }
    }

    private static final class Api29Downloads {
        private Api29Downloads() {}

        @SuppressLint("NewApi")
        static void save(Activity activity, InputStream input, String fileName, String mimeType)
                throws IOException {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            if (mimeType != null && !mimeType.trim().isEmpty()) {
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            }
            values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + File.separator + "MPI ARSA"
            );
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);

            Uri destination = activity.getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
            );
            if (destination == null) {
                throw new IOException("Unable to create MediaStore destination");
            }

            try (OutputStream output = activity.getContentResolver().openOutputStream(destination)) {
                if (output == null) throw new IOException("Unable to open MediaStore output");
                copy(input, output);
            } catch (IOException error) {
                activity.getContentResolver().delete(destination, null, null);
                throw error;
            }

            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            activity.getContentResolver().update(destination, values, null, null);
        }
    }
}
