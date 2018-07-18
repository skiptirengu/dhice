package com.skiptirengu.dhice;

import com.skiptirengu.dhice.storage.Database;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Database.register(this);
    }

    public synchronized Database getDatabase() {
        return null;
    }
}
