package com.zhtaxi.haodi.util;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Map;

/**
 * 网络工具类
 * Created by NickKong on 16/3/17.
 */
public class HttpUtil {

    /**
     * 普通传参
     * @param TAG 页面TAG
     * @param act 页面activity
     * @param mHandler 页面handler
     * @param params 参数
     * @param successCode mHandler返回码
     * @param postName 接口名称
     */
    public static void doPost(final String TAG, final Activity act,
                              final Handler mHandler, final Map params,
                              final int successCode, String postName) {

        RequestParams requestParams = new RequestParams(params);

        AsyncHttpResponseHandler asyncHttpResponseHandler = new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString, Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                mHandler.obtainMessage(0, throwable.getMessage()).sendToTarget();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString) {
                Log.d(TAG, responseString);

                if (statusCode == Constant.HTTP_STATUS_CODE_SUCCESS) {
                    mHandler.obtainMessage(successCode, responseString).sendToTarget();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        asyncHttpResponseHandler.setCharset("UTF-8");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constant.CONNECT_TIMEOUT);
        client.post(act, Constant.APP_URL + postName, requestParams, asyncHttpResponseHandler);
    }

}
