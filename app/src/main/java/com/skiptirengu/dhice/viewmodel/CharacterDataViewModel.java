package com.skiptirengu.dhice.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterBonus;
import com.skiptirengu.dhice.storage.CharacterBonusEntity;

public class CharacterDataViewModel extends ViewModel {

    private Character mCharacter;

    public CharacterBonus addBonus() {
        CharacterBonus bonus = new CharacterBonusEntity();
        mCharacter.getBonuses().add(bonus);
        return bonus;
    }
}
