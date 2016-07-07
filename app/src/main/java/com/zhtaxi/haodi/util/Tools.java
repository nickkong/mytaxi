package com.zhtaxi.haodi.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 包含图片处理类、json格式类、调用系统电话、短信、email
 * 检查网络、校验格式、日期转换、编辑框保留两位小数、获取版本信息
 */
public class Tools {

    /**
     * 获取版本信息
     */
    public static PackageInfo getPackageInfo(Context context) {

        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 检查网络状态
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查是否wifi环境
     */
    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.
                getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return !(str != null && !"".equals(str));
    }

    /**
     * 校验邮箱格式
     */
    public static boolean isEmail(String paramString) {
        return Pattern
                .compile(
                        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")
                .matcher(paramString).matches();
    }

    /**
     * 检测网络是否存在
     */
    public static void HttpTest(Activity mActivity) {
        if (!isNetworkAvailable(mActivity)) {
            AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
            builders.setTitle("温馨提示");
            builders.setMessage("连接异常,请检查您的网络是否正常！");
            builders.setPositiveButton("确定", null);
            builders.create().show();
        }
    }

    /**
     * 拍照保存图片后，刷新系统相册
     */
    public static void exportToGallery(Context context, File file) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Video.Media.DATA, file.getName());
//        final Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));
    }

    /**
     * 从本地图片路径生成bitmap
     */
    public static Bitmap laodImageFromFile(String filePath) {
        File localFile = new File(filePath);
        Bitmap localBitmap = null;
        if (localFile.exists()) {
            localBitmap = BitmapFactory.decodeFile(filePath);
        } else {
            localBitmap = null;
        }
        return localBitmap;
    }

    /**
     * 从本地图片InputStream生成bitmap
     */
    public static Bitmap loadImageFromStream(InputStream is) {
        byte[] arrayOfByte;
        Bitmap bitMap = null;
        try {
            arrayOfByte = getBytes(is);
            int i = arrayOfByte.length;
            bitMap = BitmapFactory.decodeByteArray(arrayOfByte, 0, i);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭InputStream
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitMap;
    }
    private static byte[] getBytes(InputStream paramInputStream)
            throws IOException {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte[1024];
        while (true) {
            int i = paramInputStream.read(arrayOfByte, 0, 1024);
            if (i == -1)
                return localByteArrayOutputStream.toByteArray();
            localByteArrayOutputStream.write(arrayOfByte, 0, i);
            localByteArrayOutputStream.flush();
        }
    }

    /**
     * 从网络url生成bitmap，需要放到线程
     */
    public static Bitmap loadImageFromUrl(String strImageUrl) {
        Bitmap bitMap = null;
        URL aryURI;
        try {
            aryURI = new URL(strImageUrl);
            URLConnection conn = aryURI.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            bitMap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitMap;
    }

    /**
     * 从网络url生成bitmap，有简单压缩处理，需要放到线程
     */
    public static Bitmap loadImageFromUrlWithOpt(String strImageUrl) {
        Bitmap bitMap = null;
        URL aryURI;
        try {
            aryURI = new URL(strImageUrl);
            URLConnection conn = aryURI.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            bitMap = BitmapFactory.decodeStream(is, null, opts);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitMap;
    }

    /**
     * 图片模糊效果
     */
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 某些手机拍照后照片会旋转90度，照片旋转角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片，bitmap可能会导致内存溢出
     * 配合readPictureDegree方法使用
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //bitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 从文件路径获取特定大小的图片
     */
    public static Bitmap createThumbFromFile(String imagePath, int width_limit, int height_limit) {
        Bitmap bitmap = null;
        if (width_limit == 0 || height_limit == 0) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        if (options.outWidth == 0 || options.outHeight == 0) {
            return null;
        }
        boolean isOriginWider = (options.outWidth / options.outHeight) > (width_limit / height_limit);
        int ratio = isOriginWider ? (int) (options.outWidth / width_limit) : (int) (options.outHeight / height_limit);
        if (ratio < 1)
            ratio = 1;
        options.inSampleSize = ratio;
        //	options.inPreferredConfig = Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    /**
     * 保存图片到文件
     */
    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream;
        try {
            stream = new FileOutputStream(filename);
            return bmp.compress(format, quality, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 编辑框限制最多输入两位小数
     */
    public static void saveTwoDecimal(EditText editText, CharSequence s){

        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 3);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(0, 1));
                editText.setSelection(1);
                return;
            }
        }
    }

    /**
     * bitmap保存为文件
     */
    public static boolean saveFile(Bitmap bm, String path) {
        File localFile2 = new File(path);
        boolean bool2 = false;
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(
                    localFile2);
            BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(
                    localFileOutputStream);
            if (bm != null) {
                Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.JPEG;
                bool2 = bm.compress(localCompressFormat, 80,
                        localBufferedOutputStream);
                localBufferedOutputStream.flush();
                localBufferedOutputStream.close();
            }
            return bool2;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除目录及文件
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(path); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 将javabean集合转换成json字符串
     */
    public static <E extends Object> String javaBeanListToJson(List<E> e) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<E>>() {
        }.getType();
        return gson.toJson(e, type);
    }

    /**
     * json字符串转换成Map
     */
    public static Map<String, Object> jsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * json字符串转换成List
     */
    public static <T> List<T> jsonToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * List转换成json字符串
     */
    public static <T> String listTojson(List<T> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<T>>() {
        }.getType();
        return gson.toJson(list, type);
    }

    /**
     * Map转换成json字符串
     */
    public static String map2Json(Map m) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map>() {
        }.getType();
        return gson.toJson(m, type);
    }

    /**
     * 从json HASH表达式中获取一个map，该map支持嵌套功能
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map json2Map(String jsonString) {
        JSONObject jsonObject;
        Map valueMap = new HashMap();
        try {
            jsonObject = new JSONObject(jsonString);
            Iterator keyIter = jsonObject.keys();
            String key;
            Object value;

            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jsonObject.get(key);

                if (value instanceof JSONArray) {
                    List vlist = new ArrayList();
                    vlist = Tools.getList(value.toString());
                    value = vlist;
                }
                valueMap.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return valueMap;
    }

    /**
     * 把json字符串转换为 List<Map<String, Object>> 形式，
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getList(String jsonString) {
        List<Map<String, Object>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(json2Map(jsonObject.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 下载网络图片
     */
    public static void downloadFile(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 全角转化为半角
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 半角转化为全角
     */
    public static String ToSBC(String input) {
        // 半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127 && c[i] > 32)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 清除掉特殊字符
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    /**
     * 使用格式<b>_pattern</b>格式化日期输出
     */
    public static String formatDate(Date _date, String _pattern) {
        if (_date == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_pattern);
        String stringDate = simpleDateFormat.format(_date);

        return stringDate;
    }

    /**
     * 调用系统方法拨打电话
     */
    public static void callThePhone(Context context, String tel) {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + tel));
        context.startActivity(dialIntent);
    }

    /**
     * 调用系统方法发信息
     */
    public static void sendMsg(Context context, String mobile) {
        Intent dialIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                + mobile));
        context.startActivity(dialIntent);
    }

    /**
     * 调用系统方法发邮件
     */
    public static void sendEmail(Context context, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.setType("text/plain");
        // Intent.createChooser(intent, "请选择邮件类型");
        context.startActivity(intent);
    }

    /**
     * 日期格式转换为字符串
     */
    public static String date2String(Date utilDate) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return f.format(utilDate);
    }

    /**
     * 校验是否数字
     */
    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    /**
     * 格式化手机号码为136****0000
     */
    public static String formatPhone(String mobile) {

        if (!"".equals(mobile) && mobile.length() >= 11) {
            return mobile.substring(0, 3) + "****" + mobile.substring(7);
        } else {
            return "";
        }
    }

    /**
     * dp转为px
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}