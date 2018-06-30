package com.skiptirengu.dhice.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.Character;

import java.util.ArrayList;
import java.util.Objects;

class CharacterListAdapter extends ArrayAdapter<Character> {

    CharacterListAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_list_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.txv_character_name);
        textView.setText(getDisplayText(Objects.requireNonNull(getItem(position))));
        return convertView;
    }

    private String getDisplayText(@NonNull Character character) {
        return String.format("%s - %s", character.getName(), character.getRace());
    }
}