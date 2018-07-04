package com.skiptirengu.dhice.storage;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Persistable;

@Entity
public interface CharacterBonus extends Persistable, Parcelable, Observable {
    @Key
    @Generated
    int getId();

    @Bindable
    String getDescription();

    void setDescription(String val);

    @Bindable
    int getBonus();

    void setBonus(int val);

    @Bindable
    String getType();

    void setType(String type);

    @ManyToOne
    Character getCharacter();
}
