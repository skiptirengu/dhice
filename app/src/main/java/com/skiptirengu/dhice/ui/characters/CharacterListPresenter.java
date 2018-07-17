package com.skiptirengu.dhice.ui.characters;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.skiptirengu.dhice.storage.Character;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CharacterListPresenter extends MvpBasePresenter<CharacterListContract.View> implements CharacterListContract.Presenter {
    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void destroy() {
        super.destroy();
        mDisposable.clear();
    }

    @Override
    public void loadCharacters() {
        mDisposable.add(
                Single.just(new ArrayList<Character>())
                        .subscribeOn(Schedulers.computation())
                        .delay(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(list -> ifViewAttached(view -> view.showLoading()))
                        .doOnSuccess(list -> ifViewAttached(view -> {
                            view.onCharactersResult(list);
                            view.hideLoading();
                        }))
                        .subscribe()
        );

    }
}
