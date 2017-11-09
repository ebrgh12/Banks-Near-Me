package com.girish.banksnearme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by girish on 07-10-2017.
 */

/**
 * use the above URL to get the list og banks
 * TODO: in location send current user location
 * the below is to find near by bank
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=12.9716,77.5946&radius=5000
 * &types=bank&sensor=true&key=AIzaSyChQemy8iPYPZliZrtJtErpbYf8BfjjXLQ
 *
 * the below is used to find near by ATM
 *
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=12.9716,77.5946&radius=5000
 * &types=atm&sensor=true&key=AIzaSyChQemy8iPYPZliZrtJtErpbYf8BfjjXLQ
 *
 * API KEY : AIzaSyChQemy8iPYPZliZrtJtErpbYf8BfjjXLQ
 * type : Bank/ATM
 * radius : 5000
 * location : User location
 * sensor : true
 *
 * */
public class BankAtmActivity extends AppCompatActivity implements View.OnClickListener{

    TextView bankDataTab, atmDataTab;
    RecyclerView dataListView;

    GPSTracker gpsTracker;
    Double latitude = null, longitude = null;
    Handler locationHandler;

    Integer callingEvent = 0;
    String bankResponse, location, radius = "5000", sensor = "true", API_key = "AIzaSyChQemy8iPYPZliZrtJtErpbYf8BfjjXLQ";

    NearBy nearBy;
    UtilitiesProgress utilitiesProgress;

