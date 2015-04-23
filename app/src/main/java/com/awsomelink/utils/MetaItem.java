package com.awsomelink.utils;

import com.awsomelink.R;

/**
 * Created by m.nurullayev on 23.04.2015.
 */
public class MetaItem {
    public static enum TYPE {PASSWORD, CONTACTS, PICTURE, VIDEO, UNKNOWN};
    public static final String DELIMTER = ":";
    public TYPE mType = TYPE.UNKNOWN;
    public String content;
    public String description;

    public MetaItem(TYPE type){
        mType = type;
    }

    public static MetaItem parseMetaString(String metaString){
        if( metaString == null && metaString.isEmpty() ){ return(null); }
        String[] parts = metaString.split(DELIMTER);
        if(parts == null || parts.length == 0){ return(null); }
        MetaItem metaItem = new MetaItem(getType(parts[0]));
        switch(metaItem.mType){
            case CONTACTS:
                if( parts.length == 3) {
                    metaItem.content = parts[1];
                    metaItem.description = parts[2];
                } else { metaItem.mType = TYPE.UNKNOWN; }
                break;
            default:
                metaItem.mType = TYPE.UNKNOWN;
        }
        return(metaItem);
    }

    public static TYPE getType(String type){
        if( type == null || type.isEmpty() ){ return(TYPE.UNKNOWN);}
        if( type.equals("PASSWORD") ) { return(TYPE.PASSWORD); }
        if( type.equals("CONTACTS") ) { return(TYPE.CONTACTS); }
        if( type.equals("PICTURE") ) { return(TYPE.PICTURE); }
        if( type.equals("VIDEO") ) { return(TYPE.VIDEO); }
        return(TYPE.UNKNOWN);
    }

    public static String getType(TYPE type){
        if( type == TYPE.PASSWORD ) { return("PASSWORD"); }
        if( type == TYPE.CONTACTS ) { return("CONTACTS"); }
        if( type == TYPE.PICTURE ) { return("PICTURE"); }
        if( type == TYPE.VIDEO ) { return("VIDEO"); }
        return("UNKNOWN");
    }

    public static String makeMetaString(TYPE type, String... strings){
        String result = getType(type);
        for(String s:strings){
            result += DELIMTER + s;
        }
        return(result);
    }

    public static int getI18NId(TYPE type){
        switch(type){
            case CONTACTS:
                return R.string.Contacts;
            case VIDEO:
                return R.string.Video;
            case PICTURE:
                return R.string.Images;
        }
        return R.string.Files;
    }
}
