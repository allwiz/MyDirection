package net.allwiz.mydirection.util;

import android.content.Context;

import java.io.File;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static final String UPPER_DIR = "..";



    public static String getDbFileFolderPath(Context context) {
        String path = "/data/data/" + context.getPackageName() + "/databases/";
        LogEx.d(TAG, path);
        return path;
    }


    public static String getDbFilePath(Context context, String dbFileName) {
        String path = "/data/data/" + context.getPackageName() + "/databases/" + dbFileName;
        LogEx.d(TAG, path);
        return path;
    }


    public static boolean deleteFile(String path) {
        boolean ret = false;
        if (path != null) {
            if (path.indexOf(UPPER_DIR) != -1) {
                LogEx.d(TAG, String.format("[WARN] FOUND UPPER DIRECTORY PATH %s", path));
                return false;
            }

            File file = new File(path);
            if (file.exists()) {
                ret = file.delete();
            }
        }
        return ret;
    }
}
