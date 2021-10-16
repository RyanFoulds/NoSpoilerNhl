package com.example.nospoilernhl.ui.teamselector;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.repository.GameRepository;
import com.example.nospoilernhl.repository.LogoRepository;
import com.example.nospoilernhl.repository.TeamsRepository;
import com.google.android.gms.cast.framework.CastSession;

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

    @Getter
    private final MutableLiveData<Integer> favouriteTeamId;

    @Getter
    private final MutableLiveData<Drawable> currentLogo;

    @Getter
    private final MutableLiveData<CastSession> currentCastSession;

    private final GameRepository gameRepository;

    private final TeamsRepository teamsRepository;

    private final LogoRepository logoRepository;

    public TeamSelectorViewModel(final Application application)
    {
        super(application);
        currentSelectedTeam = new MutableLiveData<>();
        currentCastSession = new MutableLiveData<>();

        gameRepository = GameRepository.getInstance(application.getApplicationContext());
        currentGameUri = gameRepository.getGameHighlightsUri();

        teamsRepository = TeamsRepository.getInstance(application.getApplicationContext());
        teams = teamsRepository.getTeams();
        favouriteTeamId = teamsRepository.getFavouriteTeamId();
        refreshTeams();

        logoRepository = LogoRepository.getInstance(application.getApplicationContext());
        currentLogo = logoRepository.getCurrentLogo();
    }

    public void updateFavouriteTeam(final int teamId)
    {
        teamsRepository.updateFavouriteTeam(teamId);
    }

    public void clearFavouriteTeam()
    {
        teamsRepository.updateFavouriteTeam(0);
    }

    public void updateTeam(final Team team)
    {
        gameRepository.updateGame(team);
    }

    public void refreshTeams()
    {
        teamsRepository.searchTeams();
    }

    public void updateLogo(final Team team)
    {
        logoRepository.updateLogo(team.getId());
    }
}
