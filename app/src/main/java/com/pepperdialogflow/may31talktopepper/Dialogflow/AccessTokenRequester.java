package com.pepperdialogflow.may31talktopepper.Dialogflow;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AccessTokenRequester {

    private static final String TAG = AccessTokenRequester.class.getName();

    public static String requestAccessToken(String jwt) {

        // Create an OkHttpClient instance (can be done once for the entire app)
        OkHttpClient client = new OkHttpClient();

        // Construct the request body
        String requestBody = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt;

        // Set up the HTTP request
        Request request = new Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .post(RequestBody.create(MediaType.parse("text/plain"), requestBody))
                .post(RequestBody.create(MediaType.parse("charset=utf-8"), requestBody))
                .build();

        // Execute the request
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Read the response
                String responseBody = response.body().string();
                // Handle the response as needed
                JSONObject accessToken = new JSONObject(responseBody);

                return accessToken.getString("access_token");
            } else {
                // Handle unsuccessful response (e.g., non-200 status code)
                Log.e(TAG,"Request unsuccessful. Response code: " + response.code());
                return null;
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}