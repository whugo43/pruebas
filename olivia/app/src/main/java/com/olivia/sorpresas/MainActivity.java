package com.olivia.sorpresas;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public final class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private WebView game;
    private TextToSpeech speech;
    private boolean speechReady;

    @Override public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fullscreen();
        game = new WebView(this);
        game.setBackgroundColor(0xFF16225C);
        WebSettings s = game.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setDefaultTextEncodingName("utf-8");
        s.setMediaPlaybackRequiresUserGesture(false);
        game.setWebViewClient(new WebViewClient());
        game.setWebChromeClient(new WebChromeClient());
        game.addJavascriptInterface(new VoiceBridge(), "AndroidVoice");
        setContentView(game);
        game.loadUrl("file:///android_asset/index.html");
        try { speech = new TextToSpeech(this, this); }
        catch (Exception ignored) { speechReady = false; speech = null; }
    }

    @Override public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS || speech == null) return;
        try {
            int r = speech.setLanguage(new Locale("es","ES"));
            speechReady = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED;
            if (speechReady) { speech.setSpeechRate(0.88f); speech.setPitch(1.15f); }
        } catch(Exception ignored) { speechReady = false; }
    }

    public final class VoiceBridge {
        @JavascriptInterface public void speak(final String text) {
            runOnUiThread(() -> {
                if (speechReady && speech != null && text != null) {
                    try { speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "olivia_voice"); }
                    catch(Exception ignored) {}
                }
            });
        }
        @JavascriptInterface public void stop() {
            runOnUiThread(() -> {
                if (speech != null) {
                    try { speech.stop(); } catch(Exception ignored) {}
                }
            });
        }
    }

    private void fullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) fullscreen();
    }
    @Override protected void onDestroy() {
        speechReady = false;
        if (speech != null) {
            try { speech.stop(); speech.shutdown(); } catch (Exception ignored) {}
        }
        if (game != null) game.destroy();
        super.onDestroy();
    }
}
