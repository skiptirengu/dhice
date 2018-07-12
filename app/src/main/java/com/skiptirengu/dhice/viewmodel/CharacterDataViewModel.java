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

    public synchronized void addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        List<CharacterBonus> bonusList = mCharacter.getBonuses();
        bonusList.add(bonus);
        mBonusResponse.setValue(new CharacterBonusResponse(bonus, bonus.nativeHashCode(), true));
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
                        .delay(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> mCharacterResponse.setValue(ViewModelResponse.loading()))
                        .subscribe(
                                character -> {
                                    mCharacter = character;
                                    emitBonuses(mCharacter);
                                    mCharacterResponse.setValue(ViewModelResponse.success(mCharacter));
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    mCharacterResponse.setValue(ViewModelResponse.errored(throwable));
                                }
                        )
        );
    }

    private synchronized void emitBonuses(Character character) {
        List<CharacterBonus> bonusList = character.getBonuses();
        for (int i = 0; i < bonusList.size(); i++) {
            CharacterBonus bonus = bonusList.get(i);
            mBonusResponse.setValue(new CharacterBonusResponse(bonus, bonus.nativeHashCode()));
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

    public synchronized void removeBonus(final int tag) {
        List<CharacterBonus> bonusList = mCharacter.getBonuses();
        for (CharacterBonus bonus : bonusList) {
            if (bonus.nativeHashCode() == tag) {
                bonusList.remove(bonus);
                mDeleteResponse.setValue(tag);
                break;
            }
        }
    }

    private String getSaveString() {
        return getApp().getString(R.string.character_saved);
    }

    public class CharacterBonusResponse {
        @NonNull
        private final CharacterBonus mBonus;
        private final boolean mNew;
        private final int mTag;

        CharacterBonusResponse(@NonNull CharacterBonus bonus, int tag, boolean isNew) {
            mNew = isNew;
            mBonus = bonus;
            mTag = tag;
        }

        CharacterBonusResponse(@NonNull CharacterBonus bonus, int tag) {
            mBonus = bonus;
            mNew = false;
            mTag = tag;
        }

        @NonNull
        public CharacterBonus getBonus() {
            return mBonus;
        }

        public boolean isNew() {
            return mNew;
        }

        public int getTag() {
            return mTag;
        }
    }
}
