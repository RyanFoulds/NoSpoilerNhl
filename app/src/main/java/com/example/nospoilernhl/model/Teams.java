package com.example.nospoilernhl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
@Builder
public class Teams
{
    private TeamWrapper away;
    private TeamWrapper home;
}
