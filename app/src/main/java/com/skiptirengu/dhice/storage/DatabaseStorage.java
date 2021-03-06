package com.skiptirengu.dhice.storage;

import android.content.Context;

import com.skiptirengu.dhice.BuildConfig;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

public class DatabaseStorage {
    private static ReactiveEntityStore<Persistable> dataStore;

    public static void register(Context context) {
        synchronized (DatabaseStorage.class) {
            if (dataStore == null) {
                DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, 1);

                ConfigurationBuilder builder = new ConfigurationBuilder(source, Models.DEFAULT);
                builder.setQuoteColumnNames(true).setQuoteTableNames(true);

                if (BuildConfig.DEBUG) {
                    builder.useDefaultLogging();
                    source.setTableCreationMode(TableCreationMode.DROP_CREATE);
                    source.setLoggingEnabled(true);
                }

                dataStore = ReactiveSupport.toReactiveStore(
                        new EntityDataStore<Persistable>(builder.build())
                );
            }
        }
    }

    public static ReactiveEntityStore<Persistable> getInstance() {
        synchronized (DatabaseStorage.class) {
            return dataStore;
        }
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
