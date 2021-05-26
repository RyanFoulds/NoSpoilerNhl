package com.example.nospoilernhl.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.example.nospoilernhl.R;

import org.apache.commons.lang3.StringUtils;

public class VideoActivity extends Activity implements MediaPlayer.OnCompletionListener
{
    private VideoView videoView;

    @SuppressLint("ClickableViewAccessibility")
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
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));
        videoView.setOnTouchListener(VideoActivity::onTouch);

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

    public void stopPlaying()
    {
        videoView.stopPlayback();
        this.finish();
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer)
    {
        finish();
    }

    private static boolean onTouch(View view, MotionEvent motionEvent) {

        if (((VideoView) view).isPlaying()) {
            ((VideoView) view).pause();
        } else {
            ((VideoView) view).start();
        }
        return true;
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
