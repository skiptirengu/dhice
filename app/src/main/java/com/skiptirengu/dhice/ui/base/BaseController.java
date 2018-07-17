package com.skiptirengu.dhice.ui.base;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController;

import java.util.Objects;

public abstract class BaseController<V extends MvpView, P extends MvpPresenter<V>> extends MvpController<V, P> {

    protected String getString(int resource) {
        return Objects.requireNonNull(getResources()).getString(resource);
    }

    private void setTitle() {
        Controller parentController = getParentController();
        while (parentController != null) {
            if (parentController instanceof BaseController && ((BaseController) parentController).getTitle() != null) {
                return;
            }
            parentController = parentController.getParentController();
        }

        String title = getTitle();
        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();

        if (title != null && actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onChangeStarted(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        if (changeType.isEnter) {
            setTitle();
        }
        super.onChangeStarted(changeHandler, changeType);
    }

    protected abstract String getTitle();
}
