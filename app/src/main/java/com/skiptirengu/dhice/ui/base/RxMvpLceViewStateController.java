package com.skiptirengu.dhice.ui.base;

import android.support.annotation.NonNull;
import android.view.View;

import com.hannesdorfmann.mosby3.conductor.viewstate.lce.MvpLceViewStateController;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;

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
}
