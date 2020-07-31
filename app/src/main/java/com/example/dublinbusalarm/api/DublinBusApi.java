package com.example.dublinbusalarm.api;

import com.example.dublinbusalarm.models.BusRoute;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DublinBusApi {

    // full api request path for a particular bus line (routeid)
    // https://data.smartdublin.ie/cgi-bin/rtpi/routeinformation?routeid=[routeid]&operator=bac

    @GET("cgi-bin/rtpi/routeinformation")
    Call<BusRoute> getBusRouteList(@Query("routeid") String routeid, @Query("operator") String operator);
}

