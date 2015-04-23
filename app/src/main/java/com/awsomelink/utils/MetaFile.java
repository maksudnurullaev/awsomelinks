package com.awsomelink.utils;

import android.content.Context;
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

/**
 * Created by m.nurullayev on 23.04.2015.
 */
public class MetaFile {
    public static final String TAG = "MetaFile";
    public static final String FILE_NAME = "META.TXT";

    public static String setMeta(Context context, Links.ITEM_TYPE itemType, String linkId, String metaString){
        try{
            File file = Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(metaString);
            pw.close();
            return(file.getAbsolutePath());
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return(null);
        }
    }

    public static String getMetaDescription(Context context, Links.ITEM_TYPE itemType, String linkId){
        List<MetaItem> metaItems = getMeta(context,itemType,linkId);
        if(metaItems.size() == 0) { return("-:-"); }
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
                default:
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
            resultString += context.getString(MetaItem.getI18NId(type)) + "(" + result.get(type) + ")" ;
        }
        Log.d(TAG, "Meta description string: " + resultString);
        return(resultString);
    }

    public static List<MetaItem> getMeta(Context context, Links.ITEM_TYPE itemType, String linkId){
        List<MetaItem> result = new ArrayList<>();
        try{
            File file = Links.getFolderLinkFile(context, itemType, linkId, FILE_NAME);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null ){
                MetaItem metaItem = MetaItem.parseMetaString(line);
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
}
