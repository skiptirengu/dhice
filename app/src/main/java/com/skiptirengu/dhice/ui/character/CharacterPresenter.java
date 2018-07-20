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
        Maybe<Character> maycharacterMaybe;

        if (id > 0) {
            maycharacterMaybe = mStorage.findByKey(Character.class, id);
        } else {
            maycharacterMaybe = Maybe.just(new CharacterEntity());
        }

        disposeOnDestroy(
                maycharacterMaybe
                        .subscribeOn(Schedulers.io())
                        .delay(250, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> onCharacterStart())
                        .doOnSuccess(character -> onCharacterResult(character))
                        .doOnError(throwable -> onCharacterError(throwable))
                        .subscribe()
        );
    }

    private void onCharacterStart() {
        ifViewAttached(view -> view.showLoading(false));
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
