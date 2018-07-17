package com.skiptirengu.dhice.ui.characters;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.skiptirengu.dhice.storage.Character;

import java.util.List;

public interface CharacterListContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void onCharactersResult(List<Character> characterList);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadCharacters();
    }
}
