package com.awsomelink.utils;

import android.content.Context;
import android.os.Environment;

import com.awsomelink.db.wildsql.WildSQLBase;

import java.io.File;
import java.util.UUID;

/**
 * Created by m.nurullayev on 17.04.2015.
 */
public class Links {
    public static final String TAG = "Links";

    public static String getNewLinkID(){
        // TODO may be needs to replace to something else (in future!)
        return(UUID.randomUUID().toString().substring(0, 8));
    }

    public static File getNewVCardFilePath(Context context){
        File file = new File(context.getFilesDir(), getNewLinkID() + ".vcf");
        return(file);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
