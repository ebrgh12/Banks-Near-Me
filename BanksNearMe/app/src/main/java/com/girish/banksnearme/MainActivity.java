package com.girish.banksnearme;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    ImageView googleLogin;

    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private static final int SIGN_IN_CODE = 0;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buidNewGoogleApiClient();

        setContentView(R.layout.activity_main);

        googleLogin = (ImageView) findViewById(R.id.google_login);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_login();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (resultCode != RESULT_OK) {
                is_signInBtn_clicked = false;
            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }

    private void google_login() {
        if (!google_api_client.isConnecting()) {
            is_signInBtn_clicked = true;
            resolveSignInError();
        }
    }

    /**
     create and  initialize GoogleApiClient object to use Google Plus Api.
     While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope.
     */
    private void buidNewGoogleApiClient() {
        google_api_client =  new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private void resolveSignInError() {
        if(connection_result != null){
            if (connection_result.hasResolution()) {
                try {
                    is_intent_inprogress = true;
                    connection_result.startResolutionForResult(MainActivity.this, SIGN_IN_CODE);
                } catch (IntentSender.SendIntentException e) {
                    is_intent_inprogress = false;
                    google_api_client.connect();
                }
            }
        }else {
            buidNewGoogleApiClient();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        // Update the UI after signin
    }

    /**
     get user's information name, email, profile pic,Date of birth,tag line and about me
     */
    public void getProfileInfo() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                String googleEmail = Plus.AccountApi.getAccountName(google_api_client);
                setPersonalInfo(currentPerson);
            } else {
                Toast.makeText(MainActivity.this,"No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPersonalInfo(Person currentPerson) {
        try{

            /* log out the user from g+ */
            if (google_api_client.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(google_api_client);
                google_api_client.disconnect();
                google_api_client.connect();
            }

            // navigate to home
            Intent intent = new Intent(MainActivity.this,BankAtmActivity.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            e.toString();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connection_result = connectionResult;

        if (!connectionResult.hasResolution()) {
            google_api_availability.getErrorDialog(MainActivity.this, connectionResult.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            if (is_signInBtn_clicked) {

                resolveSignInError();

            }
        }

    }

}
