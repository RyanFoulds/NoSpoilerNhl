package com.example.nospoilernhl.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.model.Game;
import com.example.nospoilernhl.repository.GameRepository;

import lombok.Getter;

public class HomeViewModel extends AndroidViewModel {

    @Getter
    private MutableLiveData<String> gameHighlightsUri;
    @Getter
    private MutableLiveData<Game> game;

    public HomeViewModel(Application application) {
        super(application);
        gameHighlightsUri = GameRepository.getInstance().getGameHighlightsUri();
        game = GameRepository.getInstance().getGame();
    }
}