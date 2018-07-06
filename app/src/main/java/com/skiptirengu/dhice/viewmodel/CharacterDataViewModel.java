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

import java.util.List;
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
    private MutableLiveData<CharacterBonusResponse> mBonusResponse = new MutableLiveData<>();
    private MutableLiveData<Integer> mDeleteResponse = new MutableLiveData<>();
    private MutableLiveData<ViewModelResponse<String>> mSaveResponse = new MutableLiveData<>();

    public CharacterDataViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public void addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        List<CharacterBonus> bonusList = mCharacter.getBonuses();
        bonusList.add(bonus);
        mBonusResponse.postValue(new CharacterBonusResponse(bonus, bonusList.size() - 1, true));
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

    public MutableLiveData<CharacterBonusResponse> bonus() {
        return mBonusResponse;
    }

    public MutableLiveData<ViewModelResponse<String>> save() {
        return mSaveResponse;
    }

    public MutableLiveData<Integer> delete() {
        return mDeleteResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    public void saveCharacter() {
        mDisposable.add(
                getApp().getDatabase()
                        .getDataStore()
                        .upsert(mCharacter)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> mSaveResponse.setValue(ViewModelResponse.loading()))
                        .doOnSuccess(character -> mSaveResponse.setValue(ViewModelResponse.success(getSaveString())))
                        .doOnError(throwable -> mSaveResponse.setValue(ViewModelResponse.errored(throwable)))
                        .subscribe()
        );
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
        List<CharacterBonus> bonusList = character.getBonuses();
        for (int i = 0; i < bonusList.size(); i++) {
            mBonusResponse.setValue(new CharacterBonusResponse(bonusList.get(i), i));
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

    public void removeBonus(int index) {
        mCharacter.getBonuses().remove(index);
        mDeleteResponse.postValue(index);
    }

    private String getSaveString() {
        return getApp().getString(R.string.character_saved);
    }

    public class CharacterBonusResponse {
        @NonNull
        private final CharacterBonus mBonus;
        private final boolean mNew;
        private final int mIndex;

        CharacterBonusResponse(@NonNull CharacterBonus bonus, int index, boolean isNew) {
            mNew = isNew;
            mBonus = bonus;
            mIndex = index;
        }

        CharacterBonusResponse(@NonNull CharacterBonus bonus, int index) {
            mBonus = bonus;
            mNew = false;
            mIndex = index;
        }

        @NonNull
        public CharacterBonus getBonus() {
            return mBonus;
        }

        public boolean isNew() {
            return mNew;
        }

        public int getIndex() {
            return mIndex;
        }
    }
}
