package com.example.nospoilernhl.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.api.ApiUtils;
import com.example.nospoilernhl.api.NhlApi;
import com.example.nospoilernhl.model.Date;
import com.example.nospoilernhl.model.Game;
import com.example.nospoilernhl.model.Schedule;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.model.gamecontent.Content;
import com.example.nospoilernhl.model.gamecontent.Cut;
import com.example.nospoilernhl.model.gamecontent.MediaItem;
import com.example.nospoilernhl.model.gamecontent.Playback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameRepository
{
    private final NhlApi api;

    @Getter
    private final MutableLiveData<String> gameHighlightsUri;
    @Getter
    private final MutableLiveData<Game> game;
    @Getter
    private final MutableLiveData<String> thumbnailUri;

    private static final String HIGHLIGHT_TITLE = "Extended Highlights";

    private static GameRepository instance;

    private static final Long cacheSize = (long) (5 * 1024 * 1024);

    public static GameRepository getInstance(final Context context)
    {
        if (instance == null)
        {
            instance = new GameRepository(context);
        }
        return instance;
    }

    private GameRepository()
    {
        // Do not instantiate this way.
        this.api = null;
        this.gameHighlightsUri = null;
        this.game = null;
        this.thumbnailUri = null;
    }

    private GameRepository(final Context context)
    {
        gameHighlightsUri = new MutableLiveData<>();
        game = new MutableLiveData<>();
        thumbnailUri = new MutableLiveData<>();

        final Cache cache = new Cache(context.getFilesDir(), cacheSize);

        final OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(ApiUtils.getCacheInterceptor(context, 60 * 30, 60 * 60 * 12))
                .build();

        Gson gson = new GsonBuilder().setDateFormat(DateFormat.DATE_FIELD).create();
        api = new Retrofit.Builder()
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
                        if (!response.isSuccessful() || response.body() == null)
                        {
                            game.postValue(null);
                            Log.e("Game repo", "failed to get schedule for team " + team.getName() + ". Bad response from api.");
                            updateContent("");
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
                        game.postValue(null);
                        updateContent("");
                        Log.e("Game repo", "failed to process schedule", t);
                    }
                }
        );
    }

    private void updateContent(final String gameId)
    {
        if (gameId == null || gameId.isEmpty())
        {
            gameHighlightsUri.postValue("");
            Log.e("GameRepository", "gameId was null or empty, couldn't update game highlights uri.");
            return;
        }
        // Get the content for the provided gameId and update the video uri
        api.getGameContent(gameId).enqueue(
                new Callback<Content>() {
                    @Override
                    public void onResponse(Call<Content> call, Response<Content> response) {
                        if (!response.isSuccessful() || response.body() == null)
                        {
                            gameHighlightsUri.postValue("");
                            thumbnailUri.postValue("");
                            Log.e("Game repo", "failed to get game content, bad response from api.");
                            return;
                        }
                        final List<MediaItem> mediaItems = getHighlightMediaItemFrom(response.body());
                        gameHighlightsUri.postValue(getHighlightFrom(mediaItems));
                        thumbnailUri.postValue(getThumbnailFrom(mediaItems));
                    }

                    @Override
                    public void onFailure(Call<Content> call, Throwable t) {
                        gameHighlightsUri.postValue("");
                        thumbnailUri.postValue("");
                        Log.e("Game repo", "failed to process content", t);
                    }
                }
        );
    }

    private List<MediaItem> getHighlightMediaItemFrom(final Content content)
    {
        return content.getMedia().getEpg().stream()
                .filter(epg -> epg.getTitle().equalsIgnoreCase(HIGHLIGHT_TITLE))
                .map(epg -> epg.getItems().stream().findFirst().orElse(MediaItem.dummy()))
                .collect(Collectors.toList());
    }

    private String getThumbnailFrom(final List<MediaItem> mediaItems)
    {
        return mediaItems.stream()
                         .flatMap(mediaItem -> mediaItem.getImage().getCuts().values().stream())
                         .sorted((cut1, cut2) -> Integer.compare(cut2.getWidth(), cut1.getWidth()))
                         .map(Cut::getSrc)
                         .findFirst()
                         .orElse("");
    }

    private String getHighlightFrom(final List<MediaItem> mediaItems)
    {
        return mediaItems.stream()
                         .flatMap(mediaItem -> mediaItem.getPlaybacks().stream())
                         .filter(playback -> playback.getName().contains("FLASH"))
                         .sorted((p1, p2) -> Integer.compare(p2.getBitRate(), p1.getBitRate()))
                         .peek(item -> Log.d("game repo", item.getName()))
                         .map(Playback::getUrl)
                         .findFirst()
                         .orElse("");
    }
}
