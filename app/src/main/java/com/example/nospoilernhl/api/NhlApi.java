package com.example.nospoilernhl.api;

import com.example.nospoilernhl.model.gamecontent.Content;
import com.example.nospoilernhl.model.Schedule;
import com.example.nospoilernhl.model.TeamsWrapper;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NhlApi
{
    String BASE_URL = "https://statsapi.web.nhl.com";

    @GET("/api/v1/teams")
    Call<TeamsWrapper> getTeams();

    @GET("/api/v1/schedule")
    Call<Schedule> getSchedule(@Query("startDate") final LocalDate startDate,
                               @Query("endDate") final LocalDate endDate,
                               @Query("teamId") final int teamId);

    @GET("/api/v1/game/{game_id}/content")
    Call<Content> getGameContent(@Path("game_id") final String gameId);
}
