package com.skiptirengu.dhice.ui.character;


import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.skiptirengu.dhice.storage.Character;

interface CharacterContract {
    interface View extends MvpLceView<Character> {
        void onAddBonus(android.view.View view);

        void onRemoveBonus(String position);

        void onSaveClick();

        void onCharacterSaved();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadCharacter(int id);

        void saveCharacter(Character mCharacter);
    }
}
