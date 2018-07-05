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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CharacterDataViewModel extends AndroidViewModel {

    private Character mCharacter;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private int mCharacterId;
    private MutableLiveData<ViewModelResponse<Character>> mCharacterResponse = new MutableLiveData<>();
    private MutableLiveData<ViewModelResponse<CharacterBonusResponse>> mBonusResponse = new MutableLiveData<>();

    public CharacterDataViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public void addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        bonus.setBonus(1);
        bonus.setDescription(UUID.randomUUID().toString());
        mCharacter.getBonuses().add(bonus);
        mBonusResponse.postValue(ViewModelResponse.success(new CharacterBonusResponse(bonus, true)));
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

    public MutableLiveData<ViewModelResponse<Character>> character() {
        return mCharacterResponse;
    }

    public MutableLiveData<ViewModelResponse<CharacterBonusResponse>> bonus() {
        return mBonusResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    public void fetchCharacter() {
        mDisposable.add(
                getCharacterMaybe()
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> mCharacterResponse.setValue(ViewModelResponse.loading()))
                        .subscribe(
                                character -> {
                                    character.getBonuses().add(new CharacterBonusEntity());
                                    emitBonuses(mCharacter = character);
                                    mCharacterResponse.setValue(ViewModelResponse.success(mCharacter));
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    mCharacterResponse.setValue(ViewModelResponse.errored(throwable));
                                }
                        )
        );
    }

    private void emitBonuses(Character character) {
        for (CharacterBonus bonus : character.getBonuses()) {
            mBonusResponse.postValue(ViewModelResponse.success(new CharacterBonusResponse(bonus)));
        }
    }

    private Maybe<Character> getCharacterMaybe() {
        if (isUpdate()) {
            return getApp().getDatabase().getDataStore().findByKey(Character.class, mCharacterId);
        } else {
            return Maybe.just(new CharacterEntity());
        }
    }

    private Application getApp() {
        return (Application) getApplication();
    }

    public class CharacterBonusResponse {
        @NonNull
        private final CharacterBonus mBonus;
        private final boolean mNew;

        CharacterBonusResponse(@NonNull CharacterBonus bonus, boolean isNew) {
            mNew = isNew;
            mBonus = bonus;
        }

        CharacterBonusResponse(@NonNull CharacterBonus bonus) {
            mBonus = bonus;
            mNew = false;
        }

        @NonNull
        public CharacterBonus getBonus() {
            return mBonus;
        }

        public boolean isNew() {
            return mNew;
        }
    }
}
