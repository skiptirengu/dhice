package com.skiptirengu.dhice.ui.character;

import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.storage.DatabaseStorage;
import com.skiptirengu.dhice.ui.base.RxMvpBasePresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class CharacterPresenter extends RxMvpBasePresenter<CharacterContract.View> implements CharacterContract.Presenter {

    private ReactiveEntityStore<Persistable> mStorage;

    CharacterPresenter() {
        mStorage = DatabaseStorage.getInstance();
    }

    @Override
    public void loadCharacter(int id) {
        Maybe<Character> characterMaybe;

        if (id > 0) {
            characterMaybe = mStorage.findByKey(Character.class, id);
        } else {
            characterMaybe = Maybe.just(new CharacterEntity());
        }

        disposeOnDestroy(
                characterMaybe
                        .subscribeOn(Schedulers.io())
                        .delay(250, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> onCharacterLoad())
                        .doOnSuccess(character -> onCharacterResult(character))
                        .doOnError(throwable -> onCharacterError(throwable))
                        .subscribe()
        );
    }

    @Override
    public void saveCharacter(Character entity) {
        disposeOnDestroy(
                mStorage
                        .upsert(entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> onCharacterLoad())
                        .doOnSuccess(character -> onCharacterSaved())
                        .doOnError(throwable -> onCharacterSaveError(throwable))
                        .subscribe()
        );
    }

    private void onCharacterSaved() {
        ifViewAttached(view -> view.onCharacterSaved());
    }

    private void onCharacterLoad() {
        ifViewAttached(view -> view.showLoading(false));
    }

    private void onCharacterSaveError(Throwable throwable) {
        ifViewAttached(view -> {
            view.showContent();
            view.showError(throwable, true);
        });
    }

    private void onCharacterError(Throwable throwable) {
        ifViewAttached(view -> view.showError(throwable, false));
    }

    private void onCharacterResult(Character character) {
        ifViewAttached(view -> {
            view.setData(character);
            view.showContent();
        });
    }
}
