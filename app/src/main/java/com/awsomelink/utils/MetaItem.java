package com.awsomelink.utils;

import android.text.TextUtils;

import com.awsomelink.R;

/**
 * Created by m.nurullayev on 23.04.2015.
 */
public class MetaItem {
    public static enum TYPE {PASSWORD, FILE, CONTACTS, PICTURE, VIDEO, UNKNOWN, AWSYNCHRONIZED, DESCRIPTION};
    public static final String DELIMTER = ":";
    public TYPE mType = TYPE.UNKNOWN;
    public String content;
    public String description;

    public MetaItem(TYPE type){
        mType = type;
    }

    public static MetaItem string2Meta(String metaString){
        if( metaString == null || metaString.isEmpty() ){ return(null); }
        String[] parts = metaString.split(DELIMTER);
        if(parts == null || parts.length == 0){ return(null); }
        MetaItem metaItem = new MetaItem(getType(parts[0]));
        if( metaItem.mType != TYPE.UNKNOWN ){
                if( parts.length == 3) {
                    metaItem.content = parts[1];
                    metaItem.description = parts[2];
                } else if( parts.length == 2 ) {
                    metaItem.content = parts[1];
                }
        }
        return(metaItem);
    }

    public static String meta2String(MetaItem metaItem){
        if( metaItem == null || metaItem.mType == TYPE.UNKNOWN ){ return(null); }
        String result = MetaItem.getType(metaItem.mType);
        if( metaItem.content != null && !metaItem.content.isEmpty()){
            result += DELIMTER + metaItem.content;
            if( metaItem.description != null && !metaItem.description.isEmpty()){
                result += DELIMTER + metaItem.description;
            }
        }
        return(result);
    }


    public static TYPE getType(String type){
        if(TextUtils.isEmpty(type)){ return(TYPE.UNKNOWN);}
        type = type.toUpperCase();
        for(TYPE t: TYPE.values() ){
            if( t.toString().equals(type) ) return t;
        }
        return(TYPE.UNKNOWN);
    }

    public static String getType(TYPE type){
        return( type == null ? TYPE.UNKNOWN.toString() : type.toString() );
    }

    public static String makeMetaString(TYPE type, String... strings){
        String result = getType(type);
        for(String s:strings){
            result += DELIMTER + s;
        }
        return(result);
    }

    public boolean isFileType(){
        return( isFileType(mType) );
    }

    public static boolean isFileType(TYPE type){
        switch(type){
            case FILE:
            case VIDEO:
            case PICTURE:
            case CONTACTS:
                return true;
        }
        return false;
    }

    public static int getI18NId(TYPE type){
        if( isFileType(type) ) {
            switch (type) {
                case CONTACTS:
                    return R.string.Contacts;
                case VIDEO:
                    return R.string.Video;
                case PICTURE:
                    return R.string.Images;
            }
            return R.string.Files;
        }
        return R.string.Dump;
    }
}
