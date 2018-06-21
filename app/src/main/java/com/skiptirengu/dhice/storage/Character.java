package com.skiptirengu.dhice.storage;

import com.orm.SugarRecord;

public class Character extends SugarRecord<Character> {
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
