package com.girish.banksnearme;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by girish on 07-10-2017.
 */

public interface NearBy {

    //location,radius,types,sensor,key
    @GET("json")
    Call<ResponseBody> getBank(@Query("location") String location,
                               @Query("radius") String radius,
                               @Query("types") String types,
                               @Query("sensor") String sensor,
                               @Query("key") String key);
}
