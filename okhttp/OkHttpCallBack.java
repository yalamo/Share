package com.vishee.vsccrt.utils.okhttp;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Response;

//用于处理接口返回非标准化请求 根据HtppsTatus状态码判断
public abstract class OkHttpCallBack extends CallBack<String> {

    @Override
    public String parseNetworkResponse(Call call, Response response) {
        String json = null;
        try {
            json = response.body().string();
            Log.e("okhttp", json);
//            if (response.code() == OkHttpConstant.LOGIN_TIMEOUT) {
//                //登录超时
//                Intent intent = new Intent();
//                intent.setAction(BaseApplication.getExitsAction());
//                MyApplication.getApp().sendBroadcast(intent);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public abstract void onError(Call call, Exception e);

}
