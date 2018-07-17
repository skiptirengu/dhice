package com.skiptirengu.dhice.ui.roll;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.ui.base.BaseController;

public class DamageRollController extends BaseController {
    @Override
    protected String getTitle() {
        return getString(R.string.title_roll);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.damage_roll_fragment, container, false);
    }

    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        return null;
    }
}
