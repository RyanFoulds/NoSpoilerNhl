package com.example.nospoilernhl.ui.teamselector;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.nospoilernhl.R;
import com.example.nospoilernhl.model.Team;
import com.example.nospoilernhl.ui.home.HomeFragment;

import java.util.Objects;
import java.util.stream.Collectors;

public class TeamSelectorFragment extends Fragment
{
    private TeamSelectorViewModel viewModel;

    private Spinner teamSpinner;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        viewModel = ViewModelProviders.of(this).get(TeamSelectorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_team_selector, container, false);

        teamSpinner = root.findViewById(R.id.team_selector);
        observeViewModel();
        registerOnClickListener();

        viewModel.refresh();
        return root;
    }

    private void observeViewModel()
    {
        viewModel.getTeams().observe(this, (teams) -> {
            final ArrayAdapter<Team> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()),
                    android.R.layout.simple_spinner_item, teams.stream()
                                                               .sorted((t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()))
                                                               .collect(Collectors.toList()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("team_selector", "No team selected");
            }
        });
    }


}
