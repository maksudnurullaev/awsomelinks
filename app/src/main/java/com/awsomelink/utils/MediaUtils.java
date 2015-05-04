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

    public static LinkItemAction createLinkFromImage(Context context, File file){
        // 1. new link id
        String newLinkId = Links.getNewLinkID();
        LinkItemAction linkItemAction = new LinkItemAction(newLinkId);
        linkItemAction.mFileName = file.getName();
        // 2. make folder for new link id
        File newLinkDir = new File(context.getFilesDir(),Links.mkpath(Links.OUT_FOLDER, newLinkId));
        newLinkDir.mkdirs();
        // copy file to new place
        File dstFile = new File(context.getFilesDir(),Links.mkpath(Links.OUT_FOLDER,newLinkId,file.getName()));

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
