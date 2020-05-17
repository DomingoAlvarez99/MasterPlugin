package com.masterplugin.client;

public interface Observer<T> {
    void onSuccess(T data);
    void onFailure(Throwable throwable);
}
