package com.awsomelink.utils;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import com.awsomelink.base.LinkItemAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by m.nurullayev on 29.04.2015.
 */
public class MediaUtils {
    public static final String TAG = "MediaFiles";

    public static LinkItemAction createLinkFromImage(Context context, File file, String linkId){
        // 1. new link id
        LinkItemAction linkItemAction = new LinkItemAction(linkId);
        linkItemAction.mFileName = file.getName();
        // 2. make folder for new link id
        File dstFile = new File(context.getFilesDir(),Links.mkpath(Links.OUT_FOLDER,linkId,Links.FILES_FOLDER,file.getName()));
        dstFile.mkdirs();
        try {
            InputStream in = null;
            OutputStream out = null;

            in = new FileInputStream(file);
            out = new FileOutputStream(dstFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
        } catch (FileNotFoundException ex){
            Log.e(TAG, ex.getMessage());
            return(null);
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            return(null);
        }
        return(linkItemAction);
    }
}
