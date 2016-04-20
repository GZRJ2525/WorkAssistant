package com.gzrijing.workassistant.listener;

/**
 * http网络请求，回调服务返回的结果
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
