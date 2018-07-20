package com.skiptirengu.dhice.ui.character;


import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.skiptirengu.dhice.storage.Character;

interface CharacterContract {
    interface View extends MvpLceView<Character> {

    }

    interface Presenter extends MvpPresenter<View> {

        void loadCharacter(int id);
    }
}
