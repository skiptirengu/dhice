package com.skiptirengu.dhice.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * {@link Fragment}.
 */
public class CharacterListFragment extends Fragment implements OnItemClickListener, View.OnClickListener {
    private CharacterListAdapter mAdapter;
    private ListView mListView;
    private FloatingActionButton mActionButton;
    private View mProgress;
    private View mMainLayout;
    private MainActivity mMainActivity;

    public CharacterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.character_list_fragment, container, false);

        mMainActivity = (MainActivity) getActivity();
        mMainLayout = inflate.findViewById(R.id.layout_character_list);
        mProgress = inflate.findViewById(R.id.progress_bar);
        setLoading(true);

        mAdapter = new CharacterListAdapter(requireContext());
        mListView = inflate.findViewById(R.id.listview_characters);
        mActionButton = inflate.findViewById(R.id.fab_new_character);

        mMainActivity.setTitle(R.string.fragment_title_my_characters);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mActionButton.setOnClickListener(this);
        refreshAdapter();

        return inflate;
    }

    @SuppressLint("CheckResult")
    private void refreshAdapter() {
        mMainActivity
                .getDatabase()
                .findCharacters()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        item -> mAdapter.add(item),
                        Throwable::printStackTrace,
                        () -> setLoading(false)
                );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle arguments = new Bundle();
        arguments.putInt("id", Objects.requireNonNull(mAdapter.getItem(i)).getId());
        setDataCharacterFragment(arguments);
    }

    @Override
    public void onClick(View view) {
        setDataCharacterFragment(null);
    }

    private void setDataCharacterFragment(Bundle arguments) {
        CharacterDataFragment fragment = new CharacterDataFragment();
        fragment.setArguments(arguments);
        mMainActivity.setFragment(fragment);
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mMainLayout.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mMainLayout.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }
}
