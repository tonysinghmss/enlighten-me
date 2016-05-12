package com.tony.analytics.enlightenme;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {
    private Button authBtn;
    private WebView authWebview;
    private SharedPreferences pref;
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
                authWebview.getSettings().setJavaScriptEnabled(true);
                authWebview.loadUrl(EnlightenConfig.OAUTH_URL+"?redirect_uri="+EnlightenConfig.REDIRECT_URI+"&response_type=code&client_id="+EnlightenConfig.CLIENT_ID+"&scope="+EnlightenConfig.OAUTH_SCOPE);
                authWebview.setWebViewClient(new WebViewClient() {
 
                    boolean authComplete = false;
                    Intent resultIntent = new Intent();
 
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);
 
                    }
                    String authCode;
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
 
                        if (url.contains("?code=") && authComplete != true) {
                            Uri uri = Uri.parse(url);
                            authCode = uri.getQueryParameter("code");
                            Log.i("", "CODE : " + authCode);
                            authComplete = true;
                            resultIntent.putExtra("code", authCode);
                            AuthActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                            setResult(Activity.RESULT_CANCELED, resultIntent);
 
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("Code", authCode);
                            edit.commit();
                            authDialog.dismiss();
                            new TokenGet().execute();
                            Toast.makeText(getApplicationContext(),"Authorization Code is: " +authCode, Toast.LENGTH_SHORT).show();
                        }else if(url.contains("error=access_denied")){
                            Log.i("", "ACCESS_DENIED_HERE");
                            resultIntent.putExtra("code", authCode);
                            authComplete = true;
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                            authDialog.dismiss();
                        }
                    }
                });
                authDialog.show();
                authDialog.setTitle("Instagram Authentication");
                authDialog.setCancelable(true);
            }
        });
    }
}

private class TokenGet extends AsyncTask<String, String, JSONObject> {
            private ProgressDialog pDialog;
            String Code;
            @Override
            protected void onPreExecute() {
               super.onPreExecute();
               pDialog = new ProgressDialog(AuthActivity.this);
               pDialog.setMessage("Contacting Instagram ...");
               pDialog.setIndeterminate(false);
               pDialog.setCancelable(true);
               Code = pref.getString("Code", "");
               pDialog.show();
            }
 
            @Override
            protected JSONObject doInBackground(String... args) {
               GetAccessToken jParser = new GetAccessToken();
               JSONObject json = jParser.gettoken(EnlightenConfig.TOKEN_URL,Code,EnlightenConfig.CLIENT_ID,EnlightenConfig.CLIENT_SECRET,EnlightenConfig.REDIRECT_URI,EnlightenConfig.GRANT_TYPE);
               return json;
            }
 
            @Override
            protected void onPostExecute(JSONObject json) {
                pDialog.dismiss();
                if (json != null){
                       try {
                           String tok = json.getString("access_token");
                        //String expire = json.getString("expires_in");
                        //String refresh = json.getString("refresh_token");
                           Log.d("Token Access", tok);
                           authBtn.setText("Authenticated");
                           //Access.setText("Access Token:"+tok+"nExpires:"+expire+"nRefresh Token:"+refresh);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                       Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                       pDialog.dismiss();
                }
            }
    }
