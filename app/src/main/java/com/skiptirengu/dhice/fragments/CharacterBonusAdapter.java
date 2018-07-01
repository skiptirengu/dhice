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
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

public class CharacterBonusAdapter extends ArrayAdapter<CharacterBonus> {

    CharacterBonusAdapter(Context ctx) {
        super(ctx, 0, new ArrayList<>());
    }

    CharacterBonusAdapter(Context ctx, List<CharacterBonus> bonusList) {
        super(ctx, 0, bonusList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_bonus, parent, false);
        }
        convertView.findViewById(R.id.character_bonus_description).requestFocus();
        convertView.findViewById(R.id.btn_delete_bonus).setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(parent);
            remove(getItem(position));
        });
        return convertView;
    }

    public List<CharacterBonus> getAllItems() {
        List<CharacterBonus> list = new ArrayList<>();
        for (int index = 0; index < getCount(); index++) {
            list.add(getItem(index));
        }
        return list;
    }
}
