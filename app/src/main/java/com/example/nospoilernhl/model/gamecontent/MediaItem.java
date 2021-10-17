package com.example.nospoilernhl.model.gamecontent;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaItem
{
    private String type;
    private List<Playback> playbacks;
    private Image image;

    public static MediaItem dummy()
    {
        return new MediaItem("DUMMY", Collections.emptyList(), Image.dummy());
    }
}
