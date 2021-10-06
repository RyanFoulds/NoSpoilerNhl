package com.example.nospoilernhl.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface LogoApi
{
    String BASE_URL = "https://www-league.nhlstatic.com/";

    @Headers("Content-Type: image/svg+xml")
    @GET("/images/logos/teams-current-primary-light/{TEAM_ID}.svg")
    Call<ResponseBody> getLogo(@Path("TEAM_ID") final int teamId);
}
