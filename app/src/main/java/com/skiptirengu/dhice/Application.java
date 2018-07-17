package com.skiptirengu.dhice;

import com.skiptirengu.dhice.storage.Database;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DaggerApplicationComponent.builder().create(this).inject(this);
    }

    public synchronized Database getDatabase() {
        return null;
    }
}
