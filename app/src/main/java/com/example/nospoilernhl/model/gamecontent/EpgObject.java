package com.example.nospoilernhl.model.gamecontent;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class EpgObject
{
    private String title;
    private List<MediaItem> items;
}
