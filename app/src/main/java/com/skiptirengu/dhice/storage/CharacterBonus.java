package com.skiptirengu.dhice.storage;

import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;

@Entity
public interface CharacterBonus extends Parcelable {
    @Key
    @Generated
    int getId();

    String getDescription();

    void setDescription(String val);

    int getBonus();

    void setBonus(int val);

    String getType();

    void setType(String type);

    @ManyToOne
    Character getCharacter();
}
