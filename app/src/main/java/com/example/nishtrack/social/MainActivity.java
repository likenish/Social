package com.example.nishtrack.social;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LoginButton facebook_Button;
    SignInButton google_Button;
    CallbackManager facebook_Callback;
    AccessTokenTracker facebook_AccessToken_Traker;
    GoogleApiClient googleApiClient;
    GoogleApiAvailability google_api_availability;
    ConnectionResult connection_result;
    ProgressDialog progress_dialog;
    private static final int Google_Result_Code = 999;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebook_Button = (LoginButton) findViewById(R.id.facebook_button);
        google_Button = (SignInButton) findViewById(R.id.google_button);
        facebook_AccessToken_Traker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        facebook_Button.setOnClickListener(this);
        google_Button.setOnClickListener(this);
        if(AccessToken.getCurrentAccessToken()!=null){
            Intent intent = new Intent(this,Welcome_Activity.class);
            startActivity(intent);
            finish();
        }
        else if(StaticObject.getboolean("isGoogleLogin",this)){
            Intent intent = new Intent(this,Welcome_Activity.class);
            startActivity(intent);
            finish();
        }
        else {
            intializeGoogleComponent();
        }
    }


    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
    }
/*---------------------------------------Facebook-------------------------------------------*/

    public void facebook_Auth() {
        facebook_Callback = CallbackManager.Factory.create();
        facebook_Button.setReadPermissions(Arrays.asList("public_profile, email"));
        facebook_Button.registerCallback(facebook_Callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(getApplicationContext(), Welcome_Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }
/*---------------------------------------------Google--------------------------------------------*/
    public void intializeGoogleComponent() {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
       if(googleApiClient.hasConnectedApi(Plus.API)){
       }

    }

    public void google_Auth() {
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");
        if (!googleApiClient.isConnecting()) {
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        }

    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {

            try {
                connection_result.startResolutionForResult(this, Google_Result_Code);
                is_intent_inprogress = true;
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        is_signInBtn_clicked = false;
        getProfileInfo();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            google_api_availability.getErrorDialog(this, connectionResult.getErrorCode(), Google_Result_Code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = connectionResult;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }

        }
    }

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                StaticObject.createSession(true,false,currentPerson.getImage().getUrl(),currentPerson.getDisplayName(), getApplicationContext());
                Intent intent = new Intent(getApplicationContext(),Welcome_Activity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/*-------------------------------------------------Google Finish-----------------------------*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_button:
                facebook_Auth();
                break;
            case R.id.google_button:
                google_Auth();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Google_Result_Code) {
            if (requestCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();
            }
            is_intent_inprogress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            facebook_Callback.onActivityResult(requestCode, resultCode, data);
        }
    }

}
