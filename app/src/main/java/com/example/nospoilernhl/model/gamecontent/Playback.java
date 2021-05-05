package com.example.nospoilernhl.model.gamecontent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Playback
{
    private static final Pattern p = Pattern.compile("[^\\d]*([\\d]+)");

    private String name;
    private String url;

    public int getBitRate()
    {
        final Matcher m = p.matcher(name);
        if (m.find())
        {
            final String string = m.group(1);
            return Integer.parseInt(string);
        }
        return 0;
    }
}
