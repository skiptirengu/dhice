package com.skiptirengu.dhice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

import io.requery.EntityCache;
import io.requery.TransactionIsolation;
import io.requery.TransactionListener;
import io.requery.meta.EntityModel;
import io.requery.sql.Configuration;
import io.requery.sql.ConnectionProvider;
import io.requery.sql.EntityDataStore;
import io.requery.sql.EntityStateListener;
import io.requery.sql.Mapping;
import io.requery.sql.Platform;
import io.requery.sql.StatementListener;
import io.requery.sql.TransactionMode;
import io.requery.util.function.Function;
import io.requery.util.function.Supplier;

/**
 * {@link Fragment}.
 */
public class CharacterListFragment extends Fragment implements OnItemClickListener, View.OnClickListener {
    private CharacterListAdapter mAdapter;
    private ListView mListView;
    private FloatingActionButton mActionButton;

    public CharacterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_character_list, container, false);

        mAdapter = new CharacterListAdapter(requireContext(), new ArrayList<>());
        mListView = inflate.findViewById(R.id.listview_characters);
        mActionButton = inflate.findViewById(R.id.fab_new_character);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mActionButton.setOnClickListener(this);

        return inflate;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle arguments = new Bundle();
        arguments.putBoolean("update", true);
        arguments.putLong("id", Objects.requireNonNull(mAdapter.getItem(i)).getId());
        setDataCharacterFragment(arguments);
    }

    @Override
    public void onClick(View view) {
        setDataCharacterFragment(null);
    }

    private void setDataCharacterFragment(Bundle arguments) {
        CharacterDataFragment fragment = new CharacterDataFragment();
        fragment.setArguments(arguments);
        ((MainActivity) Objects.requireNonNull(getActivity())).setFragment(fragment);
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
            return convertView;
        }
    }
}
