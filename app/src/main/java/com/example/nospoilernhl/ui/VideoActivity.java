package com.example.nospoilernhl.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nospoilernhl.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import lombok.AccessLevel;
import lombok.Setter;

public class VideoActivity extends AppCompatActivity implements Player.Listener
{
    private PlayerView playerView;
    @Setter(AccessLevel.PRIVATE)
    private long playbackPosition;
    private boolean stopped;
    private String videoUri;
    private String thumbnailUri;
    private String activeTeam;
    private boolean casting;
    private SessionManagerListener<CastSession> sessionManagerListener;

    private CastContext castContext;

    @Override
    public void onCreate(final Bundle b)
    {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_player);

        castContext = CastContext.getSharedInstance(getApplicationContext());
        sessionManagerListener = new InPlayerSessionManagerListener();
        castContext.getSessionManager().addSessionManagerListener(sessionManagerListener, CastSession.class);

        Bundle e = getIntent().getExtras();
        if (e != null)
        {
            videoUri = e.getString("videoPath");
            thumbnailUri = e.getString("imagePath");
            activeTeam = e.getString("activeTeam");
        }
        playerView = findViewById(R.id.exoplayerView);

        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), findViewById(R.id.player_cast_button));
    }
    private void initializeRemotePlayer()
    {
        destroyOldPlayer();

        final com.google.android.exoplayer2.MediaMetadata movieMetaData = new com.google.android.exoplayer2.MediaMetadata.Builder()
                .setArtworkUri(Uri.parse(thumbnailUri))
                .setDescription(activeTeam + " - latest game")
                .build();

        final CastPlayer player = new CastPlayer(castContext);
        player.setMediaItem(new MediaItem.Builder()
                                         .setMediaMetadata(movieMetaData)
                                         .setUri(videoUri)
                                         .setMimeType(MimeTypes.BASE_TYPE_VIDEO)
                                         .build());

        playerView.setPlayer(player);
        player.seekTo(playbackPosition);
        player.setPlayWhenReady(!stopped);
        player.addListener(this);

        player.prepare();
    }

    private void initializeLocalPlayer()
    {
        final ExoPlayer player = new SimpleExoPlayer.Builder(this).build();

        player.setMediaItem(new MediaItem.Builder()
                                            .setUri(videoUri)
                                            .setMimeType(MimeTypes.BASE_TYPE_VIDEO)
                                            .build());
        playerView.setPlayer(player);
        player.seekTo(playbackPosition);
        player.setPlayWhenReady(!stopped);
        player.addListener(this);

        player.prepare();
    }

    private void castVideo(final CastSession castSession)
    {
        final MediaMetadata movieMetaData = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetaData.putString(MediaMetadata.KEY_TITLE, activeTeam + " - latest game");
        movieMetaData.addImage(new WebImage(Uri.parse(thumbnailUri)));

        final MediaInfo mediaInfo = new MediaInfo.Builder(videoUri)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetaData)
                .build();

        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient != null)
        {
            remoteMediaClient.load(new MediaLoadRequestData.Builder()
                                                           .setCurrentTime(playbackPosition)
                                                           .setMediaInfo(mediaInfo)
                                                           .build());

            remoteMediaClient.addProgressListener((progress, duration) -> setPlaybackPosition(progress), 200L);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        initializeLocalPlayer();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (!casting)
        {
            destroyOldPlayer();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void destroyOldPlayer()
    {
        final Player player = playerView.getPlayer();
        if (player == null)
        {
            return;
        }

        stopped = true;
        playbackPosition = player.getCurrentPosition();
        player.release();
    }

    @Override
    public void onIsPlayingChanged(final boolean isPlaying)
    {
        playerView.setKeepScreenOn(isPlaying);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    private class InPlayerSessionManagerListener implements SessionManagerListener<CastSession>
    {

        @Override
        public void onSessionStarted(@NonNull final CastSession castSession, @NonNull final String s)
        {
            initializeRemotePlayer();
            castVideo(castSession);
            invalidateOptionsMenu();
            casting = true;
        }

        @Override
        public void onSessionEnded(@NonNull final CastSession castSession, final int i)
        {
            casting = false;
            initializeLocalPlayer();
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(@NonNull final CastSession castSession, final boolean b)
        {
        }

        @Override
        public void onSessionStartFailed(@NonNull final CastSession castSession, final int i)
        {
        }

        @Override
        public void onSessionStarting(@NonNull final CastSession castSession)
        {
        }

        @Override
        public void onSessionEnding(@NonNull final CastSession castSession)
        {
        }

        @Override
        public void onSessionResuming(@NonNull final CastSession castSession, @NonNull final String s)
        {
        }

        @Override
        public void onSessionResumeFailed(@NonNull final CastSession castSession, final int i)
        {
        }

        @Override
        public void onSessionSuspended(@NonNull final CastSession castSession, final int i)
        {
        }
    }
}
