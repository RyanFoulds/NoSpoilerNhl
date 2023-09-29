package com.example.nospoilernhl.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface LogoApi
{
    String BASE_URL = "https://assets.nhle.com/";

    @Headers("Content-Type: image/svg+xml")
    @GET("/logos/nhl/svg/{TEAM_ABBREVIATION}_light.svg")
    Call<ResponseBody> getLogo(@Path("TEAM_ABBREVIATION") final String teamAbbreviation);
}
