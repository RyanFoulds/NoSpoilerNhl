package com.example.nospoilernhl.model.gamecontent;

import java.util.Collections;
import java.util.Map;

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
public class Image
{
    private String title;
    private String altText;
    private Map<String, Cut> cuts;

    public static Image dummy()
    {
        return Image.builder()
                    .altText("DUMMY")
                    .title("DUMMY")
                    .cuts(Collections.emptyMap())
                    .build();
    }
}
