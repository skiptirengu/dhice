package com.skiptirengu.dhice.storage;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcelable;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.Persistable;

@Entity
public abstract class Character extends BaseObservable implements Persistable, Parcelable {
    @Key
    @Generated
    public abstract int getId();

    @Bindable
    public abstract String getName();

    public abstract void setName(String val);

    @Bindable
    public abstract String getRace();

    public abstract void setRace(String val);

    @Bindable
    public abstract String getPreferredAttack();

    public abstract void setPreferredAttack(String val);

    @OneToMany(mappedBy = "character", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    public abstract List<CharacterBonus> getBonuses();
}
