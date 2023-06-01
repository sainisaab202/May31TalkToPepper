package com.pepperdialogflow.may31talktopepper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Say;

import com.pepperdialogflow.may31talktopepper.Dialogflow.StreamDetectIntent;
import com.pepperdialogflow.may31talktopepper.PepperRobot.*;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    static final String TAG = MainActivity.class.getName();
    private QiContext qiContext;
    private ImageButton imgBtnOpenai, imgBtnCohere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
        Log.e(TAG, "-onCreate");

        imgBtnOpenai = findViewById(R.id.ibtnOpenai);
        imgBtnCohere = findViewById(R.id.ibtnCohere);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        QiSDK.register(this, this);
    }

    @Override
    protected void onPause() {
        QiSDK.unregister(this, this);
        super.onPause();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        Log.e(TAG, "Pepper: focus gained");

        // Create a new say action.
        Say say = SayBuilder.with(qiContext)
                .withText("Hello human! I am Pepper. Select an AI model to start a conversation with me.")
                .build();

        // Execute the action.
        say.run();
    }

    @Override
    public void onRobotFocusLost() {
        Log.e(TAG,"Pepper: focus :LOST");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e(TAG,"Pepper: focus Refused");
    }

    public void onStartButtonClicked(View view) {

        Log.e(TAG, "Start conversation button clicked!");

        checkApiToUse(view);

//        DialogflowChatbot dialogflowChatbot = new DialogflowChatbot(qiContext);
//        Chat chat = ChatBuilder.with(qiContext).withChatbot(dialogflowChatbot).build();
//        chat.async().run();

        launchActivity(view);
    }

    private void launchActivity(View view) {
        if(view.getId() == imgBtnCohere.getId()){
            Log.e(TAG, "Opening CohereAI activity...");
            Intent intent = new Intent(this, CohereActivity.class);
            startActivity(intent);
        }else if(view.getId() == imgBtnOpenai.getId()){
            Log.e(TAG, "Opening OpenAI activity...");
            Intent intent = new Intent(this, OpenAiActivity.class);
            startActivity(intent);
        }
    }

    private void checkApiToUse(View view) {
        if(view.getId() == imgBtnCohere.getId()){
            Log.e(TAG, "Using CohereAI");
            StreamDetectIntent.setApiTo(StreamDetectIntent.CohereAI);
        }else if(view.getId() == imgBtnOpenai.getId()){
            Log.e(TAG, "Using OpenAI");
            StreamDetectIntent.setApiTo(StreamDetectIntent.OpenAI);
        }
    }


}