package com.skiptirengu.dhice.ui.characters;

import android.content.Context;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.storage.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CharacterListPresenter extends MvpBasePresenter<CharacterListContract.View> implements CharacterListContract.Presenter {
    protected Database mDatabase;
    protected CompositeDisposable mDisposable;

    CharacterListPresenter(Context applicationContext) {
        mDatabase = new Database(applicationContext);
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void destroy() {
        super.destroy();
        mDisposable.clear();
    }

    @Override
    public void loadCharacters() {
        mDisposable.add(
                mDatabase
                        .getDataStore()
                        .select(Character.class)
                        .orderBy(CharacterEntity.NAME.lower())
                        .get()
                        .observable()
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> onCharacterStart())
                        .doOnSuccess(characters -> onCharactersResult(characters))
                        .doOnError(throwable -> onCharactersResult(new ArrayList<>()))
                        .subscribe()
        );

    }

    private void onCharactersResult(List<Character> list) {
        ifViewAttached(view -> {
            view.setData(list);
            view.showContent();
        });
    }

    private void onCharacterStart() {
        ifViewAttached(view -> view.showLoading(false));
    }
}
