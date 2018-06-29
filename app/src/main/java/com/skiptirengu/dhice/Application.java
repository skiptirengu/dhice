package com.skiptirengu.dhice;

import com.skiptirengu.dhice.storage.Database;

public class Application extends android.app.Application {
    private Database mDatabase;

    public synchronized Database getDatabase() {
        if (mDatabase == null) {
            mDatabase = new Database(this);
        }
        return mDatabase;
    }
}
