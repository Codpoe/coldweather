package com.app.coldweather.util;

/**
 * Created by Codpoe on 2016/2/4.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
