package com.example.dublinbusalarm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DublinBusApi {

    //https://data.smartdublin.ie/cgi-bin/rtpi/routeinformation?routeid=[routeid]&operator=bac

    @GET("cgi-bin/rtpi/routeinformation")
    Call<BusRoute> getBusRouteList(@Query("routeid") String routeid, @Query("operator") String operator);
}

