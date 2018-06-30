package com.skiptirengu.dhice.storage;

import android.content.Context;

import com.skiptirengu.dhice.BuildConfig;

import io.reactivex.Observable;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

public class Database {
    private ReactiveEntityStore<Persistable> mDataStore;

    public Database(Context ctx) {
        DatabaseSource source = new DatabaseSource(ctx, Models.DEFAULT, 1);

        ConfigurationBuilder builder = new ConfigurationBuilder(source, Models.DEFAULT);
        builder.setQuoteColumnNames(true).setQuoteTableNames(true);

        if (BuildConfig.DEBUG) {
            builder.useDefaultLogging();
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            source.setLoggingEnabled(true);
        }

        mDataStore = ReactiveSupport.toReactiveStore(
                new EntityDataStore<Persistable>(builder.build())
        );
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        return mDataStore;
    }

    public Observable<Character> findCharacterById(int id) {
        return mDataStore
                .select(Character.class)
                .where(CharacterEntity.ID.eq(id))
                .get()
                .observable();
    }

    public Observable<Character> findCharacters() {
        return mDataStore
                .select(Character.class)
                .orderBy(CharacterEntity.NAME.lower())
                .get()
                .observable();
    }
}
