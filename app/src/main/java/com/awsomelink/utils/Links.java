package com.awsomelink.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import com.awsomelink.base.LinkItemAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.nurullayev on 17.04.2015.
 */
public class Links {
    public static final String TAG = "Links";

    public enum ITEM_TYPE {OUT_BOX, IN_BOX};
    public enum ITEM_ACTION {DELETE, MORE, SHARE, AWSYNC, ADD_NEW_LINK};

    public static final String OUT_FOLDER = "OUT";
    public static final String IN_FOLDER = "IN";

    public static String getNewLinkID(){
        // TODO may be needs to replace something else...
        return(UUID.randomUUID().toString().substring(0, 8));
    }

    public static String mkpath(String... dirs){
        String result = "";
        for(int i=0; i<dirs.length;i++){
            if( !result.isEmpty() ){ result += "/"; }
            result += dirs[i];
        }
        return(result);
    }

    public static String[] getLinkIDs(Context context, ITEM_TYPE itemType){
        File dir = getFolder(context, itemType);
        List<String> IDs = new ArrayList<>();
        File[] listOfFiles = dir.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                IDs.add(listOfFiles[i].getName());
            }
        }
        return(IDs.toArray(new String[IDs.size()]));
    }

    public static File getFolder(Context context, ITEM_TYPE itemType){
        File file = new File(context.getFilesDir(), (itemType == ITEM_TYPE.IN_BOX ? IN_FOLDER : OUT_FOLDER));
        return(file);
    }

    public static File getFolderLink(Context context, ITEM_TYPE itemType, String linkId){
        File file = new File(context.getFilesDir(), mkpath((itemType == ITEM_TYPE.IN_BOX ? IN_FOLDER : OUT_FOLDER), linkId));
        return(file);
    }

    public static File getFolderLinkFile(Context context, ITEM_TYPE itemType, String linkId, String fileName){
        String itemTypeStr = (itemType == ITEM_TYPE.IN_BOX ? IN_FOLDER : OUT_FOLDER);
        File file = new File( context.getFilesDir(), mkpath( itemTypeStr, linkId, fileName ) );
        return(file);
    }

    public static File addNewLink(Context context, String linkId){
        File file = new File(context.getFilesDir(), mkpath(OUT_FOLDER, linkId));
        file.mkdir();
        return(file);
    }

    public static File getNewVCardFilePath(Context context, String linkId){
        File file = new File(context.getFilesDir(), mkpath(OUT_FOLDER, linkId, getNewLinkID()) + ".vcf");
        file.getParentFile().mkdirs();
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

    /* Delete link item */
    public static void deleteLinkItem(final Context context, final LinkItemAction linkItemAction){
                File folder = getFolderLink(context, linkItemAction.mItemType, linkItemAction.mID);
                deleteLinkItem(folder);
    }

    public static void deleteLinkItem(File folder){
        for(File f: folder.listFiles()){
            if( f.isDirectory() ) deleteLinkItem(f);
            f.delete();
        }
        folder.delete();
    }

}
