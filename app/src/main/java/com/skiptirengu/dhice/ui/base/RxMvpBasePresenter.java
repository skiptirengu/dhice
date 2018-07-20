package com.skiptirengu.dhice.ui.base;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxMvpBasePresenter<V extends MvpView> extends MvpBasePresenter<V> {
    private CompositeDisposable mOnDestroyDisposable = new CompositeDisposable();

    @Override
    public void destroy() {
        super.destroy();
        mOnDestroyDisposable.clear();
    }

    protected void disposeOnDestroy(Disposable disposable) {
        mOnDestroyDisposable.add(disposable);
    }
}
