package com.example.nospoilernhl.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class Schedule
{
    private int totalGames;
    private List<Date> dates;
}
