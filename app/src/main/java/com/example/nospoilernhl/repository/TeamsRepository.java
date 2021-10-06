package com.example.nospoilernhl.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.api.ApiUtils;
import com.example.nospoilernhl.api.NhlApi;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.model.TeamsWrapper;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamsRepository
{
    private final NhlApi nhlApi;

    @Getter
    private final MutableLiveData<List<Team>> teams;

    @Getter
    private final MutableLiveData<Integer> favouriteTeamId;

    private final SharedPreferences sharedPreferences;

    private static TeamsRepository instance;

    private static final Long cacheSize = (long) (5 * 1024 * 1024);

    private static final String FAVOURITE_TEAM_KEY = "favourite_team_id";

    public static TeamsRepository getInstance(final Context context)
    {
        if (instance == null)
        {
            instance = new TeamsRepository(context);
        }
        return instance;
    }

    private TeamsRepository()
    {
//        Do not instantiate this way.
        this.nhlApi = null;
        this.teams = null;
        this.sharedPreferences = null;
        this.favouriteTeamId = null;
    }

    private TeamsRepository(final Context context)
    {
        teams = new MutableLiveData<>();
        favouriteTeamId = new MutableLiveData<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        favouriteTeamId.postValue(sharedPreferences.getInt(FAVOURITE_TEAM_KEY, 0));

        final Cache cache = new Cache(context.getFilesDir(), cacheSize);
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(ApiUtils.getCacheInterceptor(context, 60, 60 * 60 * 24 * 7 * 52))
                .build();

        nhlApi = new retrofit2.Retrofit.Builder()
                .baseUrl(NhlApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NhlApi.class);
    }

    public void updateFavouriteTeam(final int teamId)
    {
        sharedPreferences.edit()
                         .putInt(FAVOURITE_TEAM_KEY, teamId)
                         .apply();

        favouriteTeamId.postValue(teamId);
    }

    public void searchTeams()
    {
        nhlApi.getTeams().enqueue(new Callback<TeamsWrapper>() {
            @Override
            public void onResponse(Call<TeamsWrapper> call, Response<TeamsWrapper> response) {
                if (response.body() != null)
                {
                    teams.postValue(response.body().getTeams());
                }
            }

            @Override
            public void onFailure(Call<TeamsWrapper> call, Throwable t)
            {
                teams.postValue(Collections.emptyList());
            }
        });
    }
}
