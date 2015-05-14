package com.awsomelink.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.awsomelink.base.LinkItemAction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by m.nurullayev on 29.04.2015.
 */
public class MediaUtils {
    public static final String TAG = "MediaFiles";

    public static File createNextLinkJpegfile(Context context, String linkId, boolean isBig){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + (isBig ? "xxx_" : "") + timeStamp + ".jpeg";
        String imageFilePath = Links.mkpath(context.getFilesDir().getAbsolutePath(), Links.OUT_FOLDER, linkId, Links.FILES_FOLDER, imageFileName);
        return( new File(imageFilePath) );
    }

    public static File createNextLinkJpegBigTemp(){
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,   /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );
            return(image);
        } catch (IOException e){
            Log.e(TAG, e.toString());
        }
        return(null);
    }

    public static LinkItemAction createLinkFileFromCameraImage(Context context, String linkId, Bitmap bitmap){
        File file = createNextLinkJpegfile(context,linkId, false);
        // 1. new link id
        LinkItemAction linkItemAction = new LinkItemAction(linkId);
        linkItemAction.mFileName = file.getName();
        // 2. save image
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(stream.toByteArray());
            fos.close();
        } catch (FileNotFoundException e){
            Log.e(TAG, e.toString());
            return null;
        } catch (IOException e){
            Log.e(TAG, e.toString());
            return null;
        }
        return(linkItemAction);
    }

    public static LinkItemAction createLinkFileFromFile(Context context, String linkId, File file){
        // 1. new link id
        LinkItemAction linkItemAction = new LinkItemAction(linkId);
        linkItemAction.mFileName = file.getName();
        // 2. make folder for new link id
        File dstFile = createNextLinkJpegfile(context,linkId,true);
        copyFile(file, dstFile);
        return linkItemAction;
    }

    public static void copyFile(File fileSrc, File dstFile) {
        if( !dstFile.getParentFile().exists() )
            dstFile.getParentFile().mkdirs();
        try {
            InputStream in = null;
            OutputStream out = null;

            in = new FileInputStream(fileSrc);
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
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }
}
