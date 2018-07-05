package com.skiptirengu.dhice.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ViewModelResponse<T> {
    @NonNull
    private Status mStatus;
    @Nullable
    private T mData;
    @Nullable
    private Throwable mError;

    private ViewModelResponse(@NonNull Status status, @Nullable T data, @Nullable Throwable error) {
        this.mStatus = status;
        this.mData = data;
        this.mError = error;
    }

    public static <T> ViewModelResponse<T> loading() {
        return new ViewModelResponse<>(Status.LOADING, null, null);
    }

    public static <T> ViewModelResponse<T> errored(Throwable throwable) {
        return new ViewModelResponse<>(Status.ERRORED, null, throwable);
    }

    public static <T> ViewModelResponse<T> success(T data) {
        return new ViewModelResponse<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public Status getStatus() {
        return mStatus;
    }

    @Nullable
    public T getData() {
        return mData;
    }

    @Nullable
    public Throwable getError() {
        return mError;
    }
}
