package com.example.android_werble;

import android.app.Application;

public class MyApplication extends Application {

    private static Integer range=10;

    public static Integer getGlobalRangeVariable() {
        return range;
    }

    public static void setGlobalRangeVariable(Integer rangeInteger) {
        range = rangeInteger;
    }
}
