package com.skiptirengu.dhice;

import com.skiptirengu.dhice.storage.DatabaseStorage;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseStorage.register(this);
    }

    public synchronized DatabaseStorage getDatabase() {
        return null;
    }
}
