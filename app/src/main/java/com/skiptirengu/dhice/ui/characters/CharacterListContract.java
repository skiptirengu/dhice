package com.skiptirengu.dhice.ui.characters;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.skiptirengu.dhice.storage.Character;

import java.util.List;

public interface CharacterListContract {
    interface View extends MvpLceView<List<Character>> {
    }

    interface Presenter extends MvpPresenter<View> {
        void loadCharacters();
    }
}
