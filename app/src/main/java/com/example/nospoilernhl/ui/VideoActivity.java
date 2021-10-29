package com.example.nospoilernhl.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nospoilernhl.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;

public class VideoActivity extends AppCompatActivity implements Player.Listener
{
    private PlayerView playerView;
    private long playbackPosition;
    private boolean stopped;
    private String videoUri;

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

        Bundle e = getIntent().getExtras();
        if (e != null)
        {
            videoUri = e.getString("videoPath");
        }
        playerView = findViewById(R.id.exoplayerView);

        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), findViewById(R.id.player_cast_button));
    }

    private void initializePlayer()
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

    @Override
    public void onStart()
    {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        final Player exoPlayer = playerView.getPlayer();
        if (exoPlayer == null)
        {
            return;
        }

        stopped = true;
        playbackPosition = exoPlayer.getCurrentPosition();
        exoPlayer.release();
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
}
