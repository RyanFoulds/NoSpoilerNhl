package com.example.nospoilernhl.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class TeamsWrapper
{
    List<Team> teams;
}
