package com.example.nospoilernhl.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.api.NhlApi;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.model.TeamsWrapper;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamsRepository
{
    private final NhlApi nhlApi;

    @Getter
    private final MutableLiveData<List<Team>> teams;

    private static TeamsRepository instance;

    public static TeamsRepository getInstance()
    {
        if (instance == null)
        {
            instance = new TeamsRepository();
        }
        return instance;
    }

    private TeamsRepository()
    {
        teams = new MutableLiveData<>();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        nhlApi = new retrofit2.Retrofit.Builder()
                .baseUrl(NhlApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NhlApi.class);
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
