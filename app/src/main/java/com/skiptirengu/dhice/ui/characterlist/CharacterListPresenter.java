package com.skiptirengu.dhice.ui.characterlist;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.skiptirengu.dhice.exception.EmptyResultException;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.storage.DatabaseStorage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class CharacterListPresenter extends MvpBasePresenter<CharacterListContract.View> implements CharacterListContract.Presenter {

    private ReactiveEntityStore<Persistable> mStorage;
    private CompositeDisposable mDisposable;

    CharacterListPresenter() {
        mStorage = DatabaseStorage.getInstance();
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
                mStorage
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
                        .doOnError(throwable -> onCharacterError(throwable))
                        .subscribe()
        );

    }

    private void onCharacterError(Throwable throwable) {
        ifViewAttached(view -> view.showError(throwable, false));
    }

    private void onCharactersResult(List<Character> list) {
        ifViewAttached(view -> {
            if (!list.isEmpty()) {
                view.setData(list);
                view.showContent();
            } else {
                view.showError(new EmptyResultException(), false);
            }
        });
    }

    private void onCharacterStart() {
        ifViewAttached(view -> view.showLoading(false));
    }
}
