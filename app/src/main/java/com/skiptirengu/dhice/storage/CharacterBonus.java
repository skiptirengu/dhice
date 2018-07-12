package com.skiptirengu.dhice.storage;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Persistable;
import io.requery.Transient;

@Entity
public abstract class CharacterBonus extends BaseObservable implements Persistable, Parcelable {
    @Key
    @Generated
    public abstract int getId();

    @Bindable
    public abstract String getDescription();

    public abstract void setDescription(String val);

    @Bindable
    public abstract int getBonus();

    public abstract void setBonus(int val);

    @Bindable
    public abstract String getType();

    public abstract void setType(String type);

    @ManyToOne
    public abstract Character getCharacter();

    @Transient
    public final int nativeHashCode() {
        return super.hashCode();
    }
}
