package com.awsomelink.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by m.nurullayev on 23.04.2015.
 */
public class MetaFile {
    public static final String TAG = "MetaFile";
    public static final String FILE_NAME = "META.TXT";
    public static final String FILE_NAME_AWSYNC = "META.TXT.AWSYNC";

    public static boolean addEmptyMeta(Context context, Links.LINK_TYPE itemType, String linkId){
        try{
            File file = Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME);
            if( !file.exists() ) {
                return( file.createNewFile() );
            }
            return (true);
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
        }
        return(false);
    }

    public static String setMeta(Context context, Links.LINK_TYPE itemType, String linkId, String metaString, boolean update){
        try{
            File file = Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME);
            List<MetaItem> metaItems = null;
            if( file.exists() ) {
                metaItems = MetaFile.getMeta(file);
            }
            if (metaItems != null && metaItems.size() > 0) {
                if( update ) {
                    setMeta(context, itemType, linkId, updateMeta(metaItems,MetaItem.string2Meta(metaString)));
                } else {
                    metaItems.add(MetaItem.string2Meta(metaString));
                    setMeta(context, itemType, linkId, metaItems);
                }
            } else {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pw.println(metaString);
                pw.close();
            }
            return (file.getAbsolutePath());
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return(null);
        }
    }

    public static String setMeta(Context context, Links.LINK_TYPE itemType, String linkId, List<MetaItem> metaItems){
        try{
            File file = Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for(MetaItem metaItem:metaItems){
                pw.println(MetaItem.meta2String(metaItem));
            }
            pw.close();
            return (file.getAbsolutePath());
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return(null);
        }
    }

    public static List<MetaItem> updateMeta(List<MetaItem> metaItems, MetaItem metaItem){
        boolean updated = false;
        List<MetaItem> result = new ArrayList<>();
        for(MetaItem mi:metaItems){
            if( !updated && mi.mType == metaItem.mType ){
                updated = true;
                result.add(metaItem);
            } else {
                result.add(mi);
            }
        }
        if( !updated ) { result.add(metaItem); }
        return(result);
    }

    public static boolean hasValidData(List<MetaItem> metaItems){
        for(MetaItem metaItem: metaItems){
            switch (metaItem.mType){
                case CONTACTS:
                case FILE:
                case PICTURE:
                case VIDEO:
                    return true;
            }
        }
        return false;
    }

    public static String getMetaDescription(Context context, List<MetaItem> metaItems){
        if(metaItems == null || metaItems.size() == 0) { return("-:-"); }
        HashMap<MetaItem.TYPE,Integer> result = new HashMap<>();
        for(MetaItem metaItem: metaItems){
            switch (metaItem.mType){
                case CONTACTS:
                    if( result.containsKey(metaItem.mType) ){
                        result.put(metaItem.mType, (Integer.valueOf(metaItem.description) + result.get(metaItem.mType)));
                    } else {
                        result.put(metaItem.mType, Integer.valueOf(metaItem.description));
                    }
                    break;
                case PASSWORD:    // ignoring
                case DESCRIPTION: // ignoring
                    break;
                default:
                    if( metaItem.mType == MetaItem.TYPE.PASSWORD ||  metaItem.mType == MetaItem.TYPE.AWSYNCHRONIZED ) break;
                    if( result.containsKey(metaItem.mType) ){
                        result.put(metaItem.mType, result.get(metaItem.mType) + 1);
                    } else {
                        result.put(metaItem.mType, 1);
                    }
            }
        }
        String resultString = "";
        for(MetaItem.TYPE type:result.keySet()){
            if( !resultString.isEmpty() ){ resultString += " "; }
            if( MetaItem.isFileType(type) ) {
                resultString += context.getString(MetaItem.getI18NId(type)) + "(" + result.get(type) + ")";
            }
        }
        Log.d(TAG, "Meta description string: " + resultString);
        return(TextUtils.isEmpty(resultString) ? "-:-" : resultString );
    }

    public static String getMetaContent(List<MetaItem> metaItems, MetaItem.TYPE mType){
        for(MetaItem mi:metaItems){
            if(mi.mType == mType){
                return(mi.content);
            }
        }
        return(null);
    }

    public static void setMetaAwsync(Context context, String linkId, boolean value){
        String metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(value));
        setMeta(context, Links.LINK_TYPE.OUT, linkId, metaString, true);
    }

    public static File getMetaFile(Context context, Links.LINK_TYPE itemType, String linkId){
        return(Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME));
    }

    public static File getMetaFileAWSYNC(Context context, Links.LINK_TYPE itemType, String linkId){
        return(Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME_AWSYNC));
    }

    public static File getMetaFileAwsync(Context context, Links.LINK_TYPE itemType, String linkId){
        return(Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME_AWSYNC));
    }

    public static List<MetaItem> getMeta(Context context, Links.LINK_TYPE itemType, String linkId){
        return(getMeta(getMetaFile(context, itemType, linkId)));
    }

    public static List<MetaItem> getMeta(File file){
        List<MetaItem> result = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null ){
                MetaItem metaItem = MetaItem.string2Meta(line);
                if( metaItem.mType != MetaItem.TYPE.UNKNOWN){
                    result.add(metaItem);
                    Log.d(TAG, "Found meta item: " +  MetaItem.getType(metaItem.mType));
                }
            }
            return(result);
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return(null);
        }
    }

    public static boolean isAWSynchonized(Context context, Links.LINK_TYPE itemType, String linkId){
        List<MetaItem> metaItems = getMeta(getMetaFile(context, itemType,linkId));
        MetaItem metaItem = getMetaItem(metaItems,MetaItem.TYPE.AWSYNCHRONIZED);
        if( metaItem == null ){ return(false); }
        Log.d(TAG, "Find awsync status for: " + linkId + ", status: "  + metaItem.content);
        if( metaItem != null && metaItem.content != null && Boolean.valueOf(metaItem.content) ) {

            return (true);
        }
        return(false);
    }

    public static MetaItem getMetaItem(List<MetaItem> metaItems, MetaItem.TYPE type){
        for(MetaItem metaItem:metaItems){
            if(metaItem.mType == type) return metaItem;
        }
        return(null);
    }

    public static void syncWithLocalFiles(Context context, Links.LINK_TYPE itemType, String linkId){
        List<MetaItem> metaItems = getMeta(context, itemType, linkId);
        List<MetaItem> metaItemsResult = new ArrayList<>();
        for(MetaItem metaItem: metaItems){
            if(!metaItem.isFileType() || Links.getFolderLinkFILESFile(context, itemType, linkId, metaItem.content).exists() ){
                metaItemsResult.add(metaItem);
            }
        }
        if( metaItemsResult.size() > 0) {
            setMeta(context, itemType, linkId, metaItemsResult);
        } else {
            addEmptyMeta(context, itemType, linkId);
        }
    }

    public static List<File> getUpdateFiles(Context context, Links.LINK_TYPE itemType, String linkId){
        List<MetaItem> metaItems = getMeta(context, itemType, linkId);
        File awsyncFile = getMetaFileAWSYNC(context, itemType, linkId);
        Map<String,MetaItem> mapMetaItemsAwsync = null;
        List<File> result = new ArrayList<>();

        if( awsyncFile.exists() ) {
            List<MetaItem> metaItemsAwsync = getMeta(awsyncFile);
            mapMetaItemsAwsync = getMapByContent(metaItemsAwsync);
        }
        for(MetaItem metaItem: metaItems){
            if( metaItem.isFileType() ){
                File tempFile = Links.getFolderLinkFILESFile(context, itemType, linkId, metaItem.content);
                if( tempFile.exists() && (mapMetaItemsAwsync == null || !mapMetaItemsAwsync.containsKey(metaItem.content))) {
                    result.add(tempFile);
                }
            }
        }
        return(result);
    }

    public static Map<String,MetaItem> getMapByContent(List<MetaItem> metaItems){
        Map<String,MetaItem> result = new HashMap<>();
        for( MetaItem metaItem:metaItems){
            if( !TextUtils.isEmpty(metaItem.content) ){
                result.put(metaItem.content, metaItem);
            }
        }
        return result;
    }
}
