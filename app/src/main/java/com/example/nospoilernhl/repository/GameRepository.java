package com.example.nospoilernhl.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.api.NhlApi;
import com.example.nospoilernhl.model.Date;
import com.example.nospoilernhl.model.Game;
import com.example.nospoilernhl.model.Schedule;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.model.gamecontent.Content;
import com.example.nospoilernhl.model.gamecontent.Playback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameRepository
{
    private final NhlApi api;

    @Getter
    private final MutableLiveData<String> gameHighlightsUri;
    @Getter
    private final MutableLiveData<Game> game;

    private static final String HIGHLIGHT_TITLE = "Extended Highlights";

    private static GameRepository instance;

    public static GameRepository getInstance()
    {
        if (instance == null)
        {
            instance = new GameRepository();
        }
        return instance;
    }

    private GameRepository()
    {
        gameHighlightsUri = new MutableLiveData<>();
        game = new MutableLiveData<>();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Gson gson = new GsonBuilder().setDateFormat(DateFormat.DATE_FIELD).create();
        api = new retrofit2.Retrofit.Builder()
                .baseUrl(NhlApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(NhlApi.class);
    }

    public void updateGame(final Team team)
    {
        api.getSchedule(LocalDate.now().minusDays(5), LocalDate.now(), team.getId()).enqueue(
                new Callback<Schedule>() {
                    @Override
                    public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                        if (response.body() == null)
                        {
                            return;
                        }
                        final Game newGame = response.body().getDates().stream()
                                                        .sorted((d1, d2) -> d2.getDate().compareTo(d1.getDate()))
                                                        .map(Date::getGames)
                                                        .flatMap(List::stream)
                                                        .filter(game1 -> game1.getStatus().getAbstractGameState().equals("Final"))
                                                        .findFirst()
                                                        .orElse(new Game().toBuilder()
                                                                          .gamePk("")
                                                                          .build());
                        game.postValue(newGame);
                        updateContent(newGame.getGamePk());
                    }

                    @Override
                    public void onFailure(Call<Schedule> call, Throwable t) {
                        Log.e("Game repo", "failed to process schedule", t);
                    }
                }
        );
    }

    private void updateContent(final String gameId)
    {
        // Get the content for the provided gameId and update the video uri
        api.getGameContent(gameId).enqueue(
                new Callback<Content>() {
                    @Override
                    public void onResponse(Call<Content> call, Response<Content> response) {
                        if (response.code() == 404 || response.body() == null)
                        {
                            gameHighlightsUri.postValue("");
                            return;
                        }
                        final String newUri = getHighlightFrom(response.body());
                        gameHighlightsUri.postValue(newUri);
                    }

                    @Override
                    public void onFailure(Call<Content> call, Throwable t) {
                        Log.e("Game repo", "failed to process content", t);
                    }
                }
        );
    }

    private String getHighlightFrom(final Content content)
    {
        return content.getMedia().getEpg().stream()
                .filter(epgObject -> epgObject.getTitle().equalsIgnoreCase(HIGHLIGHT_TITLE))
                .flatMap(epgObject -> epgObject.getItems().get(0).getPlaybacks().stream())
                .filter(playback -> playback.getName().contains("FLASH"))
                .sorted((p1, p2) -> Integer.compare(p2.getBitRate(), p1.getBitRate()))
                .peek(item -> Log.d("game repo", item.getName()))
                .map(Playback::getUrl)
                .findFirst()
                .orElse("");
    }
}
