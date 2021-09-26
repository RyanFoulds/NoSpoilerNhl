package com.example.nospoilernhl.ui.teamselector;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.repository.GameRepository;
import com.example.nospoilernhl.repository.TeamsRepository;

import java.util.List;

import lombok.Getter;

public class TeamSelectorViewModel extends AndroidViewModel
{
    @Getter
    private final MutableLiveData<List<Team>> teams;

    @Getter
    private final MutableLiveData<String> currentGameUri;

    @Getter
    private final MutableLiveData<Team> currentSelectedTeam;

    private final GameRepository gameRepository;

    public TeamSelectorViewModel(final Application application)
    {
        super(application);
        currentSelectedTeam = new MutableLiveData<>();
        final TeamsRepository teamsRepository = TeamsRepository.getInstance(application.getApplicationContext());
        teams = teamsRepository.getTeams();
        currentGameUri = GameRepository.getInstance().getGameHighlightsUri();
        gameRepository = GameRepository.getInstance();
        teamsRepository.searchTeams();
    }

    public void updateTeam(final Team team)
    {
        gameRepository.updateGame(team);
    }

    public void refresh(){
        // Should already have an instance of TeamsRepository by the time this is called,
        // so the null application context shouldn't matter.
        TeamsRepository.getInstance(null).searchTeams();
    }
}
