package com.aliv3.RickshawWalaUser;

import android.app.Application;
import android.content.Context;

public class RickshawWalaUser extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        RickshawWalaUser.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return RickshawWalaUser.context;
    }

}
