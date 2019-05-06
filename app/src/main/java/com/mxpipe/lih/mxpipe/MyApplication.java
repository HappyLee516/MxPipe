package com.mxpipe.lih.mxpipe;

/*
 * Created by LiHuan on 2018/10/22.
 */

import android.app.Application;

import io.objectbox.BoxStore;

public class MyApplication  extends Application {

    private static MyApplication application;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        boxStore = MyObjectBox.builder().androidContext(MyApplication.this).build();
    }

    public static MyApplication getApplication(){
        return application;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

}
