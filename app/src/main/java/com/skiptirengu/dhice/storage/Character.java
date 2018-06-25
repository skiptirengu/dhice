package com.skiptirengu.dhice.storage;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;

@Entity
public interface Character extends Persistable {
    @Key
    @Generated
    int getId();

    String getName();

    void setName(String val);

    String getRace();

    void setRace(String val);

    String getPreferredAttack();

    void setPreferredAttack(String val);
}
