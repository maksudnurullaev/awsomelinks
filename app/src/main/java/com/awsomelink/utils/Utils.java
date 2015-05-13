package com.awsomelink.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by m.nurullayev on 29.04.2015.
 */
public class Utils {
    public static final String TAG = "UTILS";

    public static File getFile4Image(Context context, Intent intent){
        Uri selectedImage = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(
                selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        if( filePath != null && !TextUtils.isEmpty(filePath) ) {
            File file = new File(filePath);
            if (file.exists()) {
                return (file);
            }
        }
        return(null);
    }

    public static Map<String,File> fileNamesArray2Map(File[] files){
        if( files == null || files.length == 0){ return null; }

        Map<String,File> result = new HashMap<>();
        for(File file:files){
            result.put(file.getName(), file);
        }
        return result;
    }

}
