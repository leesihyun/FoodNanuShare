package org.androidproject.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by seahyun on 2017-05-29.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
