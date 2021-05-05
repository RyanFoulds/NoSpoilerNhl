package com.example.nospoilernhl.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class Date
{
    private String date;
    private int totalGames;
    private List<Game> games;
}
