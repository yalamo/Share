package com.vishee.vsccrt.utils.okhttp;

public class KeyValue {

    public String key;

    public String value;

    public KeyValue() {

    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return "KeyValue [key=" + key + ", value=" + value + "]";
    }

}
