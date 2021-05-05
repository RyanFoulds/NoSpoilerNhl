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
    private MutableLiveData<List<Team>> teams;

    private GameRepository gameRepository;

    public TeamSelectorViewModel(final Application application)
    {
        super(application);
        teams = TeamsRepository.getInstance().getTeams();
        gameRepository = GameRepository.getInstance();
        TeamsRepository.getInstance().searchTeams();
    }

    public void updateTeam(final Team team)
    {
        gameRepository.updateGame(team);
    }

    public void refresh(){
        TeamsRepository.getInstance().searchTeams();
    }
}
