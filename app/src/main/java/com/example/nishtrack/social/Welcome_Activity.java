package com.example.nishtrack.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Welcome_Activity extends AppCompatActivity {
    ImageView profile_pic;
    Button logout_Button;
    TextView name_Text;
    private boolean isGoogleLogin= false;
    private boolean isFacebookLogin =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        logout_Button = (Button) findViewById(R.id.logout_button);
        name_Text = (TextView) findViewById(R.id.name_text);
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            isFacebookLogin = true;
            fbUserInfo();
        }
        else if(StaticObject.getboolean("isGoogleLogin",this)){
            isGoogleLogin = true;
            new updateUI().execute();
        }
        else {

        }
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFacebookLogin) {
                    fbLogout();
                }
                else {
                    googleLogout();
                }
            }
        });
    }

    public void fbUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
                        JSONObject jsonObject = response.getJSONObject();
                        try {
                            String url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                            String name = jsonObject.getString("name");
                          StaticObject.createSession(false,true,url,name,getApplicationContext());
                            new updateUI().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture{url},name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    class updateUI extends AsyncTask<Void,Bitmap,Bitmap>{

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profile_pic.setImageBitmap(bitmap);
            name_Text.setText(StaticObject.getString("name",getApplicationContext()));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(StaticObject.getString("photoUrl",getApplicationContext()));
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public  void fbLogout(){
        AccessToken.setCurrentAccessToken(null);
        StaticObject.update(false, false, null, null, this);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void googleLogout(){
        StaticObject.update(false, false, null, null, this);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }



}
