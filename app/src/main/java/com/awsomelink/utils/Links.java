package com.awsomelink.utils;

import android.content.Context;
import android.os.Environment;

import com.awsomelink.base.LinkItemAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.nurullayev on 17.04.2015.
 */
public class Links {
    public static final String TAG = "Links";

    public enum LINK_TYPE {OUT, IN};
    public enum LINK_ACTION {DELETE, MORE, SHARE, AWSYNC, ADD_NEW_LINK};

    public static final String OUT_FOLDER = "OUT";
    public static final String IN_FOLDER = "IN";
    public static final String FILES_FOLDER = "FILES";

    public static final String LINK_ID_KEY = "LINK_ID";
    public static final String LINK_TYPE_KEY = "LINK_TYPE";

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

    public static String[] getLinkIDs(Context context, LINK_TYPE itemType){
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

    public static String[] getLinkFiles(Context context, LINK_TYPE itemType, String linkId){
        File dir = getFolderLinkFile(context, itemType, linkId, FILES_FOLDER);
        List<String> files = new ArrayList<>();
        File[] listOfFiles = dir.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        return(files.toArray(new String[files.size()]));
    }

    public static File getFolder(Context context, LINK_TYPE itemType){
        File file = new File(context.getFilesDir(), (itemType == LINK_TYPE.IN ? IN_FOLDER : OUT_FOLDER));
        return(file);
    }

    public static File getFolderLink(Context context, LINK_TYPE itemType, String linkId){
        File file = new File(context.getFilesDir(), mkpath((itemType == LINK_TYPE.IN ? IN_FOLDER : OUT_FOLDER), linkId));
        return(file);
    }

    public static File getFolderLinkFILES(Context context, LINK_TYPE itemType, String linkId){
        String itemTypeStr = (itemType == LINK_TYPE.IN ? IN_FOLDER : OUT_FOLDER);
        File file = new File( context.getFilesDir(), mkpath( itemTypeStr, linkId, FILES_FOLDER ) );
        return(file);
    }

    public static File getFolderLinkFILESFile(Context context, LINK_TYPE itemType, String linkId, String fileName){
        String itemTypeStr = (itemType == LINK_TYPE.IN ? IN_FOLDER : OUT_FOLDER);
        File file = new File( context.getFilesDir(), mkpath( itemTypeStr, linkId, FILES_FOLDER, fileName ) );
        return(file);
    }

    public static File getFolderLinkFile(Context context, LINK_TYPE itemType, String linkId, String fileName){
        String itemTypeStr = (itemType == LINK_TYPE.IN ? IN_FOLDER : OUT_FOLDER);
        File file = new File( context.getFilesDir(), mkpath( itemTypeStr, linkId, fileName ) );
        return(file);
    }

    public static File addNewLink(Context context, String linkId){
        File file = new File(context.getFilesDir(), mkpath(OUT_FOLDER, linkId, FILES_FOLDER));
        file.mkdirs();
        return(file);
    }

    public static File getNewVCardFilePath(Context context, String linkId){
        File file = new File(context.getFilesDir(), mkpath(OUT_FOLDER, linkId, FILES_FOLDER, getNewLinkID()) + ".vcf");
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
        if( folder.listFiles() != null && folder.listFiles().length > 0 ) {
            for (File f : folder.listFiles()) {
                if (f.isDirectory()) deleteLinkItem(f);
                f.delete();
            }
        }
        folder.delete();
    }

}
