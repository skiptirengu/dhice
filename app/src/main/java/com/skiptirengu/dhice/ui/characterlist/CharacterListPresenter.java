package com.skiptirengu.dhice.ui.characterlist;

import com.skiptirengu.dhice.exception.EmptyResultException;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.storage.DatabaseStorage;
import com.skiptirengu.dhice.ui.base.RxMvpBasePresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class CharacterListPresenter extends RxMvpBasePresenter<CharacterListContract.View> implements CharacterListContract.Presenter {

    private ReactiveEntityStore<Persistable> mStorage;

    CharacterListPresenter() {
        mStorage = DatabaseStorage.getInstance();
    }

    @Override
    public void loadCharacters() {
        disposeOnDestroy(
                mStorage
                        .select(Character.class)
                        .orderBy(CharacterEntity.NAME.lower())
                        .get()
                        .observable()
                        .toList()
                        .subscribeOn(Schedulers.io())
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
