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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.nospoilernhl.R;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.ui.VideoActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeamSelectorFragment extends Fragment
{
    private TeamSelectorViewModel viewModel;

    private Spinner teamSpinner;

    private ImageView logo;

    private Button watchButton;

    private ToggleButton favouriteSwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        viewModel = ViewModelProviders.of(this).get(TeamSelectorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_team_selector, container, false);

        logo = root.findViewById(R.id.logoView);

        teamSpinner = root.findViewById(R.id.team_selector);
        observeViewModel();
        viewModel.refreshTeams();

        watchButton = root.findViewById(R.id.watch_button);
        watchButton.setOnClickListener(this::playVideoFullScreen);
        viewModel.getCurrentGameUri().observe(this, this::updateButton);

        registerSpinnerOnClickListener();

        favouriteSwitch = root.findViewById(R.id.favourite_switch);
        favouriteSwitch.setOnCheckedChangeListener(createFavouriteToggleListener());
        viewModel.getCurrentSelectedTeam().observe(this, this::updateFavouriteSwitch);
        viewModel.getFavouriteTeamId().observe(this, this::updateSpinnerOrder);

        return root;
    }

    private void observeViewModel()
    {
        viewModel.getTeams().observe(this, (teams) -> {
            final ArrayAdapter<Team> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()),
                    android.R.layout.simple_spinner_item, teams.stream()
                                                               .sorted(getTeamsComparator())
                                                               .collect(Collectors.toList()));
            adapter.setDropDownViewResource(R.layout.spinner_item);
            teamSpinner.setAdapter(adapter);
                });
    }

    /**
     * Makes sure that the user's "favourite" team is pre-selected as the first element in the list.
     */
    private Comparator<Team> getTeamsComparator()
    {
        return (t1, t2) -> Objects.equals(viewModel.getFavouriteTeamId().getValue(), t1.getId())
                ? -1
                : Objects.equals(viewModel.getFavouriteTeamId().getValue(), t2.getId())
                    ? 1
                    : t1.getName().compareToIgnoreCase(t2.getName());
    }

    private CompoundButton.OnCheckedChangeListener createFavouriteToggleListener()
    {
        return ((buttonView, isChecked) ->
        {
            // When the user checks enables the switch. Or the favourite team also becomes the selected one.
            if (isChecked
                    && viewModel.getCurrentSelectedTeam().getValue() != null)
            {
                viewModel.updateFavouriteTeam(viewModel.getCurrentSelectedTeam().getValue().getId());
                return;
            }

            // When the user unchecks the switch while the favourite team is selected.
            if (!isChecked
                && Objects.equals(viewModel.getFavouriteTeamId().getValue(),
                                  Objects.requireNonNull(viewModel.getCurrentSelectedTeam().getValue()).getId()))
            {
                viewModel.clearFavouriteTeam();
            }
        });
    }

    private void registerSpinnerOnClickListener()
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

    private void updateButton(final String gameUri)
    {
        watchButton.setEnabled(gameUri != null && !gameUri.isEmpty());
    }

    private void updateFavouriteSwitch(final Team team)
    {
        favouriteSwitch.setChecked(
                Objects.equals(viewModel.getFavouriteTeamId().getValue(), Objects.requireNonNull(viewModel.getCurrentSelectedTeam().getValue()).getId())
        );
    }

    private void updateSpinnerOrder(final int id)
    {
        if (id != 0)
        {
            viewModel.refreshTeams();
        }
    }

    private void updateLogo(final Team team)
    {
        // TODO: implement populating logo with image.
    }
}
