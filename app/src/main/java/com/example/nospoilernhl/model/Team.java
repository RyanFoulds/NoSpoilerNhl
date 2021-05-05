package com.example.nospoilernhl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Team
{
    private int id;
    private String name;
    private String link;
    private String abbreviation;
    private String teamName;
    private String locationName;
    private boolean active;

    @Override
    public String toString()
    {
        return name;
    }
}
