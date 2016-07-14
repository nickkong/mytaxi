package com.zhtaxi.haodi.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zhtaxi.haodi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class UpdateManager {

    String TAG = getClass().getSimpleName();

    private String installUrl = "";
    private String changelog = "";
    private String versionShort = "";
    private Context mContext;
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory().getPath();
    private static final String saveFileName = savePath + "/haodi.apk";
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int HANDLER_DATA = 3;
    private static final int HANDLER_NETWORK_ERR = 4;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                case HANDLER_DATA:
                    String json = msg.getData().getString("json");
                    checkUpdateInfo(json);
                    break;
                case HANDLER_NETWORK_ERR:
//                    Toast.makeText(mContext, mContext.getString(R.string.no_data_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public void sendUpdateRequest() {

        AsyncHttpResponseHandler asyncHttpResponseHandler = new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                Message message = mHandler.obtainMessage();
                message.what = HANDLER_NETWORK_ERR;
                message.sendToTarget();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
//                Log.d(TAG, responseString);

                if (statusCode == 200) {
                    Message message = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("json", responseString);
                    message.what = HANDLER_DATA;
                    message.setData(bundle);
                    message.sendToTarget();
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
        client.get(mContext, "http://api.fir.im/apps/latest/5777e71000fc746e05000028?api_token=9677acf457fea2e6a143c849c62cd09f", null, asyncHttpResponseHandler);

    }

    // 外部接口让主Activity调用
    private void checkUpdateInfo(String json) {

        final int oldVersionCode = getVersionInfo().versionCode;

        Log.d(TAG, "oldVersionCode===" + oldVersionCode);
        if (json != null) {
            Map versionMap = Tools.json2Map(json);

            String newVersion = (String) versionMap.get("version");
            installUrl = (String) versionMap.get("installUrl");
            changelog = (String) versionMap.get("changelog");
            versionShort = (String) versionMap.get("versionShort");
            int newVersionCode = oldVersionCode;
            try {
                newVersionCode = Integer.valueOf(newVersion);
                Log.d(TAG,"newVersionCode==="+newVersionCode);
            } catch (Exception e) {
            }

            if (newVersionCode > oldVersionCode) {
                showNoticeDialog();
//                    showTipsDialog(mContext.getString(R.string.update_tips),
//                            "更新","以后再说",2);
            }
        }
    }

    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新 v"+versionShort);
        builder.setMessage(changelog);
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }


//    private void showTipsDialog(String content,String dialog_left,String dialog_right,int dialogType){
//        TipsDialog tipsDialog = new TipsDialog(mContext,content,dialog_left,dialog_right,
//                dialogBtnListener,dialogType,0);
//        tipsDialog.getWindow().setGravity(Gravity.CENTER);
//        tipsDialog.setCanceledOnTouchOutside(false);
//        tipsDialog.show();
//    }
//
//    private OnDialogClickListener dialogBtnListener = new OnDialogClickListener() {
//        @Override
//        public void doConfirm() {
//            showDownloadDialog("软件版本更新",0);
//        }
//    };


//    private void showDownloadDialog(String content,int dialogType){
//        TipsDialog tipsDialog = new TipsDialog(mContext,content,
//                dialogBtnListener,dialogType);
//        tipsDialog.getWindow().setGravity(Gravity.CENTER);
//        tipsDialog.setCanceledOnTouchOutside(false);
//        final LayoutInflater inflater = LayoutInflater.from(mContext);
//        View v = inflater.inflate(R.layout.progress, null);
//        mProgress = (ProgressBar) v.findViewById(R.id.progress);
//        tipsDialog.setContentView(v);
//        tipsDialog.show();
//    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("正在下载...");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(installUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {

        final File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }

        if (downloadDialog != null) downloadDialog.cancel();

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                i.setData(Uri.fromFile(apkfile));
                i.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                i.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                i.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
                i.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getVersionInfo().packageName);

                mContext.startActivity(i);
                ((Activity) mContext).finish();
            }
        });

    }

    /**
     * 获取版本信息
     *
     * @return
     */
    public PackageInfo getVersionInfo() {

        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return info;
    }
}
