package com.skiptirengu.dhice.ui.base;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

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

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
        mOnDetachCompositeDisposable.clear();
    }

    protected void disposeOnDetach(Disposable disposable) {
        mOnDetachCompositeDisposable.add(disposable);
    }

    @DrawableRes
    protected abstract int getEmptyDrawable();

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        if (e instanceof EmptyResultException) {
            Context context = Objects.requireNonNull(getApplicationContext());
            Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, getEmptyDrawable()));
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.hintColor), PorterDuff.Mode.MULTIPLY);
            errorView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        }
        super.showError(e, pullToRefresh);
    }
}
