package com.skiptirengu.dhice.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;

import java.util.Objects;

/**
 * {@link Fragment}
 */
public class DamageRollFragment extends Fragment {

    private MainActivity mMainActivity;
    private ActionBar mToolbar;

    public DamageRollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMainActivity = Objects.requireNonNull((MainActivity) getActivity());
        mToolbar = Objects.requireNonNull(mMainActivity.getSupportActionBar());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.damage_roll_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mToolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        mToolbar.setCustomView(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_select_character, menu);
        mToolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mToolbar.setCustomView(R.layout.select_character_menu_spinner);
    }
}
