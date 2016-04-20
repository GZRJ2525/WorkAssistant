package com.gzrijing.workassistant.util;

import android.util.Log;

import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    /**
     * 小榄：120.25.169.30:80
     */
    /**
     * 自己：120.24.62.15:83
     */
    /**
     * get请求路径
     */
    private static final String URLPath = "http://120.25.169.30:80/main.ashx";
    /**
     * post请求路径
     */
    private static final String postURLPath = "http://120.25.169.30:80/mainpost.ashx";
    /**
     * 图片请求路径
     */
    public static final String imageURLPath = "http://120.25.169.30:80";
    /**
     * 音频请求路径
     */
    public static final String voiceURLPath = "http://120.25.169.30:80//Sound/";

    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    public static void sendHttpGetRequest(final String url, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(URLPath + url).build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (listener != null) {
                        listener.onFinish(response.body().string());
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }

    public static void sendHttpPostRequest(final RequestBody requestBody,
                                           final HttpCallbackListener listener) {
        mOkHttpClient.setWriteTimeout(100, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(100, TimeUnit.SECONDS);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(postURLPath)
                            .post(requestBody)
                            .build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    Log.e("response", response.toString());
                    if (listener != null) {
                        listener.onFinish(response.body().string());
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }


    public static void downloadFile(final String url, final File dir,
                                    final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.code() == 200) {
                            InputStream is = null;
                            byte[] buf = new byte[2048];
                            int len = 0;
                            FileOutputStream fos = null;
                            try {
                                is = response.body().byteStream();
                                File file = new File(dir, getFileName(url));
                                fos = new FileOutputStream(file);
                                while ((len = is.read(buf)) != -1) {
                                    fos.write(buf, 0, len);
                                }
                                fos.flush();
                                if (listener != null) {
                                    listener.onFinish("ok");
                                }
                            } catch (IOException e) {
                                if (listener != null) {
                                    listener.onError(e);
                                }
                            } finally {
                                try {
                                    if (is != null) is.close();
                                } catch (IOException e) {
                                }
                                try {
                                    if (fos != null) fos.close();
                                } catch (IOException e) {
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onFinish(response.code() + "");
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


}
