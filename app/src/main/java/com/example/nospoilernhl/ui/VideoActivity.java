package com.example.nospoilernhl.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.nospoilernhl.R;

import org.apache.commons.lang3.StringUtils;

public class VideoActivity extends Activity implements MediaPlayer.OnCompletionListener
{
    private VideoView videoView;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_video_player);
        String videoPath = null;
        Bundle e = getIntent().getExtras();
        if (e != null)
        {
            videoPath = e.getString("videoPath");
        }
        videoView = findViewById(R.id.myvideoview);
        videoView.setOnCompletionListener(this);
        videoView.setOnPreparedListener(mp -> mp.setLooping(false));

        final MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        if (!playFileRes(videoPath)) return;
        videoView.start();
    }

    private boolean playFileRes(final String videoPath)
    {
        if (StringUtils.isBlank(videoPath))
        {
            stopPlaying();
            return false;
        }
        else
        {
            videoView.setVideoURI(Uri.parse(videoPath));
            return true;
        }
    }

    private void stopPlaying()
    {
        videoView.stopPlayback();
        this.finish();
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer)
    {
        finish();
    }

    @Override
    protected void onNewIntent(final Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        String videoPath = null;
        final Bundle e = getIntent().getExtras();
        if (e != null)
        {
            videoPath = e.getString("videoPath");
        }

        playFileRes(videoPath);
    }
}
