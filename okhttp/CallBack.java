package com.vishee.vsccrt.utils.okhttp;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Description
 * Created by y.vn on 2021/3/31
 */
public abstract class CallBack<T> {

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total) {

    }

    /**
     * Thread Pool Thread
     */
    public abstract T parseNetworkResponse(Call call, Response response) throws Exception;

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     */
    public boolean validateReponse(Call call, Response response) {
        return response.isSuccessful();
    }

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(T response);

    public static CallBack CALLBACK_DEFAULT = new CallBack() {

        @Override
        public Object parseNetworkResponse(Call call, Response response) {
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}
