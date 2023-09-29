package com.example.nospoilernhl.repository;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.MutableLiveData;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.nospoilernhl.R;
import com.example.nospoilernhl.api.ApiUtils;
import com.example.nospoilernhl.api.LogoApi;
import com.example.nospoilernhl.model.Team;

import java.io.IOException;

import lombok.Getter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogoRepository
{
    private static final Long cacheSize = (long) (5 * 1024 * 1024);
    private static final int cacheExpiry = 60 * 60 * 24 * 7 * 52;

    private static LogoRepository instance;

    private final LogoApi logoApi;

    private final Drawable defaultDrawable;

    @Getter
    private MutableLiveData<Drawable> currentLogo;

    public static LogoRepository getInstance(final Context context)
    {
        if (instance == null)
        {
            instance = new LogoRepository(context);
        }
        return instance;
    }

    private LogoRepository()
    {
        // Do not instantiate this way.
        this.logoApi = null;
        this.defaultDrawable = null;
    }

    private LogoRepository(final Context context)
    {

        final Cache cache = new Cache(context.getFilesDir(), cacheSize);
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(ApiUtils.getCacheInterceptor(context, cacheExpiry, cacheExpiry))
                .build();

        logoApi = new Retrofit.Builder()
                .baseUrl(LogoApi.BASE_URL)
                .client(client)
                .build()
                .create(LogoApi.class);

        currentLogo = new MutableLiveData<>();
        defaultDrawable = AppCompatResources.getDrawable(context, R.drawable.empty);
        currentLogo.postValue(defaultDrawable);
    }

    public void updateLogo(final Team team)
    {
        final String teamAbbreviation = team.getAbbreviation();
        logoApi.getLogo(teamAbbreviation).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        final SVG svg = SVG.getFromString(response.body().source().readUtf8());
                        currentLogo.postValue(new PictureDrawable(svg.renderToPicture()));
                    }
                    catch (IOException | SVGParseException e) {
                        currentLogo.postValue(defaultDrawable);
                        Log.e("LogoRepository", "Could not load logo for team " + teamAbbreviation + ".", e);
                    }
                }
                else
                {
                    currentLogo.postValue(defaultDrawable);
                    Log.e("LogoRepository", "Could not load logo for team " + teamAbbreviation + ", bad response from api.");
                }
            }

            @Override
            public void onFailure(final Call call, final Throwable t)
            {
                currentLogo.postValue(defaultDrawable);
            }
        });
    }
}
