package com.skiptirengu.dhice.viewmodel;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.skiptirengu.dhice.Application;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterBonus;
import com.skiptirengu.dhice.storage.CharacterBonusEntity;
import com.skiptirengu.dhice.storage.CharacterEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CharacterDataViewModel extends AndroidViewModel {

    private Character mCharacter;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private int mCharacterId;
    private MutableLiveData<ViewModelResponse<Character>> mResponse = new MutableLiveData<>();

    public CharacterDataViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public CharacterBonus addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        mCharacter.getBonuses().add(bonus);
        return bonus;
    }

    public void setCharacterId(int characterId) {
        mCharacterId = characterId;
    }

    private boolean isUpdate() {
        return mCharacterId > 0;
    }

    public String getTitle() {
        return getApplication().getString(isUpdate() ? R.string.update_character : R.string.create_character);
    }

    public MutableLiveData<ViewModelResponse<Character>> response() {
        return mResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    public void fetchCharacter() {
        Maybe<Character> maybe;

        if (isUpdate()) {
            maybe = getApp().getDatabase().getDataStore().findByKey(Character.class, mCharacterId);
        } else {
            maybe = Maybe.just(new CharacterEntity());
        }

        mDisposable.add(
                maybe.subscribeOn(Schedulers.io())
                        .delay(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> mResponse.setValue(ViewModelResponse.loading()))
                        .subscribe(
                                character -> {
                                    mCharacter = character;
                                    mResponse.setValue(ViewModelResponse.success(mCharacter));
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    mResponse.setValue(ViewModelResponse.errored(throwable));
                                }
                        )
        );
    }

    private Application getApp() {
        return (Application) getApplication();
    }
}
