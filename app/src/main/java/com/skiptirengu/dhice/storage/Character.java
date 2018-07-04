package com.skiptirengu.dhice.storage;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.Persistable;

@Entity
public interface Character extends Persistable, Parcelable, Observable {
    @Key
    @Generated
    int getId();

    @Bindable
    String getName();

    void setName(String val);

    @Bindable
    String getRace();

    void setRace(String val);

    @Bindable
    String getPreferredAttack();

    void setPreferredAttack(String val);

    @OneToMany(mappedBy = "character", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<CharacterBonus> getBonuses();
}
