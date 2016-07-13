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
     * 普通传参 post
     *
     * @param TAG 页面TAG
     * @param act 页面activity
     * @param mHandler 页面handler
     * @param failureCode mHandler失败返回码
     * @param successCode mHandler成功返回码
     * @param postName 接口名称
     * @param params 参数
     */
    public static void doPost(final String TAG, final Activity act,
                              final Handler mHandler, final int failureCode,
                              final int successCode, String postName, final Map params) {

        RequestParams requestParams = new RequestParams(params);

        if(Constant.DEVELOPER_MODE){
            Log.d(TAG,"post_requestParams=="+TAG+"-->"+RequestAddress.APP_URL + postName + requestParams.toString());
        }

        AsyncHttpResponseHandler asyncHttpResponseHandler = new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString, Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                mHandler.obtainMessage(failureCode, throwable.getMessage()).sendToTarget();
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
        client.post(act, RequestAddress.APP_URL + postName, requestParams, asyncHttpResponseHandler);
    }


    /**
     * 普通传参 get
     *
     * @param TAG 页面TAG
     * @param act 页面activity
     * @param mHandler 页面handler
     * @param failureCode mHandler失败返回码
     * @param successCode mHandler成功返回码
     * @param postName 接口名称
     * @param params 参数
     */
    public static void doGet(final String TAG, final Activity act,
                             final Handler mHandler, final int failureCode,
                             final int successCode, String postName, final Map params) {

        RequestParams requestParams = new RequestParams(params);

        if(Constant.DEVELOPER_MODE){
            Log.d(TAG,"get_requestParams=="+TAG+"-->"+RequestAddress.APP_URL + postName + requestParams.toString());
        }

        AsyncHttpResponseHandler asyncHttpResponseHandler = new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                mHandler.obtainMessage(failureCode, throwable.getMessage()).sendToTarget();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
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
        client.get(act, RequestAddress.APP_URL + postName, requestParams, asyncHttpResponseHandler);

    }

}
