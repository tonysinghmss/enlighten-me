package com.tony.analytics.enlightenme;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class AuthActivity extends AppCompatActivity {
    private Button authBtn;
    private WebView authWebview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //Authentication button clicked by user
        authBtn = (Button)findViewById(R.id.auth_button);
        authBtn.setOnClickListener(new View.OnClickListener() {
            Dialog authDialog;
            @Override
            public void onClick(View v) {
                authDialog = new Dialog(AuthActivity.this);
                authDialog.setContentView(R.layout.auth_dialog);
                authWebview = (WebView)authDialog.findViewById(R.id.auth_webv);
            }
        });

    }
}
