package com.example.nospoilernhl.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiUtils
{
    public static boolean hasNetwork(final Context context)
    {
        final ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
