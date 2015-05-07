package com.awsomelink.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.awsomelink.base.Contact;
import com.awsomelink.base.LinkItemAction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by m.nurullayev on 17.04.2015.
 */
public class VCard {
    private static final String TAG = "VCard";

    public static LinkItemAction createLinkFileFromContacts(Context context, Map<String, Contact> contacts, String linkId){
        if( contacts == null || contacts.size() == 0 ){
            Log.w(TAG, "No contacts to save!");
            return(null);
        }
        LinkItemAction linkItemAction = new LinkItemAction(linkId);
        try {
            File file = Links.getNewVCardFilePath(context, linkId);
            Log.d(TAG, "New VCF file: " + file.getAbsolutePath());
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for (String key : contacts.keySet()) {
                Log.d(TAG, "Make vcard record for contact: " + key);
                pw.println("BEGIN:VCARD");
                pw.println("VERSION:2.1");
                pw.println(getN(key));
                pw.println(getFN(key));
                HashMap<String, Boolean> phones = contacts.get(key).get_phones();
                if( phones != null && phones.size() > 0 ){
                    for(String phone:phones.keySet()){
                        if( phones.get(phone) ){
                            pw.println(getTEL(phone));
                        }
                    }
                }
                pw.println("END:VCARD");
            }
            pw.close();
            linkItemAction.mFileName = file.getName();
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return(null);
        }
        return(linkItemAction);
    }

    public static String getN(String name){
        String result = "N:";
        String[] aResult = name.split(" ", 2);
        if( aResult.length == 2 ){
            result += aResult[1];
            result += ";";
            result += aResult[0];
        } else {
            result += name;
        }
        return(result);
    }

    public static String getFN(String name){
        return("FN:" + name.trim());
    }

    public static String getTEL(String tel){
        return("TEL;WORK;VOICE:" + tel);
    }

}
