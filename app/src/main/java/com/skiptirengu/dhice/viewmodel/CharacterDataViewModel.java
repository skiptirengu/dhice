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

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CharacterDataViewModel extends AndroidViewModel {

    private Character mCharacter;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private int mCharacterId;
    private LinkedList<CharacterBonus> mBonusList = new LinkedList<>();

    private MutableLiveData<ViewModelResponse<Character>> mCharacterResponse = new MutableLiveData<>();
    private MutableLiveData<CharacterBonusResponse> mBonusResponse = new MutableLiveData<>();
    private MutableLiveData<String> mDeleteResponse = new MutableLiveData<>();
    private MutableLiveData<ViewModelResponse<String>> mSaveResponse = new MutableLiveData<>();

    public CharacterDataViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public synchronized void addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        mBonusList.addFirst(bonus);
        mBonusResponse.setValue(new CharacterBonusResponse(bonus, bonus.uniqueId(), true));
    }

    public void setCharacterId(int characterId) {
        mCharacterId = characterId;
    }

    private boolean isUpdate() {
        return mCharacterId > 0;
    }

    public String getTitle() {
        return getApplication().getString(isUpdate() ? R.string.title_update_character : R.string.title_create_character);
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

    public MutableLiveData<String> delete() {
        return mDeleteResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    public synchronized void saveCharacter() {
        mCharacter.getBonuses().clear();
        mCharacter.getBonuses().addAll(mBonusList);
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
                                    mBonusList = new LinkedList<>(character.getBonuses());
                                    mCharacterResponse.setValue(ViewModelResponse.success(mCharacter));
                                    emitBonuses();
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    mCharacterResponse.setValue(ViewModelResponse.errored(throwable));
                                }
                        )
        );
    }

    private synchronized void emitBonuses() {
        for (int i = 0; i < mBonusList.size(); i++) {
            CharacterBonus bonus = mBonusList.get(i);
            mBonusResponse.setValue(new CharacterBonusResponse(bonus, bonus.uniqueId()));
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

    public synchronized void removeBonus(final String tag) {
        for (int index = 0; index < mBonusList.size(); index++) {
            CharacterBonus bonus = mBonusList.get(index);
            if (bonus.uniqueId().equals(tag)) {
                mBonusList.remove(index);
                mDeleteResponse.setValue(tag);
                break;
            }
        }
    }

    private String getSaveString() {
        return getApp().getString(R.string.message_character_saved);
    }

    public class CharacterBonusResponse {
        @NonNull
        private final CharacterBonus mBonus;
        private final boolean mNew;
        private final String mTag;

        CharacterBonusResponse(@NonNull CharacterBonus bonus, String tag, boolean isNew) {
            mNew = isNew;
            mBonus = bonus;
            mTag = tag;
        }

        CharacterBonusResponse(@NonNull CharacterBonus bonus, String tag) {
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

        public String getTag() {
            return mTag;
        }
    }
}
