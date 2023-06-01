package com.pepperdialogflow.may31talktopepper.Dialogflow;

////-----------------------------------This project is using REST API to connect with dialogflow - voice in/out
////------------------------------------Specifically for Android 6.0
////------------------------------------As, Google built library have some dependencies that are NOT available in Android 6
////------------------------------------This solution is using following approach:

////------------------------------------| App |                                   | Google Servers |
////------------------------------------1. Creating and Sign JWT                       ---
////------------------------------------2. Use JWT to request token       --->         ---
////------------------------------------3.                                <---         Token response
////------------------------------------4. Use token to call google API   --->         Token response

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;

import com.google.api.gax.rpc.ApiException;
import com.google.protobuf.ByteString;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class StreamDetectIntent{

    private static final String TAG = "StreamDetectIntent";

    public static final String CohereAI = "CohereAI";
    public static final String OpenAI = "OpenAI";
    public static Context context;

    private static String apiToUse;
    String projectId, locationId, agentId, sessionId;

    String response;

    public StreamDetectIntent(){
        //Dialogflow IDs
        projectId = "dbc-chatbot-poc";
        sessionId = UUID.randomUUID().toString();

        //will set which end point to use
        if(apiToUse.equals(StreamDetectIntent.CohereAI)){
            //CohereAI dialogflow
            locationId= "xxxxxxx";
            agentId = "xxxxxxxxxxxxx";

        }else if(apiToUse.equals(StreamDetectIntent.OpenAI)){
            //OpenAI dialogflow
            locationId= "xxxxxxx";
            agentId = "xxxxxx";
        }
    }

    //will return text coming from dialogflow
    public String detectIntentText(
            String textInput,
            Context context)
            throws IOException, ApiException {

        StreamDetectIntent.context = context;

        try {
            String DIALOGFLOW_API_ENDPOINT = "https://"+locationId+"-dialogflow.googleapis.com/v3beta1/projects/"
                    + projectId + "/locations/"
                    + locationId + "/agents/"
                    + agentId + "/sessions/"
                    + sessionId + ":detectIntent";

            // Prepare the request payload for TEXT request
            JSONObject requestBody = new JSONObject()
                    .put("queryInput", new JSONObject()
                            .put("text", new JSONObject()
                                    .put("text", textInput))
                            .put("languageCode", "en"));

            // Create an OkHttpClient for making HTTP requests
            OkHttpClient client = new OkHttpClient();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // generating JWT for requesting access token
                        String jwtToken = JwtGenerator.createJwt(context);

                        //requesting/getting access token
                        String accessToken = AccessTokenRequester.requestAccessToken(jwtToken);

                        // Prepare the HTTP request
                        Request request = new Request.Builder()
                                .url(DIALOGFLOW_API_ENDPOINT)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .addHeader("Content-Type", "application/json")
                                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                                .build();

                        // Send the HTTP request to Dialogflow
                        Response response = client.newCall(request).execute();

                        // Handle the response
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            // Extract the response text from the response and use it in your app's logic or UI.
                            String fulfillmentText = jsonResponse.getJSONObject("queryResult")
                                    .getJSONArray("responseMessages").getJSONObject(0)
                                    .getJSONObject("text").getJSONArray("text").get(0).toString();

                            Log.e(TAG,"REST-Resp:"+ fulfillmentText);

                            setQueryResult(fulfillmentText);

                        } else {
                            Log.e(TAG, "REST-Resp"+"Error: " + response.code() + " " + response.message());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };

            Thread restCall = new Thread(runnable);
            restCall.start();

            if(restCall.isDaemon()){
                return getQueryResult();
            }else{
                while (restCall.isAlive()){
                    //do nothing
                }
                return getQueryResult();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setQueryResult(String fulfillmentText) {
        response = fulfillmentText;
    }

    public String getQueryResult(){
        return response;
    }

    public static void setApiTo(String api){
        apiToUse = api;
    }
}
