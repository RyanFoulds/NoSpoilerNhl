package com.example.nospoilernhl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Game
{
    private String gamePk;
    private String gameDate;
    private Teams teams;
    private LinkWrapper content;
    private GameStatus status;
}
