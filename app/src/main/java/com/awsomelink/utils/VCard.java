package com.awsomelink.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.awsomelink.base.Contact;

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

    public static void toFile(Context context, Map<String, Contact> contacts){
        if( contacts == null || contacts.size() == 0 ){
            Log.w(TAG, "No contacts to save!");
            return ;
        }
        try {
            File file = Links.getNewVCardFilePath(context);
            Log.d(TAG, "New VCF file: " + file.getAbsolutePath());
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            // TODO make actual save all data to vcf file by VCF API
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

        } catch (IOException e){
            Log.e(TAG, e.getMessage());
        }
    }
}
