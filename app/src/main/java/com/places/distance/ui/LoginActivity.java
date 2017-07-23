package com.places.distance.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.places.distance.R;
import com.places.distance.domain.TrackingService;
import com.places.distance.domain.flickr.api.FlickrOAuthClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Login Activity used to get all the necessary information for the Flickr OAuth service
 * Created by Aurelian Cotuna
 */

public class LoginActivity extends OAuthLoginActivity<FlickrOAuthClient> {

    @BindView(R.id.login_button)
    Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }


    @Override
    public void onLoginSuccess() {
        TrackingService.refreshClient(LoginActivity.this);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
    }

    public void login() {
        getClient().connect();
    }

}
