package com.skiptirengu.dhice.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.CharacterBonus;

import java.util.ArrayList;

public class CharacterBonusAdapter extends ArrayAdapter<CharacterBonus> {

    CharacterBonusAdapter(Context ctx) {
        super(ctx, 0, new ArrayList<>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_bonus, parent, false);
        }
        return convertView;
    }
}
