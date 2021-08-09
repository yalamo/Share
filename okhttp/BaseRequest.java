package com.vishee.vsccrt.utils.okhttp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseRequest implements Serializable {

    public String url;
    public String token;

    private List<KeyValue> params;

    public List<KeyValue> getParams() {
        return params;
    }

    public BaseRequest addParam(KeyValue param) {
        if (params == null) {
            params = new ArrayList<>();
        }
        params.add(param);
        return this;
    }

}