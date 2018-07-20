package com.skiptirengu.dhice.ui.base;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.hannesdorfmann.mosby3.conductor.viewstate.lce.MvpLceViewStateController;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.exception.EmptyResultException;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxMvpLceViewStateController<CV extends View, M, V extends MvpLceView<M>, P
        extends MvpPresenter<V>> extends MvpLceViewStateController<CV, M, V, P> {

    private final CompositeDisposable mOnDetachCompositeDisposable = new CompositeDisposable();
    private boolean mHasEmptyState;

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
        mOnDetachCompositeDisposable.clear();
    }

    protected void disposeOnDetach(Disposable disposable) {
        mOnDetachCompositeDisposable.add(disposable);
    }

    protected void setHasEmptyState(boolean hasEmptyState) {
        mHasEmptyState = hasEmptyState;
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

    @DrawableRes
    protected int getEmptyDrawable() {
        return 0;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        if (mHasEmptyState && e instanceof EmptyResultException) {
            Context context = Objects.requireNonNull(getApplicationContext());
            Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, getEmptyDrawable()));
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.hintColor), PorterDuff.Mode.MULTIPLY);
            errorView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        }
        super.showError(e, pullToRefresh);
    }
}
