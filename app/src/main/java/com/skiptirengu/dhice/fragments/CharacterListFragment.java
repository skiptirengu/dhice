package com.skiptirengu.dhice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Fragment}.
 */
public class CharacterListFragment extends Fragment {

    private CharacterListAdapter mAdapter;
    private ListView mListView;

    public CharacterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_character_list, container, false);
        mAdapter = new CharacterListAdapter(requireContext(), new ArrayList<>());
        mListView = inflate.findViewById(R.id.listview_characters);
        mListView.setAdapter(mAdapter);
        return inflate;
    }

    class CharacterListAdapter extends ArrayAdapter<Character> {

        CharacterListAdapter(@NonNull Context context, List<Character> characterList) {
            super(context, 0, characterList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Character character = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_list_item, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.txv_character_name);
            textView.setText(character != null ? character.getName() : null);
            return textView;
        }
    }
}
