package com.tony.analytics.enlightenme;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class JsonHelper {
    private static final String TAG = "JSONHELPER";
    private String streamToString(InputStream is) {
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            str = sb.toString();
        }
        return str;
    }

    public JSONObject getAcstoken(String aTokenUrl, String aAuthCode, String aClientId, String aClientSecret, String aRedirectUri, String aScope){
        //String vInstagramTokenUrl = aTokenUrl+"?client_id="+aClientId+"&client_secret="+aClientSecret+"&grant_type=authorization_code"+
        //        "&redirect_uri="+aRedirectUri+"&code="+aAuthCode;
        JSONObject jsonObject = null;
        try {
            // Send a POST url to get the access token
            URL url = new URL(aTokenUrl);
            Log.i(TAG, "Opening token url " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            // Write paramaters into Url connection
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("?client_id=" + aClientId + "&client_secret=" + aClientSecret + "&grant_type=authorization_code" +
                    "&redirect_uri=" + aRedirectUri + "&code=" + aAuthCode+"&scope="+aScope);
            writer.flush();
            // Fetch the string response of the url
            String response = streamToString(urlConnection.getInputStream());
            System.out.println(response);
            jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            String mAccessToken = jsonObject.getString("access_token");
            Log.i(TAG, "Got access token: " + mAccessToken);
//            String id = jsonObject.getJSONObject("user").getString("id");
//            String userName = jsonObject.getJSONObject("user").getString("username");
//            String fullName = jsonObject.getJSONObject("user").getString("full_name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
