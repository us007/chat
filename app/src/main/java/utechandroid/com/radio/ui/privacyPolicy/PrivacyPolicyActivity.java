package utechandroid.com.radio.ui.privacyPolicy;

import android.os.Bundle;
import android.webkit.WebView;


import com.prudenttechno.radioNotification.R;

import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class PrivacyPolicyActivity extends ColorsAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadUrl("http://fms.ooo/privacy_policy.html");

    }
}
