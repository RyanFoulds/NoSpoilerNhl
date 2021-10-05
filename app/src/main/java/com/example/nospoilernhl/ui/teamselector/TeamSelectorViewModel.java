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

    private final TeamsRepository teamsRepository;

    public TeamSelectorViewModel(final Application application)
    {
        super(application);
        currentSelectedTeam = new MutableLiveData<>();

        gameRepository = GameRepository.getInstance(application.getApplicationContext());
        currentGameUri = gameRepository.getGameHighlightsUri();

        teamsRepository = TeamsRepository.getInstance(application.getApplicationContext());
        teams = teamsRepository.getTeams();
        refresh();
    }

    public void updateTeam(final Team team)
    {
        gameRepository.updateGame(team);
    }

    public void refresh()
    {
        teamsRepository.searchTeams();
    }
}
