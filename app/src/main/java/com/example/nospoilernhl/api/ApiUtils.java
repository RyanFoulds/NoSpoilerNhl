package com.example.nospoilernhl.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.Interceptor;
import okhttp3.Request;

public class ApiUtils
{
    public static boolean hasNetwork(final Context context)
    {
        final ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Interceptor getCacheInterceptor(final Context context,
                                                  final int onlineExpiry,
                                                  final int offlineExpiry)
    {
        return chain ->
        {
            final Request request = chain.request();
            if (hasNetwork(context))
            {
                request.newBuilder()
                        .header("Cache-Control", "public, max-age=" + onlineExpiry)
                        .build();
            }
            else
            {
                request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineExpiry)
                        .build();
            }
            return chain.proceed(request);
        };
    }
}
