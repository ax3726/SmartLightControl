package com.regenpod.smartlightcontrol.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.regenpod.smartlightcontrol.app.LightApplication;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author unknow
 */
public class FileUtil {

    private FileUtil() {

    }

    /**
     * SD卡路径
     */
    public static String getSdPath() {
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        }
        return "";
    }

    public static String getAppSdMainPath() {
        String dir = getSdPath();
        if (TextUtils.isEmpty(dir)) {
            dir = LightApplication.getInstance().getApplicationContext().getFilesDir().getPath() + "/";
        }
        return dir + "灯控崩溃日志/";
    }

    /**
     * 创建目录
     *
     * @param strDir 路径名称
     * @return 是否成功
     */
    public static boolean makeDir(String strDir) {
        File file = new File(strDir);
        return file.exists() || file.mkdirs();
    }

    /**
     * 从文件读取出二进制数据
     *
     * @param fileName 文件名 包含路径
     * @return 二进制数据
     */
    public static byte[] getFileByte(String fileName) {
        byte[] buffer = null;
        File file = new File(fileName);
        try (FileInputStream fis = new FileInputStream(file);) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[(int) file.length()];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
        }
        return buffer;
    }

    /**
     * 拷贝配置assets
     *
     * @param from config.json;
     * @param to   /mnt/sdcard/Prison/config.json
     */
    public static void copyAssetsFile(Context context, String from, String to) {
        try {
            if (!(new File(to)).exists()) {
                try (InputStream is = context.getResources().getAssets().open(from);
                     FileOutputStream fos = new FileOutputStream(to)) {
                    byte[] buffer = new byte[7168];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 判断文件是否存在.
     *
     * @param filename 文件名
     * @return 是否存在.
     */
    public static boolean isFileExists(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    /**
     * 判断文件是否存在并文件是否为空.
     *
     * @param filename 文件名
     * @return 是否存在.
     */
    public static boolean isFileExistsEmpty(String filename) {
        File f = new File(filename);
        return f.exists() && f.length() > 0;
    }

    public static byte[] file2bytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
        }
        return buffer;
    }

    public static String getExtensionName(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }
}
