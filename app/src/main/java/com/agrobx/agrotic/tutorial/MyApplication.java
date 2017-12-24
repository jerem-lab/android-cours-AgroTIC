package com.agrobx.agrotic.tutorial;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    private String longLifeTest;

    @Override
    public void onCreate() {
        Log.i("MyTag", "Create");
        super.onCreate();
        longLifeTest = "text from Application";
    }


    public String getLongLifeTest() {
        return longLifeTest;
    }

    public void setLongLifeTest(String longLifeTest) {
        this.longLifeTest = longLifeTest;
    }
}
