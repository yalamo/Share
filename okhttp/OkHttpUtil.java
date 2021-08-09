package com.vishee.vsccrt.utils.okhttp;

import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by y.vn on 2016/7/18.
 */
public class OkHttpUtil {

    private String TAG = "okhttp";

    private static OkHttpUtil okHttpUtil;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;
    private HttpLoggingInterceptor interceptor;

    public static OkHttpUtil getInstance() {
        if (okHttpUtil == null) {
            okHttpUtil = new OkHttpUtil();
        }
        return okHttpUtil;
    }

    //私有构造
    public OkHttpUtil() {
        interceptor = new HttpLoggingInterceptor(message -> {
            try {
                Log.i(TAG, message);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, message);
            }
        });
        //包含header、body数据
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getTrustManager())//配置
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                //其他配置
                .build();
        mPlatform = Platform.get();
    }

    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    //get请求-异步
    public void doGet(BaseRequest baseRequest, OkHttpCallBack callback) {
        doGetExcute(baseRequest, true, callback);
    }

    public void doGetExcute(BaseRequest baseRequest, boolean isAsyn, OkHttpCallBack callback) {
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .get()
                .url(baseRequest.url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .build();
        request(request, callback, isAsyn);
    }

    //post请求(map)
    public void doPostMap(BaseRequest baseRequest, OkHttpCallBack callback) {
        doPostMapExcute(baseRequest, true, callback);
    }

    //post请求(map)
    public void doPostMapExcute(BaseRequest baseRequest, boolean isAsyn, OkHttpCallBack callback) {
        FormBody.Builder builder = getFormBody(baseRequest);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(baseRequest.url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .build();
        request(request, callback, isAsyn);
    }

    //postJosn请求
    public void doPostJson(BaseRequest baseRequest, OkHttpCallBack callback) {
        doPostJsonExcute(baseRequest, true, callback);
    }

    public void doPostJsonExcute(BaseRequest baseRequest, boolean isAsyn, OkHttpCallBack callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String postContent = new Gson().toJson(baseRequest);
        RequestBody requestBody = RequestBody.Companion.create(postContent, mediaType);
        Request request = new Request.Builder()
                .url(baseRequest.url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .post(requestBody)
                .build();
        request(request, callback, isAsyn);
    }

    //post请求(表单)
    public void doPostForm(BaseRequest baseRequest, OkHttpCallBack callback) {
        doPostFormExcute(baseRequest, true, callback);
    }

    public void doPostFormExcute(BaseRequest baseRequest, boolean isAsyn, OkHttpCallBack callback) {
        MultipartBody.Builder builder = getMultipartBody(baseRequest);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(baseRequest.url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .build();
        request(request, callback, isAsyn);
    }

    //post请求(参数&文件)
    public void doPostFile(BaseRequest baseRequest, String key, File file, OkHttpCallBack callback) {
        doPostFileExcute(baseRequest, key, file, true, callback);
    }

    //post请求(单表单文件)-异步
    public void doPostFileExcute(BaseRequest baseRequest, String key, File file, boolean isAsyn, OkHttpCallBack callback) {
        MediaType mediaType = MediaType.parse("*/*");
//        MediaType mediaType = MediaType.parse("image/*");
//        MediaType mediaType = MediaType.parse("application/octet-stream");

        MultipartBody.Builder builder = getMultipartBody(baseRequest);
        //添加参数
        Map<String, String> mapParams = getMapParams(baseRequest);
        if (null != mapParams && mapParams.size() > 0) {
            for (String mapKey : mapParams.keySet()) {
                if (mapParams.get(mapKey) != null) {
                    builder.addFormDataPart(key, mapParams.get(mapKey));
                }
            }
        }
        //添加文件
        builder.addFormDataPart(key, file.getName(), RequestBody.create(file, mediaType));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(baseRequest.url)
                .post(requestBody)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .build();
        request(request, callback, isAsyn);
    }

    public void doPostFile(BaseRequest baseRequest, String key, Map<String, File> files, OkHttpCallBack callback) {
        doPostFileExcute(baseRequest, key, files, true, callback);
    }

    public void doPostFileExcute(BaseRequest baseRequest, String key, Map<String, File> files, boolean isAsyn, OkHttpCallBack callback) {
        MediaType mediaType = MediaType.parse("*/*");
//        MediaType mediaType = MediaType.parse("image/*");
//        MediaType mediaType = MediaType.parse("application/octet-stream");

        MultipartBody.Builder builder = getMultipartBody(baseRequest);
        //添加参数
        Map<String, String> mapParams = getMapParams(baseRequest);
        if (null != mapParams && mapParams.size() > 0) {
            for (String mapKey : mapParams.keySet()) {
                if (mapParams.get(mapKey) != null) {
                    builder.addFormDataPart(key, mapParams.get(mapKey));
                }
            }
        }
        //添加多文件
        if (null != files && files.size() > 0) {
            for (String mapKey : files.keySet()) {
                if (files.get(mapKey) != null) {
                    builder.addFormDataPart(key, files.get(mapKey).getName(), RequestBody.create(files.get(mapKey), mediaType));
                }
            }
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(baseRequest.url)
                .post(requestBody)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("token", baseRequest.token == null ? "" : baseRequest.token)
                .build();
        request(request, callback, isAsyn);
    }

    //异步下载文件
    public void downLoadFile(String url, FileCallBack callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();
        request(request, callback, true);
    }

    //最终执行请求
    private void request(Request request, CallBack callback, boolean isAsyn) {
        Log.e("okhttp", request.toString());
        Call call = mOkHttpClient.newCall(request);
        if (callback == null)
            callback = CallBack.CALLBACK_DEFAULT;
        CallBack finalCallback = callback;
        if (isAsyn) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sendFailResultCallback(call, e, finalCallback);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try {
                        if (call.isCanceled()) {
                            sendFailResultCallback(call, new IOException("Canceled!"), finalCallback);
                            return;
                        }
                        if (!finalCallback.validateReponse(call, response)) {
                            sendFailResultCallback(call, new IOException("request failed,reponse's code is : " + response.code()), finalCallback);
                            return;
                        }
                        Object o = finalCallback.parseNetworkResponse(call, response);
                        sendSuccessResultCallback(o, finalCallback);
                    } catch (Exception e) {
                        sendFailResultCallback(call, e, finalCallback);
                    } finally {
                        if (response.body() != null)
                            response.body().close();
                    }
                }
            });
        } else {
            Response response = null;
            try {
                response = call.execute();
                Object o = finalCallback.parseNetworkResponse(call, response);
                sendSuccessResultCallback(o, finalCallback);
            } catch (Exception e) {
                sendFailResultCallback(call, e, finalCallback);
            }
        }
    }

    //失败--uiThread
    public void sendFailResultCallback(Call call, Exception e, CallBack callback) {
        if (callback == null) return;
        mPlatform.execute(() -> callback.onError(call, e));
    }

    //成功--uiThread
    public void sendSuccessResultCallback(Object object, CallBack callback) {
        if (callback == null) return;
        mPlatform.execute(() -> callback.onResponse(object));
    }

    private FormBody.Builder getFormBody(BaseRequest request) {
        List<KeyValue> params = request.getParams();
        if (params == null) {
            params = new ArrayList<>();
        }
        Class<?> cls = request.getClass();
        Field[] fields = cls.getFields();

        for (Field field : fields) {
            KeyValue kv = new KeyValue();
            kv.key = field.getName();
            if (kv.key.equals("url")) {
                continue;
            }
            try {
                Object obj = field.get(request);

                if (obj != null) {
                    kv.value = obj.toString();
                    params.add(kv);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (KeyValue kv : params) {
            builder.add(kv.key, kv.value);
        }
        return builder;
    }

    private MultipartBody.Builder getMultipartBody(BaseRequest request) {
        List<KeyValue> params = request.getParams();
        if (params == null) {
            params = new ArrayList<>();
        }
        Class<?> cls = request.getClass();
        Field[] fields = cls.getFields();

        for (Field field : fields) {
            KeyValue kv = new KeyValue();
            kv.key = field.getName();
            if (kv.key.equals("url")) {
                continue;
            }
            try {
                Object obj = field.get(request);

                if (obj != null) {
                    kv.value = obj.toString();
                    params.add(kv);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (KeyValue kv : params) {
            multipartBodyBuilder.addFormDataPart(kv.key, kv.value);
        }
        return multipartBodyBuilder;
    }

    public static Map<String, String> getMapParams(BaseRequest request) {
        List<KeyValue> params = request.getParams();
        if (params == null) {
            params = new ArrayList<>();
        }
        Class<?> cls = request.getClass();
        Field[] fields = cls.getFields();

        for (Field field : fields) {
            KeyValue kv = new KeyValue();
            kv.key = field.getName();
            if (kv.key.equals("url")) {
                continue;
            }
            try {
                Object obj = field.get(request);

                if (obj != null) {
                    kv.value = obj.toString();
                    params.add(kv);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> map = new HashMap<>();
        for (KeyValue kv : params) {
            map.put(kv.key, kv.value);
        }

        return map;
    }

}
