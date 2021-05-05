package com.example.nospoilernhl.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.nospoilernhl.R;
import com.example.nospoilernhl.model.Game;
import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

public class HomeFragment extends Fragment implements BetterVideoCallback {

    private BetterVideoPlayer player;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        player = root.findViewById(R.id.player);
        player.setCallback(this);

        final TextView label = root.findViewById(R.id.video_label);

        homeViewModel.getGameHighlightsUri().observe(getViewLifecycleOwner(),
                (uri -> loadVideo(player, uri)));

        homeViewModel.getGame().observe(getViewLifecycleOwner(),
                (game -> updateLabel(label, game)));

        return root;
    }

    private void loadVideo(final BetterVideoPlayer videoView, final String update)
    {
        if (update != null && !update.isEmpty())
        {
            videoView.setSource(Uri.parse(update));
        }
    }

    private void updateLabel(final TextView textView, final Game update)
    {
        if(update.getTeams() != null)
        {
            textView.setText(String.format("%s vs %s - %s",
                    update.getTeams().getHome().getName(),
                    update.getTeams().getAway().getName(),
                    update.getGameDate()));
        }
    }

    @Override
    public void onStarted(BetterVideoPlayer player) {

    }

    @Override
    public void onPaused(BetterVideoPlayer player) {

    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {

    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(BetterVideoPlayer player) {

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

    }
}
