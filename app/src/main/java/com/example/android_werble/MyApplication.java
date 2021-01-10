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

    private static Integer managing=1;

    public static Integer getManaging() {
        return managing;
    }

    public static void setManaging(Integer managingInteger) {
        managing = managingInteger;
    }
}
