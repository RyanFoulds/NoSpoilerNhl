package com.example.nospoilernhl.ui.teamselector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.nospoilernhl.R;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.ui.VideoActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public class TeamSelectorFragment extends Fragment
{
    private TeamSelectorViewModel viewModel;

    private Spinner teamSpinner;

    private ImageView logo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        viewModel = ViewModelProviders.of(this).get(TeamSelectorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_team_selector, container, false);

        logo = root.findViewById(R.id.logoView);

        teamSpinner = root.findViewById(R.id.team_selector);
        observeViewModel();
        viewModel.refresh();

        final Button watchButton = root.findViewById(R.id.watch_button);
        watchButton.setOnClickListener(this::playVideoFullScreen);

        viewModel.getCurrentSelectedTeam().observe(this, this::updateLogo);

        registerOnClickListener();
        return root;
    }

    private void observeViewModel()
    {
        viewModel.getTeams().observe(this, (teams) -> {
            final ArrayAdapter<Team> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()),
                    android.R.layout.simple_spinner_item, teams.stream()
                                                               .sorted((t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()))
                                                               .collect(Collectors.toList()));
            adapter.setDropDownViewResource(R.layout.spinner_item);
            teamSpinner.setAdapter(adapter);
                });
    }

    private void registerOnClickListener()
    {
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Team selectedTeam = (Team) parent.getItemAtPosition(position);
                viewModel.updateTeam(selectedTeam);
                viewModel.getCurrentSelectedTeam().postValue(selectedTeam);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("team_selector", "No team selected");
                if (Objects.requireNonNull(viewModel.getTeams().getValue()).isEmpty())
                {
                    viewModel.refresh();
                }
            }
        });
    }

    private void playVideoFullScreen(final View view)
    {
        final String videoPath = viewModel.getCurrentGameUri().getValue();
        if (StringUtils.isBlank(videoPath))
        {
            showToast();
            Log.w("TeamSelectorFragment", "Could not find game highlights for selected team");
            return;
        }

        final Intent videoActivity = new Intent(getActivity(), VideoActivity.class);
        videoActivity.putExtra("videoPath", videoPath);
        startActivity(videoActivity);
    }

    private void showToast()
    {
        final Team currentTeam = viewModel.getCurrentSelectedTeam().getValue();
        final Toast toast = Toast.makeText(getContext(),
                String.format("No recent game found for %s",
                        currentTeam == null ? "" : currentTeam.getTeamName()),
                Toast.LENGTH_LONG);
        toast.show();
    }

    private void updateLogo(final Team team)
    {
        // TODO: implement populating logo with image.
    }
}