    List<DataModel> dataModels = new ArrayList<DataModel>();
    ListDataAdapter listDataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_atm_activity);

        /**
         * retrofit initialization and also the service
         * interface of retrofit
         * */
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .client(okHttpClient)
                .addConverterFactory(LoganSquareConverterFactory.create())
                .build();
        nearBy = retrofit.create(NearBy.class);

        gpsTracker = new GPSTracker(this);
        utilitiesProgress = new UtilitiesProgress();

        bankDataTab = (TextView) findViewById(R.id.get_bank_data);
        atmDataTab = (TextView) findViewById(R.id.get_atm_data);
        dataListView = (RecyclerView) findViewById(R.id.list_of_data);
        dataListView.setLayoutManager(new LinearLayoutManager(this));

        listDataAdapter = new ListDataAdapter(this,BankAtmActivity.this,dataModels);
        dataListView.setAdapter(listDataAdapter);

        if (gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }else {
            gpsTracker.showSettingsAlert();
        }

        bankDataTab.setOnClickListener(this);
        atmDataTab.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(latitude != null && longitude != null
                && latitude != 0.0 && longitude != 0.0){
            // call API
            if(callingEvent == 0){
                callingEvent++;
                callBankAPI();
            }
        }else {
            //call Handler every 1 minute
            recursiveHandlerLocation();
        }
    }

    private void callBankAPI() {
        dataModels.clear();
        utilitiesProgress.displayProgressDialog(BankAtmActivity.this,"Loading near by Banks...",false);
        location = String.valueOf(latitude)+","+String.valueOf(longitude);
        Call<ResponseBody> banksNearBy = nearBy.getBank(location,radius,"bank",sensor,API_key);
        banksNearBy.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utilitiesProgress.cancelProgressDialog();
                if(response.isSuccess()){
                    try {
                        bankResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(bankResponse);
                        JSONArray responseArray = jsonObject.getJSONArray("results");
                        if(responseArray.length() != 0){
                            Integer i=0;
                            while (responseArray.length() > i){
                                JSONObject responseObject = responseArray.getJSONObject(i);
                                String name = responseObject.optString("name");
                                String image = responseObject.optString("icon");
                                String address = responseObject.optString("vicinity");
                                Double rating = responseObject.optDouble("rating");

                                JSONObject locationObject = responseObject.getJSONObject("geometry");
                                JSONObject bankLocation = locationObject.getJSONObject("location");
                                Double latitude = bankLocation.optDouble("lat");
                                Double longitude = bankLocation.optDouble("lng");

                                Boolean isOpen = null;

                                if(responseObject.has("opening_hours")){
                                    JSONObject isOpenObject = responseObject.getJSONObject("opening_hours");
                                    isOpen = isOpenObject.optBoolean("open_now");
                                }else {
                                    isOpen = null;
                                }

                                dataModels.add(new DataModel(name,image,address,rating,isOpen,latitude,longitude));

                                i++;
                            }

                            // lode data to list
                            listDataAdapter.notifyDataSetChanged();

                        }else {
                            Toast.makeText(BankAtmActivity.this, "No Banks near by your location.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(gpsTracker, "Failed to get details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utilitiesProgress.cancelProgressDialog();
                Toast.makeText(gpsTracker, "Failed to get details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recursiveHandlerLocation() {
        if(locationHandler == null)
            locationHandler = new Handler();
        locationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsTracker.getLocation();
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                if(latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0){
                    locationHandler.removeCallbacks(this);
                    callBankAPI();
                }else {
                    locationHandler.postDelayed(this,1000);
                    Toast.makeText(BankAtmActivity.this, "Finding Location...", Toast.LENGTH_SHORT).show();
                }
            }
        },1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationHandler != null){
            locationHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callingEvent = 0;
        if(locationHandler != null){
            locationHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_bank_data:
                bankDataTab.setBackgroundColor(getResources().getColor(R.color.action_one));
                atmDataTab.setBackgroundColor(getResources().getColor(R.color.action_two));
                callBankAPI();
                break;
            case R.id.get_atm_data:
                bankDataTab.setBackgroundColor(getResources().getColor(R.color.action_two));
                atmDataTab.setBackgroundColor(getResources().getColor(R.color.action_one));
                callAtmAPI();
                break;
        }
    }

    private void callAtmAPI() {
        dataModels.clear();
        utilitiesProgress.displayProgressDialog(BankAtmActivity.this,"Loading near by ATM...",false);
        location = String.valueOf(latitude)+","+String.valueOf(longitude);
        Call<ResponseBody> banksNearBy = nearBy.getBank(location,radius,"atm",sensor,API_key);
        banksNearBy.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utilitiesProgress.cancelProgressDialog();
                if(response.isSuccess()){
                    try {
                        bankResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(bankResponse);
                        JSONArray responseArray = jsonObject.getJSONArray("results");
                        if(responseArray.length() != 0){
                            Integer i=0;
                            while (responseArray.length() > i){
                                JSONObject responseObject = responseArray.getJSONObject(i);
                                String name = responseObject.optString("name");
                                String image = responseObject.optString("icon");
                                String address = responseObject.optString("vicinity");
                                Double rating = responseObject.optDouble("rating");

                                JSONObject locationObject = responseObject.getJSONObject("geometry");
                                JSONObject bankLocation = locationObject.getJSONObject("location");
                                Double latitude = bankLocation.optDouble("lat");
                                Double longitude = bankLocation.optDouble("lng");

                                Boolean isOpen = null;

                                if(responseObject.has("opening_hours")){
                                    JSONObject isOpenObject = responseObject.getJSONObject("opening_hours");
                                    isOpen = isOpenObject.optBoolean("open_now");
                                }else {
                                    isOpen = null;
                                }

                                dataModels.add(new DataModel(name,image,address,rating,isOpen,latitude,longitude));

                                i++;
                            }

                            // lode data to list
                            listDataAdapter.notifyDataSetChanged();

                        }else {
                            Toast.makeText(BankAtmActivity.this, "No Banks near by your location.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(gpsTracker, "Failed to get details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utilitiesProgress.cancelProgressDialog();
                Toast.makeText(gpsTracker, "Failed to get details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void navigateTo(Double baLatitude, Double baLongitude) {
        Intent intent = new Intent(BankAtmActivity.this,MapActivity.class);
        intent.putExtra("user_lat",latitude);
        intent.putExtra("user_long",longitude);
        intent.putExtra("ba_lat",baLatitude);
        intent.putExtra("ba_long",baLongitude);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

}